package lab1;

import lab1.client.Consumer;
import lab1.client.Producer;
import lab1.controller.Broker;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

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

//以下部分分别对全广播式、选择广播式-点对点、选择广播式-发布订阅三种调度策略进行测试

    /**
     * 测试点对点
     */
    @Test
    void TestP2P() throws Exception {
        //消费者1
        Consumer cons1 = new Consumer("cons1");
        Thread thread1 = new Thread(cons1);
        thread1.start();

        //消费者2
        Consumer cons2 = new Consumer("cons2");
        Thread thread2 = new Thread(cons2);
        thread2.start();

        //生产者1
        Producer prod1 = new Producer("prod1");

        Thread.sleep(500);
        //中间件
        Broker broker = new Broker();
        broker.start();
        //订阅
        cons1.subscribe("queue1");
        cons2.subscribe("queue1");
//        broker.subscribe("queue1", cons1.getSocketAddress());
//        broker.subscribe("queue1", cons2.getSocketAddress());


        Thread.sleep(500);
        //生产者产生消息
        prod1.send("P2P", "queue1", "test p2p\n");

        thread1.interrupt();
        thread2.interrupt();
    }

    /**
     * 测试广播
     */
    @Test
    void TestFanout() throws Exception {
        //消费者1
        Consumer cons1 = new Consumer("cons1");
        Thread thread1 = new Thread(cons1);
        thread1.start();

        //消费者2
        Consumer cons2 = new Consumer("cons2");
        Thread thread2 = new Thread(cons2);
        thread2.start();

        //生产者1
        Producer prod1 = new Producer("prod1");

        Thread.sleep(500);
        //中间件
        Broker broker = new Broker();
//        broker.subscribe("", cons1.getSocketAddress());
//        broker.subscribe("", cons2.getSocketAddress());
        broker.start();
        //订阅
        cons1.subscribe("");
        cons2.subscribe("");

        Thread.sleep(500);
        //生产者产生消息
        //这里没有指定队列，依旧可以接收
        prod1.send("fanout", "", "test fanout\n");

        thread1.interrupt();
        thread2.interrupt();
    }

    /**
     * 测试发布/订阅模式
     */
    @Test
    void TestPubSub() throws Exception {
        //消费者1
        Consumer cons1 = new Consumer("cons1");
        Thread thread1 = new Thread(cons1);
        thread1.start();

        //消费者2
        Consumer cons2 = new Consumer("cons2");
        Thread thread2 = new Thread(cons2);
        thread2.start();

        //消费者3
        Consumer cons3 = new Consumer("cons3");
        Thread thread3 = new Thread(cons3);
        thread3.start();

        //生产者1
        Producer prod1 = new Producer("prod1");

        Thread.sleep(500);
        //中间件
        Broker broker = new Broker();
//        broker.subscribe("queue1", cons1.getSocketAddress());
//        broker.subscribe("queue1", cons2.getSocketAddress());
//        broker.subscribe("queue2", cons3.getSocketAddress());
        broker.start();
        //订阅
        cons1.subscribe("queue1");
        cons1.subscribe("queue1");
        cons1.subscribe("queue2");

        Thread.sleep(500);
        //生产者产生消息
        prod1.send("PubSub", "queue1", "test pubsub\n");

        thread1.interrupt();
        thread2.interrupt();
    }
}
