package com.lizhe.distributeddemo.lock;

/**
 * 分布式锁接口
 */
public interface Lock {
    //获取锁
    void getLock();
    //释放锁
    void unLock();
}
