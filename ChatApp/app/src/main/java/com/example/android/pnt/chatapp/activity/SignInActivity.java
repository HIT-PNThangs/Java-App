package com.example.android.pnt.chatapp.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.android.pnt.chatapp.databinding.ActivitySignInBinding;
import com.example.android.pnt.chatapp.utilities.Constant;
import com.example.android.pnt.chatapp.utilities.PreferenceManager;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class SignInActivity extends AppCompatActivity {

    ActivitySignInBinding binding;
    private ProgressDialog dialog;
    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignInBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        dialog = new ProgressDialog(this);

        preferenceManager = new PreferenceManager(getApplicationContext());

        if(preferenceManager.getBoolean(Constant.KEY_IS_SIGNED_IN)) {
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        }

        setListeners();
    }

    private void setListeners() {
        binding.createNewAccount.setOnClickListener(v ->
                startActivity(new Intent(getApplicationContext(), SignUpActivity.class)));

        binding.btSignIn.setOnClickListener(v -> {
            if(isValidSignUpDetails()) {
                signIn();
            }
        });
    }

    private void signIn() {
        dialog.show();

        FirebaseFirestore database = FirebaseFirestore.getInstance();

        database.collection(Constant.KEY_COLLECTION_USERS).
                whereEqualTo(Constant.KEY_EMAIL, binding.inputEmailSignIn.getText().toString()).
                whereEqualTo(Constant.KEY_PASSWORD, binding.inputPasswordSignIn.getText().toString()).
                get().
                addOnCompleteListener(task -> {
                    if(task.isSuccessful() && task.getResult() != null &&
                            task.getResult().getDocuments().size() > 0) {
                        DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);

                        preferenceManager.putBoolean(Constant.KEY_IS_SIGNED_IN, true);
                        preferenceManager.putString(Constant.KEY_USER_ID, documentSnapshot.getId());
                        preferenceManager.putString(Constant.KEY_NAME, documentSnapshot.getString(Constant.KEY_NAME));
                        preferenceManager.putString(Constant.KEY_IMAGE, documentSnapshot.getString(Constant.KEY_IMAGE));

                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        dialog.dismiss();
                        startActivity(intent);
                    } else {
                        dialog.dismiss();
                        showToast("Unable to sign in");
                    }
                });
    }

    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    private boolean isValidSignUpDetails() {
        String strEmail = binding.inputEmailSignIn.getText().toString().trim();
        String strPassword = binding.inputPasswordSignIn.getText().toString().trim();

        if(strEmail.isEmpty()) {
            showToast("Enter email");
            return false;
        } else if(!Patterns.EMAIL_ADDRESS.matcher(strEmail).matches()) {
            showToast("Enter valid image");
            return false;
        } else if(strPassword.isEmpty()) {
            showToast("Enter password");
            return false;
        } else  {
            return true;
        }
    }
}