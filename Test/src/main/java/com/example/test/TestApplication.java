package com.example.test;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.os.Process;
import android.util.Log;

import com.github.moduth.blockcanary.BlockCanary;
import com.github.moduth.blockcanary.BlockCanaryContext;
import com.txl.blockmoonlighttreasurebox.block.BlockBoxConfig;
import com.txl.blockmoonlighttreasurebox.block.BlockMonitorFace;
import com.txl.blockmoonlighttreasurebox.block.SystemAnrMonitor;

/**
 * 测试应用程序类
 * 负责在应用启动时初始化性能监控组件
 * 只在主进程中初始化监控，避免多进程重复初始化
 */
public class TestApplication extends Application {

    private final String TAG = TestApplication.class.getSimpleName();

    /**
     * Application创建时的回调
     * 判断当前进程是否为主进程，如果是则初始化BlockMonitor和SystemAnrMonitor
     */
    @Override
    public void onCreate() {
        super.onCreate();

        // 获取当前进程ID
        int pid = Process.myPid();
        String processName = "";

        // 获取ActivityManager
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);

        // 遍历所有运行中的进程，找到当前进程的名称
        for (ActivityManager.RunningAppProcessInfo process: manager.getRunningAppProcesses()) {
            if(process.pid == pid)
            {
                processName = process.processName;
            }
        }

        // 只在主进程中初始化监控
        if(processName.equals(getPackageName())){
            Log.d(TAG,"init BlockMonitor");

            // 初始化BlockMonitor（性能监控组件）
            BlockMonitorFace.init(this)
                    .updateConfig(new BlockBoxConfig.Builder()
                            .useAnalyze(true)  // 启用性能分析功能
                            .build())
                    .startMonitor();  // 开始监控

            // 初始化SystemAnrMonitor（系统ANR监控）
            SystemAnrMonitor.init(BlockMonitorFace.getBlockMonitorFace());
        }


//        BlockCanary.install(TestApplication.this, new BlockCanaryContext()).start();

    }
}
