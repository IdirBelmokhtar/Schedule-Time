package com.ecommerce.scheduletime.HomeActivity;

import static android.provider.ContactsContract.Intents.Insert.ACTION;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.core.os.BuildCompat;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ecommerce.scheduletime.Dialog.BottomDialogLanguages;
import com.ecommerce.scheduletime.R;
import com.google.android.gms.tasks.Task;
import com.google.android.play.core.review.ReviewInfo;
import com.google.android.play.core.review.ReviewManager;
import com.google.android.play.core.review.ReviewManagerFactory;

import java.util.Locale;

public class SettingsActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private LinearLayout settings_notification, settings_language, settings_rate, settings_help, settings_about_us, settings_privacy_policy, settings_share_app;
    private TextView settings_theme_txt;
    private SwitchCompat switchCompat;

    ReviewInfo reviewInfo = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        toolbar = findViewById(R.id.toolbar_settings);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        settings_notification = findViewById(R.id.settings_notification);
        settings_notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Open Notification settings
                Intent settingsIntent = new Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        .putExtra(Settings.EXTRA_APP_PACKAGE, getPackageName());
                //.putExtra(Settings.EXTRA_CHANNEL_ID, MY_CHANNEL_ID);
                startActivity(settingsIntent);
            }
        });

        settings_rate = findViewById(R.id.settings_rate);
        settings_rate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Review in google play
                ReviewManager manager = ReviewManagerFactory.create(SettingsActivity.this);
                Task<ReviewInfo> request = manager.requestReviewFlow();
                request.addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // We can get the ReviewInfo object
                        reviewInfo = task.getResult();
                    } else {
                        Toast.makeText(SettingsActivity.this, getResources().getString(R.string.review_failed_to_start), Toast.LENGTH_SHORT).show();
                    }
                });
                if (reviewInfo != null) {
                    Task<Void> flow = manager.launchReviewFlow((Activity) SettingsActivity.this, reviewInfo);
                    flow.addOnCompleteListener(task -> {
                        // The flow has finished. The API does not indicate whether the user
                        // reviewed or not, or even whether the review dialog was shown. Thus, no
                        // matter the result, we continue our app flow.
                    });
                }
            }
        });

        settings_help = findViewById(R.id.settings_help);
        settings_help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.behance.net/gallery/126520417/To-Do-App-Design")));
            }
        });

        settings_about_us = findViewById(R.id.settings_about_us);
        settings_about_us.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SettingsActivity.this, ContactUsActivity.class));
            }
        });

        settings_share_app = findViewById(R.id.settings_share_app);
        settings_share_app.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("text/plain");
                i.putExtra(Intent.EXTRA_SUBJECT, "Sharing URL");
                i.putExtra(Intent.EXTRA_TEXT, "https://play.google.com/store/apps/details?id=" + getPackageName());
                startActivity(Intent.createChooser(i, "Share URL"));
            }
        });

        settings_privacy_policy = findViewById(R.id.settings_privacy_policy);
        settings_privacy_policy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.termsfeed.com/live/53b04044-cba4-403b-8dcb-c87190596b00")));
            }
        });

        switchCompat = findViewById(R.id.settings_switchCompat);
        settings_theme_txt = findViewById(R.id.settings_theme_txt);

        //DARK MODE
        SharedPreferences preferences = getSharedPreferences("dark_mode", MODE_PRIVATE);
        String state = preferences.getString("state", "");

        if (state.equals("true")) {
            switchCompat.setChecked(true);
            settings_theme_txt.setText(getResources().getString(R.string.sombre));
        } else if (state.equals("false")) {
            switchCompat.setChecked(false);
            settings_theme_txt.setText(getResources().getString(R.string.clair));
        } else {
            if (BuildCompat.isAtLeastP()) {
                settings_theme_txt.setText(getResources().getString(R.string.auto));
            } else {
                settings_theme_txt.setText(getResources().getString(R.string.auto_battery));
            }

        }

        switchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {

                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    switchCompat.setChecked(true);
                    settings_theme_txt.setText(getResources().getString(R.string.sombre));

                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("state", "true");
                    editor.apply();
                } else {

                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    switchCompat.setChecked(false);
                    settings_theme_txt.setText(getResources().getString(R.string.auto));

                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("state", "false");
                    editor.apply();
                }
                refresh();

                //getWindow().setWindowAnimations(R.style.WindowAnimationFadeInOut);
                //recreate();

                //Intent intent = new Intent();
                //intent.setAction("action_theme_changed");
            }
        });

        settings_language = findViewById(R.id.settings_language);
        TextView language = (TextView) findViewById(R.id.lang);
        SharedPreferences pref = getSharedPreferences("language", MODE_PRIVATE);
        String lang = pref.getString("lang", "");

        if (!lang.equals("")){
            if (lang.equals("en")){
                language.setText(getResources().getString(R.string.lang_english));
            }else if (lang.equals("fr")){
                language.setText(getResources().getString(R.string.lang_french));
            }else if (lang.equals("ar")){
                language.setText(getResources().getString(R.string.lang_arabic));
            }
        }else {
            String lang_ = Locale.getDefault().getLanguage();
            if (!lang_.equals("en") && !lang_.equals("fr") && !lang_.equals("ar")){
                language.setText(getResources().getString(R.string.system_default) + " : " + Locale.getDefault().getDisplayName() + " " + getResources().getString(R.string.currently_unavailable));
            }else {
                language.setText(getResources().getString(R.string.system_default) + " : " + Locale.getDefault().getDisplayName());
            }
        }
        settings_language.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = getSharedPreferences("language", MODE_PRIVATE).edit();

                BottomDialogLanguages bottomDialogLanguages = new BottomDialogLanguages((Activity) SettingsActivity.this);

                String lang_ = Locale.getDefault().getLanguage();
                if (!lang_.equals("en") && !lang_.equals("fr") && !lang_.equals("ar")){
                    bottomDialogLanguages.getDialog_lang_default_system_().setText(getResources().getString(R.string.system_default) + " : " + Locale.getDefault().getDisplayName() + " " + getResources().getString(R.string.currently_unavailable));
                }else {
                    bottomDialogLanguages.getDialog_lang_default_system_().setText(getResources().getString(R.string.system_default) + " : " + Locale.getDefault().getDisplayName());
                }

                bottomDialogLanguages.getDialog_lang_default_system().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String lang_ = Locale.getDefault().getLanguage();
                        if (!lang_.equals("en") && !lang_.equals("fr") && !lang_.equals("ar")){
                            language.setText(getResources().getString(R.string.system_default) + " : " + Locale.getDefault().getDisplayName() + " " + getResources().getString(R.string.currently_unavailable));
                        }else {
                            language.setText(getResources().getString(R.string.system_default) + " : " + Locale.getDefault().getDisplayName());
                        }

                        editor.putString("lang", "");
                        editor.apply();
                        bottomDialogLanguages.dismiss();
                        refresh();
                    }
                });
                bottomDialogLanguages.getDialog_lang_arabic().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        language.setText(getResources().getString(R.string.lang_arabic));

                        editor.putString("lang", "ar");
                        editor.apply();
                        bottomDialogLanguages.dismiss();
                        refresh();
                    }
                });
                bottomDialogLanguages.getDialog_lang_english().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        language.setText(getResources().getString(R.string.lang_english));

                        editor.putString("lang", "en");
                        editor.apply();
                        bottomDialogLanguages.dismiss();
                        refresh();
                    }
                });
                bottomDialogLanguages.getDialog_lang_french().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        language.setText(getResources().getString(R.string.lang_french));

                        editor.putString("lang", "fr");
                        editor.apply();
                        bottomDialogLanguages.dismiss();
                        refresh();
                    }
                });

                bottomDialogLanguages.build();
                bottomDialogLanguages.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                bottomDialogLanguages.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                bottomDialogLanguages.getWindow().setWindowAnimations(R.style.SheetDialogAnimation);
                bottomDialogLanguages.getWindow().setGravity(Gravity.BOTTOM);


                /*
                final String[] listItems = {"English", "French", "عربي"};
                AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this);
                builder.setTitle("Choose Language ...");
                builder.setSingleChoiceItems(listItems, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (i == 0) {
                            setLocale("En");
                            recreate();
                        } else if (i == 1) {
                            setLocale("Fr");
                            recreate();
                        } else if (i == 2) {
                            setLocale("Ar");
                            recreate();
                        }
                        dialogInterface.dismiss();
                    }
                });
                AlertDialog langDialog = builder.create();
                langDialog.show();
*/
/*
                DialogLanguage dialogLanguage = new DialogLanguage(SettingsActivity.this);
                dialogLanguage.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialogLanguage.getSave().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (dialogLanguage.getDialog_language_RB1().isChecked()) {
                            setLocale("en");
                            recreate();
                        } else if (dialogLanguage.getDialog_language_RB2().isChecked()) {
                            setLocale("fr");
                            recreate();
                        } else if (dialogLanguage.getDialog_language_RB3().isChecked()) {
                            setLocale("ar");
                            recreate();
                        }

                        Toast.makeText(SettingsActivity.this, "save", Toast.LENGTH_SHORT).show();
                        dialogLanguage.dismiss();
                    }
                });
                dialogLanguage.build();
                Window window = dialogLanguage.getWindow();
                window.setLayout(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            */
            }
        });

    }

    private void refresh() {
        Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        /*Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("openFragment", "ProfileFragment");
        startActivity(intent);*/
        //overridePendingTransition(R.anim.slide_in_y, R.anim.slide_out_y);
    }
}