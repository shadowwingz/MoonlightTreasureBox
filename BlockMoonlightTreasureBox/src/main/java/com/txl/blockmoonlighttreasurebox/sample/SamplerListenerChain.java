package com.txl.blockmoonlighttreasurebox.sample;

import android.util.Log;

import com.txl.blockmoonlighttreasurebox.info.MessageInfo;
import com.txl.blockmoonlighttreasurebox.sample.manager.IAnrSamplerListener;
import com.txl.blockmoonlighttreasurebox.sample.manager.IMainThreadSampleListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Copyright (c) 2021, 唐小陆 All rights reserved.
 * author：txl
 * date：2021/10/23
 * description：采样监听器链，负责将采样事件分发给所有注册的监听器
 */
public class SamplerListenerChain implements IAnrSamplerListener {
    private final List<IAnrSamplerListener> anrSamplerListeners = new ArrayList<>();
    private final String TAG = SamplerListenerChain.class.getSimpleName();

    /**
     * 添加采样监听器
     * @param listener 采样监听器
     */
    public void addSampleListener(IAnrSamplerListener listener){
        anrSamplerListeners.add( listener );
    }

    /**
     * 批量添加采样监听器
     * @param listeners 采样监听器列表
     */
    public void addSampleListener(List<IAnrSamplerListener> listeners){
        anrSamplerListeners.addAll( listeners );
    }

    /**
     * 清除所有监听器
     */
    public void clearListener(){
        anrSamplerListeners.clear();
    }

    /**
     * 消息队列采样回调，将事件分发给所有监听器
     */
    @Override
    public void onMessageQueueSample(long baseTime, String msgId, String msg) {
        for (IAnrSamplerListener listener: anrSamplerListeners){
            listener.onMessageQueueSample( baseTime, msgId, msg );
        }
    }

    @Override
    public void onCpuSample(long baseTime, String msgId, String msg) {
        for (IAnrSamplerListener listener: anrSamplerListeners){
            listener.onCpuSample( baseTime, msgId, msg );
        }
    }

    @Override
    public void onMemorySample(long baseTime, String msgId, String msg) {
        for (IAnrSamplerListener listener: anrSamplerListeners){
            listener.onMemorySample( baseTime, msgId, msg );
        }
    }


    @Override
    public void onMainThreadStackSample(long baseTime, String msgId, String msg) {
        for (IAnrSamplerListener listener: anrSamplerListeners){
            listener.onMainThreadStackSample( baseTime, msgId, msg );
        }
    }

    @Override
    public void onSampleAnrMsg() {
        for (IAnrSamplerListener listener: anrSamplerListeners){
            listener.onSampleAnrMsg( );
        }
    }

    @Override
    public void onScheduledSample(boolean start,long baseTime, String msgId, long dealt) {
        for (IMainThreadSampleListener listener: anrSamplerListeners){
            listener.onScheduledSample(start, baseTime, msgId, dealt );
        }
    }

    @Override
    public void onMsgSample(long baseTime, String msgId, MessageInfo msg) {
        for (IMainThreadSampleListener listener: anrSamplerListeners){
            listener.onMsgSample( baseTime, msgId, msg );
        }
        if(msg.msgType == MessageInfo.MSG_TYPE_ANR){
            onSampleAnrMsg();//通知采集到anr
        }
    }

    @Override
    public void onJankSample(String msgId, MessageInfo msg) {
        for (IMainThreadSampleListener listener: anrSamplerListeners){
            listener.onJankSample( msgId, msg );
        }
    }

    @Override
    public void messageQueueDispatchAnrFinish() {
        Log.d(TAG,"messageQueueDispatchAnrFinish");
        for (IMainThreadSampleListener listener: anrSamplerListeners){
            listener.messageQueueDispatchAnrFinish(  );
        }
    }
}
