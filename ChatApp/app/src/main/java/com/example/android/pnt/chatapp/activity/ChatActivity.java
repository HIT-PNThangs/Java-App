package com.example.android.pnt.chatapp.activity;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.android.pnt.chatapp.adapter.ChatAdapter;
import com.example.android.pnt.chatapp.databinding.ActivityChatBinding;
import com.example.android.pnt.chatapp.models.ChatMessage;
import com.example.android.pnt.chatapp.models.User;
import com.example.android.pnt.chatapp.network.ApiClient;
import com.example.android.pnt.chatapp.network.ApiServer;
import com.example.android.pnt.chatapp.utilities.Constant;
import com.example.android.pnt.chatapp.utilities.PreferenceManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatActivity extends BaseActivity {

    private ActivityChatBinding binding;
    private User receiverUser;
    private List<ChatMessage> chatMessages;
    private ChatAdapter chatAdapter;
    private PreferenceManager preferenceManager;
    private FirebaseFirestore database;
    private String conversionId = null;
    private Boolean isReceiverAvailable = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot()); 

        loadReceiverUser();

        setListener();

        init();

        listenMessages();
    }

    private void loadReceiverUser() {
        receiverUser = (User) getIntent().getSerializableExtra(Constant.KEY_USER);

        binding.txtNameInChat.setText(receiverUser.getName());
    }

    private void init() {
        preferenceManager = new PreferenceManager(getApplicationContext());
        chatMessages = new ArrayList<>();
        chatAdapter = new ChatAdapter(
                chatMessages,
                getBitmapFromEncodeString(receiverUser.getImage()),
                preferenceManager.getString(Constant.KEY_USER_ID));

        binding.recycleView.setAdapter(chatAdapter);
        database = FirebaseFirestore.getInstance();
    }

    private void setListener() {
        binding.imgBackChat.setOnClickListener(v-> onBackPressed());

        binding.btSendMessage.setOnClickListener(v-> sendMessage());
    }

    @SuppressLint("NotifyDataSetChanged")
    private final EventListener<QuerySnapshot> eventListener = (value, error) -> {
        if(error != null) {
            return;
        }

        if(value != null) {
            int count = chatMessages.size();

            for(DocumentChange documentChange : value.getDocumentChanges()) {
                if(documentChange.getType() == DocumentChange.Type.ADDED) {
                    ChatMessage chatMessage = new ChatMessage();

                    chatMessage.setSenderId(documentChange.getDocument().getString(Constant.KEY_SENDER_ID));
                    chatMessage.setReceiverId(documentChange.getDocument().getString(Constant.KEY_RECEIVER_ID));
                    chatMessage.setMessage(documentChange.getDocument().getString(Constant.KEY_MESSAGE));
                    chatMessage.setDateTime(getReadableDateTime(documentChange.getDocument().getDate(Constant.KEY_TIMESTAMP)));
                    chatMessage.setDateObject(documentChange.getDocument().getDate(Constant.KEY_TIMESTAMP));

                    chatMessages.add(chatMessage);
                }
            }

            Collections.sort(chatMessages, (o1, o2) -> o1.getDateObject().compareTo(o2.getDateObject()));

            if(count == 0) {
                chatAdapter.notifyDataSetChanged();
            } else {
                chatAdapter.notifyItemRangeInserted(chatMessages.size(), chatMessages.size());
                binding.recycleView.smoothScrollToPosition(chatMessages.size() - 1);
            }
        }

        if(conversionId == null) {
            checkForConversion();
        }
    };

    private void listenMessages() {
        database.collection(Constant.KEY_COLLECTION_CHAT).
                whereEqualTo(Constant.KEY_SENDER_ID, preferenceManager.getString(Constant.KEY_USER_ID)).
                whereEqualTo(Constant.KEY_RECEIVER_ID, receiverUser.getId()).
                addSnapshotListener(eventListener);

        database.collection(Constant.KEY_COLLECTION_CHAT).
                whereEqualTo(Constant.KEY_RECEIVER_ID, preferenceManager.getString(Constant.KEY_USER_ID)).
                whereEqualTo(Constant.KEY_SENDER_ID,  receiverUser.getId()).
                addSnapshotListener(eventListener);
    }

    private Bitmap getBitmapFromEncodeString(String encodeImage) {
        if(encodeImage == null) {
            byte[] bytes = Base64.getDecoder().decode(encodeImage);

            return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        } else {
            return null;
        }
    }

    private void sendMessage() {
        HashMap<String, Object> message = new HashMap<>();

        message.put(Constant.KEY_SENDER_ID, preferenceManager.getString(Constant.KEY_USER_ID));
        message.put(Constant.KEY_RECEIVER_ID, receiverUser.getId());
        message.put(Constant.KEY_MESSAGE, binding.inputMessage.getText().toString());
        message.put(Constant.KEY_TIMESTAMP, new Date());

        database.collection(Constant.KEY_COLLECTION_CHAT).add(message);

        if(conversionId != null) {
            updateConversion(binding.inputMessage.getText().toString());
        } else {
            HashMap<String, Object> conversion = new HashMap<>();
            conversion.put(Constant.KEY_SENDER_ID, preferenceManager.getString(Constant.KEY_USER_ID));
            conversion.put(Constant.KEY_SENDER_NAME, preferenceManager.getString(Constant.KEY_NAME));
            conversion.put(Constant.KEY_SENDER_IMAGE, preferenceManager.getString(Constant.KEY_IMAGE));

            conversion.put(Constant.KEY_RECEIVER_ID, receiverUser.getId());
            conversion.put(Constant.KEY_RECEIVER_NAME, receiverUser.getName());
            conversion.put(Constant.KEY_RECEIVER_IMAGE, receiverUser.getImage());

            conversion.put(Constant.KEY_LAST_MESSAGE, binding.inputMessage.getText().toString());
            conversion.put(Constant.KEY_TIMESTAMP, new Date());

            addConversion(conversion);
        }

        if (!isReceiverAvailable) {
            try {
                JSONArray tokens = new JSONArray();
                tokens.put(receiverUser.getToken());

                JSONObject data = new JSONObject();
                data.put(Constant.KEY_USER_ID, preferenceManager.getString(Constant.KEY_USER_ID));
                data.put(Constant.KEY_NAME, preferenceManager.getString(Constant.KEY_NAME));
                data.put(Constant.KEY_FCM_TOKEN, preferenceManager.getString(Constant.KEY_FCM_TOKEN));
                data.put(Constant.KEY_MESSAGE, preferenceManager.getString(Constant.KEY_MESSAGE));

                JSONObject body = new JSONObject();

                body.put(Constant.REMOTE_MSG_DATA, data);
                body.put(Constant.REMOTE_MSG_REGISTRATION_IDS, tokens);

                sendNotification(body.toString());
            } catch (Exception exception) {
                showToast(exception.getMessage());
            }
        }

        binding.inputMessage.setText(null);
    }

    private void sendNotification(String messageBody) {
        ApiClient.getClient().create(ApiServer.class).sendMessage(
                Constant.getRemoteMsgHeaders(),
                messageBody
        ).enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                if(response.isSuccessful()) {
                    try {
                        if(response.body() != null) {
                            JSONObject responseJson = new JSONObject(response.body());
                            JSONArray results = responseJson.getJSONArray("results");

                            if(responseJson.getInt("failure") == 1) {
                                JSONObject error = (JSONObject) results.get(0);

                                showToast(error.getString("error"));

                                return;
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    showToast("Notification sent successfully");
                } else {
                    showToast("Error: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                showToast(t.getMessage());
            }
        });
    }

    private void listenerAvailabilityOfReceiver() {
        database.collection(Constant.KEY_COLLECTION_USERS).document(
                receiverUser.getId()
        ).addSnapshotListener(ChatActivity.this, ((value, error) -> {
            if(error != null) {
                return;
            }

            if(value != null) {
                if(value.getLong(Constant.KEY_AVAILABILITY) != null) {
                    int availability = Objects.requireNonNull(
                            value.getLong(Constant.KEY_AVAILABILITY)
                    ).intValue();

                    isReceiverAvailable = availability == 1;
                }

                receiverUser.setToken(value.getString(Constant.KEY_FCM_TOKEN));

                if(receiverUser.getImage() == null) {
                    receiverUser.setImage(value.getString(Constant.KEY_IMAGE));
                    chatAdapter.setReceiverProfileImage(getBitmapFromEncodeString(receiverUser.getImage()));
                    chatAdapter.notifyItemRangeChanged(0, chatMessages.size());
                }
            }

            if(isReceiverAvailable) {
                binding.view.setVisibility(View.VISIBLE);
            } else {
                binding.view.setVisibility(View.GONE);
            }
        }));
    }

    private String getReadableDateTime(Date date) {
        return new SimpleDateFormat("MMMM dd, yyyy - hh:mm a", Locale.getDefault()).format(date);
    }

    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void addConversion(HashMap<String, Object> conversion) {
        database.collection(Constant.KEY_COLLECTION_CONVERSATIONS).
                add(conversion).
                addOnSuccessListener(documentReference -> conversionId = documentReference.getId());
    }

    private void updateConversion(String message) {
        DocumentReference documentReference =
                database.collection(Constant.KEY_COLLECTION_CONVERSATIONS).document(conversionId);

        documentReference.update(
                Constant.KEY_LAST_MESSAGE, message,
                Constant.KEY_TIMESTAMP, new Date()
        );
    }

    private void checkForConversion() {
        if(chatMessages.size() != 0) {
            checkForConversionRemotely(
                    preferenceManager.getString(Constant.KEY_USER_ID),
                    receiverUser.getId()
            );

            checkForConversionRemotely(
                    receiverUser.getId(),
                    preferenceManager.getString(Constant.KEY_USER_ID)
            );
        }
    }

    private void checkForConversionRemotely(String senderId, String receiverId) {
        database.collection(Constant.KEY_COLLECTION_CONVERSATIONS).
                whereEqualTo(Constant.KEY_SENDER_ID, senderId).
                whereEqualTo(Constant.KEY_RECEIVER_ID, receiverId).
                get().
                addOnCompleteListener(conversionOnCompleteListener);
    }

    private final OnCompleteListener<QuerySnapshot> conversionOnCompleteListener = task -> {
        if(task.isSuccessful() && task.getResult() != null && task.getResult().getDocuments().size() > 0) {
            DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
            conversionId = documentSnapshot.getId();
        }
     };

    @Override
    protected void onResume() {
        super.onResume();
        listenerAvailabilityOfReceiver();
    }
}