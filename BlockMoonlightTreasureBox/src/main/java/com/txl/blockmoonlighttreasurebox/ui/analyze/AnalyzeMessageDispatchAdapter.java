package com.txl.blockmoonlighttreasurebox.ui.analyze;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.txl.blockmoonlighttreasurebox.R;
import com.txl.blockmoonlighttreasurebox.info.MessageInfo;

import java.util.List;

/**
 * 消息分发分析Adapter
 * 用于显示ANR时的消息分发信息列表
 */
public class AnalyzeMessageDispatchAdapter extends RecyclerView.Adapter<AnalyzeMessageQueueDispatchViewHolder> {
    List<MessageInfo> messageInfos;
    private OnItemClickListener onItemClickListener;

    /**
     * 设置item点击监听器
     * @param onItemClickListener 点击监听器
     */
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public AnalyzeMessageQueueDispatchViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new AnalyzeMessageQueueDispatchViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_analyze_message_dispatch,viewGroup,false));
    }

    @Override
    public void onBindViewHolder(@NonNull AnalyzeMessageQueueDispatchViewHolder analyzeMessageQueueDispatchViewHolder, int i) {
        final int index = i;
        analyzeMessageQueueDispatchViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onItemClickListener != null){
                    onItemClickListener.onItemClick(messageInfos.get(index));
                }
            }
        });
        analyzeMessageQueueDispatchViewHolder.parse(messageInfos.get(index));
    }

    @Override
    public int getItemCount() {
        return messageInfos == null ? 0 : messageInfos.size();
    }

    /**
     * item点击监听器接口
     */
    public interface OnItemClickListener{
        /**
         * item点击回调
         * @param messageInfo 点击的消息信息
         */
        void onItemClick(MessageInfo messageInfo);
    }
}
