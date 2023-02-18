package com.ecommerce.scheduletime.CreateAccount;

import static com.ecommerce.scheduletime.CreateAccount.Fragment.LoginFragment.getCategories;
import static com.ecommerce.scheduletime.CreateAccount.Fragment.LoginFragment.getNotes;
import static com.ecommerce.scheduletime.CreateAccount.Fragment.LoginFragment.getTasks;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.ecommerce.scheduletime.HomeActivity.MainActivity;
import com.ecommerce.scheduletime.R;
import com.ecommerce.scheduletime.Sync.SyncDataBaseService;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;

import java.util.Arrays;

public class FacebookAuthActivity extends Activity {

    private static final String TAG = "FacebookLogin";

    // [START declare_auth]
    private FirebaseAuth mAuth;
    // [END declare_auth]

    private CallbackManager mCallbackManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // [START initialize_auth]
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        // [END initialize_auth]

        // [START initialize_fblogin]
        // Initialize Facebook Login button
        mCallbackManager = CallbackManager.Factory.create();

        /*
        // login from login button fb
        LoginButton loginButton = findViewById(R.id.button_sign_in);
        loginButton.setReadPermissions("email", "public_profile");
        loginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
        */
        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile"));
        LoginManager.getInstance().registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG, "facebook:onSuccess:" + loginResult);
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.d(TAG, "facebook:onCancel");
            }

            @Override
            public void onError(FacebookException error) {
                Log.d(TAG, "facebook:onError", error);
            }
        });
        // [END initialize_fblogin]
    }

    // [START on_start_check_user]
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }
    // [END on_start_check_user]

    // [START on_activity_result]
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Pass the activity result back to the Facebook SDK
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }
    // [END on_activity_result]

    // [START auth_with_facebook]
    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(FacebookAuthActivity.this, getString(R.string.authentication_failed),
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    }
                });
    }
    // [END auth_with_facebook]

    private void updateUI(FirebaseUser user) {
        if (user != null){
            Toast.makeText(FacebookAuthActivity.this, getString(R.string.success_), Toast.LENGTH_SHORT).show();

            // Save data of user in SharedPreferences.
            SharedPreferences.Editor editor = getSharedPreferences("USER_INFO", MODE_PRIVATE).edit();
            editor.putString("user_full_name", user.getDisplayName());
            editor.putString("user_email", user.getEmail());
            editor.putString("user_photo_uri", user.getPhotoUrl().toString() + "?height=500");
            editor.putString("user_auth_with", "Facebook");
            editor.apply();

            progressSync();
        }
    }

    private void progressSync() {
        Toast.makeText(this, getResources().getString(R.string.retrieve_data), Toast.LENGTH_LONG).show();

        Intent intent = new Intent(getApplicationContext(), SyncDataBaseService.class);
        startService(intent);

        startScheduleTime();
    }

    private void startScheduleTime() {
        if (getTasks && getCategories && getNotes){
            // Start MainActivity.
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);

            SharedPreferences.Editor editor = getSharedPreferences("Data has been extracted from firebase", MODE_PRIVATE).edit();
            editor.putBoolean("change", true);
            editor.apply();
        }else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    startScheduleTime();
                }
            }, 1000);
        }
    }
}