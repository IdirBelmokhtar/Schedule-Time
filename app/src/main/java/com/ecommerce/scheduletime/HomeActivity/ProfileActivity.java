package com.ecommerce.scheduletime.HomeActivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.ecommerce.scheduletime.CreateAccount.AuthenticationActivity;
import com.ecommerce.scheduletime.NoteActivity.NoteActivity;
import com.ecommerce.scheduletime.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    private Toolbar toolbar;

    String user_full_name, user_email, user_photo_uri, user_auth_with;
    private FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
    private TextView profile_name, profile_email;
    private CircleImageView profile_image, profile_auth;
    private TextInputEditText profile_editTextTextName, profile_editTextTextEmail;

    GoogleSignInOptions gso;
    GoogleSignInClient gsc;
    GoogleSignInAccount account;

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        SharedPreferences prefs = getSharedPreferences("USER_INFO", MODE_PRIVATE);
        user_full_name = prefs.getString("user_full_name", "");
        user_email = prefs.getString("user_email", "");
        user_photo_uri = prefs.getString("user_photo_uri", "");
        user_auth_with = prefs.getString("user_auth_with", "");

        profile_name = findViewById(R.id.profile_name);
        profile_email = findViewById(R.id.profile_email);
        profile_image = findViewById(R.id.profile_image);
        profile_auth = findViewById(R.id.profile_auth);
        profile_editTextTextName = findViewById(R.id.profile_editTextTextName);
        profile_editTextTextEmail = findViewById(R.id.profile_editTextTextEmail);

        profile_name.setText(user_full_name);
        profile_email.setText(user_email);
        profile_editTextTextName.setText(user_full_name);
        profile_editTextTextEmail.setText(user_email);

        try {
            Glide.with(this).load(Uri.parse(user_photo_uri)).into(profile_image);
            if (user_auth_with.equals("Firebase")) {
                profile_auth.setVisibility(View.GONE);
                findViewById(R.id.profile_auth_background).setVisibility(View.GONE);
            } else if (user_auth_with.equals("Google")) {
                Glide.with(this).load(R.drawable.ic_icons8_google_1).into(profile_auth);
            } else if (user_auth_with.equals("Facebook")) {
                Glide.with(this).load(R.drawable.ic_icons8_facebook).into(profile_auth);
            } else if (user_auth_with.equals("Twitter")) {
                Glide.with(this).load(R.drawable.icons8_twitter).into(profile_auth);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        toolbar = findViewById(R.id.toolbar_profile);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        gsc = GoogleSignIn.getClient(ProfileActivity.this, gso);
        account = GoogleSignIn.getLastSignedInAccount(ProfileActivity.this);

    }

    public void profile_logout(View view) {
        if (fUser == null) {
            startActivity(new Intent(ProfileActivity.this, AuthenticationActivity.class));
            Toast.makeText(ProfileActivity.this, "Log In", Toast.LENGTH_SHORT).show();
        } else {

            //Firebase Logout *(Twitter)* *(Facebook)*
            if (fUser != null) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(ProfileActivity.this, AuthenticationActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }

            //Google Logout
            if (account != null) {
                gsc.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Intent intent = new Intent(ProfileActivity.this, AuthenticationActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                });
            }
            Toast.makeText(ProfileActivity.this, getResources().getString(R.string.log_out), Toast.LENGTH_SHORT).show();
        }
    }

    public void profile_save(View view) {
        //progress Dialog
        ProgressDialog progressDialog = new ProgressDialog(ProfileActivity.this);
        progressDialog.show();
        progressDialog.setContentView(R.layout.progress_dialog);
        progressDialog.getWindow().setBackgroundDrawableResource(R.color.transparent);

        mAuth.getCurrentUser().updateProfile(new UserProfileChangeRequest.Builder()
                .setDisplayName(profile_editTextTextName.getText().toString().trim())
                .build()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Toast.makeText(ProfileActivity.this, getResources().getString(R.string.saved), Toast.LENGTH_SHORT).show();

                    // Save data of user in SharedPreferences.
                    SharedPreferences.Editor editor = getSharedPreferences("USER_INFO", MODE_PRIVATE).edit();
                    editor.putString("user_full_name", mAuth.getCurrentUser().getDisplayName());
                    editor.putString("user_email", mAuth.getCurrentUser().getEmail());
                    //editor.putString("user_photo_uri", mAuth.getCurrentUser().getPhotoUrl().toString());
                    editor.putString("user_auth_with", user_auth_with);
                    editor.apply();

                    Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
                    startActivity(intent);
                    overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
                    onBackPressed();
                    progressDialog.dismiss();
                }else {
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

}