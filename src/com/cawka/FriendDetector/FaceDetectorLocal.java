package com.cawka.FriendDetector;

import android.graphics.Bitmap;
import android.graphics.PointF;
import android.util.Log;

public class FaceDetectorLocal extends iFaceDetector
{
	private final static String TAG="FaceDetector";

	/////////////////////////////////////////////////////////

	protected boolean detect( Bitmap bitmap )
	{
		android.media.FaceDetector detector=new android.media.FaceDetector( bitmap.getWidth(), bitmap.getHeight(), 5 );
		
		android.media.FaceDetector.Face faces[]=new android.media.FaceDetector.Face[ 5 ];
		int count=detector.findFaces( bitmap, faces );
		
		for( int i=0; i<count; i++ )
		{
			PointF midpoint=new PointF( );
			faces[i].getMidPoint( midpoint );

			_faces.add( Person.createPerson(bitmap, midpoint, faces[i].eyesDistance( )) );
		}
		
		// it would be great to throw an exception here if it is taking to long to compute...
		// but, there is no way I can interrupt android's face detector
		
		return true;
	}
}

