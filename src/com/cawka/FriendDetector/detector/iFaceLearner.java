package com.cawka.FriendDetector.detector;

import java.util.List;

import com.cawka.FriendDetector.Person;
import com.cawka.FriendDetector.detector.eigenfaces.Eigenface.NamedFace;

import android.graphics.Bitmap;

public interface iFaceLearner 
{
	public abstract boolean recognize( Person person ); 

	public abstract boolean learn( Bitmap bitmap, String name );
	
	public abstract List<NamedFace> getTrainSet( );
	
	public abstract void unLearn( long id );
	
	//////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////
	
	
	
//	protected static class LearningError extends Exception { }
}
