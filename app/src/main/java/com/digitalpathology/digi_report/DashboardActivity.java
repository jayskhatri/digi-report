package com.digitalpathology.digi_report;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.widget.ImageView;
import android.widget.TextView;

import com.digitalpathology.digi_report.ui.my_acc.MyAccountFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class DashboardActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ImageView homeBtn, accSettingsBtn;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private TextView headerUserName, headerUserEmail;

    private final String TAG = "DashboardActivity";

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        //hooks
        homeBtn = findViewById(R.id.btn_home);
        accSettingsBtn = findViewById(R.id.btn_user_acc);

        //reloading dashboard activity
        homeBtn.setOnClickListener(v -> {
            startActivity(new Intent(DashboardActivity.this, DashboardActivity.class));
            finish();
        });

        //opening acc settings
        accSettingsBtn.setOnClickListener(v -> {
            Fragment fragment = new MyAccountFragment();
            loadFragment(fragment);
        });

        //code which gives hamburger icon
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //code to change a hamburger icon
        toolbar.post(() -> {
            Drawable d = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_menu28, null);
            toolbar.setNavigationIcon(d);
        });

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        //header view text
        View headerView = navigationView.getHeaderView(0);
        headerUserName = headerView.findViewById(R.id.header_username);
        headerUserEmail = headerView.findViewById(R.id.header_useremail);

        //setting username and useremail
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplication());
        String fullname = pref.getString(String.valueOf(R.string.shared_pref_user_name), "null");
        String email = pref.getString(String.valueOf(R.string.shared_pref_user_email), "null");
        if(fullname.contentEquals("null") || email.contentEquals("null")){
            // getting user
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            //read user data from firestore
            DocumentReference docRef = db.collection("users").document(currentUser.getUid());
            docRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        headerUserEmail.setText(String.valueOf(document.get("email")));
                        headerUserName.setText(String.valueOf(document.get("name")));

                        SharedPreferences.Editor editor = pref.edit();
                        editor.putString(String.valueOf(R.string.shared_pref_user_name), String.valueOf(document.get("name")));
                        editor.putString(String.valueOf(R.string.shared_pref_user_email), String.valueOf(document.get("email")));
                        editor.commit();

                    } else {
                        Log.d(TAG, "user does not exist");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            });
        }else{
            headerUserName.setText(fullname);
            headerUserEmail.setText(email);
        }


        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_dashboard, R.id.nav_add_reports, R.id.nav_my_reports, R.id.nav_help, R.id.nav_acc_settings)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.dashboard, menu);
        return false;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    private boolean loadFragment(Fragment fragment) {
        //TODO: check here
        //switching fragment
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .addToBackStack(null)
                    .replace(R.id.nav_host_fragment, fragment)
                    .commit();
            return true;
        }
        return false;
    }
}