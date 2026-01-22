package com.txl.blockmoonlighttreasurebox.info;

import java.io.Serializable;

/**
 * 消息盒子类
 * 封装了分发的Message相关信息，包括Handler、Callback、消息类型等
 */
public class BoxMessage implements Serializable {
    public static final String SEPARATOR = "\r\n";
    private static final long serialVersionUID = 1L;

    private String handleName;
    /**
     * Handler内存地址
     */
    private String handlerAddress;
    private String callbackName;
    private int messageWhat;
    private long msgId;

    /**
     * 获取Handler名称
     * @return Handler名称
     */
    public String getHandleName() {
        return handleName;
    }

    /**
     * 获取Callback名称
     * @return Callback名称
     */
    public String getCallbackName() {
        return callbackName;
    }

    /**
     * 获取消息what值
     * @return 消息what值
     */
    public int getMessageWhat() {
        return messageWhat;
    }

    /**
     * 获取Handler地址
     * @return Handler地址
     */
    public String getHandlerAddress() {
        return handlerAddress;
    }

    /**
     * 获取消息ID
     * @return 消息ID
     */
    public long getMsgId() {
        return msgId;
    }

    /**
     * 设置消息ID
     * @param msgId 消息ID
     */
    public void setMsgId(long msgId) {
        this.msgId = msgId;
    }

    /**
     * 默认构造函数
     */
    public BoxMessage() {
    }

    /**
     * 构造函数
     * @param handleName Handler名称
     * @param callbackName Callback名称
     * @param messageWhat 消息what值
     * @param handlerAddress Handler地址
     */
    public BoxMessage(String handleName, String callbackName, int messageWhat,String handlerAddress) {
        this.handleName = handleName;
        this.callbackName = callbackName;
        this.messageWhat = messageWhat;
        this.handlerAddress = handlerAddress;
    }

    @Override
    public String toString() {
        return "BoxMessage{" +
                "handleName='" + handleName + '\'' +
                ", handlerAddress='" + handlerAddress + '\'' +
                ", callbackName='" + callbackName + '\'' +
                ", messageWhat=" + messageWhat +
                ", msgId=" + msgId +
                '}';
    }
}
