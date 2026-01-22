package com.txl.blockmoonlighttreasurebox.block;

import android.content.Context;

/**
 * 卡顿监控门面类
 * 采用门面模式，为用户提供统一的监控接口
 */
public class BlockMonitorFace implements IBlock{
    private final Context mApplicationContext;
    private final IBlock blockMonitor;
    private static IBlock blockMonitorFace;


    /**
     * 私有构造函数
     * @param mApplicationContext 应用上下文
     */
    private BlockMonitorFace(Context mApplicationContext) {
        blockMonitorFace = this;
        this.mApplicationContext = mApplicationContext.getApplicationContext();
        BlockMonitor.getInstance().init(this.mApplicationContext);
        blockMonitor = BlockMonitor.getInstance();
    }

    /**
     * 获取应用上下文
     * @return 应用上下文
     */
    @Override
    public Context getApplicationContext() {
        return mApplicationContext;
    }

    /**
     * 启动监控
     * @return 当前IBlock实例
     */
    @Override
    public IBlock startMonitor() {
        return blockMonitor.startMonitor();
    }

    /**
     * 停止监控
     * @return 当前IBlock实例
     */
    @Override
    public IBlock stopMonitor() {
        return blockMonitor.stopMonitor();
    }

    /**
     * 更新配置
     * @param config 新的配置对象
     * @return 当前IBlock实例
     */
    @Override
    public IBlock updateConfig(BlockBoxConfig config) {
        return blockMonitor.updateConfig(config);
    }

    /**
     * 获取BlockMonitorFace单例实例
     * @return BlockMonitorFace单例实例
     */
    public static IBlock getBlockMonitorFace() {
        return blockMonitorFace;
    }

    /**
     * 初始化BlockMonitorFace
     * @param context 应用上下文
     * @return BlockMonitorFace实例
     */
    public static IBlock init(Context context){
        if(context == null){
            throw new RuntimeException("please call getBlockMonitorFace with not null context to init ");
        }
        if(blockMonitorFace == null){//这里并不在意多线程产生了多个BlockMonitorFace 对象
            synchronized (BlockMonitorFace.class){
                if(blockMonitorFace==null){
                    blockMonitorFace = new BlockMonitorFace(context);
                }
            }
        }
        return blockMonitorFace;
    }

    /**
     * 系统ANR发生时的回调
     */
    @Override
    public void onSystemAnr() {
        blockMonitor.onSystemAnr();
    }
}
