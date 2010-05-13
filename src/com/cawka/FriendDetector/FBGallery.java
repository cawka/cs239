package com.cawka.FriendDetector;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import net.xeomax.FBRocket.FBRocket;
import net.xeomax.FBRocket.Facebook;
import net.xeomax.FBRocket.Friend;
import net.xeomax.FBRocket.LoginListener;
import net.xeomax.FBRocket.ServerErrorException;

import com.cawka.FriendDetector.detector.eigenfaces.Eigenface.NamedFace;
import com.cawka.FriendDetector.gui.ImageAdapter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.ContextMenu;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.GridView;
import android.widget.Toast;
import android.widget.AdapterView.AdapterContextMenuInfo;


public class FBGallery extends Activity implements LoginListener
{
	private GridView _grid;
	private ImageAdapter _adapter;
	
	private static int REQUEST_LOGIN = 1;
	
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
		
		_grid=(GridView)findViewById( R.id.grid );
		
        _adapter=new ImageAdapter( getBaseContext() );
    	_grid.setAdapter( _adapter );
    	
    	registerForContextMenu( _grid );
		
		_dbHandler=new FBDBHandler( this );
		
		////////////////////////////////////////////////////////////////////////
		
    	_fb = new FBRocket( getBaseContext(), "FriendDetection", API_KEY, this );
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
		if( _adapter!=null ) _adapter.clear( );
		
		super.onDestroy();
	}
	
	public void onCreateContextMenu( ContextMenu menu, View v, ContextMenuInfo menuInfo )
	{
		onCreateContextMenu( ((AdapterContextMenuInfo)menuInfo).position );
	}
	
	private void onCreateContextMenu( final int position )
	{
		NamedFace person=(NamedFace)_grid.getAdapter().getItem( position );

		Intent i=new Intent( );
		i.putExtra( "file", person.filename );
		i.putExtra( "name", person.name );
		setResult( RESULT_OK, i );
		
		finish( );	
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
							_adapter.add( face );
							continue;
						}
						
						Friend friend=_fb.getFacebook( ).getFriend( uid );
						friend.pic=friend.pic.replace( "_s.jpg", "_n.jpg" );
						
//						List<String> photos=_fb.getFacebook( ).getPhotos( uid, 1 );
//						if( photos.size()==0 ) continue; 
//						
//						friend.pic=photos.get( 0 ).replace( "{src:", "" ).replace( "}", "" );
						
						URL url=new URL( friend.pic );
						
						Log.v( TAG, friend.pic );
						InputStream is = (InputStream)url.getContent( );
						
						face=new NamedFace( uid, friend.name, is, "facebook" );
						_adapter.add( face );
						_dbHandler.saveNamedFace( face );
					}
					catch( ServerErrorException e )
					{
						Log.v( TAG, "ServerErrorException: "+e.getMessage( ) );
						
						_handler.post( new Runnable(){public void run(){Toast.makeText(getBaseContext(), "Facebook error", Toast.LENGTH_SHORT ).show();};} );
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
}
