package com.ecommerce.scheduletime.CreateAccount.Fragment;

import static android.content.Context.MODE_PRIVATE;

import static com.ecommerce.scheduletime.CreateAccount.Fragment.LoginFragment.getCategories;
import static com.ecommerce.scheduletime.CreateAccount.Fragment.LoginFragment.getNotes;
import static com.ecommerce.scheduletime.CreateAccount.Fragment.LoginFragment.getTasks;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
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
import android.widget.LinearLayout;
import android.widget.Toast;

import com.ecommerce.scheduletime.CreateAccount.FacebookAuthActivity;
import com.ecommerce.scheduletime.CreateAlarmNotification;
import com.ecommerce.scheduletime.HomeActivity.MainActivity;
import com.ecommerce.scheduletime.R;
import com.ecommerce.scheduletime.Sync.SyncDataBaseService;
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
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;

import java.util.Calendar;

public class SignUpFragment extends Fragment {

    private static final int RC_SIGN_IN = 7;
    private ProgressDialog progressDialog;
    private TextInputEditText sign_up_name, sign_up_email, sign_up_password;
    private FirebaseAuth mAuth;
    private DatabaseReference reference;
    private Button button_signUp;
    private LinearLayout linearLayoutSignIn, linearLayoutGoogle, linearLayoutFacebook, linearLayoutTwitter;

    GoogleSignInOptions gso;
    GoogleSignInClient gsc;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sign_up, container, false);

        sign_up_name = view.findViewById(R.id.sign_up_name);
        sign_up_email = view.findViewById(R.id.sign_up_email);
        sign_up_password = view.findViewById(R.id.sign_up_password);
        button_signUp = view.findViewById(R.id.button_signUp);
        linearLayoutSignIn = view.findViewById(R.id.linearLayoutSignIn);
        linearLayoutGoogle = view.findViewById(R.id.linearLayoutGoogle);
        linearLayoutFacebook = view.findViewById(R.id.linearLayoutFacebook);
        linearLayoutTwitter = view.findViewById(R.id.linearLayoutTwitter);

        mAuth = FirebaseAuth.getInstance();

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        gsc = GoogleSignIn.getClient(container.getContext(), gso);
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(container.getContext());

        button_signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sign_up(view);
            }
        });

        linearLayoutGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (account == null) {
                    SignInWithGoogle();
                }
            }
        });

        linearLayoutFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), FacebookAuthActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
            }
        });

        linearLayoutTwitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SignInWithTwitter();
            }
        });

        linearLayoutSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragmentContainerViewAuth, new LoginFragment())
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
                                    Toast.makeText(getContext(), getString(R.string.success_), Toast.LENGTH_SHORT).show();

                                    // Save data of user in SharedPreferences.
                                    SharedPreferences.Editor editor = getContext().getSharedPreferences("USER_INFO", MODE_PRIVATE).edit();
                                    editor.putString("user_full_name", mAuth.getCurrentUser().getDisplayName());
                                    editor.putString("user_email", mAuth.getCurrentUser().getEmail());
                                    editor.putString("user_photo_uri", mAuth.getCurrentUser().getPhotoUrl().toString().replace("_normal", "_bigger"));
                                    editor.putString("user_auth_with", "Twitter");
                                    editor.apply();

                                    progressSync();

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
                                    Toast.makeText(getContext(), getString(R.string.success_), Toast.LENGTH_SHORT).show();

                                    // Save data of user in SharedPreferences.
                                    SharedPreferences.Editor editor = getContext().getSharedPreferences("USER_INFO", MODE_PRIVATE).edit();
                                    editor.putString("user_full_name", mAuth.getCurrentUser().getDisplayName());
                                    editor.putString("user_email", mAuth.getCurrentUser().getEmail());
                                    editor.putString("user_photo_uri", mAuth.getCurrentUser().getPhotoUrl().toString().replace("_normal", "_bigger"));
                                    editor.putString("user_auth_with", "Twitter");
                                    editor.apply();

                                    progressSync();

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

    public void sign_up(View view) {
        FirebaseAuth.getInstance().signOut();

        //acceder a la bese de donner
        String string_name = sign_up_name.getText().toString().trim();
        String string_email = sign_up_email.getText().toString().trim();
        String string_password = sign_up_password.getText().toString().trim();

        if (TextUtils.isEmpty(string_name)) {
            sign_up_name.setError(getString(R.string.name_required));
            return;
        }
        if (TextUtils.isEmpty(string_email)) {
            sign_up_email.setError(getString(R.string.email_required));
            return;
        }
        if (TextUtils.isEmpty(string_password)) {
            sign_up_password.setError(getString(R.string.password_required));
            return;
        }
        if (string_password.length() < 6) {
            sign_up_password.setError(getString(R.string.password_must_be_more_then_six_caractaire));
            return;
        }
        //progress Dialog
        progress(string_name, string_email, string_password);
    }

    private void progress(String string_name, String string_email, String string_password) {
        progressDialog = new ProgressDialog(getContext());
        progressDialog.show();
        progressDialog.setContentView(R.layout.progress_dialog);
        progressDialog.getWindow().setBackgroundDrawableResource(R.color.transparent);

        //register the user in Firebase
        mAuth.createUserWithEmailAndPassword(string_email, string_password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Uri photoUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE +
                            "://" + getResources().getResourcePackageName(R.drawable.ic_avatar_default)
                            + '/' + getResources().getResourceTypeName(R.drawable.ic_avatar_default)
                            + '/' + getResources().getResourceEntryName(R.drawable.ic_avatar_default));

                    // Add DisplayName and PhotoUri to user.
                    mAuth.getCurrentUser().updateProfile(new UserProfileChangeRequest.Builder()
                            .setDisplayName(string_name)
                            .setPhotoUri(photoUri)
                            .build());

                    // Save data of user in SharedPreferences.
                    SharedPreferences.Editor editor = getContext().getSharedPreferences("USER_INFO", MODE_PRIVATE).edit();
                    editor.putString("user_full_name", string_name); // mAuth.getCurrentUser().getDisplayName()
                    editor.putString("user_email", string_email); // mAuth.getCurrentUser().getEmail()
                    editor.putString("user_photo_uri", photoUri.toString()); // mAuth.getCurrentUser().getPhotoUrl().toString
                    editor.putString("user_auth_with", "Firebase");
                    editor.apply();

                    // Start MainActivity.
                    progressDialog.dismiss();
                    progressSync();

                } else {
                    Toast.makeText(getContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
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

        Intent intent = new Intent(getContext(), SyncDataBaseService.class);
        getContext().startService(intent);

        startScheduleTime();
    }

    private void startScheduleTime() {
        if (getTasks && getCategories && getNotes){
            progressDialog.dismiss();
            // Start MainActivity.
            Intent intent = new Intent(getContext(), MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);

            SharedPreferences.Editor editor = getContext().getSharedPreferences("Data has been extracted from firebase", MODE_PRIVATE).edit();
            editor.putBoolean("change", true);
            editor.apply();

            /** @param call {@link CreateAlarmNotification} when user sign in -- */
            Intent intent2 = new Intent(getContext(), CreateAlarmNotification.class);
            getContext().startService(intent2);

            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);

            Intent intent3 = new Intent(getContext(), CreateAlarmNotification.class);
            PendingIntent pendingIntent = PendingIntent.getService(getContext(), 0, intent3, PendingIntent.FLAG_UPDATE_CURRENT);
            AlarmManager alarmManager = (AlarmManager) getContext().getSystemService(Context.ALARM_SERVICE);
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                    AlarmManager.INTERVAL_DAY, pendingIntent);

        }else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    startScheduleTime();
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
                Toast.makeText(getContext(), getString(R.string.there_is_an_error)+ " :" + e.getMessage(), Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(getContext(), getString(R.string.success_), Toast.LENGTH_SHORT).show();

                            // Save data of user in SharedPreferences.
                            SharedPreferences.Editor editor = getContext().getSharedPreferences("USER_INFO", MODE_PRIVATE).edit();
                            editor.putString("user_full_name", mAuth.getCurrentUser().getDisplayName());
                            editor.putString("user_email", mAuth.getCurrentUser().getEmail());
                            editor.putString("user_photo_uri", mAuth.getCurrentUser().getPhotoUrl().toString());
                            editor.putString("user_auth_with", "Google");
                            editor.apply();

                            //finish top activities
                            progressSync();
                        } else {
                            Toast.makeText(getContext(), getString(R.string.failed), Toast.LENGTH_SHORT).show();
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
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragmentContainerViewAuth, new LoginFragment())
                            .addToBackStack(null)
                            .commit();
                    return true;
                }
                return false;
            }
        });
    }
}