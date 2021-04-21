package com.digitalpathology.digi_report.common;

import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;

public class SavePhotoTask extends AsyncTask<byte[], String, String> {

    String savephotoName;

    public SavePhotoTask(String savephotoName) {
        this.savephotoName = savephotoName;
    }

    @Override
    protected String doInBackground(byte[]... jpeg) {
        File photo=new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), savephotoName);

        if (photo.exists()) {
            photo.delete();
        }

        try {
            FileOutputStream fos=new FileOutputStream(photo.getPath());

            fos.write(jpeg[0]);
            fos.close();
        }
        catch (java.io.IOException e) {
            Log.e("PictureDemo", "Exception in photoCallback", e);
        }

        return(null);
    }
}