package com.ecommerce.scheduletime.CreateAccount.Fragment;

import static android.content.Context.MODE_PRIVATE;
import static com.ecommerce.scheduletime.Sync.SyncDatabase.getCategories;
import static com.ecommerce.scheduletime.Sync.SyncDatabase.getNotes;
import static com.ecommerce.scheduletime.Sync.SyncDatabase.getTasks;
import static com.facebook.FacebookSdk.getApplicationContext;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ecommerce.scheduletime.CreateAccount.FacebookAuthActivity;
import com.ecommerce.scheduletime.HomeActivity.MainActivity;
import com.ecommerce.scheduletime.R;
import com.ecommerce.scheduletime.Sync.SyncDatabase;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.OAuthProvider;

public class LoginFragment extends Fragment {

    private static final int RC_SIGN_IN = 7;
    private ProgressDialog progressDialog;
    private TextView forgot_textview;
    private TextInputEditText sign_in_email, sign_in_password;
    private CheckBox checkBox;
    private Button button_signIn;
    private LinearLayout layout_google, layout_facebook, layout_twitter, linearLayoutSignUp;

    String email, password;

    private FirebaseAuth mAuth;

    GoogleSignInOptions gso;
    GoogleSignInClient gsc;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);


        forgot_textview = view.findViewById(R.id.forgot_textview);
        sign_in_email = view.findViewById(R.id.sign_in_email);
        sign_in_password = view.findViewById(R.id.sign_in_password);
        checkBox = view.findViewById(R.id.checkBox);
        button_signIn = view.findViewById(R.id.button_signIn);
        layout_google = view.findViewById(R.id.layout_google);
        layout_facebook = view.findViewById(R.id.layout_facebook);
        layout_twitter = view.findViewById(R.id.layout_twitter);
        linearLayoutSignUp = view.findViewById(R.id.linearLayoutSignUp);

        mAuth = FirebaseAuth.getInstance();

        try {
            SharedPreferences prefs = getApplicationContext().getSharedPreferences("USER_INFO", MODE_PRIVATE);
            String name = prefs.getString("email", "");
            String password = prefs.getString("password", "");
            sign_in_email.setText(name);
            sign_in_password.setText(password);

            if (!name.equals("") && !password.equals("")) {
                checkBox.setChecked(true);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        email = sign_in_email.getText().toString().trim();
        password = sign_in_password.getText().toString().trim();

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        gsc = GoogleSignIn.getClient(getContext(), gso);
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getContext());

        forgot_textview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragmentContainerViewAuth, new ForgetPasswordFragment())
                        .addToBackStack(null)
                        .commit();

            }
        });

        button_signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sign_in(view);
            }
        });

        layout_google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (account == null) {
                    SignInWithGoogle();
                }
            }
        });

        layout_facebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), FacebookAuthActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
            }
        });

        layout_twitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SignInWithTwitter();
            }
        });

        linearLayoutSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragmentContainerViewAuth, new SignUpFragment())
                        .addToBackStack(null)
                        .commit();
            }
        });

        return view;
    }

    private void SignInWithTwitter() {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

        OAuthProvider.Builder provider = OAuthProvider.newBuilder("twitter.com");

        // Target specific email with login hint.
        provider.addCustomParameter("lang", "fr");

        Task<AuthResult> pendingResultTask = firebaseAuth.getPendingAuthResult();
        if (pendingResultTask != null) {
            // There's something already here! Finish the sign-in for your user.
            pendingResultTask
                    .addOnSuccessListener(
                            new OnSuccessListener<AuthResult>() {
                                @Override
                                public void onSuccess(AuthResult authResult) {
                                    Toast.makeText(getContext(), getResources().getString(R.string.success_), Toast.LENGTH_SHORT).show();

                                    // Save data of user in SharedPreferences.
                                    SharedPreferences.Editor editor = getContext().getSharedPreferences("USER_INFO", MODE_PRIVATE).edit();
                                    editor.putString("user_full_name", mAuth.getCurrentUser().getDisplayName());
                                    editor.putString("user_email", mAuth.getCurrentUser().getEmail());
                                    editor.putString("user_photo_uri", mAuth.getCurrentUser().getPhotoUrl().toString().replace("_normal", "_bigger"));
                                    editor.putString("user_auth_with", "Twitter");
                                    editor.apply();

                                    Intent intent = new Intent(getContext(), MainActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);

                                    // User is signed in.
                                    // IdP data available in
                                    // authResult.getAdditionalUserInfo().getProfile().
                                    // The OAuth access token can also be retrieved:
                                    // ((OAuthCredential)authResult.getCredential()).getAccessToken().
                                    // The OAuth secret can be retrieved by calling:
                                    // ((OAuthCredential)authResult.getCredential()).getSecret().
                                }
                            })
                    .addOnFailureListener(
                            new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    // Handle failure.
                                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                                }
                            });
        } else {
            // There's no pending result so you need to start the sign-in flow.
            // See below.
            firebaseAuth
                    .startActivityForSignInWithProvider(getActivity(), provider.build())
                    .addOnSuccessListener(
                            new OnSuccessListener<AuthResult>() {
                                @Override
                                public void onSuccess(AuthResult authResult) {
                                    Toast.makeText(getContext(), getResources().getString(R.string.success_), Toast.LENGTH_SHORT).show();

                                    // Save data of user in SharedPreferences.
                                    SharedPreferences.Editor editor = getContext().getSharedPreferences("USER_INFO", MODE_PRIVATE).edit();
                                    editor.putString("user_full_name", mAuth.getCurrentUser().getDisplayName());
                                    editor.putString("user_email", mAuth.getCurrentUser().getEmail());
                                    editor.putString("user_photo_uri", mAuth.getCurrentUser().getPhotoUrl().toString().replace("_normal", "_bigger"));
                                    editor.putString("user_auth_with", "Twitter");
                                    editor.apply();

                                    Intent intent = new Intent(getContext(), MainActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);

                                    // User is signed in.
                                    // IdP data available in
                                    // authResult.getAdditionalUserInfo().getProfile().
                                    // The OAuth access token can also be retrieved:
                                    // ((OAuthCredential)authResult.getCredential()).getAccessToken().
                                    // The OAuth secret can be retrieved by calling:
                                    // ((OAuthCredential)authResult.getCredential()).getSecret().
                                }
                            })
                    .addOnFailureListener(
                            new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    // Handle failure.
                                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                                }
                            });
        }
    }

    public void sign_in(View view) {
        String email_ = sign_in_email.getText().toString().trim();
        String password_ = sign_in_password.getText().toString().trim();

        if (TextUtils.isEmpty(email_)) {
            sign_in_email.setError(getString(R.string.email_required));
            return;
        }
        if (TextUtils.isEmpty(password_)) {
            sign_in_password.setError(getString(R.string.password_required));
            return;
        }
        //progress Dialog
        progress(email_, password_);
    }

    private void progress(String email_, String password_) {
        progressDialog = new ProgressDialog(getContext());
        progressDialog.show();
        progressDialog.setContentView(R.layout.progress_dialog);
        progressDialog.getWindow().setBackgroundDrawableResource(R.color.transparent);

        mAuth.signInWithEmailAndPassword(email_, password_).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    progressDialog.dismiss();
                    progressSync();

                    //Remember user and password (Optional)
                    if (checkBox.isChecked()) {
                        SharedPreferences.Editor editor = getApplicationContext().getSharedPreferences("USER_INFO", MODE_PRIVATE).edit();
                        editor.putString("email", email_);
                        editor.putString("password", password_);
                        editor.apply();
                    } else {
                        SharedPreferences.Editor editor = getApplicationContext().getSharedPreferences("USER_INFO", MODE_PRIVATE).edit();
                        editor.putString("email", "");
                        editor.putString("password", "");
                        editor.apply();
                    }

                    // Save data of user in SharedPreferences.
                    SharedPreferences.Editor editor = getContext().getSharedPreferences("USER_INFO", MODE_PRIVATE).edit();
                    editor.putString("user_full_name", mAuth.getCurrentUser().getDisplayName());
                    editor.putString("user_email", mAuth.getCurrentUser().getEmail());
                    editor.putString("user_photo_uri", mAuth.getCurrentUser().getPhotoUrl().toString());
                    editor.putString("user_auth_with", "Firebase");
                    editor.apply();
                } else {
                    progressDialog.dismiss();
                    Toast.makeText(getContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void progressSync() {
        progressDialog = new ProgressDialog(getContext());
        progressDialog.show();
        progressDialog.setContentView(R.layout.progress_sync_dialog);
        progressDialog.getWindow().setBackgroundDrawableResource(R.color.transparent);
        progressDialog.getWindow().setLayout(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);

        new SyncDatabase(getContext(), FirebaseAuth.getInstance().getCurrentUser().getUid());

        Toast.makeText(getContext(), FirebaseAuth.getInstance().getCurrentUser().getUid(), Toast.LENGTH_SHORT).show();
        //Toast.makeText(getContext(), getResources().getString(R.string.success_), Toast.LENGTH_SHORT).show();

        startScheduleTime();
    }

    private void startScheduleTime() {
        if (getTasks && getCategories && getNotes){
            // Start MainActivity.
            Intent intent = new Intent(getContext(), MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    startScheduleTime();

                    SharedPreferences.Editor editor = getContext().getSharedPreferences("Data has been extracted from firebase", MODE_PRIVATE).edit();
                    editor.putBoolean("change", true);
                    editor.apply();
                }
            }, 1000);
        }
    }

    private void SignInWithGoogle() {
        Intent intent = gsc.getSignInIntent();
        startActivityForResult(intent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);

            } catch (ApiException e) {
                e.printStackTrace();
                Toast.makeText(getContext(), getString(R.string.there_is_an_error) + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getContext(), getResources().getString(R.string.success_), Toast.LENGTH_SHORT).show();
                            // Save data of user in SharedPreferences.
                            SharedPreferences.Editor editor = getContext().getSharedPreferences("USER_INFO", MODE_PRIVATE).edit();
                            editor.putString("user_full_name", mAuth.getCurrentUser().getDisplayName());
                            editor.putString("user_email", mAuth.getCurrentUser().getEmail());
                            editor.putString("user_photo_uri", mAuth.getCurrentUser().getPhotoUrl().toString());
                            editor.putString("user_auth_with", "Google");
                            editor.apply();

                            //finish top activities
                            Intent intent = new Intent(getContext(), MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        } else {
                            Toast.makeText(getContext(), getResources().getString(R.string.failed), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


    @Override
    public void onResume() {
        super.onResume();

        if (getView() == null) {
            return;
        }

        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
                if (keyEvent.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                    // handle back button's click listener
                    getActivity().finish();
                    return true;
                }
                return false;
            }
        });
    }
}