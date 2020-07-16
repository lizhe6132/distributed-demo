package com.lizhe.distributeddemo.lock;

import org.I0Itec.zkclient.IZkDataListener;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class ZkLock2 extends ZkAbstractLock{
    private CountDownLatch countDownLatch= null;
    //有序锁，监听自己前面的节点
    private String beforePath;//当前请求的节点前一个节点
    private String currentPath;//当前请求的节点
    //创建临时顺序节点，父节点得保证存在
    public ZkLock2() {
        if (!this.zkClient.exists(PATH2)) {
            this.zkClient.createPersistent(PATH2);
        }
    }
    @Override
    public boolean tryLock() {
        //如果currentPath为空则为第一次尝试加锁，第一次加锁赋值currentPath
        if (currentPath == null || currentPath.length() <= 0) {
            //创建一个临时顺序节点
            currentPath = this.zkClient.createEphemeralSequential(PATH2 + '/', "lock");
        }
        /*
        判断当前节点是不是有序节点中最小的，是则获取到锁，否则监听前一个节点
         */
        // 先把PATH下的所有节点排序
        List<String> childrens = this.zkClient.getChildren(PATH2);
        Collections.sort(childrens);
        if (currentPath.equals(PATH2 + '/' + childrens.get(0))) {
            return true;
        }
        //如果当前节点在所有节点中排名中不是排名第一，则获取前面的节点名称，并赋值给beforePath
        int index = Collections.binarySearch(childrens, currentPath.substring(7));
        beforePath = PATH2+ '/' + childrens.get(index - 1);
        return false;
    }

    @Override
    public void waitLock() {
        IZkDataListener listener = new IZkDataListener() {
            @Override
            public void handleDataChange(String s, Object o) throws Exception {

            }

            @Override
            public void handleDataDeleted(String s) throws Exception {
                // 删除时，通知监听它的节点的去获取锁
                if (countDownLatch != null) {
                    countDownLatch.countDown();
                }
            }
        };
        //给排在前面的的节点增加数据删除的watcher,本质是启动另外一个线程去监听前置节点
        this.zkClient.subscribeDataChanges(beforePath, listener);
        // 如果节点还存在则证明其还没得到锁，创建countDownLatch让程序等待
        if (this.zkClient.exists(beforePath)) {
            countDownLatch = new CountDownLatch(1);
            try {
                countDownLatch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        //删除监听
        this.zkClient.unsubscribeDataChanges(beforePath, listener);
    }

    @Override
    public void unLock() {
        if (currentPath != null) {
            zkClient.delete(currentPath);
            zkClient.close();
        }

    }
}
