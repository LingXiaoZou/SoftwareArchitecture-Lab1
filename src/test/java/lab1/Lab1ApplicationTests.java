package lab1;

import lab1.client.Consumer;
import lab1.client.Producer;
import lab1.controller.Broker;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SpringBootTest
class Lab1ApplicationTests {

    @Test
    void contextLoads() {
    }

    @Test
    void test() throws Exception {
        Consumer cons1 = new Consumer("tb");
        Thread thread1 = new Thread(cons1);
        thread1.start(); // 这会在新线程中运行cons1的run方法

        Consumer cons2 = new Consumer("ttb");
        Thread thread2 = new Thread(cons2);
        thread2.start(); // 这会在新线程中运行cons2的run方法


        Producer prod1 = new Producer("smy");

        Broker mq = new Broker();
        // 确保Consumer已经开始监听端口
        Thread.sleep(1000); // 等待一段时间，以便消费者线程可以启动并初始化它们的ServerSocket
        mq.subscribe("a", cons1.getSocketAddress());
        mq.subscribe("b", cons2.getSocketAddress());
        mq.subscribe("b", cons1.getSocketAddress());
        mq.start(); // Broker也应该在它自己的线程中启动

        // 发送消息可能也需要一些时间，因为需要等待Broker启动并准备好接收消息
        Thread.sleep(1000); // 等待Broker准备好
//        prod1.send("fanout", "", "this is a fanout test msg.\n");
        prod1.send("direct", "b", "This is a sub test msg.\n");

        // 在测试结束前，等待一段时间以便消息可以被处理
        Thread.sleep(200000); // 等待消息被发送和接收

        thread1.interrupt(); // 通知线程停止
        thread2.interrupt(); // 通知线程停止
    }

}
