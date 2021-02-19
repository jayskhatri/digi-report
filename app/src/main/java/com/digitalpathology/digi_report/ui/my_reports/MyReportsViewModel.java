package com.digitalpathology.digi_report.ui.my_reports;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class MyReportsViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public MyReportsViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is gallery fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}