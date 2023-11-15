package lab1.controller;

import ch.qos.logback.core.net.server.ServerRunner;
import lab1.config.config;
import lab1.model.Message;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Broker {
    private Map<String, BlockingQueue<Message>> queues;
    private ServerSocket serverSocket;
    private ExecutorService executor;

    public Broker(){
        queues = new ConcurrentHashMap<>();
        executor = Executors.newCachedThreadPool();
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
    }

    private void startMessageDispatcher() {
        // 启动一个或多个线程检查队列中的消息，并将其发送给Consumer
        executor.submit(new MessageDispatcher(queues));
    }
}

class ProducerHandler implements Runnable {
    // 处理来自Producer的连接和消息

    public void run() {
        // 接收消息，执行路由逻辑，然后将消息入队
    }
}

class MessageDispatcher implements Runnable {
    private Map<String, BlockingQueue<Message>> queues;

    public MessageDispatcher(Map<String, BlockingQueue<Message>> queues){
        this.queues = queues;
    }
    // 负责检查所有队列并将消息发送给Consumer

    public void run() {
        while (true) {
            // 检查队列中的消息并发送
        }
    }
}
