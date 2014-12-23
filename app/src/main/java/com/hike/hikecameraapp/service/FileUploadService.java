package com.hike.hikecameraapp.service;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Intent;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by adarshpandey on 12/20/14.
 */
public class FileUploadService extends IntentService {
    public static final String FILE_TO_BE_UPLOAD = "fileToBeUpload";

    private NotificationManager nm;
    private final Calendar time = Calendar.getInstance();


    private ArrayList<String> mFileTobeUpload;

    private android.os.Handler mHandler;

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     */
    public FileUploadService() {
        super("");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        mFileTobeUpload = (ArrayList<String>) intent.getSerializableExtra(FILE_TO_BE_UPLOAD);

        for (String filePath: mFileTobeUpload) {
            uploadFileToServer(new File(filePath));
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();

        nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        Toast.makeText(this, "Service created at " + time.getTime(),
                Toast.LENGTH_LONG).show();
        // showNotification();

        mHandler = new android.os.Handler();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    public boolean uploadFileToServer(final File file) {
            // Bulky HTTP operation
        try {
            Thread.sleep(2000);
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(), "File uploaded :" + file.getAbsolutePath(), Toast.LENGTH_SHORT).show();
                }
            }, 100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
//            Toast.makeText(getApplicationContext(), "File uploaded :" + file.getAbsolutePath(), Toast.LENGTH_SHORT).show();

        return true;
    }
}
