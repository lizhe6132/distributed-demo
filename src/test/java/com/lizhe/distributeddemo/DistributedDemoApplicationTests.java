package com.lizhe.distributeddemo;

import com.lizhe.distributeddemo.lock.OrderService;
import com.lizhe.distributeddemo.lock.TestZkLock;
import com.lizhe.distributeddemo.service.TicketService;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;
import java.util.concurrent.CountDownLatch;

@SpringBootTest
class DistributedDemoApplicationTests {
    @Autowired
    private TicketService ticketService;
    @Autowired
    private TestZkLock testZkLock;
    private final Integer curr = 400;
    private CountDownLatch countDownLatch = new CountDownLatch(curr);
    private final String ticketSql = "K1001";
    long timeend = 0L;

    @Test
    void testQuery() throws InterruptedException {
        System.out.println("测试开始");
        timeend = System.currentTimeMillis();
        Thread[] threads = new Thread[curr];
        for (int i = 0; i < threads.length; i++) {
            Thread thread = new Thread(new QueryTask());
            threads[i] = thread;
            thread.start();
            countDownLatch.countDown();
        }
        // 等待上面的执行完再结束主线程，便于统计执行时间
        for (Thread t: threads) {
            t.join();
        }
        System.out.println("测试结束,耗时" + (System.currentTimeMillis() - timeend));
    }
    @Test
    void testZkLoc() throws InterruptedException {
        System.out.println("测试开始");
        timeend = System.currentTimeMillis();
        Thread[] threads = new Thread[curr];
        for (int i = 0; i < threads.length; i++) {
            Thread thread = new Thread(new OrderService());
            threads[i] = thread;
            thread.start();
            countDownLatch.countDown();
        }
        // 等待上面的执行完再结束主线程，便于统计执行时间
        for (Thread t: threads) {
            t.join();
        }
        System.out.println("测试结束,耗时" + (System.currentTimeMillis() - timeend));
    }
    class QueryTask implements Runnable {

        @Override
        public void run() {
            try {
                countDownLatch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            ticketService.getTicket(UUID.randomUUID().toString());
        }
    }


}
