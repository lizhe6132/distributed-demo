package com.lizhe.distributeddemo.service.impl;

import com.lizhe.distributeddemo.mapper.TicketMapper;
import com.lizhe.distributeddemo.service.TicketService;
import com.lizhe.distributeddemo.vo.Ticket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Service
public class TicketServiceImpl implements TicketService {
    @Autowired
    private TicketMapper ticketMapper;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    private Lock lock = new ReentrantLock();
    //将锁细粒度化，标志每一趟车是否在重建缓存, redis senx 也可以实现
    private ConcurrentHashMap<String, String> mapLock = new ConcurrentHashMap<String, String>();
    //缓存击穿解决方案布隆过滤器,利用redis布隆过滤器
    @PostConstruct
    public void init() {
        //初始化ticketSeq
        System.out.println("初始化完毕");
        List<String>  ticketSeqList = ticketMapper.listTicketSeq();
        // 在redis中维护一个二进制数组
        double size = Math.pow(2, 32);
        // 将id对应的二进制数组位置设值为1（ticketSeq的hash值 % 数组长度）
        if (ticketSeqList != null && ticketSeqList.size() > 0) {
            for (String s: ticketSeqList) {
                long index = (long) Math.abs(s.hashCode() % size);
                redisTemplate.opsForValue().setBit("redis_bloom_filter", index, true);
            }
        }
    }
    /**
     * 缓存雪崩解决方案
     * 1,均匀分布失效时间
     * 2,加锁，只允许一个线程从数据库查询，并重建缓存(缺点1，线程阻塞，体验不好 2，锁太粗)
     * 3,组合方案：备份缓存 + 细粒度锁 + 缓存降级
     * @param ticketSeq
     * @return
     */
    @Override
    public Ticket getTicket(String ticketSeq) {
        // 先用布隆过滤器过滤
        double size = Math.pow(2, 32);
        long index = (long) Math.abs(ticketSeq.hashCode() % size);
        boolean exsits = redisTemplate.opsForValue().getBit("redis_bloom_filter", index);
        if (!exsits) {
            System.out.println("不存在的车次");
            return null;
        }
        // 以下是缓存雪崩的解决
        Ticket ticket = null;
        Object object = redisTemplate.opsForValue().get(ticketSeq);
        if (object != null) {
            ticket = (Ticket)object;
            System.out.println("从缓存查询得到" + ticket.getTicketStock());
            return ticket;
        }
        /*// 方式2：缓存失效加锁，2000个线程只有1个去数据库查询，其它线程等待
        lock.lock();
        try {
            // 再次尝试从缓存获取
            Object object2 = redisTemplate.opsForValue().get(ticketSeq);
            if (object2 != null) {
                ticket = (Ticket)object2;
                System.out.println("从缓存查询得到" + ticket.getTicketStock());
                return ticket;
            }
            ticket = ticketMapper.getTicketBySeq(ticketSeq);
            System.out.println("从数据库查询得到" + ticket.getTicketStock());
            redisTemplate.opsForValue().set(ticketSeq, ticket, 120L, TimeUnit.SECONDS);
        } finally {
            lock.unlock();
        }*/
        //方式3
        boolean flag = false;
        try {
            // setnx 存在则返回已有的值，不存在则put，将锁细粒度
            flag = mapLock.putIfAbsent(ticketSeq, ticketSeq) == null;
            // 如果拿到锁，从数据库查询，并重构缓存
            if (flag) {
                Object object2 = redisTemplate.opsForValue().get(ticketSeq);
                if (object2 != null) {
                    ticket = (Ticket)object2;
                    System.out.println("从缓存查询得到" + ticket.getTicketStock());
                    return ticket;
                }
                ticket = ticketMapper.getTicketBySeq(ticketSeq);
                System.out.println("从数据库查询得到" + ticket.getTicketStock());
                redisTemplate.opsForValue().set(ticketSeq, ticket, 120L, TimeUnit.SECONDS);
                // 备份缓存，不失效,只是数据可能不一致
                // redisTemplate.opsForValue().set(ticketSeq, ticket);
            } else {
                // 从备份缓存获取
                // redisTemplate.opsForValue().get(ticketSeq);
                // 降级,返回固定数据
                System.out.println("缓存降级,返回固定值: 0");
                return new Ticket();

            }
        } finally {
            // 释放锁
            if (flag) {
                mapLock.remove(ticketSeq);
            }
        }
        return ticket;
    }
}
