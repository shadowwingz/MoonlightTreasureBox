package com.example.test;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;

/**
 * ANR测试Activity
 * 用于测试在UI线程中执行耗时操作导致ANR的场景
 * 点击按钮会休眠10秒来触发ANR
 */
public class AnrTestActivity extends AppCompatActivity {

    // 防止重复点击的标志位
    boolean sleep = false;

    /**
     * Activity创建时的回调
     * 设置布局并初始化点击事件监听器
     * @param savedInstanceState 保存的实例状态
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anr_test);

        // 设置测试按钮的点击事件
        findViewById(R.id.tvTest).setOnClickListener(new View.OnClickListener() {
            /**
             * 点击回调函数
             * 在主线程中休眠10秒来触发ANR
             * @param v 被点击的视图
             */
            @Override
            public void onClick(View v) {
                if(!sleep){
                    sleep = true;
                    // 在主线程中休眠10秒，会触发ANR
                    SystemClock.sleep(10000);
                    sleep = false;
                }

            }
        });
    }
}