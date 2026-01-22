package com.txl.blockmoonlighttreasurebox.sample.manager;

/**
 * ANR采样监听器接口
 * 区别于普通的采样监听器，专门用于采集ANR相关信息的回调
 */
public interface IAnrSamplerListener extends IMainThreadSampleListener {
    /**
     * 收集消息队列中未处理的消息
     * @param baseTime 基准时间
     * @param msgId 消息ID
     * @param msg 消息内容
     */
    void onMessageQueueSample(long baseTime, String msgId, String msg);

    /**
     * CPU采样回调
     * @param baseTime 基准时间
     * @param msgId 消息ID
     * @param msg 消息内容
     */
    void onCpuSample(long baseTime, String msgId, String msg);

    /**
     * 内存采样回调
     * @param baseTime 基准时间
     * @param msgId 消息ID
     * @param msg 消息内容
     */
    void onMemorySample(long baseTime, String msgId, String msg);

    /**
     * 主线程堆栈采样回调
     * @param baseTime 基准时间
     * @param msgId 消息ID
     * @param msg 堆栈信息
     */
    void onMainThreadStackSample(long baseTime, String msgId, String msg);
}
