package lab1;

import lab1.client.Consumer;
import lab1.client.Producer;
import lab1.controller.Broker;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@SpringBootTest
public class TestThroughput {
    /**
     * 测试吞吐率
     */
    @Test
    void testThroughput() throws Exception {
        Consumer cons1 = new Consumer("cons1");
        Thread thread1 = new Thread(cons1);
        thread1.start();

        //生产者1
        Producer prod1 = new Producer("prod1");

        Thread.sleep(500);
        //中间件
        Broker broker = new Broker();
        broker.start();
        //订阅
        cons1.subscribe("red");

        Thread.sleep(500);

        //启动计时
        long startTime = System.nanoTime();

        // 执行需要计时的代码
        //生产者产生消息
        for (int i = 0; i <1000;i++){
            prod1.send("Fanout", "red", "a");
        }

        while(cons1.getMessageNum() != 1000);

        long endTime = System.nanoTime();
        long elapsedTimeNano = endTime - startTime;

        // 转换为毫秒
        long elapsedTimeMillis = TimeUnit.NANOSECONDS.toMillis(elapsedTimeNano);
        System.out.println("Elapsed time: " + elapsedTimeMillis + " milliseconds");
        float throughput = 1000*8*1000/elapsedTimeMillis;
        System.out.println("吞吐率为：1000B / "+elapsedTimeMillis +"ms = "+ throughput + "bps");


        thread1.interrupt();

    }

}
