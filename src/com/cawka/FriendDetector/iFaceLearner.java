package com.cawka.FriendDetector;

import android.graphics.Bitmap;

public interface iFaceLearner 
{
	public abstract boolean learn( Bitmap bitmap, String name );
	
	//////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////
	
//	protected static class LearningError extends Exception { }
}
