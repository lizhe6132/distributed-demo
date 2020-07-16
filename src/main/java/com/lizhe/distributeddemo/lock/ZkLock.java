package com.lizhe.distributeddemo.lock;

import org.I0Itec.zkclient.IZkDataListener;

import java.util.concurrent.CountDownLatch;

public class ZkLock extends ZkAbstractLock{
    private CountDownLatch countDownLatch;

    @Override
    public boolean tryLock() {
        try {
            zkClient.createEphemeral(PATH, "lock");
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }

    @Override
    public void waitLock() {
        IZkDataListener listener = new IZkDataListener() {
            @Override
            public void handleDataChange(String s, Object o) throws Exception {

            }

            @Override
            public void handleDataDeleted(String s) throws Exception {
                if (countDownLatch != null) {
                    countDownLatch.countDown();
                }
            }
        };
        zkClient.subscribeDataChanges(PATH, listener);
        if (zkClient.exists(PATH)) {
            countDownLatch = new CountDownLatch(1);
            try {
                countDownLatch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        zkClient.unsubscribeDataChanges(PATH, listener);
    }

    @Override
    public void unLock() {
        //释放锁
        if (zkClient != null) {
            zkClient.delete(PATH);
            zkClient.close();
            System.out.println("释放锁资源...");
        }
    }
}
