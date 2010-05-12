package com.cawka.FriendDetector;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore.Images;
import android.util.Log;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.widget.ListAdapter;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.AdapterView.AdapterContextMenuInfo;

import com.cawka.FriendDetector.detector.FaceDetectorLocal;
import com.cawka.FriendDetector.detector.FaceDetectorRemote;
import com.cawka.FriendDetector.detector.iFaceDetector;
import com.cawka.FriendDetector.detector.iFaceLearner;
import com.cawka.FriendDetector.gui.Cam;
import com.cawka.FriendDetector.gui.ImageWithFaces;
import com.cawka.FriendDetector.gui.ListOfPeople;
import com.cawka.FriendDetector.settings.DBHandle;
import com.cawka.FriendDetector.settings.Server;

public class Main extends Activity 
{
	private static final String TAG="FriendDetector";
	
	private static final int MENU_SELECT = 1;
//	private static final int MENU_ROTATE = 2;
	private static final int MENU_RETRY = 3;
	private static final int MENU_SETTINGS = 4;
	private static final int MENU_GALLERY = 5;

	private static final int SELECT_IMAGE = 1;
	private static final int CHANGE_SETTINGS = 2;
	
	////////////////////////////////////////////////////////////////////
	
	public static final int DB_VERSION=4;
	
    private ImageWithFaces _picture;
    private ListOfPeople   _names_list;
    private Cam _cam;
    
    private Handler _handler = new Handler(); //to handle UI updates
    private Thread _thread;
    
    private ProgressBar    _progress2;
    private Object _progress_lock=new Object();
    
    private List<iFaceDetector> _detectors;
    private List<iFaceLearner>  _learners;
    
    ////////////////////////////////////////////////////////////////////
   
	public void onCreate( Bundle savedInstanceState ) 
    {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.main );
        //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        
        _picture=   (ImageWithFaces)findViewById( R.id.picture );
        _names_list=(ListOfPeople)  findViewById( R.id.names_list );
        //_cam = (Cam)  findViewById( R.id.surface);
        
        Log.v("Karthik", "Creating Cam");
        _cam = new Cam();
        Log.v("Karthik", "Calling INit");
        _cam.init(this, findViewById(R.id.surface),_picture);
        Log.v("Karthik", "INit done");
        
        findViewById(R.id.surface).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				_cam.takePicture();
			}
		});
        
        
        registerForContextMenu( _names_list );
        
        _names_list.setImageWithFaces( _picture );
        _names_list.setFriendDetector( this );
        _picture   .setListOfPeople( _names_list );
        
        _picture.setOnClickListener( new OnClickListener()
        	{
				public void onClick( View v )
				{
					selectImage( );
				}
        	} );
        restoreSettings( );
    }
	
	protected void onStart( )
	{
		Log.v( TAG, "onStart" );
		super.onStart( );
	}
	
	protected void onRestart( )
	{
		Log.v( TAG, "onRestart" );
		super.onRestart( );
	}
	
	protected void onResume( )
	{
		Log.v( TAG, "onResume" );
		super.onResume( );
	}
	
	protected void onPause( )
	{
		Log.v( TAG, "onPause" );
		synchronized( _progress_lock ) 
		{
			_progress2=null;
		}
		
		super.onPause( );
	}
	
	protected void onStop( )
	{
		Log.v( TAG, "onStop" );
		super.onStop( );
	}

	protected void onDestroy()
	{
		Log.v( TAG, "onDestroy" );
		
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

	protected void onRestoreInstanceState( Bundle savedInstanceState )
	{
		Log.v( TAG, "onRestoreInstanceState" );
		super.onRestoreInstanceState( savedInstanceState );
		
		SavedState state=(SavedState)getLastNonConfigurationInstance( );
		if( state == null )
			state=(SavedState)savedInstanceState.getSerializable( "state" );

		if( state != null )
		{
			if( !state._image.equals( "" ) )
			{
				_picture.setImage( state._image,false );
				_names_list.setAdapter( state._adapter );
			}
			
			if(!state.isCamera)
			{
				switchToPicture();
			}

			state=null;
			System.gc( );
		}
	}
		
	protected void  onSaveInstanceState( Bundle outState )
	{
		Log.v( TAG, "onSaveInstanceState" );
		
		super.onSaveInstanceState( outState );
		outState.putSerializable( "state", new SavedState( ) );
	}
	
	public Object onRetainNonConfigurationInstance( ) 
	{
		Log.v( TAG, "onRetainNonConfigurationInstance" );
		return new SavedState( );
	}
	
	protected void restoreSettings( )
	{
        Log.v( TAG, "restoreSettings" );
        
        _detectors=new LinkedList<iFaceDetector>( );
        _learners =new LinkedList<iFaceLearner>( );

        int count_remote=0;
        int count_local =0;
        for( Server.Config config : new DBHandle(this).getAllConfigs() )
        {
        	if( !(config.enabled || config.enabled_recognizer) ) continue; //ignore disabled detector
        	
        	if( config.type==Server.REMOTE )
        	{
        		count_remote++;
        		
        		try
        		{
		        	FaceDetectorRemote detector=new FaceDetectorRemote(
						config.hostname,
						Integer.toString(config.port),
						config.timeout
					);
	        		detector.setFullDetection( config.enabled && config.enabled_recognizer );
		        	
		        	if( config.enabled ) 			_detectors.add( detector );
			    	if( config.enabled_recognizer ) _learners.add( detector );
        		}
        		catch( NoClassDefFoundError e )
        		{
        			Log.v( TAG, "Ice library wasn't enabled during the compilation. Proceeding without a remote detector" );
        		}
        	}
        	else if( config.type==Server.LOCAL )
        	{
        		count_local++;
        		FaceDetectorLocal detector=new FaceDetectorLocal( this );
        		detector.setFullDetection( config.enabled && config.enabled_recognizer );
        		
        		if( config.enabled ) 			_detectors.add( detector );
        		if( config.enabled_recognizer ) _learners.add( detector );
        	}
        }
        
        Log.v( TAG, Integer.toString(count_local)+" local and "+Integer.toString(count_remote)+" remote detectors are configured" );
        Log.v( TAG, "Detectors: "+Integer.toString(_detectors.size( ))+", learners: "+Integer.toString(_learners.size( ))  );
        
        System.gc( );
	}
	
	public void onCreateContextMenu( ContextMenu menu, View v,
            ContextMenuInfo menuInfo )
	{
		if( v==_names_list ) _names_list.onCreateContextMenu( ((AdapterContextMenuInfo)menuInfo).position );
	}
		

	///////////////////////////////////////////////////////////////////////
	
	public boolean onPrepareOptionsMenu( Menu menu )
	{
		if( _picture.isBitmap() )
        {
			if( menu.findItem(MENU_RETRY)==null )
			{
//		        menu.add(0, MENU_ROTATE, 0, "Rotate")
//		        	.setIcon( android.R.drawable.ic_menu_rotate );
//		        
		        menu.add(0, MENU_RETRY, 0, "Retry" )
		        	.setIcon( R.drawable.ic_menu_refresh );
			}
        }
		else
		{
//			menu.removeItem( MENU_ROTATE );
			menu.removeItem( MENU_RETRY );
		}
		
		return true;
	}
	
    public boolean onCreateOptionsMenu( Menu menu )
    {
    	if( _thread!=null ) return false;
    	
        menu.add(0, MENU_SELECT, 0, "Select")
        	.setIcon( android.R.drawable.ic_menu_search );
                
        menu.add(0, MENU_SETTINGS, 0, "Settings" )
        	.setIcon( android.R.drawable.ic_menu_preferences );
        
//        menu.add(0, MENU_GALLERY, 0, "Local training set" )
//        	.setIcon(  R.drawable.ic_menu_allfriends );

    	return true;
    }

    public boolean onOptionsItemSelected( MenuItem item )
    {
    	if( _thread!=null ) return false;
    	
    	switch( item.getItemId() ) 
    	{
	        case MENU_SELECT:
	        	selectImage( );
	        	findViewById(R.id.surface).setVisibility(View.GONE);
	        	findViewById(R.id.picture).setVisibility(View.VISIBLE);
	            return true;
    		case MENU_RETRY:
	            retryDetection( );
    			return true;
//    		case MENU_ROTATE:
//    			Bitmap bmp=_picture.getBitmap( );
//    			if( bmp==null ) return false;
//    			
//    			Matrix m=new Matrix( );
//    			m.postRotate( 90 );
//    			Bitmap rotated_bitmap=Bitmap.createBitmap( bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), m, true );
//    			
//    			bmp.recycle();
//    			bmp=null;
//    			
//				_names_list.clear( );
//
//				processBitmap( rotated_bitmap );
//    			return true;
    		case MENU_SETTINGS:
    			Intent i=new Intent( );
    			i.setAction( "com.cawka.FriendDetector.settings.List" );
    			startActivityForResult( i, CHANGE_SETTINGS );
    			
    			return true;
//    		case MENU_GALLERY:
//    			// need to revalidate detectors if the training set was modified
//    			startActivityForResult( new Intent( ).setAction( "com.cawka.FriendDetector.Gallery" ), CHANGE_SETTINGS );
//    			
//    			return true;
        }

    	return false;
    }
    
    private void selectImage( )
    {
    	if( _thread!=null ) return;
    	
    	Intent i=new Intent( Intent.ACTION_PICK );
    	i.setType( "image/*" );

    	startActivityForResult( i, SELECT_IMAGE );
    }
    
    protected void onActivityResult( int requestCode, int resultCode, Intent data )
    {
    	if( requestCode==CHANGE_SETTINGS )
    	{
    		restoreSettings( );
    		return;
    	}
    	
    	if( resultCode==RESULT_OK )
    	{
    		switch( requestCode )
    		{
    		case SELECT_IMAGE:
				_names_list.clear( );

    			String filename="";
    			try 
    			{
    				if( data.getDataString().equals("") )
    				{
    					throw new Exception( "This image or album (e.g., picasaweb album) is not supported yet" );
    				}
    				else
    				{
    					filename=getRealPathFromURI( data.getData() );
    				}
    				
				} 
    			catch( Exception e ) 
				{
    				Toast.makeText( this, e.getMessage(), Toast.LENGTH_LONG ).show( );
					return; 
				}
				
    			if( !filename.equals("") ) processImage( filename );
    				
    			break;
    		}
    	}
    }
    
    private void processImage( String filename )
    {
    	if( _thread!=null ) return;
    	
    	_picture.setImage( filename,true );

    	if( _progress2==null )
    	{
    		_progress2 =(ProgressBar) findViewById( R.id.progress_bar_image );
    	}
    	_progress2.setVisibility( View.VISIBLE );
    	
		_thread=new Thread( new FaceDetection( _picture.getBitmap( )) );
		_thread.start( );   	
    }
    
    private void retryDetection( )
    {
    	if( _thread!=null ) return;
    	
    	Bitmap bitmap=_picture.getBitmap( );
    	if( bitmap==null ) return;
    	
    	if( _progress2==null )
    	{
    		_progress2 =(ProgressBar) findViewById( R.id.progress_bar_image );
    	}
    	_progress2.setVisibility( View.VISIBLE );
    	
    	_names_list.clear( );
		_thread=new Thread( new FaceDetection( bitmap ) );
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
    
    public void onLearnRequest( Bitmap face, String name )
    {
    	if( _thread!=null ) return;

    	if( _progress2==null )
    	{
    		_progress2 =(ProgressBar) findViewById( R.id.progress_bar_image );
    	}
    	_progress2.setVisibility( View.VISIBLE );
    	
		_thread=new Thread( new FaceLearning(face,name) );
		_thread.start( );   	
	}
 
    /////////////////////////////////////////////////////////////////////////////////
    
	private class SavedState implements Serializable
	{
		private static final long	serialVersionUID	=-5766693616119479431L;
		
		public String 	   _image;
		public ListAdapter _adapter;
		Boolean isCamera;
		
		public SavedState( ) //Bitmap face, ListAdapter adapter )
		{
			_image=_picture.getImage( );
			_adapter=_names_list.getAdapter();
			isCamera = findViewById(R.id.picture).getVisibility() != View.VISIBLE;
			
		}

		private void writeObject( ObjectOutputStream out ) throws IOException
		{
			out.writeUTF( _image );
			out.writeBoolean(isCamera);
			
			out.writeInt( _adapter.getCount() );
			for( int i=0; i<_adapter.getCount(); i++ )
			{
				out.writeObject( _adapter.getItem(i) );
			}
		}

		private void readObject( ObjectInputStream in ) throws IOException,
				ClassNotFoundException
		{
			_image=in.readUTF( );
			isCamera = in.readBoolean();
			
			int count=in.readInt( );
			for( int i=0; i<count; i++ )
			{
				Person person=(Person)in.readObject( );
			}
		}
	}    
    
    /////////////////////////////////////////////////////////////////////////////////

	private class updateUI implements Runnable
    {
    	private List<Person> _faces;
    	
    	public updateUI( List<Person> faces )
    	{
    		_faces=faces;
    	}

		public void run() 
		{
			for( Person person : _faces )
			{
				if( !person.hasName() )
					person.setDefaultName( Main.this.getResources().getString(R.string.unknown_person) );
				_names_list.add( person );
			}
		}
    }
	
	private class releaseUI implements Runnable
	{
		public void run( )
		{
			synchronized( _progress_lock ) 
			// to handle the case when onPause is called almost simultaneously
			// with thread termination
			{
				if( _progress2!=null ) {  _progress2.setVisibility( View.INVISIBLE ); }
			}
			
			_thread=null;
		}
	}
    
    /////////////////////////////////////////////////////////////////////////////////

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
			
			Iterator<iFaceDetector> i_detector;
			iFaceDetector detector=null;
			for( i_detector=_detectors.iterator(); i_detector.hasNext( );  )
			{
				detector=i_detector.next( );
				
				boolean ok=detector.detect( _bitmap );
				if( ok ) break;

				_handler.post( new Runnable() 
				{ 
					public void run() 
					{ 
						Toast.makeText(Main.this, "Detector timeout, using next available", Toast.LENGTH_SHORT).show(); 
					} 
				} );
			}
			
			if( detector==null ) 
			{ 
				_handler.post( new releaseUI() ); //no detectors available
				return;
			}
			if( detector.getFullDetection() )
			{
				List<Person> list=detector.getFaces( );
				detector.resetFaces( );
				_handler.post( new updateUI(list) ); 
				_handler.post( new releaseUI() ); 
			}
			
			for( Person person : detector.getFaces() )
			{
				for( iFaceLearner learner : _learners )
				{
					boolean ok=learner.recognize( person );
					if( ok ) break;
					
					_handler.post( new Runnable() 
					{ 
						public void run() 
						{ 
							Toast.makeText(Main.this, "Recognizer timeout, using next available (if available)", Toast.LENGTH_SHORT).show(); 
						} 
					} );
				}
			}
			List<Person> list=detector.getFaces( );
			detector.resetFaces( );
			
			_handler.post( new updateUI(list) ); 
			_handler.post( new releaseUI() ); 
		}
    }
	
	private class FaceLearning implements Runnable
	{
		Bitmap _bitmap;
		String _name;
		
		public FaceLearning( Bitmap bitmap, String name )
		{
			_bitmap=bitmap;
			_name=name;
		}
		
		public void run( )
		{
			for( iFaceLearner learner : _learners )
			{
				learner.learn( _bitmap, _name );
			}
			_handler.post( new releaseUI() );
		}
	}
	
	public boolean onKeyDown(int keyCode, KeyEvent event)
    {
     
        if (keyCode == 80) {
            if(findViewById(R.id.surface).getVisibility() != View.VISIBLE)
            {
            	findViewById(R.id.picture).setVisibility(View.GONE);
            	findViewById(R.id.surface).setVisibility(View.VISIBLE);
            	//_cam.StartPreview();
            }
            else
        	{
            	Log.v("Karthik","Camera Visbile.. Take Picture");
            	_cam.takePicture();
            	Log.v("Karthik","Camera Visbile.. Making it invisble");
          	}
            
            return true;
        }
        
        return super.onKeyDown(keyCode, event);
    }
	
	public void switchToPicture()
	{
       	findViewById(R.id.surface).setVisibility(View.GONE);
    	findViewById(R.id.picture).setVisibility(View.VISIBLE);
	
	}
}

