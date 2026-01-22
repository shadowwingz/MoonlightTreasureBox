package com.txl.blockmoonlighttreasurebox.block;

/**
 * 系统ANR观察者接口
 * 定义了系统ANR发生时的回调方法
 */
public interface ISystemAnrObserver {
    /**
     * 系统ANR发生时的回调方法
     */
    void onSystemAnr();
}
