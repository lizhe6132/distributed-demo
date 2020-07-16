package com.lizhe.distributeddemo.controller;

import com.lizhe.distributeddemo.lock.RedisLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CountDownLatch;

/**
 * 测试分布式锁
 */
@RestController
public class LockController {
    private static long count = 20;//黄牛
    private CountDownLatch countDownLatch = new CountDownLatch(5);
    @Autowired()
    private RedisLock lock;
    @RequestMapping(value = "/sale")
    public Long sale() throws InterruptedException {
        count = 20;
        countDownLatch = new CountDownLatch(5);

        System.out.println("-------共20张票，分五个窗口开售-------");
        new PlusThread().start();
        new PlusThread().start();
        new PlusThread().start();
        new PlusThread().start();
        new PlusThread().start();
        return count;
    }

    // 线程类模拟一个窗口买火车票
    public class PlusThread extends Thread {
        private int amount = 0;//抢多少张票

        @Override
        public void run() {
            System.out.println(Thread.currentThread().getName() + "开始售票");
            countDownLatch.countDown();
            if (countDownLatch.getCount()==0){
                System.out.println("----------售票结果------------------------------");
            }
            try {
                countDownLatch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            while (count > 0) {
                lock.getLock();
                try {
                    if (count > 0) {
                        //模拟卖票业务处理
                        amount++;
                        count--;
                    }
                }finally{
                    lock.unLock();
                }

                try {
                    Thread.sleep(10);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            System.out.println(Thread.currentThread().getName() + "售出"+ (amount) + "张票");
        }
    }
}
