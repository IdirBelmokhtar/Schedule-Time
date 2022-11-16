package com.ecommerce.scheduletime.HomeActivity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.ecommerce.scheduletime.R;

public class ContactUsActivity extends AppCompatActivity {

    private ImageView backReturn;
    private LinearLayout contactSocialMedia_layout, clickMore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_us);

        backReturn = findViewById(R.id.backReturn);
        backReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        contactSocialMedia_layout = findViewById(R.id.contactSocialMedia_layout);
        clickMore = findViewById(R.id.clickMore);
        contactSocialMedia_layout.setVisibility(View.GONE);

        clickMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (contactSocialMedia_layout.getVisibility() == View.GONE){
                    contactSocialMedia_layout.setVisibility(View.VISIBLE);
                }else {
                    contactSocialMedia_layout.setVisibility(View.GONE);
                }
            }
        });
        clickMore.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Toast.makeText(ContactUsActivity.this, "Social Media", Toast.LENGTH_SHORT).show();
                return true;
            }
        });

        socialMedia();

        findViewById(R.id.dev_phone).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + getResources().getString(R.string.dev_phone))));
            }
        });
        findViewById(R.id.dev_location).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //String uri = String.format(Locale.ENGLISH, "geo:%f,%f", latitude, longitude);
                String uri = "https://www.google.com/maps/search/?api=1&query=" + getResources().getString(R.string.dev_location);

                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(uri)));
            }
        });
    }

    private void socialMedia() {
        ImageView instagram_home, facebook_home, whatsapp_home, messenger_home, gmail_home;

        instagram_home = findViewById(R.id.instagram_home);
        facebook_home = findViewById(R.id.facebook_home);
        whatsapp_home = findViewById(R.id.whatsapp_home);
        messenger_home = findViewById(R.id.messenger_home);
        gmail_home = findViewById(R.id.gmail_home);

        instagram_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.lien_instagram))));
            }
        });
        facebook_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.lien_facebook))));
            }
        });
        whatsapp_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    //send msg
                    Uri uri = Uri.parse("smsto:" + getString(R.string.lien_whatsapp));
                    Intent i = new Intent(Intent.ACTION_SENDTO, uri);
                    i.setPackage("com.whatsapp");
                    startActivity(i);
                } catch (Exception e) {
                    Intent i = new Intent(android.content.Intent.ACTION_VIEW);
                    i.setData(Uri.parse("https://play.google.com/store/apps/details?id=com.whatsapp"));
                    startActivity(i);//https://whatsapp.com/dl/
                    e.printStackTrace();
                }
            }
        });
        messenger_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("fb://messaging/" + getString(R.string.FbUserID)));
                    startActivity(i);

                } catch (Exception e) {
                    Intent i = new Intent(android.content.Intent.ACTION_VIEW);
                    i.setData(Uri.parse("https://play.google.com/store/apps/details?id=com.facebook.orca"));
                    startActivity(i);//https://whatsapp.com/dl/
                    e.printStackTrace();
                }
            }
        });
        gmail_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent emailIntent = new Intent(Intent.ACTION_SEND);
                emailIntent.setType("vnd.android.cursor.dir/email");
                String to[] = {getString(R.string.gmail)};
                emailIntent.putExtra(Intent.EXTRA_EMAIL, to);
                startActivity(Intent.createChooser(emailIntent, "Send email..."));

            }
        });
    }
}