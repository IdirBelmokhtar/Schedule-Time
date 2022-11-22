package com.ecommerce.scheduletime.CreateAccount.Fragment;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.ecommerce.scheduletime.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;

public class ForgetPasswordFragment extends Fragment {

    private Button send, signInPassword;
    private TextInputEditText forgot_email;
    private ProgressDialog progressDialog;
    private FirebaseAuth mAuth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_forget_password, container, false);


        forgot_email = view.findViewById(R.id.forgot_email);
        forgot_email.setText(getActivity().getIntent().getStringExtra("Email"));

        mAuth = FirebaseAuth.getInstance();
        send = view.findViewById(R.id.button_send);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                send(view);
            }
        });
        signInPassword = view.findViewById(R.id.button_signInPassword);
        signInPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Open Sign in fragment
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragmentContainerViewAuth, new LoginFragment())
                        .addToBackStack(null)
                        .commit();
            }
        });

        return view;
    }
    public void send(View view) {

        String string_email = forgot_email.getText().toString();

        if (TextUtils.isEmpty(string_email)){
            forgot_email.setError(getString(R.string.email_required));
            return;
        }
        //progress Dialog
        progressDialog = new ProgressDialog(getContext());
        progressDialog.show();
        progressDialog.setContentView(R.layout.progress_dialog);
        progressDialog.getWindow().setBackgroundDrawableResource(R.color.transparent);

        mAuth.sendPasswordResetEmail(string_email).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(getContext(), getResources().getString(R.string.we_have_sent_you_a_recovery_email), Toast.LENGTH_LONG).show();
                //Open Sign in fragment
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragmentContainerViewAuth, new LoginFragment())
                        .addToBackStack(null)
                        .commit();
                progressDialog.dismiss();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                progressDialog.dismiss();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        if(getView() == null){
            return;
        }

        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
                if (keyEvent.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK){
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