package com.cawka.FriendDetector;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import com.karthik.learnsql.R;
import com.karthik.learnsql.DBHandle;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
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
	
	public static final String Create =  "create table detectors(_id integer primary key autoincrement, _server VARCHAR[20] not null, "
        + "_port int not null, _enabled boolean not null,"
        +	"_localEnabled boolean not null, _RemoteEnabled boolean not null, _timeout int not null);";
	
	private static final String Server = "_server";
	private static final String Port = "_port";
	private static final String Enabled = "_enabled";
	private static final String LocalEnabled = "_localEnabled";
	private static final String Timeout = "_timeout";
	private static final String Table = "detectors";
	private static final String RemoteEnabled = "_RemoteEnabled";
	
	private static final int MENU_SELECT = 1;
	private static final int MENU_ROTATE = 2;
	private static final int MENU_RETRY = 3;
	private static final int MENU_SETTINGS = 4;

	private static final int SELECT_IMAGE = 1;
	private static final int CHANGE_SETTINGS = 2;
	
	private static final int MAX_SIZE = 800;


    
    private ImageWithFaces _picture;
    private ListOfPeople   _names_list;
    
    private Handler _handler = new Handler(); //to handle UI updates
    private Thread _thread;
    
//    private FaceDetection _faceDetection;
    
    private ProgressBar    _progress2;
    private Object _progress_lock=new Object();
    
    private List<iFaceDetector> _detectors;
    private List<iFaceLearner>  _learners;
    
    private DBHandle helper;
    private SQLiteDatabase db;
//    = { 
											//new FaceDetectorRemote( "cawka.homeip.net",20000 ), 
//    										new FaceDetectorRemote( "131.179.192.201",2000 ), 
//    										new FaceDetectorLocal( ) 
//    									 };
//    private iFaceLearner _learners[] = {
//								    		//new FaceDetectorRemote( "cawka.homeip.net",20000 )
////											new FaceDetectorRemote( "131.179.192.201",2000 ), 
//    								   };
    
    ////////////////////////////////////////////////////////////////////
    
	public void onCreate( Bundle savedInstanceState ) 
    {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.main );
        
        _picture=   (ImageWithFaces)findViewById( R.id.picture );
        _names_list=(ListOfPeople)  findViewById( R.id.names_list );
        
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
        helper = new DBHandle(this, Create);
        db = helper.getReadableDatabase();
		LoadServers();
    }
		
	public Cursor fetchAllServers() {

        return db.query(Table, new String[] {"_id", Server, Port,
                Enabled, LocalEnabled, RemoteEnabled, Timeout}, null, null, null, null, null);
    }

	
	private void LoadServers() {
		// TODO Auto-generated method stub
		//DB code goes here
		//or can use the local detector
		Cursor c = fetchAllServers();
    	Log.v("Karthik","Cursor " + c.getCount());
    	startManagingCursor(c);
    	
		new Toast(this).makeText(this, "Falling to Default !! " + c.getCount() + " entries in table", Toast.LENGTH_SHORT).show();
	}

	protected void restoreSettings(Intent data )
	{
        Log.v( TAG, "restoreSettings" );
        
        _detectors=new LinkedList<iFaceDetector>( );
        _learners =new LinkedList<iFaceLearner>( );
        
        SharedPreferences prefs=PreferenceManager.getDefaultSharedPreferences( getBaseContext() );
        
        MAX_SIZE=Integer.parseInt( prefs.getString(Settings.KEY_MAX_SIZE, "800") );
        
        if( prefs.getBoolean(Settings.KEY_REMOTE_ENABLED, false) )
        {
        	FaceDetectorRemote detector=new FaceDetectorRemote(
        			prefs.getString(Settings.KEY_HOSTNAME, "127.0.0.1"),
        			prefs.getString(Settings.KEY_PORT, "55436"),
        			Integer.parseInt(prefs.getString( Settings.KEY_TIMEOUT, "2000" ))
        			);
        	_detectors.add( detector );
        	_learners.add( detector );
        }
        
        if( prefs.getBoolean(Settings.KEY_LOCAL_ENABLED, true) )
        {
        	_detectors.add( new FaceDetectorLocal( ) );
        }
        
        System.gc( );
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
	
	protected void onDestroy()
	{
		super.onDestroy( );
		helper.close();
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
		Log.v( TAG, "onRetainNonConfigurationInstance" );
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
	
	public boolean onPrepareOptionsMenu( Menu menu )
	{
		if( _picture.isBitmap() )
        {
			if( menu.findItem(MENU_ROTATE)==null )
			{
		        menu.add(0, MENU_ROTATE, 0, "Rotate")
		        	.setIcon( android.R.drawable.ic_menu_rotate );
		        
		        menu.add(0, MENU_RETRY, 0, "Retry" )
		        	.setIcon( R.drawable.ic_menu_refresh );
			}
        }
		else
		{
			menu.removeItem( MENU_ROTATE );
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
    		case MENU_RETRY:
	            retryDetection( );
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
    		case MENU_SETTINGS:
    			Intent i=new Intent( );
    			i.setAction( "com.karthik.learnsql.learnsql" );
    			startActivityForResult( i, CHANGE_SETTINGS );
    			
    			return true;
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
    		restoreSettings(data );
    		return;
    	}
    	
    	if( resultCode==RESULT_OK )
    	{
    		switch( requestCode )
    		{
    		case SELECT_IMAGE:
				_names_list.clear( );
				_picture.setBitmap( null ); //recycle the bitmap

				Log.v( TAG, "Total mem: "+Double.toString(Runtime.getRuntime().totalMemory()) );
				Log.v( TAG, "Free mem: "+Double.toString(Runtime.getRuntime().freeMemory()) );

				System.gc();
    			
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
    				}
    				
				} 
    			catch( Exception e ) 
				{
    				Toast.makeText( this, e.getMessage(), Toast.LENGTH_LONG ).show( );
					return; 
				}

    			try
    			{
					Bitmap resized_bmp=ImageWithFaces.processBitmap( bmp, MAX_SIZE, orientation );	
//					bmp.recycle( );
					bmp=null;
					processBitmap( resized_bmp );
    			}
    			catch( Exception e )
    			{
    				Toast.makeText( this, e.getLocalizedMessage(), Toast.LENGTH_SHORT ).show( );
    				processBitmap( bmp );
    			}

    			break;
    		}
    	}
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
    	
		_thread=new Thread( new Thread(new FaceDetection( bitmap )) );
		_thread.start( );   	
    }
    
    private void retryDetection( )
    {
    	if( _thread!=null ) return;
    	
    	Bitmap bitmap=_picture.getBitmapSafe( );
    	if( bitmap==null ) return;
    	
    	if( _progress2==null )
    	{
    		_progress2 =(ProgressBar) findViewById( R.id.progress_bar_image );
    	}
    	_progress2.setVisibility( View.VISIBLE );
    	
    	_names_list.clear( );
		_thread=new Thread( new Thread(new FaceDetection( bitmap )) );
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
    
    /////////////////////////////////////////////////////////////////////////////////

	private class updateUI implements Runnable
    {
    	private iFaceDetector _detector;
    	
    	public updateUI( iFaceDetector detector )
    	{
    		_detector=detector;
    	}

		public void run() 
		{
			for( Person person : _detector.getFaces() )
			{
				if( !person.hasName() )
					person.setDefaultName( FriendDetector.this.getResources().getString(R.string.unknown_person) );
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
			
			// if a remote detector fails (e.g., network is unavailable or server is not running), run a local one
			for( iFaceDetector detector : _detectors )
			{
				boolean ret=detector.detect( _bitmap );
				if( ret ) 
				{
					_handler.post( new updateUI(detector) );
					// technically, we could run all detectors, but the question is how we going to merge the results
					break;
				}
				else
				{
					_handler.post( new Runnable() 
						{ 
							public void run() 
							{ 
								Toast.makeText(FriendDetector.this, "Detector timeout, using next available", Toast.LENGTH_SHORT).show(); 
							} 
						} );
				}
			}
			
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
}

