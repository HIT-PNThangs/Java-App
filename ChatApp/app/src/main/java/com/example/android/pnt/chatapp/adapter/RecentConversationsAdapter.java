package com.example.android.pnt.chatapp.adapter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android.pnt.chatapp.databinding.ItemContainerReceivedMessageBinding;
import com.example.android.pnt.chatapp.databinding.ItemContainerRecentConversionBinding;
import com.example.android.pnt.chatapp.listener.ConversionListener;
import com.example.android.pnt.chatapp.models.ChatMessage;
import com.example.android.pnt.chatapp.models.User;

import java.util.Base64;
import java.util.List;

public class RecentConversationsAdapter
        extends RecyclerView.Adapter<RecentConversationsAdapter.ConversionViewHolder>
        implements ConversionListener {
    private final List<ChatMessage> chatMessages;
    private final ConversionListener conversionListener;

    public RecentConversationsAdapter(List<ChatMessage> chatMessages, ConversionListener conversionListener) {
        this.chatMessages = chatMessages;
        this.conversionListener = conversionListener;
    }

    @NonNull
    @Override
    public ConversionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ConversionViewHolder(
                ItemContainerRecentConversionBinding.inflate(
                        LayoutInflater.from(parent.getContext()),
                        parent,
                        false
                )
        );
    }

    @Override
    public void onBindViewHolder(@NonNull ConversionViewHolder holder, int position) {
        holder.setData(chatMessages.get(position));
    }

    @Override
    public int getItemCount() {
        return chatMessages.size();
    }

    @Override
    public void onConversionClicked(User user) {

    }

    class ConversionViewHolder extends RecyclerView.ViewHolder {

        ItemContainerRecentConversionBinding binding;

        ConversionViewHolder(ItemContainerRecentConversionBinding itemContainerRecentConversionBinding) {
            super(itemContainerRecentConversionBinding.getRoot());
            binding = itemContainerRecentConversionBinding;
        }

        void setData(ChatMessage chatMessage) {
            binding.imageProfile.setImageBitmap(getConversionImage(chatMessage.getConversionImage()));
            binding.textName.setText(chatMessage.getConversionName());
            binding.textRecentMessage.setText(chatMessage.getMessage());
            binding.getRoot().setOnClickListener(v -> {
                User user = new User();

                user.setId(chatMessage.getConversionId());
                user.setName(chatMessage.getConversionName());
                user.setImage(chatMessage.getConversionImage());

                conversionListener.onConversionClicked(user);
            });
        }
    }

    private Bitmap getConversionImage(String encodeImage) {
        byte[] bytes = Base64.getDecoder().decode(encodeImage);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }
}
