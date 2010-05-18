package com.cawka.FriendDetector.gui;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import com.cawka.FriendDetector.Main;

import Ice._LoggerOperationsNC;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.Camera.PreviewCallback;
import android.hardware.Camera.Size;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

public class Cam extends SurfaceView implements SurfaceHolder.Callback, PreviewCallback 
{
	private static final String TAG = "Karthik";

    private	Camera 			_camera;
    private boolean 		_previewRunning = false;
    private ImageWithFaces 	_picture;

    private Main 			_main;

    public Cam( Context context )
	{
		super( context );
	}

	public Cam( Context context, AttributeSet attrs )
	{
		super( context, attrs );
	}
	
	public Cam( Context context, AttributeSet attrs, int defStyle )
	{
		super( context, attrs, defStyle );
	}

    public void init( Main main, ImageWithFaces picture ) 
    {
    	Log.v( TAG, "Cam Init Called" );
    	_picture = picture;
    	_main = main;
    	
        SurfaceHolder surfaceHolder=getHolder( );
        surfaceHolder.addCallback( this );
        surfaceHolder.setType( SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS );
	}

	Camera.PictureCallback	mPictureCallbackJPEG=new Camera.PictureCallback( )
		{
			public void onPictureTaken( byte[] data, Camera c )
			{
            Log.v(TAG, "PICTURE CALLBACK JPEG: data.length = " );
            if(data != null)
            	{
            		Log.v("Karthik","data.length = " + data.length);
					try
					{
						//File.createTempFile( "Cam", "Preview.jpeg",
						File temp=new File( "/sdcard/DCIM/Camera/preview.jpeg" );
						temp.delete( );
						temp.createNewFile( );
						
//						File temp=new File( tmp.getAbsolutePath( ) );
						FileOutputStream out = new FileOutputStream(temp);
						out.write(data);
						Log.v("Karthik","File + " + temp.getAbsolutePath());
						out.close();
						_main.switchToPicture( );
						_main.processImage( temp.getAbsolutePath( ), false );
//						_picture.setImage( temp.getAbsolutePath( ), false );
						
					} catch( IOException e )
					{
						e.printStackTrace();
					}
				} else
            	Log.v("Karthik","NULL");
				// Destroy();
				// mCamera.startPreview();
        }
    };

    public void surfaceCreated(SurfaceHolder holder)
    {
        Log.e(TAG, "surfaceCreated");
		_camera=Camera.open( );
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h)
    {
        Log.e("Karthik", "surfaceChanged - noticed");
        // XXX stopPreview() will crash if preview is not running
		if( _previewRunning )
		{
			_camera.stopPreview( );
        }

		Camera.Parameters p=_camera.getParameters( );

//		p.setPreviewSize( 176, 144 );
//		p.setPreviewFormat( PixelFormat.YCbCr_420_SP );
        p.setPictureFormat(PixelFormat.JPEG);
        p.setPictureSize(800, 640);
        
		_camera.setParameters( p );
		try
		{
			_camera.setPreviewDisplay( holder );
//			_camera.setPreviewCallback( this );
		} 
		catch( IOException e )
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		_camera.startPreview( );
		_previewRunning=true;
    }

    public void surfaceDestroyed(SurfaceHolder holder)
    {
        Log.e(TAG, "surfaceDestroyed");
		_camera.stopPreview( );
		_previewRunning=false;
		_camera.release( );
    }

	public void takePicture( )
	{
		Log.v("Karthik","INSIDE.. Take Picture");
		_camera.takePicture( null, null, mPictureCallbackJPEG );
	}
	
//	public void startPreview( )
//	{
//		if( _camera == null ) _camera=Camera.open( );
//
//		Camera.Parameters p=_camera.getParameters( );
//		
////		Log.v( TAG, "frame_rate" );
////		for( Integer frame_rate : p.getSupportedPreviewFrameRates( ) )
////		{
////			Log.v( TAG, frame_rate.toString( ) );
////		}
////		Log.v( TAG, "format" );
////		for( Integer format : p.getSupportedPreviewFormats( ) )
////		{
////			Log.v( TAG, format.toString( ) );
////			
////		}
////		Log.v( TAG, "sizes" );
////		for( Size size : p.getSupportedPreviewSizes( ) )
////		{
////			Log.v( TAG, Integer.toString(size.width)+", "+Integer.toString(size.height) );
////		}
//		
//		p.setPreviewSize( 176, 144 );
//		p.setPreviewFormat( PixelFormat.YCbCr_420_SP );
//		p.setPictureFormat( PixelFormat.JPEG );
//		p.setPictureSize( 800, 640 );
//
//		_camera.setParameters( p );
//		
//		
//				
////		try
////		{
////			_camera.setPreviewDisplay( holder );
////			
////			_camera.setPreviewCallback( 
////				new PreviewCallback() 
////				{ 
////					public void onPreviewFrame( byte[] _data, Camera _camera ) 
////					{
////						Log.v( TAG, Integer.toString(_data.length) );
////					}
////				} );
////		} 
////		catch( IOException e )
////		{
////			e.printStackTrace( );
////		}
////		_camera.startPreview( );		
////		_previewRunning=true;
//		_camera.startPreview( );
//	}

	public void onPreviewFrame( byte[] _data, Camera _camera ) 
	{
//		_camera.stopPreview( );

//		Bitmap bmp=Bitmap.createBitmap( 176, 144, Bitmap.Config.RGB_565 );
//		Log.v( "test", Integer.toString(_data.length) );
//		
//		bmp.recycle( );
		
//		_camera.star/tPreview( );
	}	
	
//	public void Destroy( )
//	{
////		if( _previewRunning )
////			_camera.stopPreview( );
////		_previewRunning=false;
////		_camera.release( );
//	}
}
