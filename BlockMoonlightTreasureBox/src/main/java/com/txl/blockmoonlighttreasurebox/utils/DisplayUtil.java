package com.txl.blockmoonlighttreasurebox.utils;

import android.content.Context;

/**
 * Copyright (c) 2018, 唐小陆 All rights reserved.
 * author：txl
 * date：2018/9/6
 * description：屏幕单位换算工具类，提供dp、sp与px之间的转换
 */
public class DisplayUtil {

    /**
     * 将px值转换为dip或dp
     * @param context 上下文
     * @param pxValue px值
     * @return dip值
     */
    public static int px2dip(Context context, float pxValue){
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue/scale+0.5f);
    }

    /**
     * 将dip或dp转换成px
     * @param context 上下文
     * @param dipValue dip值
     * @return px值
     */
    public static int dip2px(Context context, float dipValue){
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale+0.5f);
    }

    /**
     * 将px转换成sp
     * @param context 上下文
     * @param pxValue px值
     * @return sp值
     */
    public static int px2sp(Context context, float pxValue){
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue/scale+0.5f);
    }

    /**
     * sp转px
     * @param context 上下文
     * @param spValue sp值
     * @return px值
     */
    public static int sp2px(Context context, float spValue){
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (spValue * scale+0.5f);
    }
}
