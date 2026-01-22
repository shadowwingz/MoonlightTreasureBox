package com.example.test;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.hjq.permissions.OnPermissionCallback;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;

import java.util.List;

/**
 * 主Activity
 * 提供多种ANR和UI卡顿测试场景的入口
 * 包括：UI卡顿测试、广播ANR测试、Activity ANR测试等
 */
public class MainActivity extends Activity {

    private final String TAG = MainActivity.class.getSimpleName();

    // 卡顿测试View
    private JankView jankView;

    // ANR测试广播接收器
    AnrTestBroadcast anrTestBroadcast;

    // 主线程Handler，用于向主线程消息队列发送任务
    Handler mainHandler = new Handler(Looper.getMainLooper());

    // 计数器，用于runnable中更新文本
    int num = 0;

    /**
     * 周期性执行的Runnable
     * 模拟主线程中的CPU密集型任务
     * 每16ms执行一次，但每次都有大量计算，可能导致帧率下降
     */
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            TextView textView = findViewById(R.id.tv_text);
            textView.setText("我是：  "+num++);

            // 模拟CPU密集型任务，循环5亿次
            int i = 1;
            while (i < 500_000_000){
                i++;
            }
            // 16ms后再次执行，约60fps的帧率
            textView.postDelayed(this,16);
        }
    };

    /**
     * Activity创建时的回调
     * 初始化权限、注册广播、设置各种测试按钮的点击事件
     * @param savedInstanceState 保存的实例状态
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 请求媒体读写权限
        XXPermissions.with(this)
                .permission(Permission.READ_MEDIA_IMAGES)
                .permission(Permission.READ_MEDIA_VIDEO)
                .permission(Permission.READ_MEDIA_AUDIO)
                .request(new OnPermissionCallback() {
                    /**
                     * 权限授予成功回调
                     * @param permissions 授予的权限列表
                     * @param all 是否全部授予
                     */
                    @Override
                    public void onGranted(List<String> permissions, boolean all) {
                        if (!all) {
                            Toast.makeText(MainActivity.this,"获取部分权限成功，但部分权限未正常授予",Toast.LENGTH_SHORT).show();
                        }
                    }

                    /**
                     * 权限拒绝回调
                     * @param permissions 拒绝的权限列表
                     * @param never 是否永久拒绝
                     */
                    @Override
                    public void onDenied(List<String> permissions, boolean never) {
                        if (never) {
                            Toast.makeText(MainActivity.this,"被永久拒绝授权，请手动授予文件读写权限权限",Toast.LENGTH_SHORT).show();
                            // 如果是被永久拒绝就跳转到应用权限系统设置页
                        } else {
                            Toast.makeText(MainActivity.this,"获取文件读写权限失败",Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        // 注册ANR测试广播接收器
        anrTestBroadcast = AnrTestBroadcast.register(this);

        // 初始化卡顿测试View
        jankView = findViewById(R.id.jankView);

        // 设置UI卡顿测试按钮
        findViewById(R.id.tvTestJank).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 启用JankView的卡顿模式
                jankView.setJank(true);
            }
        });

        // 设置ANR测试场景1：严重耗时任务阻塞广播接收
        findViewById(R.id.tvTestAnr1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 发送消息的目的是提前让消息队列处理严重耗时任务，导致广播不能及时被接收处理
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        // 休眠12秒，超过前台广播10秒的超时时间
                        SystemClock.sleep(12000);
                    }
                });
                // 发送测试广播
                AnrTestBroadcast.sentBroadcast(MainActivity.this);
            }
        });

        // 设置ANR测试场景2：多个轻微耗时任务累积阻塞
        findViewById(R.id.tvTestAnr2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 发送多个不是非常严重耗时消息，模拟消息队列繁忙
                // 25个500ms的任务 = 12.5秒，累积超过广播超时时间
                for (int i=25;i>0;i--){
                    mainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            // 每个任务休眠500ms
                            SystemClock.sleep(500);
                        }
                    });
                }
                // 发送测试广播
                AnrTestBroadcast.sentBroadcast(MainActivity.this);
            }
        });

        // 设置ANR测试场景3：启动Activity中的ANR测试
        findViewById(R.id.tvTestAnr3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,AnrTestActivity.class);
                startActivity(intent);
            }
        });
    }

    /**
     * Activity销毁时的回调
     * 注销广播接收器，防止内存泄漏
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(anrTestBroadcast);
    }

    /**
     * 消耗CPU的方法
     * 执行50次循环，每次循环5亿次加法运算
     * 用于测试高CPU占用场景
     */
    private void consumeCpu(){
        int i = 1;
        while (i<50){
            int j = 1;
            while (j < 500_000_000){
                j++;
            }
            i++;
        }
    }

    /**
     * Activity恢复时的回调
     * 可以在此处触发CPU密集型任务进行测试（当前已注释）
     */
    @Override
    protected void onResume() {
        super.onResume();
//        consumeCpu();
    }

    // 用于测试线程同步的对象
    Object object = new Object();

    /**
     * 测试线程时间的方法
     * 创建三个线程来测试不同的执行场景：
     * 1. thread2: 执行CPU密集型任务
     * 2. thread1: 先持有锁并休眠6秒
     * 3. thread3: 等待获取锁
     */
    private void testThreadTime(){
        // 线程2：CPU密集型任务
        new Thread(new Runnable() {
            @Override
            public void run() {
                long time1 = SystemClock.currentThreadTimeMillis();
                int i = 0;
                while (i < 1000000000){
                    i++;
                }
                long time2 = SystemClock.currentThreadTimeMillis();
                Log.d(TAG,"thread2 time1 : "+time1+"  time2 : "+time2 + " dealt : "+(time2 - time1));
            }
        },"thread2").start();

        // 线程1：持有锁并休眠
        new Thread(new Runnable() {
            @Override
            public void run() {
                long time1 = SystemClock.currentThreadTimeMillis();
                Log.d(TAG,"thread1 time1 : start "+time1);
                try {
                    synchronized (object){
                        Thread.sleep(3000);
                        SystemClock.sleep(3000);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                long time2 = SystemClock.currentThreadTimeMillis();
                Log.d(TAG,"thread1 time1 : "+time1+"  time2 : "+time2 + " dealt : "+(time2 - time1));
            }
        },"thread1").start();

        // 线程3：等待获取锁
        new Thread(new Runnable() {
            @Override
            public void run() {
                long time1 = SystemClock.currentThreadTimeMillis();
                Log.d(TAG,"thread3 time1 : start "+time1);
                synchronized (object){
                    Log.d(TAG,"get object");
                }
                long time2 = SystemClock.currentThreadTimeMillis();
                Log.d(TAG,"thread3 time1 : "+time1+"  time2 : "+time2 + " dealt : "+(time2 - time1));
            }
        },"thread3").start();
    }
}