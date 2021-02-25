package com.digitalpathology.digi_report.common;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.digitalpathology.digi_report.R;

public class SignUpActivity extends AppCompatActivity {

    private EditText name, email, phone, pwd;
    private TextView signinhere;
    private CardView signup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        //hooks
        name = findViewById(R.id.edittext_su_name);
        email = findViewById(R.id.edittext_su_email);
        phone = findViewById(R.id.edittext_su_mobile);
        pwd = findViewById(R.id.edittext_su_pwd);

        signinhere = findViewById(R.id.tv_siginhere);
        signup = findViewById(R.id.btn_signup);

        signinhere.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
                finish();
            }
        });
    }
}