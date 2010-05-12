package com.cawka.FriendDetector.gui;

import java.io.IOException;

import com.cawka.FriendDetector.Person;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.BitmapFactory.Options;
import android.media.ExifInterface;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class ImageWithFaces extends View 
{
	private Bitmap _bmp=null; //to optimize performance and limit number of times an image should be decoded
	private String _image="";
	private int    _orientation=0;
	private int    _resample=1;
	
	private Bitmap _drawableBitmap=null;
	private Paint  _paint;
	private ListOfPeople _names_list;
	
	private float _ratio=1.0f;
	private Rect  _srcRect;
	private Rect  _dstRect;
	private int   _offsetX;
	private int   _offsetY;
	

	private static int MAX_SIZE = 800;
    
	private static final String TAG="FriendDetector.ImageWithFaces";
	
	///////////////////////////////////////////////////////////////////////

	public ImageWithFaces( Context context, AttributeSet attrs )
	{
		super( context, attrs );
		
		_drawableBitmap=BitmapFactory.decodeResource( getContext().getResources( ), android.R.drawable.ic_menu_search );
		
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
		return _bmp;
	}
	
	public boolean isBitmap( )
	{
		return !_image.equals( "" );
	}

	///////////////////////////////////////////////////////////////////////
	
	public void setImage( String image, boolean immidiatelyInvalidate )
	{
		_image=image;
		_orientation=0;
		_resample=1;
		
		Log.v("Karthik",image);
		
		try
		{
			ExifInterface exif=new ExifInterface( image );
			_orientation=Integer.parseInt( exif.getAttribute(ExifInterface.TAG_ORIENTATION) );
			
			int width =Integer.parseInt( exif.getAttribute(ExifInterface.TAG_IMAGE_WIDTH) );
			int height=Integer.parseInt( exif.getAttribute(ExifInterface.TAG_IMAGE_LENGTH) );
			
			if( Math.max(width, height)>MAX_SIZE )
			{
				_resample=(int)Math.round( 1.0*Math.max(width, height)/MAX_SIZE+0.5 );
			}
		}
		catch( IOException ex )
		{
			//not a jpeg file
			Log.d( TAG, image+" is a jpeg file or doesn't have exif information available" );
		}
		
		if( _bmp!=null ) _bmp.recycle( );
		_bmp=null;

		if( _drawableBitmap!=null ) _drawableBitmap.recycle( );
		_drawableBitmap=null;
		
		if( immidiatelyInvalidate )
		{
			makeDrawableBitmap( );
			
			invalidate( );
		}
	}
	
	public String getImage( )
	{
		return _image;
	}
	
	public int getImageOrientation( )
	{
		return _orientation;
	}
	
	private void makeDrawableBitmap( )
	{
		if( _image.equals("") ) return;
		Log.v( TAG, _image );
		
		Log.v( TAG, "beforeDecode" );
		Options opts=new Options();
		opts.inSampleSize=_resample;
		Bitmap bmp=BitmapFactory.decodeFile( _image, opts );
		_bmp=resizeBitmap( bmp, 1.0f, _orientation );
		Log.v(  TAG, "afterDecode" );
		
		Log.v( TAG, "resample: "+Float.toString(_resample)+"width: "+Integer.toString(_bmp.getWidth())+", height: "+Integer.toString(_bmp.getHeight()) );
		
		_ratio=calculateResizeRatio( _bmp, getMeasuredWidth()-8, getMeasuredHeight()-8, 0 );
		
		Log.v( TAG, "beforeResize" );
		_drawableBitmap=resizeBitmap( _bmp, _ratio, 0 );
		Log.v( TAG, "afterResize" );

		Log.v( TAG, "ratio: "+Float.toString(_ratio)+", width: "+Integer.toString(_drawableBitmap.getWidth())+", height: "+Integer.toString(_drawableBitmap.getHeight()) );
		
		calculateRectsForDrawableBitmap( );
	}
	
	private void calculateRectsForDrawableBitmap( )
	{
		_srcRect=new Rect( 0, 0, _drawableBitmap.getWidth(),    _drawableBitmap.getHeight() );
		_dstRect=new Rect( _srcRect );
		
		_offsetX=(getWidth()-_drawableBitmap.getWidth())/2;
		_offsetY=(getHeight()-_drawableBitmap.getHeight())/2;
		_dstRect.offset( _offsetX, _offsetY );
	}
	
	public static float calculateResizeRatio( Bitmap input, int maxWidth, int maxHeight, int orientation )
	{
		float ratio=1.0f;
		
        switch( orientation )
        {
        case ExifInterface.ORIENTATION_ROTATE_270:
        case ExifInterface.ORIENTATION_ROTATE_90:
        	int tmp=maxWidth;
        	maxWidth=maxHeight;
        	maxHeight=tmp;
        	break;
        }
		
		if( input.getWidth()>maxWidth ) 
			ratio=1.0f*maxWidth/input.getWidth();

		if( ratio*input.getHeight()>maxHeight )
			ratio=1.0f*maxHeight/input.getHeight();

		return ratio;
	}
	
    private static Bitmap resizeBitmap( Bitmap input, float ratio, int orientation )
    {
		// create a matrix for the manipulation
        Matrix matrix = new Matrix();
        // resize the bit map
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
    
//    private Bitmap processBitmap( Bitmap input, int maxSize, int orientation )
//    {
//		// create a matrix for the manipulation
//        Matrix matrix = new Matrix();
//        // resize the bit map
//        float ratio=calculateResizeRatio( input, maxSize, maxSize );
//        matrix.postScale( ratio, ratio );
//        
//        switch( orientation )
//        {
//        case ExifInterface.ORIENTATION_ROTATE_180:
//        	matrix.postRotate( 180 );
//        	break;
//        case ExifInterface.ORIENTATION_ROTATE_270:
//        	matrix.postRotate( 270 );
//        	break;
//        case ExifInterface.ORIENTATION_ROTATE_90:
//        	matrix.postRotate( 90 );
//        	break;
//        case ExifInterface.ORIENTATION_NORMAL:
//        case ExifInterface.ORIENTATION_UNDEFINED:
//        	// ok
//        	break;
//        case ExifInterface.ORIENTATION_TRANSPOSE:
//        case ExifInterface.ORIENTATION_TRANSVERSE:
//        case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
//        case ExifInterface.ORIENTATION_FLIP_VERTICAL:
//        	// Hope that these cases are not common
//        	break;
//        }
//        
//		Bitmap output=Bitmap.createBitmap( input, 0, 0, input.getWidth(), input.getHeight(), matrix, true );
//		return output;
//    }
	    
	protected void onDraw( Canvas canvas )
	{
//		if( _image.equals("") ) return;
		
		if( _drawableBitmap==null ) makeDrawableBitmap( );
		if( _srcRect==null || _dstRect==null ) calculateRectsForDrawableBitmap( );
		
		canvas.drawBitmap( _drawableBitmap, _srcRect, _dstRect, null );
		
		for( int i=0; i<_names_list.getAdapter().getCount(); i++ )
		{
			Person p=(Person)_names_list.getAdapter().getItem( i );
			
			p.draw( canvas, _ratio, _offsetX, _offsetY ); // for optimization purposes
		}
	}
}
