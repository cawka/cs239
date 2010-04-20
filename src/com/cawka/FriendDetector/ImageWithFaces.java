package com.cawka.FriendDetector;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Toast;

public class ImageWithFaces extends View 
{
	private Bitmap _bmp=null;
	private Paint  _paint;
	private ListOfPeople _names_list;
	
	///////////////////////////////////////////////////////////////////////

	public ImageWithFaces( Context context, AttributeSet attrs )
	{
		super( context, attrs );
		
		init();
	}

	public ImageWithFaces( Context context, AttributeSet attrs, int defStyle )
	{
		super( context, attrs, defStyle );

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
	
	public Bitmap getBitmap( )
	{
		Bitmap bmp;
		bmp=_bmp;
		_bmp=null;
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
		_bmp=bmp;
		invalidate( ); //or postInvalidate. we'll see
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

    public static Bitmap resizeBitmap( Bitmap input, Rect rect, Float [] ratio )
    {
    	return resizeBitmap( input, rect.width(), rect.height(), ratio ); 
    }
	
	public void draw( Canvas canvas )
	{
		if( _bmp==null ) return;
		
		Float [] ratio=new Float[1];
		ratio[0]=1.0f;
		Bitmap out=resizeBitmap( _bmp, canvas.getClipBounds(), ratio );
		
		Rect src=new Rect( 0, 0, out.getWidth(),    out.getHeight() );
		Rect dst=new Rect( src );
		
		int offsetX=(canvas.getClipBounds().width()-out.getWidth())/2;
		int offsetY=(canvas.getClipBounds().height()-out.getHeight())/2;
		dst.offset( offsetX, offsetY );

		canvas.drawBitmap( out, src, dst, null );
		
		for( int i=0; i<_names_list.getAdapter().getCount(); i++ )
		{
			Person p=(Person)_names_list.getAdapter().getItem( i );
			
			p.draw( canvas, ratio[0], offsetX, offsetY ); // for optimization purposes
		}
	}
}
