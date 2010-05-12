package com.cawka.FriendDetector.gui;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import com.cawka.FriendDetector.Main;

import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

public class Cam implements SurfaceHolder.Callback
{
    private static final String TAG = "CameraApiTest";
    Camera mCamera;
    boolean mPreviewRunning = false;
    ImageWithFaces picture;
    private Main main;

    public void init(Main main, View findViewById, ImageWithFaces picture) {
		// TODO Auto-generated method stub
    	Log.v("Karthik", "Cam Init Called + "+ findViewById);
    	this.picture = picture;
    	mSurfaceView = (SurfaceView)findViewById;
    	this.main = main;
        mSurfaceHolder = mSurfaceView.getHolder();
        mSurfaceHolder.addCallback(this);
        mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	}

	
    Camera.PictureCallback mPictureCallbackRAW = new Camera.PictureCallback() {
        public void onPictureTaken(byte[] data, Camera c) {
            Log.v(TAG, "PICTURE CALLBACK RAW: data.length = ");
            if(data != null)
            	Log.v("Karthik","data.length = " + data.length); 
            else 
            	Log.v("Karthik","NULL");
            //mCamera.startPreview();
        }
    };

    Camera.PictureCallback mPictureCallbackJPEG = new Camera.PictureCallback() {
        public void onPictureTaken(byte[] data, Camera c) {
            Log.v(TAG, "PICTURE CALLBACK JPEG: data.length = " );
            if(data != null)
            	{
            		Log.v("Karthik","data.length = " + data.length);
            		try {
						File tmp = File.createTempFile("Cam", "Preview.jpeg",new File("/sdcard/DCIM/Camera/"));
						tmp.delete();
						File temp = new File(tmp.getAbsolutePath());
						FileOutputStream out = new FileOutputStream(temp);
						out.write(data);
						Log.v("Karthik","File + " + temp.getAbsolutePath());
						out.close();
						main.switchToPicture();
						picture.setImage(temp.getAbsolutePath(), false);
						
						
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
            	}
            else 
            	Log.v("Karthik","NULL");
            
        }
    };

    public void surfaceCreated(SurfaceHolder holder)
    {
        Log.e(TAG, "surfaceCreated");
        mCamera = Camera.open();
        //mCamera.startPreview();
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h)
    {
        Log.e("Karthik", "surfaceChanged - noticed");
        // XXX stopPreview() will crash if preview is not running
        if (mPreviewRunning) {
            mCamera.stopPreview();
        }

        Camera.Parameters p = mCamera.getParameters();
        p.setPreviewSize(w, h);
        p.setPictureFormat(PixelFormat.JPEG);
        p.setPictureSize(800, 640);
        
        mCamera.setParameters(p);
        try {
			mCamera.setPreviewDisplay(holder);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        mCamera.startPreview();
        mPreviewRunning = true;
    }

    public void surfaceDestroyed(SurfaceHolder holder)
    {
        Log.e(TAG, "surfaceDestroyed");
        mCamera.stopPreview();
        mPreviewRunning = false;
        mCamera.release();
    }

    private SurfaceView mSurfaceView;
    private SurfaceHolder mSurfaceHolder;

	public void takePicture() {
		// TODO Auto-generated method stub
		Log.v("Karthik","INSIDE.. Take Picture");
		mCamera.takePicture(null, mPictureCallbackRAW, mPictureCallbackJPEG);
		
	}
	
}

