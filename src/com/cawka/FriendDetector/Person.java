package com.cawka.FriendDetector;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;

public class Person 
{
	private String _name;
	private Bitmap _face;
	private Rect   _faceRect;
	private Paint  _paint;
	
	private static int i=0;
	
	private static final int [] COLORS={ 0xFFFF0000, 0xFF00FF00, 0xFF0000FF, 0xFFFFFF00, 0xFFFF00FF, 0xFF00FFFF, 0xFFFFFFFF };
	
	public Person( String name )
	{
		_name=name;
		
		_paint=new Paint( Paint.ANTI_ALIAS_FLAG );

		i++;
		if( i>=COLORS.length ) i=0;
				
		
		_paint.setColor( COLORS[i] );
		_paint.setStyle( Paint.Style.STROKE );
		_paint.setStrokeWidth( 1.5f );
		
	}
	
	public void setFace( PointF midpoint, float eyeDistance, Bitmap picture )
	{
		_faceRect=new Rect( Math.max(2,(int)(midpoint.x-eyeDistance*1.3)), 
							Math.max(2,(int)(midpoint.y-eyeDistance*1.7)),
							Math.min(picture.getWidth()-2, (int)(midpoint.x+eyeDistance*1.3)), 
							Math.min(picture.getHeight()-2, (int)(midpoint.y+eyeDistance*1.7)) );
	}
	
	
	public void   setName( String name ) { _name=name; }
	public String getName( )             { return _name; }
	
	public void   setBitmap( Bitmap face ) { _face=face; }
	public Bitmap getBitmap( ) //there should be something compressed somewhere
	{
		return _face;
	}
	
	public void draw( Canvas canvas, float ratio, int offsetX, int offsetY )
	{
//		canvas.drawCircle( offsetX+m.x*ratio, offsetY+m.y*ratio, eye*ratio, _paint );
		Rect r=new Rect( );
		r.bottom=(int)(ratio*_faceRect.bottom);
		r.left=  (int)(ratio*_faceRect.left);
		r.top=   (int)(ratio*_faceRect.top);
		r.right= (int)(ratio*_faceRect.right);
		r.offset( offsetX, offsetY );

		canvas.drawRect( r, _paint );
	}
}
