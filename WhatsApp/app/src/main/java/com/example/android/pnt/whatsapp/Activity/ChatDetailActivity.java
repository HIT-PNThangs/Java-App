package com.example.android.pnt.whatsapp.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.LinearLayout;

import com.example.android.pnt.whatsapp.Adapter.ChatAdapter;
import com.example.android.pnt.whatsapp.Model.MessageModel;
import com.example.android.pnt.whatsapp.R;
import com.example.android.pnt.whatsapp.databinding.ActivityChatDetailBinding;
import com.google.android.gms.tasks.OnSuccessListener;
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

    String senderId;
    String senderRoom;
    String receiverRoom;
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

        String senderId = auth.getUid();
        String receiveId = getIntent().getStringExtra("userId");
        String userName = getIntent().getStringExtra("userName");
        String profilePic = getIntent().getStringExtra("profilePic");

        binding.userName.setText(userName);
        Picasso.get().load(profilePic).placeholder(R.drawable.avatar).into(binding.profileImage);

        messageModels = new ArrayList<>();
        chatAdapter = new ChatAdapter(messageModels, this, receiveId);

        binding.chatRecyclerView.setAdapter(chatAdapter);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        binding.chatRecyclerView.setLayoutManager(manager);

        senderRoom = senderId + receiveId;
        receiverRoom = receiveId + senderId;
    }

    private void setListener() {
        binding.back.setOnClickListener(v -> {
            finish();
        });

        database.getReference().child("Chats")
                .child(senderRoom)
                .addValueEventListener(new ValueEventListener() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        messageModels.clear();
                        for(DataSnapshot item : snapshot.getChildren()) {
                            MessageModel model = item.getValue(MessageModel.class);

                            if(model != null) {
                                model.setMessage(item.getKey());
                                messageModels.add(model);
                            }
                        }

                        chatAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


        binding.send.setOnClickListener(v -> {
            String message = binding.enterMessage.getText().toString();
            MessageModel model = new MessageModel(senderId, message);
            model.setTimestamp(new Date().getTime());
            binding.enterMessage.setText("");

            database.getReference().child("Chats").child(senderRoom).push()
                    .setValue(model).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            database.getReference()
                                    .child("Chats")
                                    .child(receiverRoom)
                                    .push()
                                    .setValue(model);
                        }
                    });
        });
    }
}