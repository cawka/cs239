package com.cawka.FriendDetector;

import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.cawka.FriendDetector.R;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore.Images;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.widget.ListAdapter;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.AdapterView.AdapterContextMenuInfo;

public class FriendDetector extends Activity
{
	private static final String TAG="FriendDetector";
	
    private static final int MENU_SELECT = 1;
	private static final int MENU_ROTATE = 2;
	private static final int MENU_XML    = 3;

	private static final int SELECT_IMAGE = 1;
	
	private static final int MAX_SIZE = 800;

    
    private ImageWithFaces _picture;
    private ListOfPeople   _names_list;
    
    private Handler _handler = new Handler(); //to handle UI updates
    private Thread _thread;
    
    private FaceDetection _faceDetection;
    
    private ProgressBar    _progress2;
    private Object _progress_lock=new Object();
    
    ////////////////////////////////////////////////////////////////////
    
	public void onCreate( Bundle savedInstanceState ) 
    {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.main );
        
        _picture=   (ImageWithFaces)findViewById( R.id.picture );
        _names_list=(ListOfPeople)  findViewById( R.id.names_list );
        
        registerForContextMenu( _names_list );
        
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
	
	public void onCreateContextMenu( ContextMenu menu, View v,
            ContextMenuInfo menuInfo )
	{
		if( v==_names_list ) _names_list.onCreateContextMenu( ((AdapterContextMenuInfo)menuInfo).position );
	}
		
	protected void onStart( )
	{
		super.onStart( );
		
        SavedState state=(SavedState)getLastNonConfigurationInstance( );
        if( state!=null )
        {
        	if( state._bitmap!=null && !state._bitmap.isRecycled() )
        	{
    			_picture.setBitmap( state._bitmap );
            	_names_list.setAdapter( state._adapter );
        	}
        	
        	state=null;
        	System.gc( );
        }
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
	
	protected void onResume( )
	{
		super.onResume( );
	}
	
	protected void onPause( )
	{
		synchronized( _progress_lock ) 
		{
			_progress2=null;
		}
		
		super.onPause( );
	}
	
	///////////////////////////////////////////////////////////////////////
	
    public boolean onCreateOptionsMenu( Menu menu )
    {
    	if( _thread!=null ) return false;
    	
        menu.add(0, MENU_SELECT, 0, "Select")
        	.setIcon( android.R.drawable.ic_menu_search );
        
        if( _picture.isBitmap() )
        {
	        menu.add(0, MENU_ROTATE, 0, "Rotate")
	        	.setIcon( android.R.drawable.ic_menu_rotate );
        }
        
        if( _names_list.getAdapter().getCount()>0 )
        {
        	menu.add(0, MENU_XML, 0, "XML" )
        		.setIcon( android.R.drawable.ic_menu_info_details );
        }

    	return true;
    }

    public boolean onOptionsItemSelected( MenuItem item )
    {
    	if( _thread!=null ) return false;
    	
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
    		case MENU_XML:
    			try
    			{
//    				StringBuilder request=new StringBuilder();
//    				request.append( "<FriendDetector>\n" );
//    				for( int i=0; i<_names_list.getAdapter().getCount(); i++ )
//    				{
//    					Person person=(Person)_names_list.getAdapter().getItem( i );
//    					person.appendWithRequest( request );
//    				}
//    				request.append( "</FriendDetector>\n" );

//    				Log.v( TAG, request.toString() );
    			}
    			catch( Exception ex )
    			{
    				//
    			}

    			return true;
        }

    	return false;
    }
     
    private void processBitmap( Bitmap bitmap )
    {
    	if( _thread!=null ) return;
    	
    	_picture.setBitmap( bitmap );

    	if( _progress2==null )
    	{
    		_progress2 =(ProgressBar) findViewById( R.id.progress_bar_image );
    	}
    	_progress2.setVisibility( View.VISIBLE );
    	
		_faceDetection=new FaceDetection( bitmap );
		_thread=new Thread( _faceDetection );
		_thread.start( );   	
    }
    
    protected String getRealPathFromURI( Uri contentUri ) 
    {
    	// can post image  
    	String [] proj={ Images.Media.DATA };
    	Cursor cursor = managedQuery( contentUri,  
    	        proj, // Which columns to return  
    	        null,       // WHERE clause; which rows to return (all rows)  
    	        null,       // WHERE clause selection arguments (none)  
    	        null); // Order-by clause (ascending by name)  
    	int column_index = cursor.getColumnIndexOrThrow( Images.Media.DATA );  	
		cursor.moveToFirst();  

		return cursor.getString(column_index);  
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
    			int orientation=-1;//ExifInterface.ORIENTATION_UNDEFINED;
    			try 
    			{
    				if( data.getDataString().equals("") )
    				{
    					throw new Exception( "This image or album (e.g., picasaweb album) is not supported yet" );
    				}
    				else
    				{
    					String filename=getRealPathFromURI( data.getData() );
    					bmp=BitmapFactory.decodeFile( filename );
    					
    					try
    					{
	    					ExifInterface exif=new ExifInterface( filename );
	    					orientation=Integer.parseInt( exif.getAttribute(ExifInterface.TAG_ORIENTATION) );
    					}
    					catch( IOException ex )
    					{
    						//not a jpeg file
    						Log.d( TAG, filename+" is a jpeg file or doesn't have exif information available" );
    					}
    					
    					Log.v( TAG, Integer.toString(orientation) );
    				}
    				
				} 
    			catch( Exception e ) 
				{
    				Toast.makeText( this, e.getMessage(), Toast.LENGTH_LONG ).show( );
					return; 
				}
				
				Bitmap resized_bmp=ImageWithFaces.processBitmap( bmp, MAX_SIZE, orientation );				
				bmp=null;

				processBitmap( resized_bmp );

    			break;
    		}
    	}
    }
    
    private void selectImage( )
    {
    	if( _thread!=null ) return;
    	
    	Intent i=new Intent( Intent.ACTION_PICK );
    	i.setType( "image/*" );

    	startActivityForResult( i, SELECT_IMAGE );
    }
    
    
    private class FaceDetection implements Runnable
    {
    	private Bitmap _bitmap;
    	
    	public FaceDetection( Bitmap bitmap )
    	{
    		_bitmap=bitmap;
    	}
    	
		public void run( ) 
		{
			Person.resetColors( );
			final FaceDetector detector=new FaceDetector( _bitmap );
			
			_handler.post( new Runnable()
				{
					public void run() 
					{
						synchronized( _progress_lock ) 
//						 to handle the case when onPause is called almost simultaneously
//						 with thread termination
						{
							if( _progress2!=null ) {  _progress2.setVisibility( View.INVISIBLE ); }
						}
						
						for( Person person : detector.getFaces() )
						{
							if( !person.hasName() )
								person.setDefaultName( FriendDetector.this.getResources().getString(R.string.unknown_person) );
							_names_list.add( person );
						}
						
						_thread=null;
					} 
				} );
		}
    }
}

