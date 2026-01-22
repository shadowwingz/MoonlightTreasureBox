package com.txl.blockmoonlighttreasurebox.sample;

import com.txl.blockmoonlighttreasurebox.sample.manager.ISamplerManager;

/**
 * Copyright (c) 2021, 唐小陆 All rights reserved.
 * author：txl
 * date：2021/10/23
 * description：采样器工厂类，用于创建采样管理器实例
 */
public class SamplerFactory {
    /**
     * 私有构造函数，防止实例化
     */
    private SamplerFactory() {
        throw new RuntimeException("SamplerFactory can not call this construction method");
    }

    /**
     * 创建采样管理器实例
     * @return 采样管理器实例
     */
    public static ISamplerManager createSampleManager(){
        return SampleManagerImpl.getInstance();
    }
}
