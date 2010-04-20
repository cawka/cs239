package com.cawka.FriendDetector;

import java.io.FileNotFoundException;
import java.io.IOException;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class ImageWithFaces extends View 
{
	private Bitmap _bmp=null;
	private Bitmap _drawableBitmap=null;
	private Paint  _paint;
	private ListOfPeople _names_list;
	
	///////////////////////////////////////////////////////////////////////

	public ImageWithFaces( Context context, AttributeSet attrs )
	{
		super( context, attrs );
		
//		int resource=attrs.getAttributeResourceValue("android", "src", android.R.drawable.btn_star );
//		this.setBackgroundResource( resource );
		
		init();
	}

	public ImageWithFaces( Context context, AttributeSet attrs, int defStyle )
	{
		super( context, attrs, defStyle );
		
		int resource=attrs.getAttributeResourceValue("android", "src", android.R.drawable.btn_star );
		this.setBackgroundResource( resource );

		init();
	}

	public ImageWithFaces( Context context ) 
	{
		super( context );

		init();
	}
	
	protected void init( )
	{
		_paint=new Paint( Paint.ANTI_ALIAS_FLAG );
		_paint.setColor( 0xFF00FF00 );
	}
	
//	protected void finalize( )
//	{
//		if( _bmp!=null ) _bmp.recycle( );
//		_bmp=null;
//	}
	
	public void setListOfPeople( ListOfPeople names_list )
	{
		_names_list=names_list;
	}
	
	private float _ratio=1.0f;
	private Rect  _srcRect;
	private Rect  _dstRect;
	private int   _offsetX;
	private int   _offsetY;
	
	public Bitmap getBitmap( )
	{
		Bitmap bmp;
		bmp=_bmp;
		_bmp=null;
		if( _drawableBitmap!=null ) _drawableBitmap.recycle( );
		_drawableBitmap=null;
		
		return bmp;
	}
	
	public boolean isBitmap( )
	{
		return _bmp!=null;
	}

	///////////////////////////////////////////////////////////////////////
	
	public void setBitmap( Bitmap bmp )
	{
		if( _bmp!=null ) _bmp.recycle( );
		if( _drawableBitmap!=null ) _drawableBitmap.recycle( );
		_bmp=bmp;
		_drawableBitmap=null;
		
		invalidate( );
	}
	
	private void makeDrawableBitmap( )
	{
		Float [] ratio=new Float[1];
		ratio[0]=1.0f;
		_drawableBitmap=resizeBitmap( _bmp, getWidth(), getHeight(), ratio );
		_ratio=ratio[0];
		
		_srcRect=new Rect( 0, 0, _drawableBitmap.getWidth(),    _drawableBitmap.getHeight() );
		_dstRect=new Rect( _srcRect );
		
		_offsetX=(getWidth()-_drawableBitmap.getWidth())/2;
		_offsetY=(getHeight()-_drawableBitmap.getHeight())/2;
		_dstRect.offset( _offsetX, _offsetY );
	}
	
    public static Bitmap resizeBitmap( Bitmap input, int maxWidth, int maxHeight, Float[] ratio )
    {
		int width= input.getWidth();
		int height=input.getHeight();
		
		if( width>maxWidth ) 
			ratio[0]=ratio[0]*maxWidth/width;

		width=(int)(ratio[0]*input.getWidth());
		height=(int)(ratio[0]*input.getHeight());
		
		if( height>maxHeight )
			ratio[0]=ratio[0]*maxHeight/height;
		
		width=(int)(ratio[0]*input.getWidth());
		height=(int)(ratio[0]*input.getHeight());
		
		// create a matrix for the manipulation
        Matrix matrix = new Matrix();
        // resize the bit map
        matrix.postScale( ratio[0], ratio[0] );
        
		Bitmap output=Bitmap.createBitmap( input, 0, 0, input.getWidth(), input.getHeight(), matrix, true );
		return output;
    }
    
    public static Bitmap resizeBitmap( Bitmap input, int maxWidth, int maxHeight )
    {
    	Float [] ratio=new Float[1];
    	ratio[0]=1.0f;
    	return resizeBitmap( input, maxWidth, maxHeight, ratio );
    }
	    
	protected void onDraw( Canvas canvas )
	{
		if( _bmp==null ) return;
		
		if( _drawableBitmap==null ) makeDrawableBitmap( );
		
		canvas.drawBitmap( _drawableBitmap, _srcRect, _dstRect, null );
		
		for( int i=0; i<_names_list.getAdapter().getCount(); i++ )
		{
			Person p=(Person)_names_list.getAdapter().getItem( i );
			
			p.draw( canvas, _ratio, _offsetX, _offsetY ); // for optimization purposes
		}
	}
}
