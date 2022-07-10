package com.example.pnt.android.videomeetingapp.Activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.pnt.android.videomeetingapp.Network.ApiClient;
import com.example.pnt.android.videomeetingapp.Network.ApiService;
import com.example.pnt.android.videomeetingapp.R;
import com.example.pnt.android.videomeetingapp.Utilities.Constants;
import com.example.pnt.android.videomeetingapp.databinding.ActivityIncomingInvitationBinding;

import org.jitsi.meet.sdk.JitsiMeetActivity;
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URL;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class IncomingInvitationActivity extends AppCompatActivity {
    ActivityIncomingInvitationBinding binding;
    String type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityIncomingInvitationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        init();

        setListener();
    }

    private void init() {
        type = getIntent().getStringExtra(Constants.REMOTE_MSG_MEETING_TYPE);

        if (type != null) {
            if (type.equals("video")) {
                binding.imageMeetingType.setImageResource(R.drawable.ic_videocam);
            } else if (type.equals("audio")) {
                binding.imageMeetingType.setImageResource(R.drawable.ic_call);
            }
        }

        String strFirstName = getIntent().getStringExtra(Constants.KEY_FIRST_NAME);
        if (strFirstName != null) {
            binding.txtFirstChar.setText(strFirstName.substring(0, 1));
        }

        binding.txtUserName.setText(
                String.format(
                        "%s %s",
                        strFirstName,
                        getIntent().getStringExtra(Constants.KEY_LAST_NAME)
                )
        );

        binding.txtEmail.setText(getIntent().getStringExtra(Constants.KEY_EMAIL));
    }

    private void setListener() {
        binding.imgAcceptInvitation.setOnClickListener(view -> sendInvitationResponse(
                Constants.REMOTE_MSG_INVITATION_ACCEPTED,
                getIntent().getStringExtra(Constants.REMOTE_MSG_INVITER_TOKEN)
        ));

        binding.imgStopInvitation.setOnClickListener(view -> sendInvitationResponse(
                Constants.REMOTE_MSG_INVITATION_REJECTED,
                getIntent().getStringExtra(Constants.REMOTE_MSG_INVITER_TOKEN)
        ));
    }

    private void sendInvitationResponse(String type, String receiverToken) {
        try {
            JSONArray tokens = new JSONArray();
            tokens.put(receiverToken);

            JSONObject body = new JSONObject();
            JSONObject data = new JSONObject();

            data.put(Constants.REMOTE_MSG_TYPE, Constants.REMOTE_MSG_INVITATION_RESPONSE);
            data.put(Constants.REMOTE_MSG_INVITATION_RESPONSE, type);

            body.put(Constants.REMOTE_MSG_DATA, data);
            body.put(Constants.REMOTE_MSG_REGISTRATION_IDS, tokens);

            sendRemoteMessage(body.toString(), type);
        } catch (Exception exception) {
            Toast.makeText(getApplicationContext(), exception.getMessage(), Toast.LENGTH_LONG).show();
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
                            if (type.equals(Constants.REMOTE_MSG_INVITATION_ACCEPTED)) {

                                try {
                                    URL serverURL = new URL("https://meet.jit.si");

                                    JitsiMeetConferenceOptions.Builder builder = new JitsiMeetConferenceOptions.Builder();
                                    builder.setServerURL(serverURL);
                                    builder.setWelcomePageEnabled(false);
                                    builder.setRoom(getIntent().getStringExtra(Constants.REMOTE_MSG_MEETING_ROOM));

                                    if (type.equals("audio")) {
                                        builder.setVideoMuted(true);
                                    }

                                    JitsiMeetActivity.launch(IncomingInvitationActivity.this, builder.build());
                                    finish();
                                } catch (Exception exception) {
                                    Toast.makeText(getApplicationContext(),
                                            exception.getMessage(), Toast.LENGTH_LONG).show();
                                }

                            } else {
                                Toast.makeText(getApplicationContext(),
                                        "Invitation Rejected", Toast.LENGTH_LONG).show();

                                finish();
                            }
                        } else {
                            Toast.makeText(getApplicationContext(),
                                    response.message(), Toast.LENGTH_LONG).show();

                            finish();
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                        Toast.makeText(getApplicationContext(),
                                t.getMessage(), Toast.LENGTH_LONG).show();

                        finish();
                    }
                });
    }

    private final BroadcastReceiver invitationResponseReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String type = intent.getStringExtra(Constants.REMOTE_MSG_INVITATION_RESPONSE);

            if (type != null) {
                if (type.equals(Constants.REMOTE_MSG_INVITATION_CANCELLED)) {
                    Toast.makeText(getApplicationContext(),
                            "Invitation Cancelled", Toast.LENGTH_LONG).show();

                    finish();
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