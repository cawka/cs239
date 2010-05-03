package com.cawka.FriendDetector;

import java.io.ByteArrayOutputStream;
import java.util.LinkedList;
import java.util.List;

import FriendDetector.Face;
import FriendDetector.RecognizerPrx;
import FriendDetector.RecognizerPrxHelper;
import android.graphics.Bitmap;
import android.graphics.PointF;
import android.util.Log;

public class FaceDetector
{
	private static String TAG="FaceDetector";
	
	private Bitmap _bitmap;
	private List<Person> _faces=new LinkedList<Person>( );
	
	/////////////////////////////////////////////////////////
	public FaceDetector( Bitmap bitmap )
	{
		_bitmap=bitmap;
		
		// use magic to dispatch actual detection either locally or remotely
		// if( magic )
//		performDetection( );
		testRemoteDetection( );
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
	
	protected void testRemoteDetection( )
	{
		Ice.Communicator ic=null;
		try
		{
			ic = Ice.Util.initialize( );

			Ice.ObjectPrx base=ic.stringToProxy( "FaceDetector:default -h cawka.homeip.net -p 55436" );
			if( base==null ) throw new RuntimeException( "Remote server is not available" );

			RecognizerPrx recognizer=RecognizerPrxHelper.checkedCast( base );
			if( recognizer==null ) throw new RuntimeException( "Remote server configuration error" );

			ByteArrayOutputStream os=new ByteArrayOutputStream( );
			_bitmap.compress( Bitmap.CompressFormat.JPEG, 100, os );

//			byte test[]={1,1,1,1,0,0,44,21,1,3,5,1,5,8,0,2,1,1,3,5};
////			for( int i=0; i<10; i++ ) test[i]=(byte)('A'+i);
			Face[] faces=recognizer.findFacesAndRecognizePeople( os.toByteArray() );
		
			for( Face face : faces )
			{
				_faces.add( Person.createPerson(_bitmap, face.position, face.name) );
			}

			Log.v( TAG, "remote call" );
		}
		catch( Ice.LocalException e )
		{
			Log.v( TAG, e.getMessage()+"e.getStackTrace().toString()" );
			
			Log.v( TAG, Log.getStackTraceString(e) );
		}
		catch( Exception e )
		{
//			e.printStackTrace();
			Log.v( TAG, e.getMessage() );
		}
	}
}

