package com.cawka.FriendDetector.detector;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import com.cawka.FriendDetector.Person;
import com.cawka.FriendDetector.detector.eigenfaces.DBHandleEigen;
import com.cawka.FriendDetector.detector.eigenfaces.Eigenface;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PointF;
import android.graphics.Bitmap.CompressFormat;
import android.os.Environment;
import android.util.Log;

public class FaceDetectorLocal extends iFaceDetector implements iFaceLearner
{
	private final static String TAG="FaceDetector";

	private Context _context;
	private Eigenface _eigenface;
	
	public FaceDetectorLocal( Context context )
	{
		_context=context;
		
		new Thread( new Runnable(){
			public void run( )
			{
				Eigenface eigenface=new Eigenface( _context );
				_eigenface=eigenface;
			}
		} ).start( );
	}
	
	/////////////////////////////////////////////////////////

	public boolean detect( Bitmap bitmap )
	{
		android.media.FaceDetector detector=new android.media.FaceDetector( bitmap.getWidth(), bitmap.getHeight(), 5 );
		
		android.media.FaceDetector.Face faces[]=new android.media.FaceDetector.Face[ 5 ];
		int count=detector.findFaces( bitmap, faces );
		
		for( int i=0; i<count; i++ )
		{
			PointF midpoint=new PointF( );
			faces[i].getMidPoint( midpoint );

			Person person=Person.createPerson( bitmap, midpoint, faces[i].eyesDistance( ) );
			
			if( _eigenface!=null )
			{
				String name=_eigenface.recognize( person.getFace( ) );
				if( !name.equals("") ) person.setName( name );
			}
			
			_faces.add( person );
		}
		
		// it would be great to throw an exception here if it is taking to long to compute...
		// but, there is no way I can interrupt android's face detector
		
		return true;
	}

	public boolean learn( Bitmap bitmap, String name )
	{
		String status=Environment.getExternalStorageState( );
        if( !status.equals(Environment.MEDIA_MOUNTED) ) return false;

        DBHandleEigen db=new DBHandleEigen( _context );
		
		String id=db.add( name );
		
		File filepath=new File( Environment.getExternalStorageDirectory()+"/friendDetector" );
		filepath.mkdirs( );

		File filename=new File( filepath, id+".png" );
		try
		{
			FileOutputStream fos = new FileOutputStream( filename );
			bitmap.compress( CompressFormat.PNG, 100, fos );
			fos.close( );
		}
		catch( FileNotFoundException e )
		{
			Log.e( TAG, "Cannot open file "+filename );
		}
		catch( IOException e )
		{
			Log.e( TAG, "Cannot close file "+filename );
		}
		
		_eigenface.update( ); //recalculate eigenfaces
		return true;
	}
}
