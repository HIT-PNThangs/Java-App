package com.example.android.pnt.cloneinstagram;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import com.example.android.pnt.cloneinstagram.databinding.ActivityRegisterBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {
    ActivityRegisterBinding binding;
    ProgressDialog progressDialog;

    private DatabaseReference mRootRef;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        init();

       setListener();
    }

    private void init() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        mRootRef = database.getReference("Users");

        mAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);
    }

    private void setListener() {
        binding.register.setOnClickListener(v -> register());

        binding.loginUser.setOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
        });
    }

    private void register() {
        String strUserName = binding.username.getText().toString();
        String strName = binding.name.getText().toString();
        String strEmail = binding.email.getText().toString();
        String strPassword = binding.password.getText().toString();

        if(TextUtils.isEmpty(strUserName) || TextUtils.isEmpty(strName)
                || TextUtils.isEmpty(strEmail) || TextUtils.isEmpty(strPassword)) {
            Toast.makeText(getApplicationContext(), "Empty credentials", Toast.LENGTH_SHORT).show();
        } else if(strPassword.length() < 6) {
            Toast.makeText(this, "Password too short!", Toast.LENGTH_SHORT).show();
        } else {
            registerUser(strUserName, strName, strEmail, strPassword);
        }
    }

    private void registerUser(String userName, String name, String email, String password) {
        progressDialog.show();

        mAuth.createUserWithEmailAndPassword(email, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                HashMap<String, Object> map = new HashMap<>();

                map.put("Name", name);
                map.put("Email", email);
                map.put("UserName", userName);
                map.put("Id", mAuth.getCurrentUser().getUid());

                mRootRef.child(mAuth.getCurrentUser().getUid()).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()) {
                            progressDialog.dismiss();

                            startActivity(new Intent(getApplicationContext(), MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
                            finish();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();

                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}