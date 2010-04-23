package com.cawka.FriendDetector;

import java.io.ByteArrayOutputStream;

import FriendDetector.Face;
import FriendDetector.RecognizerPrx;
import FriendDetector.RecognizerPrxHelper;
import android.graphics.Bitmap;
import android.util.Log;

public class FaceDetectorRemote extends iFaceDetector 
{
	private final static String TAG="FaceDetector";
	
	public FaceDetectorRemote( Bitmap bmp ) throws DetectionError
	{
		super( bmp );
	}

	protected void performDetection( ) throws DetectionError
	{
		Ice.Communicator ic=null;
		try
		{
			ic = Ice.Util.initialize( );

			Ice.ObjectPrx base=ic.stringToProxy( "FaceDetector:default -h cawka.homeip.net -t 1000 -p 55436" );
			if( base==null ) throw new RuntimeException( "Remote server is not available" );

			RecognizerPrx recognizer=RecognizerPrxHelper.checkedCast( base );
			if( recognizer==null ) throw new RuntimeException( "Remote server configuration error" );

			ByteArrayOutputStream os=new ByteArrayOutputStream( );
			_bitmap.compress( Bitmap.CompressFormat.JPEG, 100, os );

			Face[] faces=recognizer.findFacesAndRecognizePeople( os.toByteArray() );
		
			for( Face face : faces )
			{
				_faces.add( Person.createPerson(_bitmap, face.position, face.name) );
			}
		}
		catch( Ice.LocalException e )
		{
			Log.v( TAG, e.getMessage()+"e.getStackTrace().toString()" );
			
			Log.v( TAG, Log.getStackTraceString(e) );
			
			throw new DetectionError( );
		}
		catch( Exception e )
		{
			Log.v( TAG, e.getMessage() );
			
			throw new DetectionError( );
		}
	}	
}
