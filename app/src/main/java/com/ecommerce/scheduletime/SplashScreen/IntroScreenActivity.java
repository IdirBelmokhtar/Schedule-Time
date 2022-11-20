package com.ecommerce.scheduletime.SplashScreen;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ecommerce.scheduletime.CreateAccount.AuthenticationActivity;
import com.ecommerce.scheduletime.R;

public class IntroScreenActivity extends AppCompatActivity {

    private ViewPager slide_intro_ViewPager;
    private LinearLayout dots_intro_Layout;
    private TextView skip_intro;

    private Button start_btn;

    private int mCurrentPage;

    private TextView[] mDots;

    private SliderIntroAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro_screen);

        //getSupportActionBar().hide();
        //getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.green_write_2));

        this.slide_intro_ViewPager = (ViewPager) findViewById(R.id.slide_intro_ViewPager);
        this.dots_intro_Layout = (LinearLayout) findViewById(R.id.dots_intro_Layout);
        this.skip_intro = (TextView) findViewById(R.id.skip_intro);
        this.start_btn = (Button) findViewById(R.id.start_btn);

        adapter = new SliderIntroAdapter(this);

        slide_intro_ViewPager.setAdapter(adapter);

        addDotsIndicator(0);

        slide_intro_ViewPager.addOnPageChangeListener(listener);

        start_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences preferences = getSharedPreferences("intro", MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("state", "done");
                editor.apply();

                startActivity(new Intent(getApplicationContext(), AuthenticationActivity.class));
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();
            }
        });

        skip_intro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences preferences = getSharedPreferences("intro", MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("state", "done");
                editor.apply();

                startActivity(new Intent(getApplicationContext(), AuthenticationActivity.class));
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();
            }
        });
    }

    public void addDotsIndicator(int position) {

        mDots = new TextView[3];
        dots_intro_Layout.removeAllViews();

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(4, 0, 4, 0);

        for (int i = 0; i < mDots.length; i++) {
            //The Unicode and HTML Entities of Bullet Point is (&#8226;) you find it here https://unicode-table.com/fr/html-entities/
            mDots[i] = new TextView(this);
            mDots[i].setText(Html.fromHtml("&#8728;"));
            mDots[i].setTextSize(32);
            //DARK MODE
            SharedPreferences preferences_ = getSharedPreferences("dark_mode", MODE_PRIVATE);
            String state = preferences_.getString("state", "");
            if (state.equals("true")) {
                mDots[i].setTextColor(getResources().getColor(R.color.white_));
            } else if (state.equals("false")) {
                mDots[i].setTextColor(getResources().getColor(R.color.blue));
            } else {
                mDots[i].setTextColor(getResources().getColor(R.color.blue));
            }
            mDots[i].setLayoutParams(params);

            dots_intro_Layout.addView(mDots[i]);
        }

        if (mDots.length > 0) {
            mDots[position].setText(Html.fromHtml("&#8226;"));
            mDots[position].setTextSize(44);
            //DARK MODE
            SharedPreferences preferences_ = getSharedPreferences("dark_mode", MODE_PRIVATE);
            String state = preferences_.getString("state", "");
            if (state.equals("true")) {
                mDots[position].setTextColor(getResources().getColor(R.color.white_));
            } else if (state.equals("false")) {
                mDots[position].setTextColor(getResources().getColor(R.color.blue));
            } else {
                mDots[position].setTextColor(getResources().getColor(R.color.blue));
            }
            mDots[position].setLayoutParams(params);
        }

    }

    ViewPager.OnPageChangeListener listener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            addDotsIndicator(position);

            mCurrentPage = position;
            if (position == mDots.length - 1) {
                dots_intro_Layout.setVisibility(View.GONE);
                start_btn.setVisibility(View.VISIBLE);
            } else {
                start_btn.setVisibility(View.GONE);
                dots_intro_Layout.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };
}