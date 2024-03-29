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

import com.digitalpathology.digi_report.R;
import com.digitalpathology.digi_report.object.User;
import com.digitalpathology.digi_report.utils.ConnectionDetector;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class SignUpActivity extends AppCompatActivity {

    private EditText name, email, phone, pwd;
    private TextView signinhere;
    private CardView signup;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final String TAG = "SignUpActivity";

    private ConnectionDetector connectionDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mAuth = FirebaseAuth.getInstance();
        connectionDetector = new ConnectionDetector(this);

        mAuth = FirebaseAuth.getInstance();

        //hooks
        name = findViewById(R.id.edittext_su_name);
        email = findViewById(R.id.edittext_su_email);
        phone = findViewById(R.id.edittext_su_mobile);
        pwd = findViewById(R.id.edittext_su_pwd);

        signinhere = findViewById(R.id.tv_siginhere);
        signup = findViewById(R.id.btn_signup);

        signinhere.setOnClickListener(v -> {
            startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
            finish();
        });

        signup.setOnClickListener(v -> {
            if(validateFields(name, email, phone, pwd)){
                signUP(name.getText().toString(), email.getText().toString(), pwd.getText().toString(), phone.getText().toString());
            }else {
                Toast.makeText(SignUpActivity.this, "Jay rehne de", Toast.LENGTH_LONG).show();
            }
        });
    }

    boolean validateFields(EditText fullName, EditText email, EditText phone, EditText password){
        if(connectionDetector.isInternetAvailble()) {
            if (fullName.getText().toString().isEmpty()) {
                name.setError("Please Enter valid name");
                return false;
            }
            if (email.getText().toString().isEmpty()) {
                email.setError("Please Enter Email Id");
                return false;
            }
            if(phone.getText().toString().isEmpty()){
                phone.setError("Please Enter Phone number");
                return false;
            }
            if (password.getText().toString().isEmpty()) {
                pwd.setError("Please Enter Password");
                return false;
            }
            if (!email.getText().toString().contains("@")) {
                email.setError("Please Enter valid Email ID");
                return false;
            }
            if(phone.getText().toString().length() < 8 && phone.getText().toString().length() > 15){
                phone.setError("Phone number is invalid");
                return false;
            }
            if (password.getText().toString().length() < 6) {
                pwd.setError("Password should be greater than 6 character");
                return false;
            }
        }else {
            // Generating dialog
            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            // Chain together various setter methods to set the dialog characteristics
            builder.setMessage("Please turn your Internet on or connect to WIFI")
                    .setTitle("No Internet Connection");

            builder.setNeutralButton("OK", (dialogInterface, i) -> {

            });

            // Get the <code><a href="/reference/android/app/AlertDialog.html">AlertDialog</a></code> from <code><a href="/reference/android/app/AlertDialog.Builder.html#create()">create()</a></code>
            AlertDialog dialog = builder.create();
            dialog.show();
            return false;
        }
        return true;
    }

    void signUP(final String sfullName, String semail, String spassword, String sphone){
        if(connectionDetector.isInternetAvailble()) {
            mAuth.createUserWithEmailAndPassword(semail, spassword)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();

                            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplication());
                            SharedPreferences.Editor editor = pref.edit();
                            editor.putString(String.valueOf(R.string.shared_pref_user_name), sfullName);
                            editor.putString(String.valueOf(R.string.shared_pref_user_email), semail);
                            editor.putBoolean(getString(R.string.shared_pref_first_time_user), false);
                            editor.commit();

                            User dbuser = new User(user.getUid(), sfullName, semail, sphone, 0);
                            db.collection("users").document(user.getUid()).set(dbuser);

                            user.sendEmailVerification()
                                    .addOnCompleteListener(task1 -> {
                                        if (task1.isSuccessful()) {
                                            Toast.makeText(this, "Verification email sent", Toast.LENGTH_SHORT).show();
                                        }
                                    });

                            updateUI(user);
                            finish();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            if (task.getException().getMessage().contains("The email address is badly formatted.")) {
                                email.setError("Please enter valid email id");
                            } else if (task.getException().getMessage().contains("The email address is already in use by another account")) {
                                email.setError("Email ID is already in use");
                                Toast.makeText(SignUpActivity.this, "Email ID is already registered",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }else {
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

    @Override
    protected void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null && currentUser.isEmailVerified())
            updateUI(currentUser);
    }

    void updateUI(FirebaseUser currentUser){
        Log.d(TAG, "current User");
        if(currentUser!=null) {
            Toast.makeText(this, "Please verify your email id and then login", Toast.LENGTH_SHORT).show();
            Intent i = new Intent(SignUpActivity.this, LoginActivity.class);
//            i.putExtra("CURRENT_USER", currentUser);
            startActivity(i);
            finish();
        }
    }
}

