package com.example.android.pnt.cloneinstagram;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.LayoutInflater;

import com.example.android.pnt.cloneinstagram.databinding.ActivityPostBinding;

public class PostActivity extends AppCompatActivity {

    ActivityPostBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
    }
}