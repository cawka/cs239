package com.cawka.FriendDetector;

import java.io.ByteArrayOutputStream;

import FriendDetector.Face;
import FriendDetector.RecognizerPrx;
import FriendDetector.RecognizerPrxHelper;
import android.graphics.Bitmap;
import android.util.Log;

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
	
	protected boolean detect( Bitmap bitmap )
	{
		try
		{
			if( _recognizer==null ) tryConnect( );

			ByteArrayOutputStream os=new ByteArrayOutputStream( );
			bitmap.compress( Bitmap.CompressFormat.JPEG, 100, os );

			Face[] faces=_recognizer.findFacesAndRecognizePeople( os.toByteArray() );
		
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
}
