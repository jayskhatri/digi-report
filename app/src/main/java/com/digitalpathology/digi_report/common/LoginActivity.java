package com.digitalpathology.digi_report.common;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.digitalpathology.digi_report.R;

public class LoginActivity extends AppCompatActivity {

    private EditText email, pwd;
    private TextView signuphere;
    private CardView signin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //hooks
        email = findViewById(R.id.edittext_si_email);
        pwd = findViewById(R.id.edittext_si_pwd);
        signuphere = findViewById(R.id.tv_signup_here);
        signin = findViewById(R.id.btn_signin);

        signuphere.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
                finish();
            }
        });


    }
}