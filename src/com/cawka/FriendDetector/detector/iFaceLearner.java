package com.cawka.FriendDetector.detector;

import com.cawka.FriendDetector.Person;
import com.cawka.FriendDetector.gui.ImageAdapter;

import android.graphics.Bitmap;

public interface iFaceLearner 
{
	public abstract boolean recognize( Person person ); 

	public abstract boolean learn( Bitmap bitmap, String name );
	
	public abstract void getTrainSet( ImageAdapter adapter );
	
	public abstract void unLearn( long id );
	
	//////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////
	
	
	
//	protected static class LearningError extends Exception { }
}
