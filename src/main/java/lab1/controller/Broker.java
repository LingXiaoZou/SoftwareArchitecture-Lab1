package lab1.controller;

import lab1.Util.JsonUtil;
import lab1.model.Exchange;
import lab1.model.Message;

import java.io.*;
import java.net.*;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

public class Broker {
    // 队列
    private Map<String, BlockingQueue<Message>> queues;

    // 保存Broker的serverSocket
    private ServerSocket serverSocket;
    // 线程池
    private ExecutorService executor;
    // 订阅者信息 String是订阅的消息type
    private Map<String, List<InetSocketAddress>> subscribers;
    // Exchange交换器
    private Exchange exchange;


    public Broker(){
        queues = new ConcurrentHashMap<>();
        subscribers = new ConcurrentHashMap<>();
        executor = Executors.newCachedThreadPool();
        exchange = new Exchange("x", (ConcurrentHashMap<String, BlockingQueue<Message>>) queues);
    }

    /**
     * start函数：
     * params:
     * start函数完成了两件事，1是启动消息分发线程startMessageDispatcher，2是启动监听信息的函数listenForProducers
     * @throws Exception
     */
    public void start() throws Exception {
        System.out.println("Broker start.\n");

        // 启动消息分发线程
        Thread dispatcherThread = new Thread(this::startMessageDispatcher);
        dispatcherThread.start();

        // 启动用于监听Producer的Socket的线程
        Thread producerListenerThread = new Thread(() -> {
            try {
                listenForProducers(); // 尝试运行listenForProducers方法
            } catch (IOException e) {
                e.printStackTrace(); // 打印异常信息
                // 还可以添加更多的异常处理逻辑
            }
        });
        producerListenerThread.start();
    }

    /**
     * subscribe函数
     * @param queueKey 队列标识符
     * @param subscriberAddress 用户的Socket地址
     * 完成订阅新用户的功能
     */
    // 订阅新用户
    public void subscribe(String queueKey, InetSocketAddress subscriberAddress) {
        BlockingQueue<Message> queue = exchange.getBindings().get(queueKey);

        if (queue == null) {
            // 如果队列不存在，创建并绑定新队列
            queue = new LinkedBlockingQueue<Message>();
            exchange.bind(queue, queueKey);
            System.out.println("Subscribe add new queue: "+ queueKey);
        }

        // 将订阅者地址添加到subscribers映射中
        subscribers.computeIfAbsent(queueKey, k -> new CopyOnWriteArrayList<>()).add(subscriberAddress);
    }

    // 取消订阅
    public void unsubscribe(String messageType, InetSocketAddress subscriberAddress) {
        List<InetSocketAddress> subscriberAddresses = subscribers.get(messageType);
        if (subscriberAddresses != null) {
            subscriberAddresses.remove(subscriberAddress);
        }
    }

    /**
     * 监听来自Producer的信息，并且对每一个消息创建新线程指定ProduceHandler函数
     */
    private void listenForProducers() throws IOException {
        // 监听来自Producer的连接
        // 对每个连接创建一个新线程来处理消息入队
        serverSocket = new ServerSocket(8000);

        while(true){
            // 以下是获取message信息以及开启线程处理
            Socket socket = serverSocket.accept();
            try (InputStream in = socket.getInputStream();
                 InputStreamReader isr = new InputStreamReader(in);
                 BufferedReader br = new BufferedReader(isr)) {

                StringBuilder receivedData = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    receivedData.append(line);
                }

                String json = receivedData.toString();
                // 反序列化得到Message
                Message message = JsonUtil.deserializeMessage(json);

                System.out.println("Broker Received message from: " + message.getName() + "," + message.getData());

                executor.submit(new ProducerHandler(message, this.exchange));
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    /**
     * 启动一个线程来检查队列中消息，MessageDispatcher
     */
    private void startMessageDispatcher() {
        // 启动一个或多个线程检查队列中的消息，并将其发送给Consumer
        executor.submit(new MessageDispatcher(queues, subscribers));
    }
}

/**
 * ProducerHandler类
 */
class ProducerHandler implements Runnable {
    /**
     * 只完成对应type队列的创建，具体路由逻辑交给exchange进行
     */
    // 处理来自Producer的连接和消息
    private final Message message;
    private final Exchange exchange;

    public ProducerHandler(Message message, Exchange exchange) {
        this.message = message;
        this.exchange = exchange;
    }

    public void run() {
        // 检查是否存在对应 dataType 的队列
        String dataType = message.getQueueKey();
        BlockingQueue<Message> queue = exchange.getBindings().get(dataType);

        if (queue == null) {
            // 如果队列不存在，创建并绑定新队列
            queue = new LinkedBlockingQueue<Message>();
            exchange.bind(queue, dataType);
        }

        // 将消息传递给 Exchange 进行处理
        exchange.routeMessage(message);
    }
}

/**
 * 负责监听所有queue情况，并完成转发
 */
class MessageDispatcher implements Runnable {
    private Map<String, BlockingQueue<Message>> queues;
    private Map<String, List<InetSocketAddress>> subscribers;

    public MessageDispatcher(Map<String, BlockingQueue<Message>> queues,
                             Map<String, List<InetSocketAddress>> subscribers){
        this.queues = queues;
        this.subscribers = subscribers;
    }
    // 负责检查所有队列并将消息发送给Consumer

    public void run() {
        while (true) {
            for (Map.Entry<String, BlockingQueue<Message>> entry : queues.entrySet()) {
                BlockingQueue<Message> queue = entry.getValue();
                String dataType = entry.getKey();
                Message message = queue.poll();

                if (message != null) {
                    distributeMessage(dataType, message);
                }
            }
        }
    }

    private void distributeMessage(String dataType, Message message) {
        List<InetSocketAddress> subscriberAddresses = subscribers.get(dataType);
        String mode = message.getSendMode();

        if (subscriberAddresses != null) {
            for (InetSocketAddress address : subscriberAddresses) {
                //检查“订阅”者
                //TODO：修改逻辑，现在是对第一个订阅的发消息
                sendMessageToSubscriber("From: " + message.getName()+ "\n" + message.getData(), address);
                if (mode.equals("direct"))
                    return;
            }
        }
    }

    // 发送消息到单个订阅者的方法
    private void sendMessageToSubscriber(String message, InetSocketAddress subscriberAddress) {
        // 实现通过Socket发送消息到订阅者的逻辑

        try (Socket socket = new Socket(subscriberAddress.getAddress(), subscriberAddress.getPort());
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {
            System.out.println("Broker.MessageDispatcher.sendMessageToSubscriber: send data：" + message);
            out.println(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
