package com.txl.blockmoonlighttreasurebox.cache;

import android.os.SystemClock;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Copyright (c) 2021, 唐小陆 All rights reserved.
 * author：txl
 * date：2021/10/23
 * description：基于时间的LRU缓存，依据时间作为偏差，存储指定时间范围内的节点
 * @param <V> 缓存值的类型
 */
public class TimeLruCache<V> implements Serializable {
    /**
     * 时间偏移量，默认30秒，单位是毫秒
     * 当最后一个元素和第一个元素的时间偏差超过该值时，会移除链表表头的元素
     */
    private long offsetTime = 30 * 1000;
    private long lastPutTime = 0;
    private V lastValue;
    private static final long serialVersionUID = 1L;
    //按插入顺序保存 依次移除时间最早的
    private final LinkedHashMap<Long,V> linkedHashMap = new TimeLinkedHashMap(0, 0.75f, false);

    /**
     * 默认构造函数，使用默认的时间偏移量（30秒）
     */
    public TimeLruCache() {
    }



    /**
     * 构造函数，指定时间偏移量
     * 当最后一个和第一个时间偏差超过该值的时候，会将LinkedHashMap 中链表表头的元素移除
     * @param offsetTime 时间偏移量（毫秒）
     */
    public TimeLruCache(long offsetTime) {
        this.offsetTime = offsetTime;
    }

    /**
     * 存入值，使用当前系统时间作为基准时间
     * @param value 要存储的值
     */
    public void put(V value){
        put( SystemClock.elapsedRealtime(),value );
    }

    /**
     * 存入值，使用指定的基准时间
     * @param baseTime 基准时间
     * @param value 要存储的值
     */
    public void put(long baseTime, V value){
        linkedHashMap.put( baseTime,value );
        lastPutTime = baseTime;
        lastValue = value;
    }

    /**
     * 获取最后存入的值
     * 存储下最后一个元素，方便快速获取
     * @return 最后存入的值
     */
    public V getLastValue() {
        return lastValue;
    }

    /**
     * 获取所有缓存值
     * @return 所有缓存值的列表
     */
    public List<V> getAll(){
        List<V> list = new ArrayList<>();
        for (Map.Entry<Long, V> entry : linkedHashMap.entrySet()) {//
            list.add(entry.getValue());
        }
        return list;
    }

    /**
     * 基于时间的LinkedHashMap
     * 重写removeEldestEntry方法来实现基于时间的LRU淘汰策略
     */
    private class TimeLinkedHashMap extends LinkedHashMap<Long,V>{

        /**
         * 构造函数
         * @param initialCapacity 初始容量
         * @param loadFactor 负载因子
         * @param accessOrder 访问顺序
         */
        public TimeLinkedHashMap(int initialCapacity, float loadFactor, boolean accessOrder) {
            super( initialCapacity, loadFactor, accessOrder );
        }

        /**
         * 判断是否移除最老的条目
         * 在插入新条目后调用，如果返回true则移除最老的条目
         * @param eldest 最老的条目
         * @return 是否移除该条目
         */
        @Override
        protected boolean removeEldestEntry(Entry<Long, V> eldest) {
            //这样会不会导致存储的数据不够 offsetTime ？
            Iterator<Entry<Long,V>> iterator = linkedHashMap.entrySet().iterator();
            while (iterator.hasNext()){
                Entry<Long, V> entry = iterator.next();
                if(entry == eldest){
                    Entry<Long, V> temp = null;
                    if(iterator.hasNext()){
                        temp = iterator.next();
                    }
                    //在去除第一个的时候，剩下的数据也大于指定时间
                    return temp != null && (lastPutTime - temp.getKey() > offsetTime);
                }
            }
            return false;
        }
    }
}
