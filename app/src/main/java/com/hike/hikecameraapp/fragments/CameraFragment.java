package com.hike.hikecameraapp.fragments;

import android.content.Context;
import android.content.Intent;
import android.hardware.Camera;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Gallery;
import android.widget.ImageView;

import com.hike.hikecameraapp.R;
import com.hike.hikecameraapp.components.CameraView;
import com.hike.hikecameraapp.service.FileUploadService;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * Created by adarshpandey on 12/20/14.
 */
public class CameraFragment extends Fragment {
    private static final String TAG = CameraFragment.class.getName();
    private Camera mCamera;
    private CameraView mCameraView;
    private Button mUploadFiles;
    private Gallery mGallery;

    private GalleryImageAdapter galleryImageAdapter;

    private ArrayList<String> fileToBeUpload = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_camera, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getView() != null) {

        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getView() != null) {
            mCamera = getCameraInstance();
            mCameraView = new CameraView(getActivity(), mCamera);
            FrameLayout frameLayout = (FrameLayout) getView().findViewById(R.id.camera_preview);
            frameLayout.addView(mCameraView);

            Button capture = (Button) getView().findViewById(R.id.capture);
            capture.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    mCamera.takePicture(null, null, mPictureCallback);
//                    mCamera.startPreview();

                    takePicture();
                }
            });

            mGallery = (Gallery) getView().findViewById(R.id.gallery);
            galleryImageAdapter = new GalleryImageAdapter(getActivity());
            mGallery.setAdapter(galleryImageAdapter);

            mUploadFiles = (Button) getView().findViewById(R.id.upload);
            mUploadFiles.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent serviceIntent = new Intent(getActivity(), FileUploadService.class);
                    serviceIntent.putExtra(FileUploadService.FILE_TO_BE_UPLOAD, fileToBeUpload);
                    getActivity().startService(serviceIntent);
                    mUploadFiles.setEnabled(false);
                    fileToBeUpload = new ArrayList<String>();
                    galleryImageAdapter.notifyDataSetChanged();
                }
            });
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
    }

    /**
     * Execute the AsyncTask that will handle the preview of the captured photo.
     */
    public void takePicture() {
        TakePictureTask takePictureTask = new TakePictureTask();
        takePictureTask.execute();
    }

    private Camera.PictureCallback mPictureCallback = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            File pictureFile = getOutputMediaFile(0);

            Log.d(TAG, "Picture call back thread: " + Thread.currentThread().getName());

            if (pictureFile == null){
                Log.d(TAG, "Error creating media file, check storage permissions: ");
                return;
            }

            Log.d(TAG, "File to save at: " + pictureFile.getPath());

            try {
                FileOutputStream fos = new FileOutputStream(pictureFile);
                fos.write(data);
                fos.close();

                fileToBeUpload.add(pictureFile.getPath());
            } catch (FileNotFoundException e) {
                Log.d(TAG, "File not found: " + e.getMessage());
            } catch (IOException e) {
                Log.d(TAG, "Error accessing file: " + e.getMessage());
            }

        }
    };

    private File getOutputMediaFile(int type) {
        File dir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DCIM), getActivity().getPackageName());
        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                Log.e(TAG, "Failed to create storage directory.");
                return null;
            }
        }
        String timeStamp =
                new SimpleDateFormat("yyyMMdd_HHmmss", Locale.US).format(new Date());
        if (type == 0) {
            return new File(dir + File.separator + "VIDI_IMG_"
                    + timeStamp + ".jpg");
        } else {
            return null;
        }
    }

    public static Camera getCameraInstance() {
        Camera c = null;

        try {
            c = Camera.open();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return c;
    }

    /**
     * A pretty basic example of an AsyncTask that takes the photo and
     * then sleeps for a defined period of time before finishing. Upon
     * finishing, it will restart the preview - Camera.startPreview().
     */

    private class TakePictureTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPostExecute(Void result) {
            // This returns the preview back to the live mCamera feed
            if (fileToBeUpload.size() > 0) {
                mUploadFiles.setEnabled(true);
            }
            galleryImageAdapter.notifyDataSetChanged();
            mCamera.startPreview();
        }

        @Override
        protected Void doInBackground(Void... params) {

            Log.d(TAG, "Picture call back thread ---- Do in background---- : " + Thread.currentThread().getName());

            mCamera.takePicture(null, null, mPictureCallback);

            // Sleep for however long, you could store this in a variable and
            // have it updated by a menu item which the user selects.
            try {
                Thread.sleep(1000); // 3 second preview
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            return null;
        }

    }

    private class GalleryImageAdapter extends BaseAdapter {
        private Context mContext;


        public GalleryImageAdapter(Context context) {
            mContext = context;
        }

        public int getCount() {
            return fileToBeUpload.size();
        }

        public Object getItem(int position) {
            return position;
        }

        public long getItemId(int position) {
            return position;
        }


        // Override this method according to your need
        public View getView(int index, View view, ViewGroup viewGroup) {
            // TODO Auto-generated method stub
            ImageView i = new ImageView(mContext);


            Picasso.with(mContext).load(new File(fileToBeUpload.get(index))).into(i);

            return i;
        }
    }
}
