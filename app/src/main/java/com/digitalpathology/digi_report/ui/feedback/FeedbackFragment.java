package com.digitalpathology.digi_report.ui.feedback;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.digitalpathology.digi_report.R;
import com.digitalpathology.digi_report.utils.ConnectionDetector;
import com.google.android.play.core.review.ReviewInfo;
import com.google.android.play.core.review.ReviewManager;
import com.google.android.play.core.review.ReviewManagerFactory;
import com.google.android.play.core.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class FeedbackFragment extends Fragment {

    private FeedbackViewModel mViewModel;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private TextView totalReports, title;
    private EditText fb;
    private CardView submitBtn;
    private ConnectionDetector con;
    private ReviewInfo reviewInfo;
    private DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference();
    private FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

    private final String TAG = "FeedbackFragment";

    public static FeedbackFragment newInstance() {
        return new FeedbackFragment();
    }

    @Override
    public void onResume() {
        super.onResume();
        title.setText("Feedback");
    }

    @Override
    public void onStart() {
        super.onStart();
        // getting user
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String totalreports = pref.getString(String.valueOf(R.string.shared_pref_total_reports), "null");

        if(totalreports.contentEquals("null")){
            if(con.isInternetAvailble()){
                //read user data from firestore
                DocumentReference docRef = db.collection("users").document(currentUser.getUid());
                docRef.get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                            totalReports.setText(String.valueOf(document.get("numberOfReportsUploaded")));
                            SharedPreferences.Editor editor = pref.edit();
                            editor.putString(String.valueOf(R.string.shared_pref_total_reports), String.valueOf(document.get("numberOfReportsUploaded")));
                            editor.commit();

                        } else {
                            Log.d(TAG, "user does not exist");
                        }
                    } else {
                        Log.d(TAG, "get failed with ", task.getException());
                    }
                });
            }else{
                Toast.makeText(getActivity(), "Internet Unavailable", Toast.LENGTH_SHORT).show();
            }
        }else {
            totalReports.setText(totalreports);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_feedback, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(FeedbackViewModel.class);
        con = new ConnectionDetector(getActivity());
        fb = getActivity().findViewById(R.id.edittext_feedback);
        submitBtn = getActivity().findViewById(R.id.btn_submit);

        submitBtn.setOnClickListener(v -> {
            sendfeedback(fb.getText().toString());
            fb.setText("");
        });

        //code to change a hamburger icon
        Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
        toolbar.post(() -> {
            Drawable d = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_menu28, null);
            toolbar.setNavigationIcon(d);
        });

        title = getActivity().findViewById(R.id.toolbar_title);
        title.setText("Feedback");

        totalReports = getActivity().findViewById(R.id.total_saved_report);

        launchInAppReview();
    }

    private void launchMarket() {
        Uri uri = Uri.parse("market://details?id=" + getActivity().getPackageName());
        Intent myAppLinkToMarket = new Intent(Intent.ACTION_VIEW, uri);
        try {
            startActivity(myAppLinkToMarket);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(getActivity(), " unable to find market app", Toast.LENGTH_LONG).show();
        }
    }

    private void launchInAppReview(){

        ReviewManager manager = ReviewManagerFactory.create(getActivity());
        Task<ReviewInfo> request = manager.requestReviewFlow();
        request.addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // We can get the ReviewInfo object
                reviewInfo = task.getResult();
                Log.d(TAG, "launchInAppReview: no problem");

                Task<Void> flow = manager.launchReviewFlow(getActivity(), reviewInfo);
                flow.addOnCompleteListener(task1 -> {
                    Log.d(TAG, "launchInAppReview: launchreview has prob");
                    // The flow has finished. The API does not indicate whether the user
                    // reviewed or not, or even whether the review dialog was shown. Thus, no
                    // matter the result, we continue our app flow.
                });
            } else {
                // There was some problem, log or handle the error code.
//                @ReviewErrorCode int reviewErrorCode = ((TaskException) task.getException()).getErrorCode();
                Log.d(TAG, "launchInAppReview: problem");
//                Toast.makeText(getActivity(), "Issues", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void sendfeedback(String feedback){
        //adding feedback to firebase realtime database
        Long ts = System.currentTimeMillis();
        if(con.isInternetAvailble())
            databaseRef.child("feedback/").child(currentUser.getUid()).child("" + ts).child("feedback").setValue(feedback);
        else
            Toast.makeText(getActivity(), "Internet Unavailable", Toast.LENGTH_SHORT).show();
    }
}