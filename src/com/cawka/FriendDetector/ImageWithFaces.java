package com.cawka.FriendDetector;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.media.ExifInterface;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class ImageWithFaces extends View 
{
	private Bitmap _bmp=null;
	private Bitmap _drawableBitmap=null;
	private Paint  _paint;
	private ListOfPeople _names_list;
	
	private float _ratio=1.0f;
	private Rect  _srcRect;
	private Rect  _dstRect;
	private int   _offsetX;
	private int   _offsetY;
	
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
	
	public void setListOfPeople( ListOfPeople names_list )
	{
		_names_list=names_list;
	}
	
	public Bitmap getBitmap( )
	{
		Bitmap bmp;
		bmp=_bmp;
		_bmp=null;
		if( _drawableBitmap!=null ) _drawableBitmap.recycle( );
		_drawableBitmap=null;
		
		return bmp;
	}
	
	public Bitmap getBitmapSafe( ) //leave bitmap
	{
		return _bmp;
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
		_ratio=calculateResizeRatio( _bmp, getWidth(), getHeight() );
		_drawableBitmap=resizeBitmap( _bmp, _ratio );
		
		_srcRect=new Rect( 0, 0, _drawableBitmap.getWidth(),    _drawableBitmap.getHeight() );
		_dstRect=new Rect( _srcRect );
		
		_offsetX=(getWidth()-_drawableBitmap.getWidth())/2;
		_offsetY=(getHeight()-_drawableBitmap.getHeight())/2;
		_dstRect.offset( _offsetX, _offsetY );
	}
	
	public static float calculateResizeRatio( Bitmap input, int maxWidth, int maxHeight )
	{
		float ratio=1.0f;
		
		if( input.getWidth()>maxWidth ) 
			ratio=1.0f*maxWidth/input.getWidth();

		if( ratio*input.getHeight()>maxHeight )
			ratio=1.0f*maxHeight/input.getHeight();

		return ratio;
	}
	
    public static Bitmap resizeBitmap( Bitmap input, float ratio )
    {
		// create a matrix for the manipulation
        Matrix matrix = new Matrix();
        // resize the bit map
        matrix.postScale( ratio, ratio );
        
		Bitmap output=Bitmap.createBitmap( input, 0, 0, input.getWidth(), input.getHeight(), matrix, true );
		return output;
    }
    
    public static Bitmap processBitmap( Bitmap input, int maxSize, int orientation )
    {
		// create a matrix for the manipulation
        Matrix matrix = new Matrix();
        // resize the bit map
        float ratio=calculateResizeRatio( input, maxSize, maxSize );
        matrix.postScale( ratio, ratio );
        
        switch( orientation )
        {
        case ExifInterface.ORIENTATION_ROTATE_180:
        	matrix.postRotate( 180 );
        	break;
        case ExifInterface.ORIENTATION_ROTATE_270:
        	matrix.postRotate( 270 );
        	break;
        case ExifInterface.ORIENTATION_ROTATE_90:
        	matrix.postRotate( 90 );
        	break;
        case ExifInterface.ORIENTATION_NORMAL:
        case ExifInterface.ORIENTATION_UNDEFINED:
        	// ok
        	break;
        case ExifInterface.ORIENTATION_TRANSPOSE:
        case ExifInterface.ORIENTATION_TRANSVERSE:
        case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
        case ExifInterface.ORIENTATION_FLIP_VERTICAL:
        	// Hope that these cases are not common
        	break;
        }
        
		Bitmap output=Bitmap.createBitmap( input, 0, 0, input.getWidth(), input.getHeight(), matrix, true );
		return output;
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
