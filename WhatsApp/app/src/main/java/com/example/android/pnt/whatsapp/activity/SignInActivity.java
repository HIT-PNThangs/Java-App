package com.example.android.pnt.whatsapp.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.android.pnt.whatsapp.R;
import com.example.android.pnt.whatsapp.databinding.ActivitySignInBinding;
import com.example.android.pnt.whatsapp.databinding.ActivitySignUpBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class SignInActivity extends AppCompatActivity {

    ActivitySignInBinding binding;
    FirebaseAuth auth;
    FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignInBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        init();

        setListener();
    }

    private void init() {
        getSupportActionBar().hide();

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
    }

    private void showToast(String s) {
        Toast.makeText(this, s, Toast.LENGTH_LONG).show();
    }

    private void setListener() {
        binding.btnSignIn.setOnClickListener(v -> {
            String strEmail = binding.edtEmail.getText().toString();
            String strPass = binding.edtPass.getText().toString();

            if(!strEmail.isEmpty() && !strPass.isEmpty()) {
                binding.progressBar2.setVisibility(View.VISIBLE);

                auth.signInWithEmailAndPassword(strEmail, strPass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        binding.progressBar2.setVisibility(View.GONE);
                    }
                });
            } else {
                showToast("Enter Credentials");
            }
        });
    }
}