package com.example.android.pnt.whatsapp.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import com.example.android.pnt.whatsapp.Model.Users;
import com.example.android.pnt.whatsapp.R;
import com.example.android.pnt.whatsapp.databinding.ActivitySettingBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Objects;

public class SettingActivity extends AppCompatActivity {
    ActivitySettingBinding binding;
    FirebaseAuth auth;
    FirebaseDatabase database;
    FirebaseStorage storage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySettingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        init();

        setListener();
    }

    private void init() {
        getSupportActionBar().hide();

        storage = FirebaseStorage.getInstance();
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        database.getReference().child("Users")
                .child(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()))
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Users users = snapshot.getValue(Users.class);

                        Picasso.get()
                                .load(Objects.requireNonNull(users)
                                .getProfilePic())
                                .placeholder(R.drawable.avatar)
                                .into(binding.profileImage);

                        binding.txtUserName.setText(users.getUserName());
                        binding.edStatus.setText(users.getStatus());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void setListener() {
        binding.backArrow.setOnClickListener(view -> {
            startActivity(new Intent(SettingActivity.this, MainActivity.class));
        });

        binding.plus.setOnClickListener(view -> {
            Intent intent = new Intent();

            intent.setAction(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");

            startActivityForResult(intent, 25);
        });

        binding.save.setOnClickListener(view -> {
            String status = binding.edStatus.getText().toString().trim();
            String userName = binding.txtUserName.getText().toString().trim();

            if(!status.equals("") && !userName.equals("")) {
                HashMap<String, Object> map = new HashMap<>();
                map.put("userName", userName);
                map.put("status", status);

                database.getReference()
                        .child("Users")
                        .child(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()))
                        .updateChildren(map);

                Toast.makeText(SettingActivity.this, "Profile update", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(SettingActivity.this, "Please Enter Fully Information", Toast.LENGTH_LONG).show();

            }


        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(data.getData() != null) {
            Uri sFile = data.getData();
            binding.profileImage.setImageURI(sFile);

            StorageReference reference = storage.getReference().child("profile_pic")
                    .child(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()));

            reference.putFile(sFile).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            database.getReference().child("Users")
                                    .child(FirebaseAuth.getInstance().getUid())
                                    .child("profilePic")
                                    .setValue(uri.toString());
                        }
                    });
                }
            });
        }
    }
}