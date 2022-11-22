package com.ecommerce.scheduletime.SplashScreen;

import android.content.Context;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.ecommerce.scheduletime.R;

public class SliderIntroAdapter extends PagerAdapter {

    Context context;
    LayoutInflater layoutInflater;

    public SliderIntroAdapter(Context context) {
        this.context = context;
    }

    //Array
    public int[] slide_intro_image_ = {
            R.drawable.walcome1,
            R.drawable.walcome2,
            R.drawable.walcome3
    };
    public String[] slide_intro_heading_ = {
            context.getResources().getString(R.string.slide_intro_heading_one),
            context.getResources().getString(R.string.slide_intro_heading_two),
            context.getResources().getString(R.string.slide_intro_heading_three)

    };
    public String[] slide_intro_description_ = {
            context.getResources().getString(R.string.slide_intro_description_one),
            context.getResources().getString(R.string.slide_intro_description_two),
            context.getResources().getString(R.string.slide_intro_description_three),
    };

    @Override
    public int getCount() {
        return slide_intro_image_.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == (LinearLayout) object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.slide_intro_layout, container, false);

        ImageView slide_intro_image = (ImageView) view.findViewById(R.id.slide_intro_image);
        TextView slide_intro_heading = (TextView) view.findViewById(R.id.slide_intro_heading);
        TextView slide_intro_description = (TextView) view.findViewById(R.id.slide_intro_description);

        slide_intro_image.setImageResource(slide_intro_image_[position]);
        slide_intro_heading.setText(slide_intro_heading_[position]);
        slide_intro_heading.setTypeface(slide_intro_heading.getTypeface(), Typeface.BOLD);
        slide_intro_description.setText(slide_intro_description_[position]);
        slide_intro_description.setGravity(Gravity.CENTER_HORIZONTAL);

        container.addView(view);

        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {

        container.removeView((LinearLayout)object);

    }
}
