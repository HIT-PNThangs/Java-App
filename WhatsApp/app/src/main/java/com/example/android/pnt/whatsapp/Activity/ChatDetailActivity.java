package com.example.android.pnt.whatsapp.Activity;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.android.pnt.whatsapp.Adapter.ChatAdapter;
import com.example.android.pnt.whatsapp.Model.MessageModel;
import com.example.android.pnt.whatsapp.R;
import com.example.android.pnt.whatsapp.databinding.ActivityChatDetailBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ChatDetailActivity extends AppCompatActivity {
    ActivityChatDetailBinding binding;
    FirebaseDatabase database;
    FirebaseAuth auth;

    String senderId, senderRoom, receiverRoom;
    List<MessageModel> messageModels;
    ChatAdapter chatAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        init();

        setListener();
    }

    private void init() {
        getSupportActionBar().hide();

        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();

        senderId = auth.getUid();

        String receiveId = getIntent().getStringExtra("userId");
        String userName = getIntent().getStringExtra("userName");
        String profilePic = getIntent().getStringExtra("profilePic");

        senderRoom = senderId + receiveId;
        receiverRoom = receiveId + senderId;

        binding.userName.setText(userName);
        Picasso.get().load(profilePic).placeholder(R.drawable.avatar).into(binding.profileImage);

        messageModels = new ArrayList<>();

        database.getReference().child("Chats")
                .child(senderRoom)
                .addValueEventListener(new ValueEventListener() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        messageModels.clear();

                        for (DataSnapshot item : snapshot.getChildren()) {
                            MessageModel model = item.getValue(MessageModel.class);

                            if (model != null) {
                                model.setMessageId(item.getKey());
                                messageModels.add(model);
                            }
                        }

                        chatAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        chatAdapter = new ChatAdapter(messageModels, this, receiveId);

        binding.chatRecyclerView.setAdapter(chatAdapter);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        binding.chatRecyclerView.setLayoutManager(manager);
    }

    private void setListener() {
        binding.back.setOnClickListener(v -> {
            finish();
        });

        binding.send.setOnClickListener(v -> {
            String message = binding.enterMessage.getText().toString();
            MessageModel model = new MessageModel(senderId, message);
            model.setTimestamp(new Date().getTime());
            model.setuId(auth.getUid());

            binding.enterMessage.setText("");

            database.getReference()
                    .child("Chats")
                    .child(senderRoom)
                    .push()
                    .setValue(model)
                    .addOnSuccessListener(unused ->
                            database.getReference()
                                    .child("Chats")
                                    .child(receiverRoom)
                                    .push()
                                    .setValue(model));
        });
    }
}