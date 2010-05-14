package com.cawka.FriendDetector;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import net.xeomax.FBRocket.FBRocket;
import net.xeomax.FBRocket.Friend;
import net.xeomax.FBRocket.LoginListener;
import net.xeomax.FBRocket.ServerErrorException;

import com.cawka.FriendDetector.detector.eigenfaces.Eigenface.NamedFace;
import com.cawka.FriendDetector.gui.ImageAdapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.GridView;
import android.widget.Toast;
import android.widget.AdapterView.AdapterContextMenuInfo;


public class FBGallery extends Activity implements LoginListener
{
	private GridView _grid;
	private ImageAdapter _adapter;
	
	private static final int REQUEST_LOGIN = 1;
	
	private static final int MENU_LOGOUT = 0;
	
	private FBRocket _fb;
	
	Handler _handler=new Handler( );
	
	Thread _thread=null;
	FBDBHandler _dbHandler;
	
	private static final String TAG="FriendDetector.FacebookPlugin";

	private static final String API_KEY="56e0dc5f9c7e6aa9a86c3f2cfd4550f8";
	
	protected void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		this.setContentView( R.layout.gallery );
		
		SavedState state=(SavedState)getLastNonConfigurationInstance( );
		
		_grid=(GridView)findViewById( R.id.grid );
		
		if( state==null )
			_adapter=new ImageAdapter( getBaseContext() );
		else
			_adapter=state.adapter;
		
    	_grid.setAdapter( _adapter );
    	
    	registerForContextMenu( _grid );
		
		_dbHandler=new FBDBHandler( this );
		
		////////////////////////////////////////////////////////////////////////

		if( state==null )
			_fb = new FBRocket( getBaseContext(), "FriendDetection", API_KEY, this );
		else
			_fb = state.fb;
	}

	protected void onStart( )
	{
		super.onStart( );
		
    	// Determine whether there exists a previously-saved Facebook:
    	if( !_fb.trySavedLogin() )
    	{
    		_fb.requestLoginActivity( this, REQUEST_LOGIN );
    	}
	}
	
	protected void onDestroy( )
	{
		if( _thread!=null )
		{
			_thread.interrupt( );
			
			try{ _thread.join( ); } catch( Exception e ) { }
		}
//		if( _adapter!=null ) _adapter.clear( );
		
		super.onDestroy();
	}
		
	public Object onRetainNonConfigurationInstance( ) 
	{
		return new SavedState( );
	}	
	
	
    public boolean onCreateOptionsMenu( Menu menu )
    {
    	if( _thread!=null ) return false;
    	
        menu.add( 0, MENU_LOGOUT, 0, "Logout" );
 
        
    	return true;
    }

    public boolean onOptionsItemSelected( MenuItem item )
    {
    	if( _thread!=null ) return false;
    	
    	switch( item.getItemId() ) 
    	{
	        case MENU_LOGOUT:
	        	_fb.logout( );
	        	_dbHandler.deleteAll( );
	        	finish( );
	            return true;
    	}

    	return false;
    }	
	
	public void onCreateContextMenu( ContextMenu menu, View v, ContextMenuInfo menuInfo )
	{
		onCreateContextMenu( ((AdapterContextMenuInfo)menuInfo).position );
	}
	
	private void onCreateContextMenu( final int position )
	{
		final NamedFace person=(NamedFace)_grid.getAdapter().getItem( position );

		new AlertDialog.Builder( this )
        .setMessage( "Select action" )
        .setPositiveButton("Use photo for training",  new DialogInterface.OnClickListener(){
			public void onClick( DialogInterface dialog, int which )
			{
				Intent i=new Intent( );
				i.putExtra( "file", person.filename );
				i.putExtra( "name", person.name );
				setResult( RESULT_OK, i );
				
				finish( );				}
		} )
        .setNeutralButton("Get more photos", new DialogInterface.OnClickListener(){
			public void onClick( DialogInterface dialog, int which )
			{
				new Thread( new ExtraPhotosLoader(position,person) ).start( );
			}
		} )
		.setNegativeButton( "Do nothing", new DialogInterface.OnClickListener(){
			public void onClick( DialogInterface dialog, int which )
			{
				// really, do nothing
			}
		} )
        .show();		
	}
	
	protected void onActivityResult( int requestCode, int resultCode, Intent data )
	{
		if( requestCode==REQUEST_LOGIN )
			_fb.onLoginActivityReturn( resultCode, data );
		
		// other crazy shit
	}
	
	public void onLoginFail( )
	{
		Toast.makeText( this, "Login failed!", Toast.LENGTH_SHORT ).show( );
	}

	public void onLoginSuccess( )
	{
		Toast.makeText( this, "Login success!", Toast.LENGTH_SHORT ).show( );
		
    	_thread=new Thread( new FriendsLoader() );
    	_thread.start( );
	}
	
	private class SavedState //implements Serializable
	{
		//private static final long	serialVersionUID	=658779515116443999L;

		public ImageAdapter adapter;
		public FBRocket fb;

		public SavedState( ) //Bitmap face, ListAdapter adapter )
		{
			adapter=_adapter;
			fb=_fb;
		}
	}
	
	private class FriendsLoader implements Runnable
	{
		public void run( )
		{
			try
			{
				List<String> uids=_fb.getFacebook( ).getFriendUIDs( );
				for( String uid : uids )
				{
					if( Thread.interrupted() ) 
					{
						Log.v( TAG, "Thread.interrupted()==true" );
						return;
					}
					
					Log.v( TAG, uid );
					try
					{
						NamedFace face=_dbHandler.getNamedFace( uid );
						if( face!=null )
						{
							face.extra="0";
							_adapter.add( face );
							continue;
						}
						
						Friend friend=_fb.getFacebook( ).getFriend( uid );
						friend.pic=friend.pic.replace( "_s.jpg", "_n.jpg" );
						
						URL url=new URL( friend.pic );
						
						Log.v( TAG, friend.pic );
						InputStream is = (InputStream)url.getContent( );
						
						face=new NamedFace( uid, friend.name, is, "facebook", "0" );
						
						_adapter.add( face );
						_dbHandler.saveNamedFace( face );
					}
					catch( ServerErrorException e )
					{
						Log.v( TAG, "ServerErrorException: "+e.getMessage( ) );
						
//						_handler.post( new Runnable(){public void run(){Toast.makeText(getBaseContext(), "Facebook error", Toast.LENGTH_SHORT ).show();};} );
					}
					catch( MalformedURLException e )
					{
						Log.v( TAG, "Malformed URL: "+e.getMessage( ) );
					}
					catch( IOException e )
					{
						Log.v( TAG, "IOException: "+e.getMessage( ) );
					}
					catch( Exception e )
					{
						Log.v( TAG, "Exception:"+e.getMessage( ) );
					}
				}

			} 
			catch( ServerErrorException e )
			{
				Log.v( TAG, "ServerErrorException: "+e.getMessage( ) );
			}
			
			_thread=null;
		}
	}
	
	private class ExtraPhotosLoader implements Runnable
	{
		NamedFace person;
		int       position;
		
		public ExtraPhotosLoader( int position, NamedFace person )
		{
			this.position=position;
			this.person=person;
		}
		
		public void run( )
		{
			try
			{
				int offset=Integer.parseInt( person.extra );
				List<String> photos=_fb.getFacebook( ).getPhotos( person.id, 5, offset );
				Log.v( TAG, "Request for extra photos returned "+Integer.toString(photos.size())+" items" );
				
				int i=1;
				for( String photo : photos )
				{
					URL url=new URL( photo );
					
					Log.v( TAG, "Extra photo: "+photo );
					InputStream is = (InputStream)url.getContent( );				
					
					NamedFace newface=new NamedFace( person.id, person.name, is, "facebook", Integer.toString( offset+i ) );
					_adapter.add( position+i, newface );
					
					i++;
				}
			}
			catch( ServerErrorException e )
			{
				Log.v( TAG, "ServerErrorException: "+e.getMessage( ) );
				
	//			_handler.post( new Runnable(){public void run(){Toast.makeText(getBaseContext(), "Facebook error", Toast.LENGTH_SHORT ).show();};} );
			} 
			catch( MalformedURLException e )
			{
				Log.v( TAG, "Malformed URL: "+e.getMessage( ) );
			}
			catch( IOException e )
			{
				Log.v( TAG, "IOException: "+e.getMessage( ) );
			}	
		}
	}
}
