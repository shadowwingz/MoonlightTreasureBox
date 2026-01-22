package com.txl.blockmoonlighttreasurebox.sample.manager;

import com.txl.blockmoonlighttreasurebox.block.BlockBoxConfig;
import com.txl.blockmoonlighttreasurebox.info.MessageInfo;
import com.txl.blockmoonlighttreasurebox.sample.AbsSampler;

/**
 * Copyright (c) 2021, 唐小陆 All rights reserved.
 * author：txl
 * date：2021/10/23
 * description：采样管理器接口，负责管理和调度各种采样任务
 */
public interface ISamplerManager extends BlockBoxConfig.IConfigChangeListener, IMainThreadSampleListener{
    /**
     * 开始ANR采样
     * 注意：此方法不是线程安全的
     * @param msgId 当前处理的消息ID
     * @param baseTime 当前消息的基准时间
     */
    void startAnrSample(String msgId, long baseTime);

    /**
     * 启动采样管理器
     */
    void start();

    /**
     * 停止采样管理器
     */
    void stop();

    /**
     * 添加其他类型的采样器
     * @param sampler 采样器
     */
    void addSample(AbsSampler sampler);

}
