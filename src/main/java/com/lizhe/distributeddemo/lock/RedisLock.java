package com.lizhe.distributeddemo.lock;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scripting.support.ResourceScriptSource;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class RedisLock extends AbstractLock{
    private static final String LOCK = "lock_key";
    // 用于存储每个线程上锁的值，便于各解各的锁
    private ThreadLocal<String> threadLocal = new ThreadLocal<String>();
    @Autowired
    private RedisTemplate redisTemplate;
    private DefaultRedisScript<Long> getRedisScript;

    @PostConstruct
    public void init(){
        getRedisScript = new DefaultRedisScript<Long>();
        getRedisScript.setResultType(Long.class);
        getRedisScript.setScriptSource(new ResourceScriptSource(new ClassPathResource("unlock.lua")));
        System.out.println("加载lua脚本成功");
    }

    @Override
    public boolean tryLock() {
        String lockVal = UUID.randomUUID().toString();
        boolean flag = redisTemplate.opsForValue().setIfAbsent(LOCK, lockVal, 10, TimeUnit.MILLISECONDS);
        if (flag) {
            // 上锁成功
            threadLocal.set(lockVal);
            return true;
        }
        return false;
    }

    @Override
    public void waitLock() {
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {

        }
    }

    /**
     * 调用lua脚本解锁
     */
    @Override
    public void unLock() {
        long n = (long) redisTemplate.execute(getRedisScript, Arrays.asList(LOCK), Arrays.asList(threadLocal.get()));
        if (n > 0) {
            System.out.println(Thread.currentThread().getName() + "释放锁资源" + n);
        }
    }
}
