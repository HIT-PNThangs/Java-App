package com.example.pnt.android.videomeetingapp.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.pnt.android.videomeetingapp.R;
import com.example.pnt.android.videomeetingapp.databinding.ActivityIncomingInvitationBinding;

public class IncomingInvitationActivity extends AppCompatActivity {
    ActivityIncomingInvitationBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityIncomingInvitationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }
}