package com.example.android.pnt.whatsapp.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.android.pnt.whatsapp.Model.Users;
import com.example.android.pnt.whatsapp.R;
import com.example.android.pnt.whatsapp.databinding.ActivitySignUpBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpActivity extends AppCompatActivity {

    ActivitySignUpBinding binding;
    FirebaseAuth auth;
    FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
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
            String strEmail = binding.edtEmail.getText().toString().trim();
            String strUserName = binding.edtUsername.getText().toString().trim();
            String strPass = binding.edtPass.getText().toString().trim();
            String strRePass = binding.edtRePass.getText().toString().trim();

            if(!strEmail.isEmpty() && !strUserName.isEmpty() && !strPass.isEmpty() && !strRePass.isEmpty()) {
                binding.progressBar.setVisibility(View.VISIBLE);

                auth.createUserWithEmailAndPassword(strEmail, strPass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {
                            binding.progressBar.setVisibility(View.GONE);

                            Users users = new Users(strUserName, strEmail, strPass);
                            String id = task.getResult().getUser().getUid();

                            database.getReference().child("Users").child(id).setValue(users);

                            showToast("Sign Up Success");
                        } else {
                            binding.progressBar.setVisibility(View.GONE);

                            showToast(task.getException().toString());
                        }
                    }
                });
            } else {
                showToast("Enter Credentials");
            }
        });
    }

    private void signUp() {

    }
}