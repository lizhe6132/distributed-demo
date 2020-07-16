package com.lizhe.distributeddemo.lock;


/**
 * Created by VULCAN on 2018/9/20.
 */
public class OrderService implements Runnable {
   private static int count = 0;

    //private Lock lock = new ZkLock();

    private Lock lock = new ZkLock2();
    //private Lock lock = new ZookeeperDistrbuteLock();
    //private Lock lock = new ZookeeperDistrbuteLock2();


    public void run() {
        getNumber();
    }
    public void getNumber() {
        try {
            lock.getLock();

            System.out.println(Thread.currentThread().getName() + ",生成订单ID:" + count++);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.unLock();
        }
    }
    public static void main(String[] args) {
        System.out.println("####生成唯一订单号###");
//		OrderService orderService = new OrderService();
        for (int i = 0; i < 1000; i++) {
            new Thread( new OrderService()).start();
        }
    }
}