package com.example.android.pnt.whatsapp.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.android.pnt.whatsapp.R;
import com.example.android.pnt.whatsapp.databinding.ActivityChatDetailBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

public class ChatDetailActivity extends AppCompatActivity {
    ActivityChatDetailBinding binding;
    FirebaseDatabase database;
    FirebaseAuth auth;

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

        final String senderId = auth.getUid();
        String receiveId = getIntent().getStringExtra("userId");
        String userName = getIntent().getStringExtra("userName");
        String profilePic = getIntent().getStringExtra("profilePic");

        binding.userName.setText(userName);
        Picasso.get().load(profilePic).placeholder(R.drawable.avatar).into(binding.profileImage);
    }

    private void setListener() {
        binding.back.setOnClickListener(v -> {

        });
    }
}