package com.example.android.pnt.whatsapp.Activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.android.pnt.whatsapp.Adapter.ChatAdapter;
import com.example.android.pnt.whatsapp.Model.MessageModel;
import com.example.android.pnt.whatsapp.databinding.ActivityGroupChatBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class GroupChatActivity extends AppCompatActivity {

    ActivityGroupChatBinding binding;
    FirebaseDatabase database;
    List<MessageModel> messageModels;
    ChatAdapter chatAdapter;
    String senderId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGroupChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        init();

        setListener();
    }

    @SuppressLint("SetTextI18n")
    private void init() {
        getSupportActionBar().hide();

        database = FirebaseDatabase.getInstance();
        messageModels = new ArrayList<>();
        chatAdapter = new ChatAdapter(messageModels, getApplicationContext());
        binding.chatRecyclerView.setAdapter(chatAdapter);

        LinearLayoutManager manager = new LinearLayoutManager(this);
        binding.chatRecyclerView.setLayoutManager(manager);

        database.getReference().child("Group Chat")
                .addValueEventListener(new ValueEventListener() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        messageModels.clear();

                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            MessageModel model = dataSnapshot.getValue(MessageModel.class);

                            messageModels.add(model);
                        }

                        chatAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        senderId = FirebaseAuth.getInstance().getUid();

        binding.userName.setText("Group Chat");
    }

    private void setListener() {
        binding.back.setOnClickListener(view -> {
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
        });

        binding.send.setOnClickListener(view -> {
            String message = binding.enterMessage.getText().toString();
            MessageModel model = new MessageModel(senderId, message);
            model.setTimestamp(new Date().getTime());
            binding.enterMessage.setText("");

            database.getReference().child("Group Chat")
                    .push()
                    .setValue(model)
                    .addOnSuccessListener(unused -> {
                        Toast.makeText(this, "Message Send", Toast.LENGTH_LONG).show();
                    });
        });
    }
}