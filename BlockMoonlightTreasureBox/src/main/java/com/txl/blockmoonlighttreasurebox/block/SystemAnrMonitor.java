package com.txl.blockmoonlighttreasurebox.block;

/**
 * 系统ANR监控类
 * 通过JNI调用native方法来hook系统的SignalCatcher，从而监听系统级别的ANR事件
 */
public class SystemAnrMonitor {
    static {
        System.loadLibrary("block_signal");
    }

    /**
     * Hook SignalCatcher的native方法
     * @param observed ANR观察者
     */
    private native void hookSignalCatcher(ISystemAnrObserver observed);

    /**
     * 取消Hook SignalCatcher的native方法
     */
    private native void unHookSignalCatcher();

    /**
     * 初始化系统ANR监控
     * @param systemAnrObserver ANR观察者
     */
    public static void init(ISystemAnrObserver systemAnrObserver){
        new SystemAnrMonitor().hookSignalCatcher(systemAnrObserver);
    }
}
