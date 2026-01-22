package com.txl.blockmoonlighttreasurebox.sample.manager;

import com.txl.blockmoonlighttreasurebox.info.MessageInfo;

/**
 * 主线程采样监听器接口
 * 这些方法调用都在主线程，注意不要做耗时操作
 */
public interface IMainThreadSampleListener {
    /**
     * 当前主线程的调度能力采样
     * @param start true表示发起本次调度，false表示结束
     * @param baseTime 基准时间
     * @param msgId 消息ID
     * @param dealt 耗时
     */
    void onScheduledSample(boolean start, long baseTime, String msgId, long dealt);

    /**
     * 采集消息队列每次处理的消息
     * 注意：当消息类型是ANR的时候，调用者不是主线程
     * @param baseTime 基准时间
     * @param msgId 消息ID
     * @param msg 消息信息
     */
    void onMsgSample(long baseTime, String msgId, MessageInfo msg);

    /**
     * 掉帧采样回调
     * @param msgId 消息ID
     * @param msg 消息信息
     */
    void onJankSample(String msgId, MessageInfo msg);

    /**
     * 消息队列中发生ANR的消息已经处理完毕
     */
    void messageQueueDispatchAnrFinish();

    /**
     * 采样ANR消息完成的回调
     */
    void onSampleAnrMsg();
}
