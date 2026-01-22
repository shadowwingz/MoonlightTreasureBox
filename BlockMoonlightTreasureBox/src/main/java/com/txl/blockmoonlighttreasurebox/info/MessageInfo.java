package com.txl.blockmoonlighttreasurebox.info;

import android.os.SystemClock;

import androidx.annotation.IntDef;
import androidx.annotation.Nullable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 消息信息类
 * 用于存储消息分发的相关信息，包括消息类型、耗时、CPU时间等
 */
public class MessageInfo implements Serializable {
    private static final long serialVersionUID = 1L;

    public static final int MSG_TYPE_NONE = 0x00;
    public static final int MSG_TYPE_INFO = 0x01;
    public static final int MSG_TYPE_WARN = 0x02;
    public static final int MSG_TYPE_ANR = 0x04;
    /**
     * 掉帧
     * */
    public static final int MSG_TYPE_JANK = 0x08;
    /**
     * 连续两个消息之间的间隙
     * */
    public static final int MSG_TYPE_GAP = 0x10;
    /**
     * 通过ActivityThread$H handle 发送的消息
     * */
    public static final int MSG_TYPE_ACTIVITY_THREAD_H = 0x20;

    @IntDef({MSG_TYPE_NONE,MSG_TYPE_INFO,MSG_TYPE_WARN,MSG_TYPE_ANR,MSG_TYPE_JANK,MSG_TYPE_GAP,MSG_TYPE_ACTIVITY_THREAD_H})
    private @interface MsgType{}

    public @MsgType int msgType = MSG_TYPE_INFO;

    /**
     * 消息数量，至少有一条消息
     */
    public int count = 0;
    /**
     * 消息分发耗时（wall时间）
     */
    public long wallTime = 0;
    /**
     * CPU时间
     * SystemClock.currentThreadTimeMillis()是当前线程方法的执行时间，不包含线程休眠或锁竞争等待
     * CPU时间是函数真正执行时间
     */
    public long cpuTime = 0;
    public List<BoxMessage> boxMessages = new ArrayList<>();

    /**
     * 消息被创建的时间
     */
    public long messageCreateTime = SystemClock.elapsedRealtime();

    @Override
    public String toString() {
        return "MessageInfo{" +
                "msgType=" + msgTypeToString(msgType) +
                ", count=" + count +
                ", wallTime=" + wallTime +
                ", cpuTime=" + cpuTime +
                ", boxMessages=" + boxMessages +
                '}';
    }

    /**
     * 将消息类型转换为字符串
     * @param msgType 消息类型
     * @return 消息类型字符串
     */
    public static String msgTypeToString(@MsgType int msgType){
        switch (msgType){
            case MSG_TYPE_NONE:
                return "MSG_TYPE_NONE";
            case MSG_TYPE_INFO:
                return "MSG_TYPE_INFO";
            case MSG_TYPE_WARN:
                return "MSG_TYPE_WARN";
            case MSG_TYPE_ANR:
                return "MSG_TYPE_ANR";
            case MSG_TYPE_JANK:
                return "MSG_TYPE_JANK";
            case MSG_TYPE_GAP:
                return "MSG_TYPE_GAP";
            case MSG_TYPE_ACTIVITY_THREAD_H:
                return "MSG_TYPE_ACTIVITY_THREAD_H";
        }
        return "";
    }
}
