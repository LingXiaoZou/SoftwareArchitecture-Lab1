package lab1.controller;

import ch.qos.logback.core.net.server.ServerRunner;
import lab1.Util.JsonUtil;
import lab1.config.config;
import lab1.model.Exchange;
import lab1.model.Message;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.*;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Broker {
    // 队列
    private Map<String, BlockingQueue<String>> queues;

    private ServerSocket serverSocket;
    // 线程池
    private ExecutorService executor;
    // 订阅者信息 String是订阅的消息type
    private Map<String, InetSocketAddress> subscriber;
    // Exchange交换器
    private Exchange exchange;


    public Broker(){
        queues = new ConcurrentHashMap<>();
        subscriber = new ConcurrentHashMap<>();
        executor = Executors.newCachedThreadPool();
        exchange = new Exchange("x", "0");
    }

    public void start() throws IOException {
        // 启动用于监听Producer的Socket
        listenForProducers();

        // 启动消息分发线程
        startMessageDispatcher();
    }

    private void listenForProducers() throws IOException {
        // 监听来自Producer的连接
        // 对每个连接创建一个新线程来处理消息入队
        serverSocket = new ServerSocket(8000);

        while(true){
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

                System.out.println("Received message from: " + message.getName());

                executor.submit(new ProducerHandler(message));
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    private void startMessageDispatcher() {
        // 启动一个或多个线程检查队列中的消息，并将其发送给Consumer
        executor.submit(new MessageDispatcher(queues));
    }
}

class ProducerHandler implements Runnable {
    // 处理来自Producer的连接和消息
    private final Message message;

    public ProducerHandler(Message message){
        this.message = message;
    }

    public void run() {
        // 接收消息，执行路由逻辑，然后将消息入队
    }
}

class MessageDispatcher implements Runnable {
    private Map<String, BlockingQueue<String>> queues;

    public MessageDispatcher(Map<String, BlockingQueue<String>> queues){
        this.queues = queues;
    }
    // 负责检查所有队列并将消息发送给Consumer

    public void run() {
        while (true) {
            // 检查队列中的消息并发送

        }
    }
}
