package com.digitalpathology.digi_report.common;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.digitalpathology.digi_report.R;
import com.digitalpathology.digi_report.helper_classes.SliderAdapter;

public class OnBoarding extends AppCompatActivity {

    ViewPager viewPager;
    LinearLayout dotsLayout;
    SliderAdapter sliderAdapter;
    TextView dots[];

    Button getStarted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_on_boarding);

        //hooks
        viewPager = findViewById(R.id.view_pager);
        dotsLayout = findViewById(R.id.linear_layout_dots);
        getStarted = findViewById(R.id.btn_getstarted);

        sliderAdapter = new SliderAdapter(this);
        viewPager.setAdapter(sliderAdapter);

        addDots(0);
        viewPager.addOnPageChangeListener(changeListener);

        getStarted.setVisibility(View.GONE);
        getStarted.setOnClickListener(v -> {
            startActivity(new Intent(OnBoarding.this, SignUpActivity.class));
            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplication());
            SharedPreferences.Editor editor = pref.edit();
            editor.putBoolean(getString(R.string.shared_pref_first_time_user), false);
            finish();
        });
    }

    private void addDots(int pos){

        dots = new TextView[3];
        dotsLayout.removeAllViews();

        for(int i=0;i<dots.length;i++){
            dots[i] = new TextView(this);
            dots[i].setText(Html.fromHtml("&#8226;"));
            dots[i].setTextSize(35);

            dotsLayout.addView(dots[i]);
        }

        if (dots.length > 0){
            dots[pos].setTextColor(getResources().getColor(R.color.colorPrimaryDark));
        }

        if(pos==2){
            getStarted.setVisibility(View.VISIBLE);
        }else{
            getStarted.setVisibility(View.GONE);
        }

    }

    ViewPager.OnPageChangeListener changeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            addDots(position);
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };
}
