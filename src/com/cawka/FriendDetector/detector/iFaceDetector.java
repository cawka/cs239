package com.cawka.FriendDetector.detector;

import java.util.LinkedList;
import java.util.List;

import com.cawka.FriendDetector.Person;

import android.graphics.Bitmap;

public abstract class iFaceDetector 
{
	protected List<Person> _faces=new LinkedList<Person>(); 
	
	//////////////////////////////////////////////////////////////////

	// releasing _faces after this call
	public List<Person> getFaces( ) 
	{ 
		if( _faces.size()==0 ) return _faces;
		
		List<Person> ret=_faces;
		_faces=new LinkedList<Person>( );
		return ret; 
	}
	
	public abstract boolean detect( Bitmap bmp );

	//////////////////////////////////////////////////////////////////

	protected static class DetectionError extends Exception { }
}
