package com.txl.blockmoonlighttreasurebox.utils;

import androidx.annotation.NonNull;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * 磁盘IO线程执行器
 * 在新的后台线程上执行任务，专门用于磁盘IO操作
 */
public class DiskIOThreadExecutor implements Executor {

    private final Executor mDiskIO;

    /**
     * 构造函数，创建单线程执行器
     */
    public DiskIOThreadExecutor() {
        mDiskIO = Executors.newSingleThreadExecutor();
    }

    /**
     * 在磁盘IO线程上执行任务
     * @param command 要执行的任务
     */
    @Override
    public void execute(@NonNull Runnable command) {
        mDiskIO.execute(command);
    }
}
