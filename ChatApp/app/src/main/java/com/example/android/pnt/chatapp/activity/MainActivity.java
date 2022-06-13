package com.example.android.pnt.chatapp.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.android.pnt.chatapp.adapter.RecentConversationsAdapter;
import com.example.android.pnt.chatapp.databinding.ActivityMainBinding;
import com.example.android.pnt.chatapp.listener.ConversionListener;
import com.example.android.pnt.chatapp.models.ChatMessage;
import com.example.android.pnt.chatapp.models.User;
import com.example.android.pnt.chatapp.utilities.Constant;
import com.example.android.pnt.chatapp.utilities.PreferenceManager;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.Constants;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends BaseActivity implements ConversionListener {

    private ActivityMainBinding binding;
    private PreferenceManager preferenceManager;
    private List<ChatMessage> conversations;
    private RecentConversationsAdapter conversationsAdapter;
    private FirebaseFirestore database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        preferenceManager = new PreferenceManager(getApplicationContext());

        init();

        loadUserDetails();

        getToken();

        setListener();

        listenConversations();
    }

    private void setListener() {
        binding.imgSignOut.setOnClickListener(v -> signOut());

        binding.fabNewChat.setOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(), UsersActivity.class));
        });
    }

    private void init() {
        conversations = new ArrayList<>();
        conversationsAdapter = new RecentConversationsAdapter(conversations, this);
        binding.conversationsRecyclerView.setAdapter(conversationsAdapter);

        database = FirebaseFirestore.getInstance();
    }

    private void loadUserDetails() {
        binding.txtName.setText(preferenceManager.getString(Constant.KEY_NAME));

        byte[] bytes = Base64.getDecoder().decode(preferenceManager.getString(Constant.KEY_IMAGE));
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        binding.imgProfile.setImageBitmap(bitmap);
    }

    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void listenConversations() {
        database.collection(Constant.KEY_COLLECTION_CONVERSATIONS).
                whereEqualTo(Constant.KEY_SENDER_ID, preferenceManager.getString(Constant.KEY_USER_ID)).
                addSnapshotListener(eventListener);

        database.collection(Constant.KEY_COLLECTION_CONVERSATIONS).
                whereEqualTo(Constant.KEY_RECEIVER_ID, preferenceManager.getString(Constant.KEY_USER_ID)).
                addSnapshotListener(eventListener);
    }

    @SuppressLint("NotifyDataSetChanged")
    private final EventListener<QuerySnapshot> eventListener = (value, error) -> {
        if(error != null) {
            return;
        }

        if(value != null) {
            for(DocumentChange documentChange : value.getDocumentChanges()) {
                if(documentChange.getType() == DocumentChange.Type.ADDED) {
                    String senderId = documentChange.getDocument().getString(Constant.KEY_SENDER_ID);
                    String receiverId = documentChange.getDocument().getString(Constant.KEY_RECEIVER_ID);

                    ChatMessage chatMessage = new ChatMessage();
                    chatMessage.setSenderId(senderId);
                    chatMessage.setReceiverId(receiverId);

                    if(preferenceManager.getString(Constant.KEY_USER_ID).equals(senderId)) {
                        chatMessage.setConversionImage(documentChange.getDocument().getString(Constant.KEY_RECEIVER_IMAGE));
                        chatMessage.setConversionName(documentChange.getDocument().getString(Constant.KEY_RECEIVER_NAME));
                        chatMessage.setConversionId(documentChange.getDocument().getString(Constant.KEY_RECEIVER_ID));
                    } else {
                        chatMessage.setConversionId(documentChange.getDocument().getString(Constant.KEY_SENDER_ID));
                        chatMessage.setConversionName(documentChange.getDocument().getString(Constant.KEY_SENDER_NAME));
                        chatMessage.setConversionImage(documentChange.getDocument().getString(Constant.KEY_SENDER_ID));
                    }

                    chatMessage.setMessage(documentChange.getDocument().getString(Constant.KEY_LAST_MESSAGE));
                    chatMessage.setDateObject(documentChange.getDocument().getDate(Constant.KEY_TIMESTAMP));

                    conversations.add(chatMessage);
                } else if(documentChange.getType() == DocumentChange.Type.MODIFIED) {
                    for(int i = 0; i < conversations.size(); i++) {
                        String senderId = documentChange.getDocument().getString(Constant.KEY_SENDER_ID);
                        String receiverId = documentChange.getDocument().getString(Constant.KEY_RECEIVER_ID);

                        if(conversations.get(i).getSenderId().equals(senderId) &&
                                conversations.get(i).getReceiverId().equals(receiverId)) {
                            conversations.get(i).setMessage(documentChange.getDocument().getString(Constant.KEY_LAST_MESSAGE));
                            conversations.get(i).setDateObject(documentChange.getDocument().getDate(Constant.KEY_TIMESTAMP));
                            break;
                        }
                    }
                }
            }

            Collections.sort(conversations, (o1, o2) -> o2.getDateObject().compareTo(o1.getDateObject()));

            conversationsAdapter.notifyDataSetChanged();
            binding.conversationsRecyclerView.smoothScrollToPosition(0);
        }
    };

    private void getToken() {
        FirebaseMessaging.getInstance().getToken().addOnSuccessListener(this::updateToken);
    }

    private void updateToken(String token) {
        preferenceManager.putString(Constant.KEY_FCM_TOKEN, token);

        FirebaseFirestore database = FirebaseFirestore.getInstance();
        DocumentReference documentReference =
                database.collection(Constant.KEY_COLLECTION_USERS).document(
                        preferenceManager.getString(Constant.KEY_USER_ID)
                );

        documentReference.update(Constant.KEY_FCM_TOKEN, token)
            .addOnFailureListener(e -> showToast("Unable to update token"));
    }

    private void signOut() {
        showToast("Signing out...");

        FirebaseFirestore database = FirebaseFirestore.getInstance();

        DocumentReference documentReference =
                database.collection(Constant.KEY_COLLECTION_USERS).document(
                        preferenceManager.getString(Constant.KEY_USER_ID)
                );

        HashMap<String, Object> updates = new HashMap<>();

        updates.put(Constant.KEY_FCM_TOKEN, FieldValue.delete());

        documentReference.update(updates)
                .addOnSuccessListener(unused -> {
                    preferenceManager.clear();
                    startActivity(new Intent(getApplicationContext(), SignInActivity.class));
                    finish();
                })
                .addOnFailureListener(e -> {
                    showToast("Unable to sign out");
                });
    }

    @Override
    public void onConversionClicked(User user) {
        Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
        intent.putExtra(Constant.KEY_USER, user);
        startActivity(intent);
    }
}