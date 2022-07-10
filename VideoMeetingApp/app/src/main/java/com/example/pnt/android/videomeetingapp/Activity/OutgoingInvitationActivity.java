package com.example.pnt.android.videomeetingapp.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Toast;

import com.example.pnt.android.videomeetingapp.Models.User;
import com.example.pnt.android.videomeetingapp.Network.ApiClient;
import com.example.pnt.android.videomeetingapp.Network.ApiService;
import com.example.pnt.android.videomeetingapp.R;
import com.example.pnt.android.videomeetingapp.Utilities.Constants;
import com.example.pnt.android.videomeetingapp.Utilities.PreferenceManager;
import com.example.pnt.android.videomeetingapp.databinding.ActivityOutgoingInvitationBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.protobuf.Api;

import org.json.JSONArray;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OutgoingInvitationActivity extends AppCompatActivity {
    ActivityOutgoingInvitationBinding binding;

    private PreferenceManager preferenceManager;
    private String inviterToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOutgoingInvitationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        init();

        setListener();
    }

    private void init() {
        String type = getIntent().getStringExtra("type");

        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                inviterToken = task.getResult();
            }
        });

        if (type != null) {
            if (type.equals("video")) {
                binding.imageMeetingType.setImageResource(R.drawable.ic_videocam);
            }
        }

        User user = (User) getIntent().getSerializableExtra("user");
        if (user != null) {
            binding.txtFirstChar.setText(user.getFirstName().substring(0, 1));
            binding.txtUserName.setText(String.format("%s %s", user.getFirstName(), user.getLastName()));
            binding.txtEmail.setText(user.getEmail());
        }

        if(type != null && user != null) {
            initiateMeeting(type, user.getToken());
        }
    }

    private void setListener() {
        binding.imgStopInvitation.setOnClickListener(view -> onBackPressed());
    }

    private void initiateMeeting(String meetingType, String receiverToken) {
        try {
            JSONArray tokens = new JSONArray();
            tokens.put(receiverToken);

            JSONObject body = new JSONObject();
            JSONObject data = new JSONObject();

            data.put(Constants.REMOTE_MSG_TYPE, Constants.REMOTE_MSG_INVITATION);
            data.put(Constants.REMOTE_MSG_MEETING_TYPE, meetingType);
            data.put(Constants.KEY_FIRST_NAME, preferenceManager.getString(Constants.KEY_FIRST_NAME));
            data.put(Constants.KEY_LAST_NAME, preferenceManager.getString(Constants.KEY_LAST_NAME));
            data.put(Constants.KEY_EMAIL, preferenceManager.getString(Constants.KEY_EMAIL));
            data.put(Constants.REMOTE_MSG_INVITER_TOKE, inviterToken);

            body.put(Constants.REMOTE_MSG_DATA, data);
            body.put(Constants.dREMOTE_MSG_REGISTRATION_IDS, tokens);

            sendRemoteMessage(body.toString(), Constants.REMOTE_MSG_INVITATION);

        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private void sendRemoteMessage(String remoteMessage, String type) {
        ApiClient.getClient().create(ApiService.class).sendRemoteMessage(
                        Constants.getRemoteMessageHeaders(), remoteMessage)
                .enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                        if (response.isSuccessful()) {
                            if(type.equals(Constants.REMOTE_MSG_INVITATION)) {
                                Toast.makeText(OutgoingInvitationActivity.this,
                                        "Invitation sent successfully", Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(OutgoingInvitationActivity.this,
                                    response.message(), Toast.LENGTH_LONG).show();
                            finish();
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                        Toast.makeText(OutgoingInvitationActivity.this,
                                t.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }
}