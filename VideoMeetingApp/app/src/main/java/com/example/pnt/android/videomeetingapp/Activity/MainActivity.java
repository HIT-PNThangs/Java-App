package com.example.pnt.android.videomeetingapp.Activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.pnt.android.videomeetingapp.Adapter.UserAdapter;
import com.example.pnt.android.videomeetingapp.Listener.UserListener;
import com.example.pnt.android.videomeetingapp.Models.User;
import com.example.pnt.android.videomeetingapp.Utilities.Constants;
import com.example.pnt.android.videomeetingapp.Utilities.PreferenceManager;
import com.example.pnt.android.videomeetingapp.databinding.ActivityMainBinding;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity implements UserListener {
    ActivityMainBinding binding;
    FirebaseFirestore database;

    private PreferenceManager preferenceManager;
    private List<User> users;
    private UserAdapter userAdapter;

    private final int REQUEST_CODE_BATTERY_OPTIMIZATIONS = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        init();

        setListener();
    }

    private void init() {
        preferenceManager = new PreferenceManager(getApplicationContext());

        database = FirebaseFirestore.getInstance();

        binding.textTitle.setText(
                String.format("%s %s",
                        preferenceManager.getString(Constants.KEY_FIRST_NAME),
                        preferenceManager.getString(Constants.KEY_LAST_NAME)
                )
        );

        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                sendFCMTokenToDatabase(task.getResult());
            }
        });

        users = new ArrayList<>();
        userAdapter = new UserAdapter(users, this);

        binding.userRecyclerView.setAdapter(userAdapter);

        binding.swipeRefresh.setOnRefreshListener(this::getAllUsers);

        getAllUsers();
        checkForBatteryOptimizations();
    }

    private void setListener() {
        binding.signOut.setOnClickListener(view -> signOut());
    }

    private void sendFCMTokenToDatabase(String token) {
        DocumentReference documentReference =
                database.collection(Constants.KEY_COLLECTION_USERS).document(preferenceManager.getString(Constants.KEY_USER_ID));

        documentReference.update(Constants.KEY_FCM_TOKEN, token)
                .addOnFailureListener(e ->
                        Toast.makeText(getApplicationContext(), "Unable to send token: " + e.getMessage(), Toast.LENGTH_LONG).show());
    }

    private void signOut() {
        Toast.makeText(getApplicationContext(), "Signing Out ...", Toast.LENGTH_LONG).show();

        DocumentReference documentReference =
                database.collection(Constants.KEY_COLLECTION_USERS).document(
                        preferenceManager.getString(Constants.KEY_USER_ID)
                );

        HashMap<String, Object> update = new HashMap<>();
        update.put(Constants.KEY_FCM_TOKEN, FieldValue.delete());
        documentReference.update(update)
                .addOnSuccessListener(unused -> {
                    preferenceManager.clearPreference();
                    startActivity(new Intent(getApplicationContext(), SignInActivity.class));
                    finish();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(getApplicationContext(), "Unable to sign up", Toast.LENGTH_LONG).show());
    }

    @SuppressLint("NotifyDataSetChanged")
    private void getAllUsers() {
        binding.swipeRefresh.setRefreshing(true);

        database.collection(Constants.KEY_COLLECTION_USERS)
                .get()
                .addOnCompleteListener(task -> {
                    binding.swipeRefresh.setRefreshing(false);

                    String myUserId = preferenceManager.getString(Constants.KEY_USER_ID);

                    if (task.isSuccessful() && task.getResult() != null) {
                        users.clear();

                        for (QueryDocumentSnapshot snapshot : task.getResult()) {
                            if (myUserId.equals(snapshot.getId())) {
                                continue;
                            }

                            User user = new User();
                            user.setFirstName(snapshot.getString(Constants.KEY_FIRST_NAME));
                            user.setLastName(snapshot.getString(Constants.KEY_LAST_NAME));
                            user.setEmail(snapshot.getString(Constants.KEY_EMAIL));
                            user.setToken(snapshot.getString(Constants.KEY_FCM_TOKEN));

                            users.add(user);
                        }

                        if (users.size() > 0) {
                            userAdapter.notifyDataSetChanged();
                        } else {
                            binding.txtErrorMessage.setText(String.format("%s", "No users available"));
                            binding.txtErrorMessage.setVisibility(View.VISIBLE);
                        }
                    } else {
                        binding.txtErrorMessage.setText(String.format("%s", "No users available"));
                        binding.txtErrorMessage.setVisibility(View.VISIBLE);
                    }
                });
    }

    @Override
    public void initiateVideoMeeting(User user) {
        if (user.getToken() == null || user.getToken().trim().isEmpty()) {
            Toast.makeText(getApplicationContext(),
                    user.getFirstName() + " " + user.getLastName() + " is not available for meeting",
                    Toast.LENGTH_LONG).show();
        } else {
            Intent intent = new Intent(getApplicationContext(), OutgoingInvitationActivity.class);

            intent.putExtra("user", user);
            intent.putExtra("type", "video");

            startActivity(intent);
        }
    }

    @Override
    public void initiateAudioMeeting(User user) {
        if (user.getToken() == null || user.getToken().trim().isEmpty()) {
            Toast.makeText(getApplicationContext(),
                    user.getFirstName() + " " + user.getLastName() + " is not available for audio",
                    Toast.LENGTH_LONG).show();
        } else {
            Intent intent = new Intent(getApplicationContext(), OutgoingInvitationActivity.class);

            intent.putExtra("user", user);
            intent.putExtra("type", "audio");

            startActivity(intent);
        }
    }

    @Override
    public void onMultipleUsersAction(Boolean isMultipleUsersSelected) {
        if (isMultipleUsersSelected) {
            binding.imgConference.setVisibility(View.VISIBLE);
            binding.imgConference.setOnClickListener(view -> {
                Intent intent = new Intent(getApplicationContext(), OutgoingInvitationActivity.class);

                intent.putExtra("selectedUsers", new Gson().toJson(userAdapter.getSelectedUsers()));
                intent.putExtra("type", "video");
                intent.putExtra("isMultiple", true);

                startActivity(intent);
            });
        } else {
            binding.imgConference.setVisibility(View.GONE);
        }
    }

    private void checkForBatteryOptimizations() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
            if (!powerManager.isIgnoringBatteryOptimizations(getPackageName())) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

                builder.setTitle("Warning");
                builder.setTitle("Battery optimization is enable. It can interrupt running background services.");
                builder.setPositiveButton("Disable", (dialogInterface, i) -> {
                    Intent intent = new Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS);
                    startActivityForResult(intent, REQUEST_CODE_BATTERY_OPTIMIZATIONS);
                });

                builder.setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.dismiss());

                builder.create();
                builder.show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_BATTERY_OPTIMIZATIONS) {
            checkForBatteryOptimizations();
        }
    }
}