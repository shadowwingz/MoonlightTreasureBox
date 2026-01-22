package com.txl.blockmoonlighttreasurebox.block;

import com.txl.blockmoonlighttreasurebox.handle.FileSample;
import com.txl.blockmoonlighttreasurebox.sample.manager.IAnrSamplerListener;
import com.txl.blockmoonlighttreasurebox.sample.manager.IMainThreadSampleListener;

import java.util.ArrayList;
import java.util.List;

/**
 * 卡顿监控配置类
 * 用于配置卡顿监控的各项阈值参数，包括警告时间、ANR时间、掉帧数等
 */
public class BlockBoxConfig {
    /**
     * 超过这个时间输出警告 超过这个时间消息单独罗列出来
     */
    private long warnTime = 300;
    //这个值暂定50ms
    private long gapTime = 50;
    /**
     * 超过这个时间可直接判定为anr
     */
    private long anrTime = 3000;
    /**
     * 三大流程掉帧数 超过这个值判定为jank
     */
    private int jankFrame = 30;

    private boolean useAnalyze = true;

    /**
     * 判断是否使用分析功能
     * @return 是否使用分析功能
     */
    public boolean isUseAnalyze() {
        return useAnalyze;
    }

    private List<IAnrSamplerListener> anrSamplerListeners = new ArrayList<>();

    /**
     * 获取ANR采样监听器列表
     * @return ANR采样监听器列表
     */
    public List<IAnrSamplerListener> getAnrSamplerListeners() {
        return anrSamplerListeners;
    }

    /**
     * 获取警告时间阈值
     * @return 警告时间阈值（毫秒）
     */
    public long getWarnTime() {
        return warnTime;
    }

    /**
     * 获取消息间隔时间阈值
     * @return 消息间隔时间阈值（毫秒）
     */
    public long getGapTime() {
        return gapTime;
    }

    /**
     * 获取ANR判定时间阈值
     * @return ANR判定时间阈值（毫秒）
     */
    public long getAnrTime() {
        return anrTime;
    }

    /**
     * 获取掉帧数阈值
     * @return 掉帧数阈值
     */
    public int getJankFrame() {
        return jankFrame;
    }

    /**
     * 私有构造函数，使用Builder模式创建实例
     */
    private BlockBoxConfig() {
    }



    /**
     * Builder类，用于构建BlockBoxConfig实例
     */
    public static class Builder{
        private final BlockBoxConfig config;
        /**
         * 构造函数，初始化配置对象
         */
        public Builder(){
            config = new BlockBoxConfig();
            FileSample fileSample = FileSample.instance;
            config.anrSamplerListeners.add( fileSample );
        }

        /**
         * 设置警告时间阈值
         * @param warnTime 警告时间阈值（毫秒）
         * @return Builder对象
         */
        public Builder setWarnTime(long warnTime) {
            config.warnTime = warnTime;
            return this;
        }

        /**
         * 设置消息间隔时间阈值
         * @param gapTime 消息间隔时间阈值（毫秒）
         * @return Builder对象
         */
        public Builder setGapTime(long gapTime) {
            config.gapTime = gapTime;
            return this;
        }

        /**
         * 设置ANR判定时间阈值
         * @param anrTime ANR判定时间阈值（毫秒）
         * @return Builder对象
         */
        public Builder setAnrTime(long anrTime) {
            config.anrTime = anrTime;
            return this;
        }

        /**
         * 设置掉帧数阈值
         * @param jankFrme 掉帧数阈值
         * @return Builder对象
         */
        public Builder setJankFrame(int jankFrme) {
            config.jankFrame = jankFrme;
            return this;
        }

        /**
         * 添加ANR采样监听器
         * @param anrSamplerListener ANR采样监听器
         * @return Builder对象
         */
        public Builder addAnrSampleListener(IAnrSamplerListener anrSamplerListener) {
            config.anrSamplerListeners.add( anrSamplerListener );
            return this;
        }

        /**
         * 添加第一个ANR采样监听器
         * @param anrSamplerListener ANR采样监听器
         * @return Builder对象
         */
        public Builder addFirstAnrSampleListener(IAnrSamplerListener anrSamplerListener) {
            config.anrSamplerListeners.add( anrSamplerListener );
            return this;
        }

        /**
         * 设置是否使用分析功能
         * @param useAnalyze 是否使用分析功能
         * @return Builder对象
         */
        public Builder useAnalyze(boolean useAnalyze){
            config.useAnalyze = useAnalyze;
            return this;
        }

        /**
         * 构建配置对象
         * @return BlockBoxConfig实例
         */
        public BlockBoxConfig build(){
            return config;
        }
    }

    /**
     * 配置变化监听接口
     */
    public interface IConfigChangeListener{
        /**
         * 配置变化时的回调
         * @param config 变化后的配置对象
         */
        void onConfigChange(BlockBoxConfig config);
    }
}
