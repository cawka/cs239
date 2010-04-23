package com.cawka.FriendDetector;

import java.util.LinkedList;
import java.util.List;

import android.graphics.Bitmap;

public abstract class iFaceDetector 
{
	protected List<Person> _faces=new LinkedList<Person>(); 
	protected Bitmap _bitmap;
	
	//////////////////////////////////////////////////////////////////

	public iFaceDetector( Bitmap bmp ) throws DetectionError
	{
		_bitmap=bmp;
		performDetection( );
	} 
	public List<Person> getFaces( ) { return _faces; }
	
	protected abstract void performDetection( ) throws DetectionError;

	//////////////////////////////////////////////////////////////////

	public class DetectionError extends Exception { }
}
