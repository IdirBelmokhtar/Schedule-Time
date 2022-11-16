package com.ecommerce.scheduletime.HomeActivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.ecommerce.scheduletime.CreateAccount.AuthenticationActivity;
import com.ecommerce.scheduletime.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ProfileActivity extends AppCompatActivity {

    private Toolbar toolbar;

    private FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();

    GoogleSignInOptions gso;
    GoogleSignInClient gsc;
    GoogleSignInAccount account;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

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
            Toast.makeText(ProfileActivity.this, "Log Out", Toast.LENGTH_SHORT).show();
        }
    }

    public void profile_save(View view) {
    }
}