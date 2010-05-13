package com.cawka.FriendDetector.detector;

import java.io.ByteArrayOutputStream;
import java.util.LinkedList;
import java.util.List;

import com.cawka.FriendDetector.Person;
import com.cawka.FriendDetector.detector.eigenfaces.Eigenface.NamedFace;

import FriendDetector.Face;
import FriendDetector.FacePictureWithName;
import FriendDetector.RecognizerPrx;
import FriendDetector.RecognizerPrxHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.Toast;

public class FaceDetectorRemote extends iFaceDetector implements iFaceLearner
{
	private final static String TAG="FaceDetector";
	
	private Ice.Communicator _ic=Ice.Util.initialize();
	private RecognizerPrx _recognizer=null;
	
	private String _proxy="131.179.192.201"; //test01 server in LASR lab
	private String _port ="55436";
	private int    _timeout=1000;
	
	////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////
	
	public FaceDetectorRemote( String proxy, String port, int ms_timeout )
	{
		_proxy  =proxy;
		_port   =port;
		_timeout=ms_timeout; //timeout in milliseconds. Technically, it can be longer due to DNS and connection issues
	}
	
	
	private void tryConnect( ) throws Exception
	{
		Log.v( TAG, "tryConnect" );
		
		Ice.ObjectPrx base=_ic.stringToProxy( "FaceDetector:default -h "+_proxy+" -t "+Integer.toString(_timeout)+" -p "+_port );
		if( base==null ) throw new RuntimeException( "Remote server is not available" );
		
		_recognizer=RecognizerPrxHelper.checkedCast( base );
		if( _recognizer==null ) throw new RuntimeException( "Remote server configuration error" );
	}
	
	////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////
	
	public boolean detect( Bitmap bitmap )
	{
		try
		{
			if( _recognizer==null ) tryConnect( );

			ByteArrayOutputStream os=new ByteArrayOutputStream( );
			bitmap.compress( Bitmap.CompressFormat.JPEG, 100, os );

			Face[] faces;
			if( _fullDetection )
				faces=_recognizer.findFacesAndRecognizePeople( os.toByteArray() );
			else
				faces=_recognizer.findFaces( os.toByteArray() );
		
			for( Face face : faces )
			{
				_faces.add( Person.createPerson(bitmap, face.position, face.name) );
			}
			
			return true;
		}
		catch( Exception e ) //Ice.LocalException e
		{
			_recognizer=null; //to make sure next time it will try again to connect
			
			Log.v( TAG, (e.getMessage()!=null)?e.getMessage():"unknown error" );
			Log.v( TAG, Log.getStackTraceString(e) );
			
			return false;
		}
	}	

	public boolean recognize( Person person )
	{
		try
		{
			if( _recognizer==null ) tryConnect( );
			
			ByteArrayOutputStream os=new ByteArrayOutputStream( );
			person.getFace( ).compress( Bitmap.CompressFormat.JPEG, 100, os );

			String name=_recognizer.recognizeFace( os.toByteArray() );
			if( name!="" ) person.setName( name );
		}
		catch( Exception e ) //Ice.LocalException e
		{
			_recognizer=null; //to make sure next time it will try again to connect
			
			Log.v( TAG, (e.getMessage()!=null)?e.getMessage():"unknown error" );
			Log.v( TAG, Log.getStackTraceString(e) );
			
			return false;
		}
		
		return true;
	}	
	////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////

	public boolean learn( Bitmap bitmap, String name )
	{
		try
		{
			if( _recognizer==null ) tryConnect( );
			
			ByteArrayOutputStream os=new ByteArrayOutputStream( );
			bitmap.compress( Bitmap.CompressFormat.JPEG, 100, os );

			_recognizer.learn( os.toByteArray(), name );
			
			return true;
		}
		catch( Exception e ) //Ice.LocalException e
		{
			_recognizer=null; //to make sure next time it will try again to connect
			
			Log.v( TAG, (e.getMessage()!=null)?e.getMessage():"unknown error" );
			Log.v( TAG, Log.getStackTraceString(e) );
			
			return false;
		}
	}


	public List<NamedFace> getTrainSet( )
	{
		List<NamedFace> ret=new LinkedList<NamedFace>();
		try
		{
			if( _recognizer==null ) tryConnect( );
			
			for( FacePictureWithName face : _recognizer.getTrainSet() ) 
			{
				Bitmap bmp=BitmapFactory.decodeByteArray( face.jpegFileOfFace, 0, face.jpegFileOfFace.length );
				ret.add( new NamedFace(Integer.toString(face.id), bmp, face.name) );
			}

			return ret;
		}
		catch( Exception e ) //Ice.LocalException e
		{
			_recognizer=null; //to make sure next time it will try again to connect
			
			Log.v( TAG, (e.getMessage()!=null)?e.getMessage():"unknown error" );
			Log.v( TAG, Log.getStackTraceString(e) );
		}
		return ret;
	}

	public void unLearn( long id )
	{
		try
		{
			if( _recognizer==null ) tryConnect( );
			
			_recognizer.unLearn( (int)id );
		}
		catch( Exception e ) //Ice.LocalException e
		{
			_recognizer=null; //to make sure next time it will try again to connect
			
			Log.v( TAG, (e.getMessage()!=null)?e.getMessage():"unknown error" );
			Log.v( TAG, Log.getStackTraceString(e) );
		}
	}
}
