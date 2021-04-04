package com.digitalpathology.digi_report.ui.my_acc;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.digitalpathology.digi_report.R;
import com.digitalpathology.digi_report.common.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MyAccountFragment extends Fragment {

    private MyAccountViewModel myAccountViewModel;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private TextView username, useremail;

    private final String TAG = "MyAccountFragment";

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        myAccountViewModel =
                new ViewModelProvider(this).get(MyAccountViewModel.class);
        View root = inflater.inflate(R.layout.fragment_my_account, container, false);

        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
        //code to change a hamburger icon
        toolbar.post(() -> {
            Drawable d = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_menu28, null);
            toolbar.setNavigationIcon(d);
        });

        //logout btn
        CardView logoutBtn = getActivity().findViewById(R.id.btn_logout);
        logoutBtn.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(getActivity(), LoginActivity.class));
            getActivity().finish();
            removeAllPreferenceData();
        });

        username = getActivity().findViewById(R.id.username);
        useremail = getActivity().findViewById(R.id.useremail);

    }

    private void removeAllPreferenceData(){
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplication());
        SharedPreferences.Editor editor = pref.edit();
        editor.clear().commit();
    }

    @Override
    public void onStart() {
        super.onStart();

        /**
         * Logic for updating the user name and user email inside the my account screen
         */

        // getting user
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        //read user data from firestore
        DocumentReference docRef = db.collection("users").document(currentUser.getUid());
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    Log.d(TAG, "DocumentSnapshot data: " + document.getData());
//                    totalReports.setText(String.valueOf(document.get("numberOfReportsUploaded")));
//                    user = new User(String.valueOf(document.get("uid")), String.valueOf(document.get("name")), String.valueOf(document.get("email")),  String.valueOf(document.get("phone")));
                        useremail.setText(String.valueOf(document.get("email")));
                        username.setText(String.valueOf(document.get("name")));
                } else {
                    Log.d(TAG, "user does not exist");
                }
            } else {
                Log.d(TAG, "get failed with ", task.getException());
            }
        });
    }
}