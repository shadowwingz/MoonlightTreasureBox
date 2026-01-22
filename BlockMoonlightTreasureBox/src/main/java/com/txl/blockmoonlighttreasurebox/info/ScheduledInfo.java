package com.txl.blockmoonlighttreasurebox.info;

import java.io.Serializable;

/**
 * Copyright (c) 2021, 唐小陆 All rights reserved.
 * author：txl
 * date：2021/10/24
 * description：调度信息类，用于记录主线程调度能力的相关信息
 */
public class ScheduledInfo implements Serializable {
    public static final long NO_DEALT = -1;
    private static final long serialVersionUID = 1L;
    private long dealt = NO_DEALT;
    private String msgId;
    /**
     * 当前调度是否接收到了结束的信息，如果没有接收到说明主线程很久都没有处理对应的回调
     */
    private boolean start = true;

    /**
     * 构造函数
     * @param dealt 耗时
     * @param msgId 消息ID
     * @param start 是否开始
     */
    public ScheduledInfo(long dealt, String msgId, boolean start) {
        this.dealt = dealt;
        this.msgId = msgId;
        this.start = start;
    }

    /**
     * 获取耗时
     * @return 耗时
     */
    public long getDealt() {
        return dealt;
    }

    /**
     * 设置耗时
     * @param dealt 耗时
     */
    public void setDealt(long dealt) {
        this.dealt = dealt;
    }

    /**
     * 获取消息ID
     * @return 消息ID
     */
    public String getMsgId() {
        return msgId;
    }

    /**
     * 判断是否开始
     * @return 是否开始
     */
    public boolean isStart() {
        return start;
    }

    /**
     * 设置开始状态
     * @param start 是否开始
     */
    public void setStart(boolean start) {
        this.start = start;
    }
}
