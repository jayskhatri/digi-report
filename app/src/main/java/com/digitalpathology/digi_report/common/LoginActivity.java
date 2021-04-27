package com.digitalpathology.digi_report.common;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.digitalpathology.digi_report.DashboardActivity;
import com.digitalpathology.digi_report.R;
import com.digitalpathology.digi_report.object.User;
import com.digitalpathology.digi_report.utils.ConnectionDetector;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {

    private EditText email, pwd;
    private TextView signuphere, forgotPwd;
    private CardView signin;
    private ConnectionDetector connectionDetector;
    private FirebaseAuth mAuth;
    private User user;
    private FirebaseFirestore clouddb = FirebaseFirestore.getInstance();
    private final String TAG = "LoginActivity";

    @Override
    protected void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser!=null && currentUser.isEmailVerified())
            updateUI(currentUser);
    }

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
        forgotPwd = findViewById(R.id.tv_forgot_pwd);
        signin = findViewById(R.id.btn_signin);

        signuphere.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
            finish();
        });

        forgotPwd.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, ForgotPasswordActivity.class));
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

    private void Login(String emailid, String password){
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
                mAuth.signInWithEmailAndPassword(emailid, password)
                        .addOnCompleteListener(this, (OnCompleteListener<AuthResult>) task -> {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "signInWithEmail:success");
                                FirebaseUser currentUser = mAuth.getCurrentUser();
                                dialog.dismiss();

                                /** this may cause a problem because, task1 inside task */

                                SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplication());
                                //read user data from firestore
                                DocumentReference docRef = clouddb.collection("users").document(currentUser.getUid());
                                docRef.get().addOnCompleteListener(task1 -> {
                                    if (task1.isSuccessful()) {
                                        DocumentSnapshot document = task1.getResult();
                                        if (document.exists()) {
                                            Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                                            user = new User(String.valueOf(document.get("uid")), String.valueOf(document.get("name")), String.valueOf(document.get("email")),  String.valueOf(document.get("phone")), Integer.parseInt(String.valueOf(document.get("numberOfReportsUploaded"))));
                                            SharedPreferences.Editor editor = pref.edit();
                                            editor.putString(String.valueOf(R.string.shared_pref_user_name), user.getName());
                                            editor.putString(String.valueOf(R.string.shared_pref_user_email), user.getEmail());
                                            editor.commit();
                                        } else {
                                            Log.d(TAG, "user does not exist");
                                        }
                                    } else {
                                        Log.d(TAG, "get failed with ", task1.getException());
                                    }
                                });
                                if(currentUser.isEmailVerified()) {
                                    updateUI(currentUser);
                                    finish();
                                }
                                else {
                                    Toast.makeText(this, "Please verify your email id", Toast.LENGTH_SHORT).show();
                                    email.setError("Please verify email ID");
                                }

                                SharedPreferences.Editor editor = pref.edit();
                                editor.putBoolean(getString(R.string.shared_pref_first_time_user), false);
                                editor.apply();

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