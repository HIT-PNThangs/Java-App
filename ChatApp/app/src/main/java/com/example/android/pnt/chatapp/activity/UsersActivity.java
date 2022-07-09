package com.example.android.pnt.chatapp.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.android.pnt.chatapp.adapter.UserAdapter;
import com.example.android.pnt.chatapp.databinding.ActivityUsersBinding;
import com.example.android.pnt.chatapp.listener.UserListener;
import com.example.android.pnt.chatapp.models.User;
import com.example.android.pnt.chatapp.utilities.Constant;
import com.example.android.pnt.chatapp.utilities.PreferenceManager;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class UsersActivity extends BaseActivity implements UserListener {

    ActivityUsersBinding binding;
    private ProgressDialog dialog;
    PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUsersBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        dialog = new ProgressDialog(UsersActivity.this);

        preferenceManager = new PreferenceManager(getApplicationContext());

        getUser();

        binding.imageBack.setOnClickListener(v -> onBackPressed());
    }

    private void getUser() {
        dialog.show();
        FirebaseFirestore database = FirebaseFirestore.getInstance();

        database.collection(Constant.KEY_COLLECTION_USERS)
                .get()
                .addOnCompleteListener(task -> {
                    dialog.dismiss();
                    String currentUserId = preferenceManager.getString(Constant.KEY_USER_ID);

                    if (task.isSuccessful() && task.getResult() != null) {
                        List<User> users = new ArrayList<>();

                        for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                            if (currentUserId.equals(documentSnapshot.getId())) {
                                continue;
                            }

                            User user = new User();

                            user.setName(documentSnapshot.getString(Constant.KEY_NAME));
                            user.setEmail(documentSnapshot.getString(Constant.KEY_EMAIL));
                            user.setImage(documentSnapshot.getString(Constant.KEY_IMAGE));
                            user.setToken(documentSnapshot.getString(Constant.KEY_FCM_TOKEN));
                            user.setId(documentSnapshot.getId());

                            users.add(user);
                        }

                        if (users.size() > 0) {
                            UserAdapter adapter = new UserAdapter(users, this);

                            binding.userRecyclerVew.setAdapter(adapter);
                            binding.userRecyclerVew.setVisibility(View.VISIBLE);
                        } else {
                            dialog.dismiss();
                            showErrorMessage();
                        }
                    } else {
                        dialog.dismiss();
                        showErrorMessage();
                    }
                });
    }

    private void showErrorMessage() {
        binding.textError.setText(String.format("%s", "Bo user available"));
        binding.textError.setVisibility(View.VISIBLE);
    }

    @Override
    public void onUserClicked(User user) {
        Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
        intent.putExtra(Constant.KEY_USER, user);
        startActivity(intent);
        finish();
    }
}