package com.cawka.FriendDetector;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import com.cawka.FriendDetector.detector.eigenfaces.Eigenface.NamedFace;
import com.cawka.FriendDetector.gui.ImageAdapter;

import android.app.Activity;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.CompressFormat;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Contacts;
import android.util.Log;
import android.view.ContextMenu;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.AdapterView.AdapterContextMenuInfo;

public class ContactsImageGallery extends Activity
{
	private GridView _grid;
	private ImageAdapter _adapter;
	
	protected void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		setContentView( R.layout.gallery );
		
		SavedState state=(SavedState)getLastNonConfigurationInstance( );
		
		_grid=(GridView)findViewById( R.id.grid );
		
		if( state==null )
			_adapter=new ImageAdapter( getBaseContext() );
		else
			_adapter=state.adapter;
		
    	_grid.setAdapter( _adapter );
	
		registerForContextMenu( _grid );
		
		// asynchronously load all contacts
		if( state==null )
			new Thread( new ContactsLoader() ).start( );
	}
	
	protected void onDestroy( )
	{
//		_adapter.clear( );
		
		super.onDestroy();
	}
	
	public Object onRetainNonConfigurationInstance( ) 
	{
		return new SavedState( );
	}	
	
	public void onCreateContextMenu( ContextMenu menu, View v, ContextMenuInfo menuInfo )
	{
		onCreateContextMenu( ((AdapterContextMenuInfo)menuInfo).position );
	}
	
	private void onCreateContextMenu( final int position )
	{
		try
		{
			NamedFace person=(NamedFace)_grid.getAdapter().getItem( position );

			File temp=new File( "/sdcard/contact_picture.jpeg" );
			temp.delete( );
			temp.createNewFile( );
			
			FileOutputStream out=new FileOutputStream( temp );
			person.bitmap.compress( CompressFormat.JPEG, 80, out );
			out.close( );			
			
			Intent i=new Intent( );
			i.putExtra( "file", temp.getAbsolutePath( ) );
			i.putExtra( "name", person.name );
			setResult( RESULT_OK, i );
			
			finish( );
		}
		catch( IOException e )
		{
			//ignore
		}
	}	
	
	private class SavedState //implements Serializable
	{
		//private static final long	serialVersionUID	=658779515116443999L;

		public ImageAdapter adapter;

		public SavedState( ) //Bitmap face, ListAdapter adapter )
		{
			adapter=_adapter;
		}
	}	
	
	private class ContactsLoader implements Runnable
	{
		public void run( )
		{
			// Load the display name for the specified person
			Cursor cursor=getContentResolver().query( Contacts.CONTENT_URI, new String[] {
					Contacts._ID, Contacts.DISPLAY_NAME, Contacts.PHOTO_ID }, null, null, Contacts.DISPLAY_NAME );
			try
			{
				cursor.moveToFirst( );
				while( !cursor.isAfterLast() )
				{
					Long contactId=cursor.getLong( 0 );
					String name=cursor.getString( 1 );
					
					String photo_id=cursor.getString( 2 );
					if( photo_id!=null )
					{
						Cursor cursor2=getContentResolver().query( 
								ContactsContract.Data.CONTENT_URI, 
								new String[] { ContactsContract.CommonDataKinds.Photo.PHOTO },
								ContactsContract.CommonDataKinds.Photo.PHOTO_ID+"=?",
								new String[] { photo_id },
								null
								);
						if( cursor2!=null && cursor2.moveToFirst( ) )
						{
							byte[] temp=cursor2.getBlob( 0 );
							if( temp!=null )
							{
								Bitmap bmp=BitmapFactory.decodeByteArray( temp, 0, temp.length );
								ImageView view=new ImageView( getBaseContext() );
								view.setBackgroundResource( R.drawable.block_background );
								view.setImageBitmap( bmp );
								
								_adapter.add( new NamedFace(contactId.toString( ),bmp,name) );
							}
						}
						if( cursor2!=null ) cursor2.close( );
					}
					cursor.moveToNext( );
				}
			} 
			finally
			{
				cursor.close( );
			}
		}
	}
}
