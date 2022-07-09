package com.example.pnt.android.videomeetingapp.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.pnt.android.videomeetingapp.Utilities.Constants;
import com.example.pnt.android.videomeetingapp.Utilities.PreferenceManager;
import com.example.pnt.android.videomeetingapp.databinding.ActivitySignInBinding;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.regex.Pattern;

public class SignInActivity extends AppCompatActivity {
    ActivitySignInBinding binding;
    FirebaseFirestore database;
    PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignInBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        init();

        setListener();
    }

    private void init() {
        database = FirebaseFirestore.getInstance();
        preferenceManager = new PreferenceManager(getApplicationContext());

        if(preferenceManager.getBoolean(Constants.KEY_IS_SIGNED_IN)) {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }
    }

    private void setListener() {
        binding.textSignUp.setOnClickListener(view -> startActivity(new Intent(getApplicationContext(), SignUpActivity.class)));

        binding.btSignIn.setOnClickListener(view -> {

            String strEmail = binding.inputEmail.getText().toString().trim();
            String strPassword = binding.inputPassword.getText().toString().trim();

            if (strEmail.isEmpty()) {
                showToast("Enter Email");
            } else if (!isEmail(strEmail)) {
                showToast("Enter valid Email");
            } else if (strPassword.isEmpty()) {
                showToast("Enter Password");
            } else {
                signIn();
            }
        });
    }

    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
    }

    private boolean isEmail(String email) {
        String EMAIL_PATTERN = "^[a-z][a-z0-9_.]{5,32}@[a-z0-9]{2,}(\\.[a-z0-9]{2,4})$";
        return Pattern.compile(EMAIL_PATTERN).matcher(email).matches();
    }

    private void signIn() {
        binding.signInProgressBar.setVisibility(View.VISIBLE);
        binding.btSignIn.setVisibility(View.GONE);

        database.collection(Constants.KEY_COLLECTION_USERS)
                .whereEqualTo(Constants.KEY_EMAIL, binding.inputEmail.getText().toString().trim())
                .whereEqualTo(Constants.KEY_PASSWORD, binding.inputPassword.getText().toString().trim())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null && task.getResult().getDocuments().size() > 0) {
                        binding.signInProgressBar.setVisibility(View.GONE);
                        binding.btSignIn.setVisibility(View.VISIBLE);

                        DocumentSnapshot snapshot = task.getResult().getDocuments().get(0);

                        preferenceManager.putBoolean(Constants.KEY_IS_SIGNED_IN, true);
                        preferenceManager.putString(Constants.KEY_FIRST_NAME, snapshot.getString(Constants.KEY_FIRST_NAME));
                        preferenceManager.putString(Constants.KEY_LAST_NAME, snapshot.getString(Constants.KEY_LAST_NAME));
                        preferenceManager.putString(Constants.KEY_EMAIL, snapshot.getString(Constants.KEY_EMAIL));

                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    } else {
                        binding.signInProgressBar.setVisibility(View.GONE);
                        binding.btSignIn.setVisibility(View.VISIBLE);

                        showToast("Unable to sign in");
                    }
                });
    }
}