package com.txl.blockmoonlighttreasurebox.sample;

import android.util.Log;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Copyright (c) 2021, 唐小陆 All rights reserved.
 * author：txl
 * date：2021/10/15
 * description：抽象采样器类，定义了采样器的基本结构和行为
 */
public abstract class AbsSampler {
    protected final String TAG = getClass().getSimpleName();
    protected SampleListener mSampleListener;
    /**
     * 是否可以进行采样
     */
    protected AtomicBoolean mShouldSample = new AtomicBoolean(true);

    /**
     * 进行采样，子类需要实现具体的采样逻辑
     * @param msgId 消息ID
     * @param needListener 是否需要通知监听器
     */
    protected abstract void doSample(String msgId, boolean needListener);

    /**
     * 设置采样监听器
     * @param mSampleListener 采样监听器
     */
    public void setSampleListener(SampleListener mSampleListener) {
        this.mSampleListener = mSampleListener;
    }

    /**
     * 开始采样
     * @param msgId 消息ID
     * @param needListener 是否需要通知监听器
     */
    public void startSample(String msgId, boolean needListener){
        if(!mShouldSample.get()){
            Log.d( TAG,"Abandon this sampling, it is already sampling" );
            return;
        }
        mShouldSample.set( false );
        doSample(msgId,needListener);
        mShouldSample.set( true );
    }

    /**
     * 采样监听器接口
     * 定义了采样结束时的回调方法
     */
    public interface SampleListener{
        /**
         * 采样结束时的回调
         * @param msgId 消息ID
         * @param msg 采样结果
         */
        void onSampleEnd(String msgId,String msg);
    }
}
