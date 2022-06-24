package com.example.android.pnt.whatsapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.android.pnt.whatsapp.databinding.ActivitySignInBinding;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class SignInActivity extends AppCompatActivity {

    ActivitySignInBinding binding;
    FirebaseAuth auth;
    FirebaseDatabase database;

    // Sign In with Google by Firebase
    GoogleSignInClient mGoogleSignInClient;
    private static final int RC_SIGN_IN = 9001;
    private static final String TAG = "Sign In";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignInBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        init();

        setListener();
    }

    private void init() {
        Objects.requireNonNull(getSupportActionBar()).hide();

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        if (auth.getCurrentUser() != null) {
            startActivity(new Intent(SignInActivity.this, MainActivity.class));
        }

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("379546987675-k0rvvqu1jv53cb4mhnf3ph6k3ob19nk5.apps.googleusercontent.com")
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    private void showToast(String s) {
        Toast.makeText(this, s, Toast.LENGTH_LONG).show();
    }

    private void setListener() {
        binding.btnSignIn.setOnClickListener(v -> {
            String strEmail = Objects.requireNonNull(binding.edtEmail.getText()).toString();
            String strPass = Objects.requireNonNull(binding.edtPass.getText()).toString();

            if (!strEmail.isEmpty() && !strPass.isEmpty()) {
                binding.progressBar2.setVisibility(View.VISIBLE);

                auth.signInWithEmailAndPassword(strEmail, strPass)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                binding.progressBar2.setVisibility(View.GONE);

                                if (task.isSuccessful()) {
                                    startActivity(new Intent(SignInActivity.this, MainActivity.class));
                                    showToast("Sign In Success.");
                                } else {
                                    showToast(Objects.requireNonNull(task.getException()).toString());
                                }
                            }
                        });
            } else {
                showToast("Enter Credentials");
            }
        });

        binding.txtSignUp.setOnClickListener(v -> startActivity(new Intent(SignInActivity.this, SignUpActivity.class)));

        binding.btnGoogle.setOnClickListener(v -> signInGoogle());
    }

    //------- sign in google -------------------------------
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
                showToast("Google sign in failed: " + e);
            }
        }
    }
    // [END onactivityresult]

    // [START auth_with_google]
    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = auth.getCurrentUser();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            showToast("signInWithCredential:failure" + task.getException());
                        }
                    }
                });
    }
    // [END auth_with_google]

    private void signInGoogle() {
        //Calling Intent and call startActivityForResult() method
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    // -----------------end---------------------------------------

//    private void signOut() {
//        // Firebase sign out
//        FirebaseAuth.getInstance().signOut();
//
//        // Google sign out
//        mGoogleSignInClient.signOut().addOnCompleteListener(this,
//                new OnCompleteListener<Void>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Void> task) {
//                        // Google Sign In failed, update UI appropriately
//                        Toast.makeText(getApplicationContext(), "Signed out of google", Toast.LENGTH_SHORT).show();
//                    }
//                });
//    }

//    private void revokeAccess() {
//        // Firebase sign out
//        FirebaseAuth.getInstance().signOut();
//
//        // Google revoke access
//        mGoogleSignInClient.revokeAccess().addOnCompleteListener(this,
//                new OnCompleteListener<Void>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Void> task) {
//                        // Google Sign In failed, update UI appropriately
//                        Log.w(TAG, "Revoked Access");
//                    }
//                });
//    }
}