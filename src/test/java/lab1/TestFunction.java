package lab1;

import lab1.client.Consumer;
import lab1.client.Producer;
import lab1.controller.Broker;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class TestFunction {

    @Test
    void contextLoads() {
    }


//分别对全广播式、选择广播式-点对点、选择广播式-发布订阅三种调度策略进行测试

    /**
     * 测试点对点
     */
    @Test
    void testP2P() throws Exception {
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
        cons1.subscribe("red");
        cons2.subscribe("red");
//        broker.subscribe("queue1", cons1.getSocketAddress());
//        broker.subscribe("queue1", cons2.getSocketAddress());

        Thread.sleep(500);
        //生产者产生消息
        prod1.send("P2P", "red", "test p2p\n");

        thread1.interrupt();
        thread2.interrupt();
    }

    /**
     * 测试广播
     */
    @Test
    void testFanout() throws Exception {
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
        cons1.subscribe("red");
        cons2.subscribe("green");

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
    void testPubSub() throws Exception {
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
        cons1.subscribe("red");
        cons2.subscribe("red");
        cons3.subscribe("green");

        Thread.sleep(500);
        //生产者产生消息
        prod1.send("PubSub", "red", "test pubsub\n");

        thread1.interrupt();
        thread2.interrupt();
        thread3.interrupt();
    }
}
