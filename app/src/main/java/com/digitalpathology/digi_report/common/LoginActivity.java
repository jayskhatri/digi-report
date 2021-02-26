package com.digitalpathology.digi_report.common;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.digitalpathology.digi_report.DashboardActivity;
import com.digitalpathology.digi_report.R;
import com.digitalpathology.digi_report.utils.ConnectionDetector;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private EditText email, pwd;
    private TextView signuphere;
    private CardView signin;
    private ConnectionDetector connectionDetector;
    private FirebaseAuth mAuth;
    private final String TAG = "LoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        connectionDetector = new ConnectionDetector(this);
        mAuth = FirebaseAuth.getInstance();

        //hooks
        email = findViewById(R.id.edittext_si_email);
        pwd = findViewById(R.id.edittext_si_pwd);
        signuphere = findViewById(R.id.tv_signup_here);
        signin = findViewById(R.id.btn_signin);

        signuphere.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
            finish();
        });

        signin.setOnClickListener(v -> {
            if(validateFields(email.getText().toString(), pwd.getText().toString())){
                Login(email.getText().toString(), pwd.getText().toString());
            }
        });
    }

    boolean validateFields(String semail, String spassword){
        if(connectionDetector.isInternetAvailble()) {
            Log.d(TAG, "validateField: emailID: " + email + "  password: " + spassword);
            if (!semail.contains("@")) {
                email.setError("Please Enter valid Email ID");
                return false;
            }
            if (semail.isEmpty()) {
                email.setError("Please Enter Email Id");
                return false;
            }
            if (spassword.isEmpty()) {
                pwd.setError("Please Enter Password");
                return false;
            }
        }
        else {
            //generating dialog
            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            // 2. Chain together various setter methods to set the dialog characteristics
            builder.setMessage("Please turn your Internet on or connect to WIFI to analyze.")
                    .setTitle("No Internet Connection");

            builder.setNeutralButton("OK", (dialogInterface, i) -> {

            });

            // 3. Get the <code><a href="/reference/android/app/AlertDialog.html">AlertDialog</a></code> from <code><a href="/reference/android/app/AlertDialog.Builder.html#create()">create()</a></code>
            AlertDialog dialog = builder.create();
            dialog.show();
            return false;
        }
        return true;
    }

    private void Login(String email, String password){
        if(connectionDetector.isInternetAvailble()) {
            //generating dialog
            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            // 2. Chain together various setter methods to set the dialog characteristics
            builder.setMessage("We are redirecting you to the main screen")
                    .setTitle("Please wait");

            // 3. Get the <code><a href="/reference/android/app/AlertDialog.html">AlertDialog</a></code> from <code><a href="/reference/android/app/AlertDialog.Builder.html#create()">create()</a></code>
            final AlertDialog dialog = builder.create();
            dialog.show();
            try {
                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this, (OnCompleteListener<AuthResult>) task -> {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "signInWithEmail:success");
                                FirebaseUser user = mAuth.getCurrentUser();
                                dialog.dismiss();
                                updateUI(user);
                                finish();
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "signInWithEmail:failure", task.getException());
                                Toast.makeText(LoginActivity.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            }

                            // ...
                        });
            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
                Toast.makeText(getApplicationContext(), "Something happened", Toast.LENGTH_SHORT).show();
            }
        }
        else {
            //generating dialog
            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            // 2. Chain together various setter methods to set the dialog characteristics
            builder.setMessage("Please turn your Internet on or connect to WIFI to analyze.")
                    .setTitle("No Internet Connection");

            builder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            });

            // 3. Get the <code><a href="/reference/android/app/AlertDialog.html">AlertDialog</a></code> from <code><a href="/reference/android/app/AlertDialog.Builder.html#create()">create()</a></code>
            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }

    private void updateUI(FirebaseUser user) {
        if (user != null){
            Intent i = new Intent(LoginActivity.this, DashboardActivity.class);
            startActivity(i);
            finish();
        }
    }

}