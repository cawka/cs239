package com.cawka.FriendDetector;

import com.cawka.FriendDetector.iFaceDetector.DetectionError;

import android.graphics.Bitmap;
import android.graphics.PointF;
import android.util.Log;

public class FaceDetectorLocal extends iFaceDetector
{
	private final static String TAG="FaceDetector";

	/////////////////////////////////////////////////////////

	public FaceDetectorLocal( Bitmap bmp ) throws DetectionError
	{
		super( bmp );
	}


	protected void performDetection( )
	{
		android.media.FaceDetector detector=new android.media.FaceDetector( _bitmap.getWidth(), _bitmap.getHeight(), 5 );
		
		android.media.FaceDetector.Face faces[]=new android.media.FaceDetector.Face[ 5 ];
		int count=detector.findFaces( _bitmap, faces );
		
		for( int i=0; i<count; i++ )
		{
			PointF midpoint=new PointF( );
			faces[i].getMidPoint( midpoint );

			_faces.add( Person.createPerson(_bitmap, midpoint, faces[i].eyesDistance( )) );
		}
		
		// it would be great to throw an exception here if it is taking to long to compute...
		// but, there is no way I can interrupt android's face detector
	}
}

