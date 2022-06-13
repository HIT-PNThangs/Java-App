package com.example.android.pnt.chatapp.activity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.android.pnt.chatapp.databinding.ActivitySignUpBinding;
import com.example.android.pnt.chatapp.utilities.Constant;
import com.example.android.pnt.chatapp.utilities.PreferenceManager;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Base64;
import java.util.HashMap;

public class SignUpActivity extends AppCompatActivity {

    ActivitySignUpBinding binding;
    private PreferenceManager preferenceManager;
    private String encodeImage;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        dialog = new ProgressDialog(this);

        preferenceManager = new PreferenceManager(getApplicationContext());

        setListeners();
    }

    private void setListeners() {
        binding.txtSignIn.setOnClickListener(v ->
                startActivity(new Intent(getApplicationContext(), SignInActivity.class)));

        binding.btSignUp.setOnClickListener(v -> {
            if(isValidSignUpDetails()) {
                signUp();
            }
        });

        binding.imageProfile.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            pickImage.launch(intent);
        });
    }

    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void signUp() {
        dialog.show();

        FirebaseFirestore database = FirebaseFirestore.getInstance();

        HashMap<String, Object> user = new HashMap<>();
        user.put(Constant.KEY_NAME, binding.inputNameSignUp.getText().toString().trim());
        user.put(Constant.KEY_EMAIL, binding.inputEmailSignUp.getText().toString().trim());
        user.put(Constant.KEY_PASSWORD, binding.inputPasswordSignUp.getText().toString().trim());
        user.put(Constant.KEY_IMAGE, encodeImage);

        database.collection(Constant.KEY_COLLECTION_USERS).
                whereEqualTo(Constant.KEY_EMAIL, binding.inputEmailSignUp.getText().toString()).
                whereEqualTo(Constant.KEY_PASSWORD, binding.inputPasswordSignUp.getText().toString()).
                get().
                addOnCompleteListener(task -> {
                            if (task.isSuccessful() && task.getResult() != null &&
                                    task.getResult().getDocuments().size() > 0) {
                                dialog.dismiss();
                                showToast("Trùng email");
                            } else {
                                dialog.dismiss();

                                database.collection(Constant.KEY_COLLECTION_USERS).
                                        add(user).
                                        addOnSuccessListener(documentReference -> {
                                            dialog.dismiss();

                                            preferenceManager.putBoolean(Constant.KEY_IS_SIGNED_IN, true);
                                            preferenceManager.putString(Constant.KEY_USER_ID, documentReference.getId());
                                            preferenceManager.putString(Constant.KEY_NAME, binding.inputNameSignUp.getText().toString().trim());
                                            preferenceManager.putString(Constant.KEY_IMAGE, encodeImage);

                                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            startActivity(intent);
                                            finish();
                                        }).
                                        addOnFailureListener(exception -> {
                                            dialog.dismiss();

                                            showToast(exception.getMessage());
                                        });
                            }
                        });
    }

    @SuppressLint("NewApi")
    private String encodeImage(Bitmap bitmap) {
        int previewWidth = 150;
        int previewHeight = bitmap.getHeight() * previewWidth / bitmap.getWidth();

        Bitmap previewBitmap = Bitmap.createScaledBitmap(bitmap, previewWidth, previewHeight, false);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        previewBitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);
        byte[] bytes = byteArrayOutputStream.toByteArray();

        return Base64.getEncoder().encodeToString(bytes);
    }

    private final ActivityResultLauncher<Intent> pickImage = registerForActivityResult(new
            ActivityResultContracts.StartActivityForResult(),
            result -> {
                if(result.getResultCode() == RESULT_OK) {
                    if(result.getData() != null) {
                        Uri imageUri = result.getData().getData();

                        try {
                            InputStream inputStream = getContentResolver().openInputStream(imageUri);
                            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                            binding.txtAddImage.setVisibility(View.GONE);
                            binding.imageProfile.setImageBitmap(bitmap);
                            encodeImage = encodeImage(bitmap);
                        } catch (FileNotFoundException exception) {
                            exception.printStackTrace();
                        }
                    }
                }
            });

    private boolean isValidSignUpDetails() {
        String strName = binding.inputNameSignUp.getText().toString().trim();
        String strEmail = binding.inputEmailSignUp.getText().toString().trim();
        String strPassword = binding.inputPasswordSignUp.getText().toString().trim();
        String strConfirmPassword = binding.inputConfirmPassword.getText().toString().trim();

        boolean is = false;

        if(encodeImage == null) {
            showToast("Select profile image");
        } else if(strName.isEmpty()) {
            showToast("Enter name");
        } else if(strEmail.isEmpty()) {
            showToast("Enter email");
        } else if(!Patterns.EMAIL_ADDRESS.matcher(strEmail).matches()) {
            showToast("Enter valid email");
        } else if(strPassword.isEmpty()) {
            showToast("Enter password");
        } else if(strConfirmPassword.isEmpty()) {
            showToast("Enter confirm password");
        } else if(!strPassword.equals(strConfirmPassword)) {
            showToast("Password && Confirm password must be same");
        } else {
            is = true;
        }

        return is;
    }
}