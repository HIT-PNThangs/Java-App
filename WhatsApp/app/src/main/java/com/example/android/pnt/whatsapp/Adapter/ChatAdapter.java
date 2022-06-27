package com.example.android.pnt.whatsapp.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android.pnt.whatsapp.Model.MessageModel;
import com.example.android.pnt.whatsapp.R;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter {
    List<MessageModel> messageModels;
    Context context;
    String receiveId;

    final int SENDER_VIEW_TYPE = 1;
    final int RECEIVER_VIEW_TYPE = 2;

    public ChatAdapter() { }

    public ChatAdapter(List<MessageModel> messageModels, Context context) {
        this.messageModels = messageModels;
        this.context = context;
    }

    public ChatAdapter(List<MessageModel> messageModels, Context context, String receiveId) {
        this.messageModels = messageModels;
        this.context = context;
        this.receiveId = receiveId;
    }

    @Override
    public int getItemViewType(int position) {
        if(messageModels.get(position).getuId().equals(FirebaseAuth.getInstance().getUid())) {
            return SENDER_VIEW_TYPE;
        } else {
            return RECEIVER_VIEW_TYPE;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == SENDER_VIEW_TYPE) {
            View view = LayoutInflater.from(context).inflate(R.layout.sample_sender, parent, false);
            return new SenderViewHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.sample_reciver, parent, false);
            return new ReceiverViewHolder(view);
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        MessageModel messageModel = messageModels.get(position);

        if(holder.getClass() == SenderViewHolder.class) {
            ((SenderViewHolder) holder).senderMessage.setText(messageModel.getMessageId());
            ((SenderViewHolder) holder).senderTime.setText(messageModel.getTimestamp().toString());
        } else {
            ((ReceiverViewHolder) holder).receiveMessage.setText(messageModel.getMessageId());
            ((ReceiverViewHolder) holder).receiveTime.setText(messageModel.getTimestamp().toString());
        }
    }

    @Override
    public int getItemCount() {
        return messageModels == null ? 0 : messageModels.size();
    }

    public static class ReceiverViewHolder extends RecyclerView.ViewHolder {
        TextView receiveMessage, receiveTime;

        public ReceiverViewHolder(@NonNull View itemView) {
            super(itemView);

            receiveMessage = itemView.findViewById(R.id.receive_text);
            receiveTime = itemView.findViewById(R.id.receive_time);
        }
    }

    public static class SenderViewHolder extends RecyclerView.ViewHolder {
        TextView senderMessage, senderTime;

        public SenderViewHolder(@NonNull View itemView) {
            super(itemView);

            senderMessage = itemView.findViewById(R.id.sender_text);
            senderTime = itemView.findViewById(R.id.sender_time);
        }
    }
}
