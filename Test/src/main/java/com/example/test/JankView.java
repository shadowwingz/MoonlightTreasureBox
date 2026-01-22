package com.example.test;

import android.content.Context;
import android.graphics.Canvas;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.Log;

import androidx.annotation.Nullable;

/**
 * UI卡顿测试View
 * 继承自AppCompatTextView，用于测试和模拟UI渲染卡顿场景
 * 通过在draw方法中人为插入延迟来触发帧率下降
 */
public class JankView extends androidx.appcompat.widget.AppCompatTextView {

    // 是否启用卡顿的标志位
    private boolean jank;

    // 卡顿次数计数器
    private int jankCount;

    /**
     * 构造函数
     * @param context 上下文
     */
    public JankView(Context context) {
        super(context);
    }

    /**
     * 构造函数
     * @param context 上下文
     * @param attrs XML属性集
     */
    public JankView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * 构造函数
     * @param context 上下文
     * @param attrs XML属性集
     * @param defStyleAttr 默认样式属性
     */
    public JankView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * 绘制视图的方法
     * 当jank为true时，会在绘制过程中休眠500ms导致严重卡顿
     * 正常帧率为16ms一帧，500ms会导致约30帧的丢失
     * @param canvas 画布
     */
    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        long start = SystemClock.elapsedRealtime();
        if(jank){
            // 休眠500ms，远超16ms的正常帧时间，会导致明显卡顿
            SystemClock.sleep(500);
        }
        long end = SystemClock.elapsedRealtime();
        Log.d("JankView","BlockMonitor start "+start + "  end "+end + "   "+(end - start));
    }

    /**
     * 设置是否启用卡顿
     * 每次调用会增加计数并更新显示文本
     * @param jank true为启用卡顿，false为正常绘制
     */
    public void setJank(boolean jank) {
        this.jank = jank;
        setText("jankCount "+jankCount++);
        requestLayout(); // 触发布局重新计算和重绘
    }
}
