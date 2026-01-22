package com.txl.blockmoonlighttreasurebox.ui.analyze;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.txl.blockmoonlighttreasurebox.R;
import com.txl.blockmoonlighttreasurebox.info.AnrInfo;
import com.txl.blockmoonlighttreasurebox.info.MessageInfo;
import com.txl.blockmoonlighttreasurebox.ui.AnalyzeProtocol;

/**
 * 分析每一个ANR消息的Activity
 * 展示ANR的详细信息，包括调度信息、消息队列信息、堆栈信息等
 */
public class AnalyzeActivity extends Activity {
    private final AnalyzeSchedulingAdapter analyzeSchedulingAdapter = new AnalyzeSchedulingAdapter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analyze);
        initView();
    }

    /**
     * 初始化视图
     * 设置各个RecyclerView和TextView，显示ANR的详细信息
     */
    private void initView(){
        RecyclerView recyclerMainThreadScheduling = findViewById(R.id.recyclerMainThreadScheduling);
        recyclerMainThreadScheduling.setAdapter(analyzeSchedulingAdapter);
        RecyclerView recyclerViewMessageQueue = findViewById(R.id.recyclerViewMessageQueue);
        AnalyzeMessageDispatchAdapter analyzeMessageDispatchAdapter = new AnalyzeMessageDispatchAdapter();
        recyclerViewMessageQueue.setAdapter(analyzeMessageDispatchAdapter);
        TextView tvNameMessageQueueDispatchItemInfo = findViewById(R.id.tvNameMessageQueueDispatchItemInfo);
        TextView tvNameMessageQueueInfo = findViewById(R.id.tvNameMessageQueueInfo);
        TextView tvNameMainThreadStackInfo = findViewById(R.id.tvNameMainThreadStackInfo);
        AnrInfo anrInfo = AnalyzeProtocol.anrInfo;
        analyzeMessageDispatchAdapter.setOnItemClickListener(new AnalyzeMessageDispatchAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(MessageInfo messageInfo) {
                tvNameMessageQueueDispatchItemInfo.setText(messageInfo.toString());
            }
        });

        tvNameMessageQueueInfo.setText(new String(anrInfo.messageQueueSample));
        tvNameMainThreadStackInfo.setText(anrInfo.mainThreadStack);
        analyzeSchedulingAdapter.scheduledInfos = anrInfo.scheduledSamplerCache.getAll();
        analyzeSchedulingAdapter.notifyDataSetChanged();
        analyzeMessageDispatchAdapter.messageInfos = anrInfo.messageSamplerCache.getAll();
        analyzeMessageDispatchAdapter.notifyDataSetChanged();
    }
}