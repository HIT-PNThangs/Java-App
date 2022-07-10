package com.example.pnt.android.videomeetingapp.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.pnt.android.videomeetingapp.Utilities.Constants;
import com.example.pnt.android.videomeetingapp.Utilities.PreferenceManager;
import com.example.pnt.android.videomeetingapp.databinding.ActivitySignUpBinding;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.regex.Pattern;

public class SignUpActivity extends AppCompatActivity {
    ActivitySignUpBinding binding;
    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        preferenceManager = new PreferenceManager(getApplicationContext());

        setListener();
    }

    private void setListener() {
        binding.textSignIn.setOnClickListener(view -> {
            startActivity(new Intent(getApplicationContext(), SignInActivity.class));
            finish();
            overridePendingTransition(0, 0);
        });

        binding.imageBack.setOnClickListener(view -> onBackPressed());

        binding.btSignUp.setOnClickListener(view -> {
            String strFirstName = binding.inputFirstName.getText().toString().trim();
            String strLastName = binding.inputLastName.getText().toString().trim();
            String strEmail = binding.inputEmail.getText().toString().trim();
            String strPassword = binding.inputPassword.getText().toString().trim();
            String strConfirmPassword = binding.inputConfirmPassword.getText().toString().trim();

            if (strFirstName.isEmpty()) {
                showToast("Enter First Name");
            } else if (strLastName.isEmpty()) {
                showToast("Enter Last Name");
            } else if (strEmail.isEmpty()) {
                showToast("Enter Email");
            } else if (!isEmail(strEmail)) {
                showToast("Enter valid Email");
            } else if (strPassword.isEmpty()) {
                showToast("Enter Password");
            } else if (strConfirmPassword.isEmpty()) {
                showToast("Enter Confirm Password");
            } else if (!strPassword.equals(strConfirmPassword)) {
                showToast("Password & Confirm Password must be same");
            } else {
                signUp();
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

    private void signUp() {
        binding.signUpProgressBar.setVisibility(View.VISIBLE);
        binding.btSignUp.setVisibility(View.GONE);

//        FirebaseAuth auth = FirebaseAuth.getInstance();
//        auth.createUserWithEmailAndPassword(binding.inputEmail.getText().toString().trim(),
//                binding.inputPassword.getText().toString().trim()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
//            @Override
//            public void onComplete(@NonNull Task<AuthResult> task) {
//                if (task.isSuccessful()) {
//                    FirebaseFirestore database = FirebaseFirestore.getInstance();
//                    HashMap<String, Object> user = new HashMap<>();
//
//                    user.put(Constants.KEY_FIRST_NAME, binding.inputFirstName.getText().toString().trim());
//                    user.put(Constants.KEY_LAST_NAME, binding.inputLastName.getText().toString().trim());
//                    user.put(Constants.KEY_EMAIL, binding.inputEmail.getText().toString().trim());
//
//                    database.collection(Constants.KEY_COLLECTION_USERS)
//                            .add(user)
//                            .addOnSuccessListener(documentReference -> {
//                                binding.signUpProgressBar.setVisibility(View.VISIBLE);
//                                binding.btSignUp.setVisibility(View.GONE);
//
//                                preferenceManager.putBoolean(Constants.KEY_IS_SIGNED_IN, true);
//                                preferenceManager.putString(Constants.KEY_USER_ID, documentReference.getId());
//                                preferenceManager.putString(Constants.KEY_FIRST_NAME, binding.inputFirstName.getText().toString().trim());
//                                preferenceManager.putString(Constants.KEY_LAST_NAME, binding.inputLastName.getText().toString().trim());
//                                preferenceManager.putString(Constants.KEY_EMAIL, binding.inputEmail.getText().toString().trim());
//
//                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
//                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                                startActivity(intent);
//                                finish();
//                            })
//                            .addOnFailureListener(e -> {
//                                binding.signUpProgressBar.setVisibility(View.GONE);
//                                binding.btSignUp.setVisibility(View.VISIBLE);
//
//                                showToast("Error: " + e.getMessage());
//                            });
//                } else {
//                    binding.signUpProgressBar.setVisibility(View.VISIBLE);
//                    binding.btSignUp.setVisibility(View.GONE);
//
//                    Toast.makeText(getApplicationContext(),
//                            Objects.requireNonNull(task.getException()).toString(), Toast.LENGTH_LONG).show();
//                }
//            }
//        });

        FirebaseFirestore database = FirebaseFirestore.getInstance();
        HashMap<String, Object> user = new HashMap<>();

        user.put(Constants.KEY_FIRST_NAME, binding.inputFirstName.getText().toString().trim());
        user.put(Constants.KEY_LAST_NAME, binding.inputLastName.getText().toString().trim());
        user.put(Constants.KEY_EMAIL, binding.inputEmail.getText().toString().trim());
        user.put(Constants.KEY_PASSWORD, binding.inputPassword.getText().toString().trim());

        database.collection(Constants.KEY_COLLECTION_USERS)
                .add(user)
                .addOnSuccessListener(documentReference -> {
                    binding.signUpProgressBar.setVisibility(View.VISIBLE);
                    binding.btSignUp.setVisibility(View.GONE);

                    preferenceManager.putBoolean(Constants.KEY_IS_SIGNED_IN, true);
                    preferenceManager.putString(Constants.KEY_USER_ID, documentReference.getId());
                    preferenceManager.putString(Constants.KEY_FIRST_NAME, binding.inputFirstName.getText().toString().trim());
                    preferenceManager.putString(Constants.KEY_LAST_NAME, binding.inputLastName.getText().toString().trim());
                    preferenceManager.putString(Constants.KEY_EMAIL, binding.inputEmail.getText().toString().trim());

                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                })
                .addOnFailureListener(e -> {
                    binding.signUpProgressBar.setVisibility(View.GONE);
                    binding.btSignUp.setVisibility(View.VISIBLE);

                    showToast("Error: " + e.getMessage());
                });
    }
}