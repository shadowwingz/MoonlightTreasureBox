package com.txl.blockmoonlighttreasurebox.sample;

import com.txl.blockmoonlighttreasurebox.info.BoxMessage;
import com.txl.blockmoonlighttreasurebox.utils.AppExecutors;

/**
 * Copyright (c) 2021, 唐小陆 All rights reserved.
 * author：txl
 * date：2021/10/23
 * description：堆栈采样器，用于采集指定线程的堆栈信息
 */
public class StackSampler extends AbsSampler {

    //需要采集那个线程的栈
    Thread mSampleThread;

    /**
     * 构造函数
     * @param mSampleThread 需要采集堆栈的线程
     */
    public StackSampler(Thread mSampleThread) {
        this.mSampleThread = mSampleThread;
    }

    /**
     * 执行堆栈采样
     * @param msgId 消息ID
     * @param needListener 是否需要通知监听器
     */
    @Override
    protected void doSample(String msgId, boolean needListener) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append( "msgId: " )
                .append( msgId )
                .append( BoxMessage.SEPARATOR );
        for (StackTraceElement stackTraceElement : mSampleThread.getStackTrace()) {
            stringBuilder
                    .append(stackTraceElement.toString())
                    .append( BoxMessage.SEPARATOR);
        }
        if(needListener && mSampleListener != null){
            mSampleListener.onSampleEnd(msgId, new String(stringBuilder) );
        }
    }
}
