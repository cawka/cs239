package com.cawka.FriendDetector;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;

public class Person 
{
	private String _name;
	private Bitmap _face;
	private Rect   _faceRect;
	private Paint  _paint;
	private int    _index;
	
	private static int ColorIndex=0;
	
	final private static int FACE_WIDTH =64;
	final private static int FACE_HEIGHT=80;
	
	private static final int [] COLORS={ 0xFFFF0000, 0xFF00FF00, 0xFF0000FF, 0xFFFFFF00, 0xFFFF00FF, 0xFF00FFFF, 0xFFFFFFFF };
	
	/////////////////////////////////////////////////////////////
	
	private Person( )
	{
		_paint=new Paint( Paint.ANTI_ALIAS_FLAG );

		ColorIndex++;
		if( ColorIndex>=COLORS.length ) ColorIndex=0;
		
		_index=ColorIndex;
				
		_paint.setColor( COLORS[ColorIndex] );
		_paint.setStyle( Paint.Style.STROKE );
		_paint.setStrokeWidth( 1.5f );
	}
	
	
	public static Person createPerson( Bitmap picture, PointF midpoint, float eyeDistance )
	{
		Person person=new Person( );
		person._faceRect=new Rect( Math.max(2,(int)(midpoint.x-eyeDistance*1.3)), 
				Math.max(2,(int)(midpoint.y-eyeDistance*1.7)),
				Math.min(picture.getWidth()-2, (int)(midpoint.x+eyeDistance*1.3)), 
				Math.min(picture.getHeight()-2, (int)(midpoint.y+eyeDistance*1.625)) );
		
		Matrix x=new Matrix();
		x.postScale( 1.0f*FACE_WIDTH/person._faceRect.width(),
					1.0f*FACE_HEIGHT/person._faceRect.height() );
		person._face=Bitmap.createBitmap( picture, person._faceRect.left,    person._faceRect.top,
												   person._faceRect.width(), person._faceRect.height(), x, true );
		return person;
	}
	
	public static void resetColors( )
	{
		ColorIndex=0;
	}
	
	
	public void   setName( String name ) { _name=name; }
	public String getName( )             { return _name; }
	
	public void   setFace( Bitmap face ) { _face=face; }
	public Bitmap getFace( ) //there should be something compressed somewhere
	{
		return _face;
	}
	
	public int	  getColor( ) { return COLORS[_index]; }
	
	public void draw( Canvas canvas, float ratio, int offsetX, int offsetY )
	{
		Rect r=new Rect( );
		r.bottom=(int)(ratio*_faceRect.bottom);
		r.left=  (int)(ratio*_faceRect.left);
		r.top=   (int)(ratio*_faceRect.top);
		r.right= (int)(ratio*_faceRect.right);
		r.offset( offsetX, offsetY );

		canvas.drawRect( r, _paint );
	}
}
