package com.cawka.FriendDetector.detector;

import android.graphics.Bitmap;

public interface iFaceLearner 
{
	public abstract boolean learn( Bitmap bitmap, String name );
	
	//////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////
	
//	protected static class LearningError extends Exception { }
}
