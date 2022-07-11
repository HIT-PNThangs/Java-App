package com.example.android.pnt.whatsapp.Adapter;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
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
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter {
    List<MessageModel> messageModels;
    Context context;
    String receiveId;

    final int SENDER_VIEW_TYPE = 1;
    final int RECEIVER_VIEW_TYPE = 2;

    public ChatAdapter() {
    }

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
        if (messageModels.get(position).getuId().equals(FirebaseAuth.getInstance().getUid())) {
            return SENDER_VIEW_TYPE;
        } else {
            return RECEIVER_VIEW_TYPE;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == SENDER_VIEW_TYPE) {
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

        holder.itemView.setOnLongClickListener(view -> {
            new AlertDialog.Builder(context)
                    .setTitle("Delete")
                    .setMessage("Are you sure want to delete this message?")
                    .setPositiveButton("Yes", (dialogInterface, i) -> {
                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                        String senderRoom = FirebaseAuth.getInstance().getUid() + receiveId;

                        database.getReference().child("Chats").child(senderRoom)
                                .child(messageModel.getMessageId())
                                .setValue(null);
                    })
                    .setNegativeButton("No", (dialogInterface, i) -> dialogInterface.dismiss())
                    .show();

            return false;
        });

        if (holder.getClass() == SenderViewHolder.class) {
            ((SenderViewHolder) holder).senderMessage.setText(messageModel.getMessage());

            Date date = new Date(messageModel.getTimestamp());
            @SuppressLint("SimpleDateFormat")
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("h:mm a");

            String strDate = simpleDateFormat.format(date);
            ((SenderViewHolder) holder).senderTime.setText(strDate);

        } else {
            ((ReceiverViewHolder) holder).receiveMessage.setText(messageModel.getMessage());

            Date date = new Date(messageModel.getTimestamp());
            @SuppressLint("SimpleDateFormat")
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("h:mm a");

            String strDate = simpleDateFormat.format(date);
            ((ReceiverViewHolder) holder).receiveTime.setText(strDate);
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
