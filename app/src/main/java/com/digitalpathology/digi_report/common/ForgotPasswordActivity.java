package com.digitalpathology.digi_report.common;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.digitalpathology.digi_report.R;
import com.digitalpathology.digi_report.utils.ConnectionDetector;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordActivity extends AppCompatActivity {

    private final String TAG = ForgotPasswordActivity.class.getSimpleName();
    private CardView sendLink;
    private EditText email;
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private ConnectionDetector connectionDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        email = findViewById(R.id.cv_fp_email);
        sendLink = findViewById(R.id.btn_send_link);
        connectionDetector = new ConnectionDetector(this);


        sendLink.setOnClickListener(v ->{
            if(connectionDetector.isInternetAvailble()) {
                auth.sendPasswordResetEmail(email.getText().toString())
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                startActivity(new Intent(ForgotPasswordActivity.this, LoginActivity.class));
                                finish();
                            }else{
                                Toast.makeText(this, "Something went wrong, Please try after sometime", Toast.LENGTH_SHORT).show();
                            }
                        });
            }else {
                Toast.makeText(this, "Internet unavailable", Toast.LENGTH_SHORT).show();
            }
        });

    }
}