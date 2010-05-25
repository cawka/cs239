package com.cawka.FriendDetector.detector;

import java.util.LinkedList;
import java.util.List;

import com.cawka.FriendDetector.Person;

import android.graphics.Bitmap;
import android.util.Log;

public abstract class iFaceDetector 
{
	protected List<Person> _faces=new LinkedList<Person>(); 
	
	protected boolean _fullDetection=true;
	
	//////////////////////////////////////////////////////////////////

	// releasing _faces after this call
	public List<Person> getFaces( ) 
	{ 
		return _faces;
	}
	
	public void resetFaces( )
	{
		Log.v( "TEST", "resetFaces" );
		_faces=new LinkedList<Person>( );
	}
	
	public abstract boolean detect( Bitmap bmp );
	
	public void setFullDetection( boolean enabled )
	{
		_fullDetection=enabled;
	}
	
	public boolean getFullDetection( ) { return _fullDetection; }

	//////////////////////////////////////////////////////////////////

	protected static class DetectionError extends Exception { }
}
