package com.ecommerce.scheduletime.SplashScreen;

import static com.ecommerce.scheduletime.HomeActivity.MainActivity.scrollToNewTask;
import static com.ecommerce.scheduletime.SQLite.MyDatabaseHelper.TASK_NEW_ID;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.ecommerce.scheduletime.CreateAccount.AuthenticationActivity;
import com.ecommerce.scheduletime.HomeActivity.MainActivity;
import com.ecommerce.scheduletime.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;

public class SplashScreenActivity extends AppCompatActivity {

    private final int SPLASH_SCREEN = 2300;


    private FirebaseAuth mAuth;

    GoogleSignInOptions gso;
    GoogleSignInClient gsc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        try {
            // Scroll to task notification id.
            SharedPreferences prefs = getSharedPreferences("id_task", MODE_PRIVATE);
            String idName = prefs.getString("idName", "");
            if (!idName.equals("")) {
                scrollToNewTask = true;
                TASK_NEW_ID = Integer.parseInt(idName);

                SharedPreferences.Editor editor = getSharedPreferences("id_task", MODE_PRIVATE).edit();
                editor.putString("idName", "");
                editor.apply();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        SharedPreferences preferences = getSharedPreferences("intro",MODE_PRIVATE);
        String intro = preferences.getString("state","");

        ImageView schedule_time_icon = findViewById(R.id.schedule_time_icon);
        schedule_time_icon.setAnimation(AnimationUtils.loadAnimation(this, R.anim.zoom_in));

        mAuth = FirebaseAuth.getInstance();

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        gsc = GoogleSignIn.getClient(SplashScreenActivity.this, gso);

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(SplashScreenActivity.this);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //if second time
                if (intro.equals("done")){
                    if (account != null) {
                        //Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                        //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        //startActivity(intent);
                    }
                    if (mAuth.getCurrentUser() != null) {
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
                        finish();
                    }else {
                        Intent intent = new Intent(getApplicationContext(), AuthenticationActivity.class);
                        startActivity(intent);
                        overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
                        finish();
                    }
                }else {
                    //if first time
                    Intent intent = new Intent(getApplicationContext(), IntroScreenActivity.class);
                    startActivity(intent);
                    overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
                    finish();
                }
            }
        },SPLASH_SCREEN);
    }

    private void connect(){



    }
}