package com.txl.blockmoonlighttreasurebox.block;

import android.content.Context;

/**
 * 卡顿监控接口
 * 定义了卡顿监控的基本操作，包括启动、停止、更新配置等
 */
public interface IBlock extends ISystemAnrObserver{
    /**
     * 获取应用上下文
     * @return 应用上下文
     */
    Context getApplicationContext();

    /**
     * 启动监控
     * @return 当前IBlock实例
     */
    IBlock startMonitor();

    /**
     * 停止监控
     * @return 当前IBlock实例
     */
    IBlock stopMonitor();

    /**
     * 更新配置
     * @param config 新的配置对象
     * @return 当前IBlock实例
     */
    IBlock updateConfig(BlockBoxConfig config);
}
