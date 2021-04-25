package com.digitalpathology.digi_report;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.digitalpathology.digi_report.common.LoginActivity;
import com.digitalpathology.digi_report.common.OnBoarding;
import com.digitalpathology.digi_report.common.SignUpActivity;

public class SplashActivity extends AppCompatActivity {

    private final String TAG = "SplashActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        SharedPreferences sharedPref = SplashActivity.this.getPreferences(Context.MODE_PRIVATE);
        boolean defaultValue = true;
        boolean isAppFirstTimeOpened = sharedPref.getBoolean(getString(R.string.shared_pref_first_time_user), defaultValue);
//        Log.i(TAG, "value isAppFirstTimeOpened: " + isAppFirstTimeOpened);
        if(isAppFirstTimeOpened){
//            Log.i(TAG, "in if value isAppFirstTimeOpened: " + isAppFirstTimeOpened);
            new Handler().postDelayed(() -> {
                startActivity(new Intent(SplashActivity.this, OnBoarding.class));
                finish();
            },3000);
        }

        else {
//            Log.i(TAG, "in else value isAppFirstTimeOpened: " + isAppFirstTimeOpened);
            new Handler().postDelayed(() -> {
                startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                finish();
            }, 3000);
        }
    }
}