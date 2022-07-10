package com.example.pnt.android.videomeetingapp.Activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.pnt.android.videomeetingapp.Models.User;
import com.example.pnt.android.videomeetingapp.Network.ApiClient;
import com.example.pnt.android.videomeetingapp.Network.ApiService;
import com.example.pnt.android.videomeetingapp.R;
import com.example.pnt.android.videomeetingapp.Utilities.Constants;
import com.example.pnt.android.videomeetingapp.Utilities.PreferenceManager;
import com.example.pnt.android.videomeetingapp.databinding.ActivityOutgoingInvitationBinding;
import com.google.common.reflect.TypeToken;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;

import org.jitsi.meet.sdk.JitsiMeetActivity;
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions;
import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.net.URL;
import java.util.List;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OutgoingInvitationActivity extends AppCompatActivity {
    ActivityOutgoingInvitationBinding binding;

    private PreferenceManager preferenceManager;
    private String inviterToken;
    private String meetingRoom;
    private String type;

    private int rejectionCount = 0;
    private int totalReceivers = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOutgoingInvitationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        init();
    }

    private void init() {
        type = getIntent().getStringExtra("type");

        preferenceManager = new PreferenceManager(this);

        if (type != null) {
            if (type.equals("video")) {
                binding.imageMeetingType.setImageResource(R.drawable.ic_videocam);
            } else if (type.equals("audio")) {
                binding.imageMeetingType.setImageResource(R.drawable.ic_call);
            }
        }

        User user = (User) getIntent().getSerializableExtra("user");
        if (user != null) {
            binding.txtFirstChar.setText(user.getFirstName().substring(0, 1));
            binding.txtUserName.setText(String.format("%s %s", user.getFirstName(), user.getLastName()));
            binding.txtEmail.setText(user.getEmail());
        }

        binding.imgStopInvitation.setOnClickListener(view -> {
            if (getIntent().getBooleanExtra("isMultiple", false)) {
                Type type1 = new TypeToken<List<User>>() {
                }.getType();

                List<User> receivers = new Gson().fromJson(getIntent().getStringExtra("selectedUsers"), type1);
                cancelInvitation(null, receivers);
            } else {
                if (user != null) {
                    cancelInvitation(user.getToken(), null);
                }
            }
        });

        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                inviterToken = task.getResult();

                if (type != null) {
                    if (getIntent().getBooleanExtra("isMultiple", false)) {
                        Type type1 = new TypeToken<List<User>>() {
                        }.getType();

                        List<User> receivers = new Gson().fromJson(getIntent().getStringExtra("selectedUsers"), type1);

                        if(receivers != null) {
                            totalReceivers = receivers.size();
                        }

                        initiateMeeting(type, null, receivers);
                    }
                } else {
                    if (user != null) {
                        totalReceivers = 1;
                        initiateMeeting(type, user.getToken(), null);
                    }
                }
            }
        });
    }

    private void initiateMeeting(String meetingType, String receiverToken, List<User> receivers) {
        try {
            JSONArray tokens = new JSONArray();

            if (receiverToken != null) {
                tokens.put(receiverToken);
            }

            if (receivers != null && receivers.size() > 0) {
                StringBuilder userNames = new StringBuilder();

                for (User item : receivers) {
                    tokens.put(item.getToken());
                    userNames.append(item.getFirstName()).append(" ").append(item.getLastName()).append("\n");
                }
                binding.txtFirstChar.setVisibility(View.GONE);
                binding.txtEmail.setVisibility(View.GONE);
                binding.txtUserName.setText(userNames.toString());
            }

            JSONObject body = new JSONObject();
            JSONObject data = new JSONObject();

            data.put(Constants.REMOTE_MSG_TYPE, Constants.REMOTE_MSG_INVITATION);
            data.put(Constants.REMOTE_MSG_MEETING_TYPE, meetingType);
            data.put(Constants.KEY_FIRST_NAME, preferenceManager.getString(Constants.KEY_FIRST_NAME));
            data.put(Constants.KEY_LAST_NAME, preferenceManager.getString(Constants.KEY_LAST_NAME));
            data.put(Constants.KEY_EMAIL, preferenceManager.getString(Constants.KEY_EMAIL));
            data.put(Constants.REMOTE_MSG_INVITER_TOKEN, inviterToken);

            meetingRoom =
                    preferenceManager.getString(Constants.KEY_USER_ID) + "_"
                            + UUID.randomUUID().toString().substring(0, 5);

            data.put(Constants.REMOTE_MSG_MEETING_ROOM, meetingRoom);

            body.put(Constants.REMOTE_MSG_DATA, data);
            body.put(Constants.REMOTE_MSG_REGISTRATION_IDS, tokens);

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
                            if (type.equals(Constants.REMOTE_MSG_INVITATION)) {
                                Toast.makeText(OutgoingInvitationActivity.this,
                                        "Invitation sent successfully", Toast.LENGTH_LONG).show();
                            } else if (type.equals(Constants.REMOTE_MSG_INVITATION_RESPONSE)) {
                                Toast.makeText(OutgoingInvitationActivity.this,
                                        "Invitation Cancelled", Toast.LENGTH_LONG).show();
                                finish();
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

                        finish();
                    }
                });
    }

    private void cancelInvitation(String receiverToken, List<User> receivers) {
        try {
            JSONArray tokens = new JSONArray();

            if(receiverToken != null) {
                tokens.put(receiverToken);
            }

            if(receivers != null && receivers.size() > 0) {
                for(User user : receivers) tokens.put(user.getToken());
            }

            JSONObject body = new JSONObject();
            JSONObject data = new JSONObject();

            data.put(Constants.REMOTE_MSG_TYPE, Constants.REMOTE_MSG_INVITATION_RESPONSE);
            data.put(Constants.REMOTE_MSG_INVITATION_RESPONSE, Constants.REMOTE_MSG_INVITATION_CANCELLED);

            body.put(Constants.REMOTE_MSG_DATA, data);
            body.put(Constants.REMOTE_MSG_REGISTRATION_IDS, tokens);

            sendRemoteMessage(body.toString(), Constants.REMOTE_MSG_INVITATION_RESPONSE);
        } catch (Exception exception) {
            Toast.makeText(getApplicationContext(), exception.getMessage(), Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private final BroadcastReceiver invitationResponseReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String type = intent.getStringExtra(Constants.REMOTE_MSG_INVITATION_RESPONSE);

            if (type != null) {
                if (type.equals(Constants.REMOTE_MSG_INVITATION_ACCEPTED)) {
                    try {
                        URL serverURL = new URL("https://meet.jit.si");

                        JitsiMeetConferenceOptions.Builder builder = new JitsiMeetConferenceOptions.Builder();
                        builder.setServerURL(serverURL);
                        builder.setWelcomePageEnabled(false);
                        builder.setRoom(meetingRoom);

                        if (type.equals("audio")) {
                            builder.setVideoMuted(true);
                        }

                        JitsiMeetActivity.launch(OutgoingInvitationActivity.this, builder.build());
                        finish();
                    } catch (Exception exception) {
                        Toast.makeText(OutgoingInvitationActivity.this,
                                exception.getMessage(), Toast.LENGTH_LONG).show();

                        finish();
                    }
                } else if (type.equals(Constants.REMOTE_MSG_INVITATION_REJECTED)) {
                    rejectionCount += 1;

                    if(rejectionCount == totalReceivers) {
                        Toast.makeText(OutgoingInvitationActivity.this,
                                "Invitation Rejected", Toast.LENGTH_LONG).show();
                        finish();
                    }

                }
            }
        }
    };

    @Override
    protected void onStart() {
        super.onStart();

        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(
                invitationResponseReceiver,
                new IntentFilter(Constants.REMOTE_MSG_INVITATION_RESPONSE)
        );
    }

    @Override
    protected void onStop() {
        super.onStop();

        LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(
                invitationResponseReceiver
        );
    }
}