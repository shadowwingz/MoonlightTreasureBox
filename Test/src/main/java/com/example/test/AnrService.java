package com.example.test;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.SystemClock;

/**
 * ANR测试服务
 * 用于测试Service启动时的ANR场景
 * 在onCreate方法中休眠20秒来触发ANR
 */
public class AnrService extends Service {

    /**
     * 构造函数
     */
    public AnrService() {
    }

    /**
     * Service创建时的回调
     * 休眠20秒来触发ANR（Application Not Responding）
     */
    @Override
    public void onCreate() {
        super.onCreate();
        SystemClock.sleep(20000);
    }

    /**
     * 绑定Service时的回调
     * @param intent 绑定意图
     * @return 返回通信通道，当前未实现
     */
    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}