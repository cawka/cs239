package com.cawka.FriendDetector;

import java.util.LinkedList;
import java.util.List;

import android.graphics.Bitmap;
import android.graphics.PointF;

public class FaceDetector
{
	private Bitmap _bitmap;
	private List<Person> _faces=new LinkedList<Person>( );
	
	/////////////////////////////////////////////////////////
	public FaceDetector( Bitmap bitmap )
	{
		_bitmap=bitmap;
		
		// use magic to dispatch actual detection either locally or remotely
		// if( magic )
		performDetection( );
	}
	
	public List<Person> getFaces( ) { return _faces; }
	
	protected void performDetection( ) // local version
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
	}
}

