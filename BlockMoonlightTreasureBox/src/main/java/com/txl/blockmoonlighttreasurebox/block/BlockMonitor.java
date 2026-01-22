package com.txl.blockmoonlighttreasurebox.block;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.util.Log;
import android.util.Printer;
import android.view.Choreographer;

import com.txl.blockmoonlighttreasurebox.info.BoxMessage;
import com.txl.blockmoonlighttreasurebox.info.MessageInfo;
import com.txl.blockmoonlighttreasurebox.sample.manager.ISamplerManager;
import com.txl.blockmoonlighttreasurebox.sample.SamplerFactory;
import com.txl.blockmoonlighttreasurebox.ui.DisplayUtils;
import com.txl.blockmoonlighttreasurebox.utils.BoxMessageUtils;
import com.txl.blockmoonlighttreasurebox.utils.ReflectUtils;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 监控卡顿消息的核心类
 * 实现了Printer接口，通过Looper的消息分发机制来监控主线程的卡顿、ANR等问题
 * 同时实现了ISystemAnrObserver接口，用于接收系统级别的ANR通知
 */
class BlockMonitor implements Printer,IBlock, ISystemAnrObserver {
    private final String TAG = BlockMonitor.class.getSimpleName();
    private boolean start = false;
    private Context applicationContext;

    /**
     * 每一帧的时间
     */
    private final float mFrameIntervalNanos = ReflectUtils.reflectLongField( Choreographer.getInstance(), Choreographer.class, "mFrameIntervalNanos", 16000000 )*0.000001f;
    private final long noInit = -1;
    private long startTime = noInit, tempStartTime = noInit, lastEnd = noInit;
    private long cupStartTime = noInit, cpuTempStartTime = noInit, lastCpuEnd = noInit;
    /**
     * 超过这个时间 就发生anr
     * */
    private long monitorAnrTime = noInit,monitorMsgId = 0;


    /**
     * 每次消息处理完成后需要置空
     */
    private MessageInfo messageInfo;
    private BoxMessage currentMsg;
    private AnrMonitorThread anrMonitorThread;

    private BlockBoxConfig config;

    /**
     * 采集anr时的相关信息
     * */
    private ISamplerManager samplerManager;
//    /**
//     * 正常采集
//     * */
//    private SamplerListenerChain sampleListener = new SamplerListenerChain();

    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private long checkId = -1;
    Runnable checkThreadRunnable = new Runnable() {
        long dealtTime = SystemClock.elapsedRealtime();

        @Override
        public void run() {
            //时间偏差 差值越大说明调度能力越差
            //就采集一次。
            long offset = SystemClock.elapsedRealtime() - dealtTime - config.getWarnTime();
            if(checkId > -1){
                //需要注意的是，这个只能反映发生anr前的调度能力，会存在发生anr前最后一次调度检测没有收集到。
                samplerManager.onScheduledSample(false, dealtTime,""+monitorMsgId,offset );
            }
            if(start){
                checkId++;
                dealtTime = SystemClock.elapsedRealtime();
//                sampleListener.onScheduledSample(true, dealtTime,""+checkId,offset );
                mainHandler.postDelayed(checkThreadRunnable, config.getWarnTime());
            }
        }
    };

    /**
     * 监控主线程调度能力
     * 通过定时检查主线程的调度情况来评估调度能力
     */
    private void startCheckTime() {
        if(config == null){
            throw new RuntimeException("before start please set config");
        }
        if(start)
        mainHandler.post(checkThreadRunnable);
    }

    /**
     * 初始化监控器
     * @param applicationContext 应用上下文
     */
    public void init(Context applicationContext) {
        this.applicationContext = applicationContext;
        samplerManager = SamplerFactory.createSampleManager();
        updateConfig( new BlockBoxConfig.Builder().build() );
    }

    /**
     * 更新配置
     * @param config 新的配置对象
     * @return 当前BlockMonitor实例
     */
    public IBlock updateConfig(BlockBoxConfig config) {
        this.config = config;
        if(applicationContext != null)
        DisplayUtils.showAnalyzeActivityInLauncher(applicationContext,config.isUseAnalyze());
        samplerManager.onConfigChange( config );
        return this;
    }

    /**
     * 私有构造函数，使用单例模式
     */
    private BlockMonitor() {

    }

    //调用println 是奇数次还是偶数  默认false 偶数  true 奇数
    private final AtomicBoolean odd = new AtomicBoolean(false);

    /**
     * Printer接口的实现方法
     * 通过判断奇偶来区分消息开始和结束
     * @param x Looper打印的消息
     */
    @Override
    public void println(String x) {
        if(x.contains("<<<<< Finished to") && !odd.get()){
            return;
        }
        //原来是偶数次，那么这次进来就是奇数
        if (!odd.get()) {
            msgStart( x );
        } else {
            msgEnd( x );
        }
        odd.set(!odd.get());

    }

    /**
     * 处理消息开始事件
     * @param msg 消息内容
     */
    private void msgStart(String msg) {
        tempStartTime = SystemClock.elapsedRealtime();
        monitorAnrTime = tempStartTime + config.getAnrTime();
        currentMsg = BoxMessageUtils.parseLooperStart( msg );
        currentMsg.setMsgId( monitorMsgId );
        cpuTempStartTime = SystemClock.currentThreadTimeMillis();
        //两次消息时间差较大，单独处理消息且增加一个gap消息 不应该存在两个连续的gap消息
        if (tempStartTime - lastEnd > config.getGapTime() && lastEnd != noInit) {
            if (messageInfo != null) {
                handleMsg();
            }
            messageInfo = new MessageInfo();
            messageInfo.msgType = MessageInfo.MSG_TYPE_GAP;
            messageInfo.wallTime = tempStartTime - lastEnd;
            messageInfo.cpuTime = cpuTempStartTime - lastCpuEnd;
            startTime = tempStartTime;
            handleMsg();
        }
        if (messageInfo == null) {
            messageInfo = new MessageInfo();
            startTime = SystemClock.elapsedRealtime();
            tempStartTime = startTime;
            cupStartTime = SystemClock.currentThreadTimeMillis();
            cpuTempStartTime = cupStartTime;
        }
    }

    /**
     * 处理消息结束事件
     * @param msg 消息内容
     */
    private void msgEnd(String msg) {
        synchronized (BlockMonitor.class){
            lastEnd = SystemClock.elapsedRealtime();
            lastCpuEnd = SystemClock.currentThreadTimeMillis();
            long dealt = lastEnd - tempStartTime;
            handleJank( dealt );
            boolean msgActivityThread = BoxMessageUtils.isBoxMessageActivityThread(currentMsg);
            if(messageInfo == null){//在这个位置为空 现阶段的逻辑只有 anr 采集时将原来的 messageInfo 置空了
                messageInfo = new MessageInfo();
            }
            if (dealt > config.getWarnTime() || msgActivityThread) {
                if (messageInfo.count > 1) {//先处理原来的信息
                    messageInfo.msgType = MessageInfo.MSG_TYPE_INFO;
                    handleMsg();
                }
                messageInfo = new MessageInfo();
                messageInfo.wallTime = lastEnd - tempStartTime;
                messageInfo.cpuTime = lastCpuEnd - cpuTempStartTime;
                messageInfo.boxMessages.add( currentMsg );
                messageInfo.msgType = MessageInfo.MSG_TYPE_WARN;
                boolean anr = dealt > config.getAnrTime();
                if (anr) {
                    messageInfo.msgType = MessageInfo.MSG_TYPE_ANR;
                }else if(msgActivityThread){
                    messageInfo.msgType = MessageInfo.MSG_TYPE_ACTIVITY_THREAD_H;
                }

                handleMsg();
                if(anr){
                    samplerManager.messageQueueDispatchAnrFinish();
                }
            } else {
                //统计每一次消息分发耗时 他们的叠加就是总耗时
                messageInfo.wallTime += lastEnd - startTime;
                //生成消息的时候，当前线程总的执行时间
                messageInfo.cpuTime += lastCpuEnd - cpuTempStartTime;
                messageInfo.boxMessages.add( currentMsg );
                messageInfo.count++;
                if (messageInfo.wallTime > config.getWarnTime()) {
                    handleMsg();
                }
            }
            monitorMsgId++;
        }
    }

    /**
     * 处理掉帧问题
     * @param dealt 消息处理耗时
     */
    private void handleJank(long dealt) {

        if (BoxMessageUtils.isBoxMessageDoFrame( currentMsg ) && dealt > mFrameIntervalNanos * config.getJankFrame()) {
            MessageInfo temp = messageInfo;
            messageInfo = new MessageInfo();
            messageInfo.msgType = MessageInfo.MSG_TYPE_JANK;
            messageInfo.boxMessages.add( currentMsg );
            messageInfo.wallTime = lastEnd - tempStartTime;
            messageInfo.cpuTime = lastCpuEnd - cpuTempStartTime;
            samplerManager.onJankSample( monitorMsgId+"",messageInfo );
//            handleMsg();
            messageInfo = temp;
        }
    }

    /**
     * 获取应用上下文
     * @return 应用上下文
     */
    @Override
    public Context getApplicationContext() {
        return applicationContext;
    }

    /**
     * 开始监控
     * @return 当前BlockMonitor实例
     */
    public synchronized IBlock startMonitor() {
        if(start){
            Log.e( TAG,"already start" );
            return null;
        }
        start = true;
        anrMonitorThread = new AnrMonitorThread("anrMonitorThread");
        anrMonitorThread.start();
        Looper.getMainLooper().setMessageLogging( this );
        startCheckTime();
        return this;
    }

    /**
     * 停止监控
     * @return 当前BlockMonitor实例
     */
    public synchronized IBlock stopMonitor(){
        Looper.getMainLooper().setMessageLogging( null );
        mainHandler.removeCallbacksAndMessages( null );
        anrMonitorThread = null;
        start = false;
        checkId = -1;
        return this;
    }

    /**
     * 处理消息
     * 将消息信息发送给采样管理器进行处理
     */
    private void handleMsg() {
        if (messageInfo != null) {
            MessageInfo temp = messageInfo;
            messageInfo = null;
            long msgId = 0L;
            if (temp.boxMessages != null && temp.boxMessages.size() != 0){
                msgId = temp.boxMessages.get(0).getMsgId();
            }
            Log.d(TAG,"add msg wallTime other wallTime : "+temp.wallTime +"  cpuTime "+temp.cpuTime+"   MSG_TYPE : "+MessageInfo.msgTypeToString(temp.msgType)+"  msgId "+msgId);
            samplerManager.onMsgSample( SystemClock.elapsedRealtimeNanos(),monitorMsgId+"",temp );
        }
        messageInfo = null;
    }

    /**
     * 获取BlockMonitor单例实例
     * @return BlockMonitor单例实例
     */
    protected static BlockMonitor getInstance() {
        return BlockMonitorHolder.blockMonitor;
    }

    /**
     * 系统ANR发生时的回调
     * 处理系统级别的ANR事件
     */
    @Override
    public void onSystemAnr() {
        if(start){
            Log.d(TAG,"onSystemAnr thread name "+Thread.currentThread().getName());
            if(messageInfo.count > 1){
                handleMsg();
            }
            messageInfo = new MessageInfo();
            messageInfo.wallTime = SystemClock.elapsedRealtime() - Math.max(tempStartTime,lastEnd);
            //这个时候可能在处理消息，也可能处于idle状态
            long threadTime = Math.max(lastCpuEnd, cpuTempStartTime);
            messageInfo.cpuTime = SystemClock.currentThreadTimeMillis() - threadTime;
            messageInfo.msgType = MessageInfo.MSG_TYPE_ANR;
            messageInfo.boxMessages.add( currentMsg );
            handleMsg();
            samplerManager.startAnrSample(monitorMsgId+"",SystemClock.elapsedRealtime());
            samplerManager.messageQueueDispatchAnrFinish();
        }
    }


    private static class BlockMonitorHolder {
        static BlockMonitor blockMonitor = new BlockMonitor();
    }

    /**
     * ANR监控线程
     * 这个线程需要和checkTime配合工作，否则在极端情况下，如果主线程一直idle会产生错误的监测结果
     */
    private class AnrMonitorThread extends Thread{
        private long msgId = noInit;
        private long anrTime;

        /**
         * 构造函数
         * @param name 线程名称
         */
        public AnrMonitorThread(String name) {
            super(name);
        }

        /**
         * 启动ANR监控线程
         */
        @Override
        public synchronized void start() {
            super.start();
            Log.d(TAG,"AnrMonitorThread  start ");
            anrTime = SystemClock.elapsedRealtime() + config.getAnrTime();//重置anr 发生时间
        }

        /**
         * ANR监控线程的运行方法
         * 持续监控主线程的消息处理时间，当超过ANR阈值时触发ANR采样
         */
        @Override
        public void run() {
            super.run();
            Log.d(TAG,"AnrMonitorThread  run ");
            while (start){
                //以消息开始时间加上超时时长为目标超时时间，每次超时时间到了之后，检查当前时间是否大于或等于目标时间，
                // 如果满足，则说明目标时间没有更新，也就是说本次消息没结束，则抓取堆栈。如果每次超时之后，
                // 检查当前时间小于目标时间，则说明上次消息执行结束，新的消息开始执行并更新了目标超时时间，
                // 这时异步监控需对齐目标超时，再次设置超时监控，如此往复。
                long now  = SystemClock.elapsedRealtime();
                if(now >= anrTime){//时间到了  因为Main线程存在checkTime 机制 不会存在因为长时间 idle 发生anr
                    if(monitorMsgId == msgId){
                        synchronized (BlockMonitor.class){
                            if(monitorMsgId != msgId){
                                continue;
                            }
                            anrTime = now + config.getAnrTime();//重置anr 发生时间
                            //发生anr
                            Object mLogging = ReflectUtils.reflectFiled(Looper.getMainLooper(),Looper.class,"mLogging");
                            if(mLogging != BlockMonitor.this){
                                Log.e(TAG,"startMonitor MainLooper printer set by other : "+mLogging);
                                stopMonitor();
                                return;
                            }
                            Log.e(TAG,"occur anr start dump stack and other info ");
                            if(start){
                                //采集当前调度的anr消息
                                // fixme 如何统计主线程此时的cpu时间？  能不能暂时先把信息发送过去？等到正真本次消息调度结束在来修正？cpu时间
                                // fixme 如果等待 修正cpu时间 那么再次期间还会有别的消息采集吗？
                                //这个位置需要锁  要和msgEnd抢
                                handleMsg();
                                messageInfo = new MessageInfo();
                                messageInfo.wallTime = SystemClock.elapsedRealtime() - tempStartTime;
                                messageInfo.cpuTime = -1;
                                messageInfo.msgType = MessageInfo.MSG_TYPE_ANR;
                                messageInfo.boxMessages.add( currentMsg );
                                handleMsg();
                                samplerManager.startAnrSample(msgId+"",SystemClock.elapsedRealtime());
                            }
                        }
                    }else {
                        //消息已经被处理了  重置anr时间
                        msgId = monitorMsgId;
                        anrTime = monitorAnrTime;
                    }
                }
                long sleepTime = anrTime - SystemClock.elapsedRealtime();
                if(sleepTime > 0){
//                    Log.d(TAG,"AnrMonitorThread  sleep time  "+sleepTime);
                    SystemClock.sleep(sleepTime);
                }
            }
            Log.d(TAG,"AnrMonitorThread  run end start : "+start);
        }
    }
}
