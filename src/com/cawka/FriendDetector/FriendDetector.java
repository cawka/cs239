package com.cawka.FriendDetector;

import com.cawka.FriendDetector.R;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.hardware.Camera;
import android.media.FaceDetector;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore.Images;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListAdapter;
import android.widget.Toast;

public class FriendDetector extends Activity
{
    private static final int MENU_SELECT = 1;
	private static final int MENU_ROTATE = 2;

	private static final int SELECT_IMAGE = 1;
	
	private static final int MAX_WIDTH = 800;
	private static final int MAX_HEIGHT = 800;
    
    private ImageWithFaces _picture;
    private ListOfPeople   _names_list;
    
    private Handler _handler = new Handler(); //to handle UI updates
    private Thread _thread;
    
    private FaceDetection _faceDetection;
    
    private ProgressDialog _progress=null;

	public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.main );
        
        _picture=   (ImageWithFaces)findViewById( R.id.picture );
        _names_list=(ListOfPeople)  findViewById( R.id.names_list );
        
        _names_list.setImageWithFaces( _picture );
        _picture   .setListOfPeople( _names_list );
        
        _picture.setOnClickListener( new OnClickListener()
        	{
				public void onClick( View v )
				{
					selectImage( );
				}
        	} );
    }
	
	static int stuff=1;
	
	protected void onStart( )
	{
		super.onStart( );
		
        SavedState state=(SavedState)getLastNonConfigurationInstance( );
        if( state!=null )
        {
        	if( state._bitmap!=null && !state._bitmap.isRecycled())
        	{
    			_picture.setBitmap( state._bitmap );
            	_names_list.setAdapter( state._adapter );
        	}
        	
        	state=null;
        	System.gc( );
        }
        
		stuff++;
		//		android.util.Log.
//		Log.i( "info", "WHER ARE YOU???");
	}
	
	private class SavedState
	{
		public Bitmap _bitmap;
		public ListAdapter _adapter;
		
		public SavedState( ) //Bitmap face, ListAdapter adapter )
		{
			_bitmap=_picture.getBitmap( );
			_adapter=_names_list.getAdapter();
		}
	}
	
	protected void onDestroy()
	{
		super.onDestroy( );
		try
		{
			if( _thread!=null ) _thread.join( );
			System.gc( );
		}
		catch( Exception e )
		{
			
		}
	}
	
	public Object onRetainNonConfigurationInstance( ) 
	{
		return new SavedState( );
	}
	
	protected void onPause( )
	{
		if( _progress!=null ) { _progress.dismiss( ); _progress=null; }
		super.onPause( );
	}
	
//	public void onSaveInstanceState( Bundle outState ) 
//    {
////		_stop=true;
////		try { if( _thread!=null ) _thread.join( ); } catch (InterruptedException e) { } //have to wait till thread finished
//		
//        super.onSaveInstanceState( outState ); 
//    }
//
//	public void onRestoreInstanceState( Bundle inState )
//	{
//		
//	}

//	public void onConfigurationChanged( Configuration configuration )
//	{
//	
//	}
	
    public boolean onCreateOptionsMenu( Menu menu )
    {
        menu.add(0, MENU_SELECT, 0, "Select")
        	.setIcon( android.R.drawable.ic_menu_search );
        
        if( _picture.isBitmap() )
        {
	        menu.add(0, MENU_ROTATE, 0, "Rotate")
	        	.setIcon( android.R.drawable.ic_menu_rotate );
        }

    	return true;
    }

    public boolean onOptionsItemSelected( MenuItem item )
    {
    	switch( item.getItemId() ) 
    	{
	        case MENU_SELECT:
	        	selectImage( );
	            return true;
    		case MENU_ROTATE:
    			Bitmap bmp=_picture.getBitmap( );
    			if( bmp==null ) return false;
    			
    			Matrix m=new Matrix( );
    			m.postRotate( 90 );
    			Bitmap rotated_bitmap=Bitmap.createBitmap( bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), m, true );
    			
    			bmp.recycle();
    			bmp=null;
    			
				_names_list.clear( );

				processBitmap( rotated_bitmap );
    			return true;
        }

    	return false;
    }
    
    private void processBitmap( Bitmap bitmap )
    {
    	_picture.setBitmap( bitmap );
    	
		_progress=ProgressDialog.show(this, "Working...", "Detecting faces", true, false);
		
		_faceDetection=new FaceDetection( _handler, bitmap );
		_thread=new Thread( _faceDetection );
		_thread.start( );   	
    }
    
    protected void onActivityResult( int requestCode, int resultCode, Intent data )
    {
    	if( resultCode==RESULT_OK )
    	{
    		switch( requestCode )
    		{
    		case SELECT_IMAGE:
				_names_list.clear( );
				_picture.setBitmap( null ); //recycle the bitmap
    			
    			Bitmap bmp=null;
    			try 
    			{
    				if( data.getDataString().equals("") )
    				{
    					throw new Exception( "This image or album (e.g., picasaweb album) is not supported yet" );
    				}
    				else
    				{
    					
    					bmp=Images.Media.getBitmap( this.getContentResolver(), data.getData() );
    				}
    				
				} 
    			catch( Exception e ) 
				{
    				Toast.makeText( this, e.getMessage(), Toast.LENGTH_LONG ).show( );
					return; 
				}
				
				Bitmap resized_bmp=ImageWithFaces.resizeBitmap( bmp, MAX_WIDTH, MAX_HEIGHT );
    			Toast.makeText( this, Integer.toString(resized_bmp.getWidth()), Toast.LENGTH_LONG ).show( );
				
//				Bitmap resized_bmp=Bitmap.createBitmap( bmp ); //resizing doesn't work :(
				bmp=null;

				processBitmap( resized_bmp );

    			break;
    		}
    	}
    }
    
    private void selectImage( )
    {
    	Intent i=new Intent( Intent.ACTION_PICK );
    	i.setType( "image/*" );

    	startActivityForResult( i, SELECT_IMAGE );
    }
    
    
    boolean _stop=false;
    
    private class FaceDetection implements Runnable
    {
    	private Bitmap _bitmap;
        private FaceDetector   _detector;
        private Handler __handler;
    	
    	public FaceDetection( Handler handler, Bitmap bitmap )
    	{
    		_bitmap=bitmap;
    		__handler=handler;
    	}
    	
    	public void setHandler( Handler handler ) { _handler=handler; }
    	
		public void run( ) 
		{
			_detector=new FaceDetector( _bitmap.getWidth(), _bitmap.getHeight(), 5 );
			
			final FaceDetector.Face faces[]=new FaceDetector.Face[ 5 ];
			final int count=_detector.findFaces( _bitmap, faces );
			
			if( _progress!=null ) { _progress.dismiss( ); _progress=null; }

			__handler.post( new Runnable()
				{
					public void run() 
					{
						for( int i=0; i<count; i++ )
						{
							PointF midpoint=new PointF();
							faces[i].getMidPoint( midpoint );

							Person person=new Person( "Unknown person #"+Integer.toString(i+1) );
							person.setFace( midpoint, faces[i].eyesDistance(), _bitmap );
							
							_names_list.add( person ); //add info in ListView		
							_thread=null;
						}
					} 
				} );
		}
    }
}
