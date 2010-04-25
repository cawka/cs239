package com.cawka.FriendDetector;

import java.util.LinkedList;
import java.util.List;

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
	
	protected abstract boolean detect( Bitmap bmp );

	//////////////////////////////////////////////////////////////////

	protected static class DetectionError extends Exception { }
}
