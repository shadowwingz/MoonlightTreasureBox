package com.txl.blockmoonlighttreasurebox.info;

import com.txl.blockmoonlighttreasurebox.cache.TimeLruCache;

import java.io.Serializable;

/**
 * ANR信息类
 * 存储所有ANR发生时产生的信息，包括消息采样、调度采样、消息队列采样和主线程堆栈等
 */
public class AnrInfo implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final long OFFSET_TIME = 60 * 1000; //1分钟
    public TimeLruCache<MessageInfo> messageSamplerCache = new TimeLruCache<>(30L *1000*1000_000);
    public TimeLruCache<ScheduledInfo> scheduledSamplerCache = new TimeLruCache<>();
    public StringBuilder messageQueueSample = new StringBuilder();
    public String mainThreadStack;
    public String markTime;
}
