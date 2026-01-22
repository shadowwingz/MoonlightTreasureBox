package com.txl.blockmoonlighttreasurebox.handle;

import android.util.Log;

import com.txl.blockmoonlighttreasurebox.info.MessageInfo;
import com.txl.blockmoonlighttreasurebox.sample.manager.IAnrSamplerListener;

/**
 * Copyright (c) 2021, 唐小陆 All rights reserved.
 * author：txl
 * date：2021/10/23
 * description：日志采样处理类，实现了IAnrSamplerListener接口，用于将采样数据输出到Logcat
 */
public final class LogSample implements IAnrSamplerListener {
    private final String TAG = LogSample.class.getSimpleName();

    /**
     * 消息队列采样回调，将信息输出到日志
     * @param baseTime 基准时间
     * @param msgId 消息ID
     * @param msg 消息内容
     */
    @Override
    public void onMessageQueueSample(long baseTime, String msgId, String msg) {
        StringBuilder builder = new StringBuilder();
        builder.append( "onMessageQueueSample" )
                .append( "  baseTime : " )
                .append( baseTime )
                .append( " msgId : " )
                .append( msgId )
                .append( "  msg : " )
                .append( msg );
        Log.d( TAG,new String(builder) );
    }

    /**
     * CPU采样回调，将信息输出到日志
     * @param baseTime 基准时间
     * @param msgId 消息ID
     * @param msg 消息内容
     */
    @Override
    public void onCpuSample(long baseTime, String msgId, String msg) {
        StringBuilder builder = new StringBuilder();
        builder.append( "onCpuSample" )
                .append( "  baseTime : " )
                .append( baseTime )
                .append( " msgId : " )
                .append( msgId )
                .append( "  msg : " )
                .append( msg );
        Log.d( TAG,new String(builder) );
    }

    /**
     * 内存采样回调，将信息输出到日志
     * @param baseTime 基准时间
     * @param msgId 消息ID
     * @param msg 消息内容
     */
    @Override
    public void onMemorySample(long baseTime, String msgId, String msg) {
        StringBuilder builder = new StringBuilder();
        builder.append( "onMemorySample" )
                .append( "  baseTime : " )
                .append( baseTime )
                .append( " msgId : " )
                .append( msgId )
                .append( "  msg : " )
                .append( msg );
        Log.d( TAG,new String(builder) );
    }

    /**
     * 主线程堆栈采样回调，将信息输出到日志
     * @param baseTime 基准时间
     * @param msgId 消息ID
     * @param msg 堆栈信息
     */
    @Override
    public void onMainThreadStackSample(long baseTime, String msgId, String msg) {
        StringBuilder builder = new StringBuilder();
        builder.append( "onMainThreadStackSample" )
                .append( "  baseTime : " )
                .append( baseTime )
                .append( " msgId : " )
                .append( msgId )
                .append( "  msg : " )
                .append( msg );
        Log.d( TAG,new String(builder) );
    }

    /**
     * 采样ANR消息完成的回调
     */
    @Override
    public void onSampleAnrMsg() {

    }

    /**
     * 调度采样回调，将信息输出到日志
     * @param start 是否开始
     * @param baseTime 基准时间
     * @param msgId 消息ID
     * @param dealt 耗时
     */
    @Override
    public void onScheduledSample(boolean start,long baseTime, String msgId, long dealt) {
        StringBuilder builder = new StringBuilder();
        builder.append( "onScheduledSample" )
                .append( "  baseTime : " )
                .append( baseTime )
                .append( " msgId : " )
                .append( msgId )
                .append( "  dealt : " )
                .append( dealt );
        Log.d( TAG,new String(builder) );
    }

    /**
     * 消息采样回调，将信息输出到日志
     * @param baseTime 基准时间
     * @param msgId 消息ID
     * @param msg 消息信息
     */
    @Override
    public void onMsgSample(long baseTime, String msgId, MessageInfo msg) {
        StringBuilder builder = new StringBuilder();
        builder.append( "onMsgSample" )
                .append( "  baseTime : " )
                .append( baseTime )
                .append( " msgId : " )
                .append( msgId )
                .append( "  msg : " )
                .append( msg );
        Log.d( TAG,new String(builder) );
    }

    /**
     * 掉帧采样回调，将信息输出到日志
     * @param msgId 消息ID
     * @param msg 消息信息
     */
    @Override
    public void onJankSample(String msgId, MessageInfo msg) {
        StringBuilder builder = new StringBuilder();
        builder.append( "onJankSample" )
                .append( " msgId : " )
                .append( msgId )
                .append( "  msg : " )
                .append( msg );
        Log.d( TAG,new String(builder) );
    }

    /**
     * 消息队列分发ANR完成的回调
     */
    @Override
    public void messageQueueDispatchAnrFinish() {
    }
}
