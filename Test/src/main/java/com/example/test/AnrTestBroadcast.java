package com.example.test;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.util.Log;

/**
 * ANR测试广播接收器
 * 用于测试广播接收超时导致的ANR场景
 * 支持发送有序广播来测试前台广播超时
 */
public class AnrTestBroadcast extends BroadcastReceiver {

    private static String TAG = AnrTestBroadcast.class.getSimpleName();
    // 测试ANR的广播Action
    private static final String ACTION_TEST_ANR = "com.txl.test_anr";

    /**
     * 接收广播时的回调
     * @param context 上下文
     * @param intent 接收到的广播意图
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Log.d(TAG,"onReceive action : "+action);
    }

    /**
     * 发送测试ANR的有序广播
     * 使用前台广播标志，超时时间较短（约10秒）
     * @param context 上下文
     */
    public static void sentBroadcast(Context context){
        Intent intent = new Intent();
        // 设置为前台广播，超时时间更短
        intent.addFlags( Intent.FLAG_RECEIVER_FOREGROUND );
        intent.setAction(ACTION_TEST_ANR);
        context.sendOrderedBroadcast(intent,null);
    }

    /**
     * 注册广播接收器
     * 根据Android版本选择不同的注册方式
     * @param context 上下文
     * @return 注册的广播接收器实例
     */
    public static AnrTestBroadcast register(Context context){
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_TEST_ANR);
        AnrTestBroadcast anrTestBroadcast = new AnrTestBroadcast();

        // Android 13及以上版本需要指定RECEIVER_NOT_EXPORTED
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.registerReceiver(anrTestBroadcast, intentFilter, Context.RECEIVER_NOT_EXPORTED);
        } else {
            context.registerReceiver(anrTestBroadcast, intentFilter);
        }
        return anrTestBroadcast;
    }
}
