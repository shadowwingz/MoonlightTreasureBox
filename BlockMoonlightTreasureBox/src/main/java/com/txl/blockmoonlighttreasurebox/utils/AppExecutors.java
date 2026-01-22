/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.txl.blockmoonlighttreasurebox.utils;

import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * 全局线程池执行器
 * 为整个应用程序提供全局的线程池，用于分组执行不同类型的任务
 * 通过将任务分组，避免任务饥饿的影响（例如磁盘读取不会等待在Web服务请求后面）
 */
public class AppExecutors {

    private static final int THREAD_COUNT = 3;
    private AppExecutors appExecutors;

    private final Executor diskIO;

    private final Executor networkIO;

    private final MainThreadExecutor mainThread;

    @VisibleForTesting
    AppExecutors(Executor diskIO, Executor networkIO, MainThreadExecutor mainThread) {
        this.diskIO = diskIO;
        this.networkIO = networkIO;
        this.mainThread = mainThread;
    }

    /**
     * 构造函数，初始化三个执行器
     */
    public AppExecutors() {
        this(new DiskIOThreadExecutor(), Executors.newFixedThreadPool(THREAD_COUNT),
                new MainThreadExecutor());
    }

    /**
     * 获取磁盘IO执行器
     * @return 磁盘IO执行器
     */
    public Executor diskIO() {
        return diskIO;
    }

    /**
     * 获取网络IO执行器
     * @return 网络IO执行器
     */
    public Executor networkIO() {
        return networkIO;
    }

    /**
     * 获取主线程执行器
     * @return 主线程执行器
     */
    public MainThreadExecutor mainThread() {
        return mainThread;
    }

    /**
     * 主线程执行器，用于在主线程上执行任务
     */
    public static class MainThreadExecutor implements Executor {
        private Handler mainThreadHandler = new Handler(Looper.getMainLooper());

        /**
         * 在主线程上执行任务
         * @param command 要执行的任务
         */
        @Override
        public void execute(@NonNull Runnable command) {
            mainThreadHandler.post(command);
        }

        /**
         * 延迟在主线程上执行任务
         * @param command 要执行的任务
         * @param ms 延迟时间（毫秒）
         */
        public void executeDelay(@NonNull Runnable command, long ms){
            mainThreadHandler.postDelayed( command,ms );
        }

    }

    /**
     * 获取AppExecutors单例实例
     * @return AppExecutors单例实例
     */
    public static AppExecutors getInstance(){
        return AppExecutorsHolder.appExecutors;
    }

    /**
     * AppExecutors持有类，用于实现单例模式
     */
    static class AppExecutorsHolder{
        static AppExecutors appExecutors = new AppExecutors();
    }
}
