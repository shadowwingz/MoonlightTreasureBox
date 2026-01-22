package com.txl.blockmoonlighttreasurebox.utils;

import com.txl.blockmoonlighttreasurebox.info.BoxMessage;

/**
 * 消息工具类
 * 提供消息解析和判断的通用方法
 */
public class BoxMessageUtils {
    /**
     * 解析Looper发出的消息
     * 消息样例：
     * >>>>> Dispatching to Handler (android.view.ViewRootImpl$ViewRootHandler) {3346d43} com.example.test.MainActivity$1@7250fab: 0
     * @param msg 消息字符串
     * @return 解析后的BoxMessage对象
     */
    public static BoxMessage parseLooperStart(String msg){
        BoxMessage boxMessage;
        try {
            msg = msg.trim();
            String[] msgA = msg.split(":");
            int what = Integer.parseInt(msgA[1].trim());
            //>>>>> Dispatching to Handler (android.view.ViewRootImpl$ViewRootHandler) {3346d43} com.example.test.MainActivity$1@7250fab
            msgA = msgA[0].split("\\{.*\\}");
            String callback = msgA[1];
            //>>>>> Dispatching to Handler (android.view.ViewRootImpl$ViewRootHandler)
            msgA = msgA[0].split("\\(");
            msgA = msgA[1].split("\\)");
            String handler = msgA[0];
            msgA = msg.split( "\\{" );
            msgA = msgA[1].split( "\\}" );
            boxMessage = new BoxMessage(handler,callback,what,msgA[0]);
        }catch (Exception e){
            e.printStackTrace();
            boxMessage = new BoxMessage();
        }
        return boxMessage;
    }

    /**
     * 判断某条消息是否为doFrame消息（帧渲染消息）
     * @param message 消息对象
     * @return 是否为doFrame消息
     */
    public static boolean isBoxMessageDoFrame(BoxMessage message){
        return message != null && "android.view.Choreographer$FrameHandler".equals(message.getHandleName()) && message.getCallbackName().contains("android.view.Choreographer$FrameDisplayEventReceiver");
    }

    /**
     * 判断某条消息是否来自ActivityThread$H
     * @param message 消息对象
     * @return 是否来自ActivityThread$H
     */
    public static boolean isBoxMessageActivityThread(BoxMessage message){
        return message != null && "android.app.ActivityThread$H".equals(message.getHandleName());
    }
}
