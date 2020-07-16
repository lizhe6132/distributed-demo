package com.lizhe.distributeddemo.lock;

import org.I0Itec.zkclient.ZkClient;

public abstract class ZkAbstractLock extends AbstractLock {
    private static final String CONNECTSTRING = "192.168.0.130:2181";
    // 创建zk连接
    protected ZkClient zkClient = new ZkClient(CONNECTSTRING);
    // 分布式锁path
    protected static final String PATH = "/lock";
    protected static final String PATH2 = "/lock2";
}
