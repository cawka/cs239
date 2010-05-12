package com.cawka.FriendDetector;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import com.cawka.FriendDetector.Gallery.ImageAdapter;
import com.cawka.FriendDetector.detector.eigenfaces.Eigenface.NamedFace;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.CompressFormat;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Contacts;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ImageView.ScaleType;

public class ContactsImageGallery extends Activity
{
	private GridView _grid;
	List<NamedFace> _faces=new LinkedList<NamedFace>();
	
	protected void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		
		setContentView( R.layout.gallery );
		
		_grid=(GridView)findViewById( R.id.grid );
		
		loadContacts( );
		
		_grid.setAdapter( new ImageAdapter() );
		
		registerForContextMenu( _grid );
	}
	
	protected void onDestroy( )
	{
		Log.v( "Test", "onDestroy" );
		for( NamedFace face : _faces )
		{
			face.bitmap.recycle( );
		}
		_faces.clear( );
		
		super.onDestroy();
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
	
	private void loadContacts( )
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
							Log.v( "test", "photo_id:"+photo_id+", blob size:"+Integer.toString(cursor2.getBlob(0).length) );
							
							Bitmap bmp=BitmapFactory.decodeByteArray( temp, 0, temp.length );
							ImageView view=new ImageView( getBaseContext() );
							view.setBackgroundResource( R.drawable.block_background );
							view.setImageBitmap( bmp );
							
							_faces.add( new NamedFace(contactId,bmp,name) );
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
	
	public class ImageAdapter extends BaseAdapter 
	{
		public ImageAdapter( )
		{
		}
		
	    public int getCount( ) 
	    {
	        return _faces.size( );
	    }

	    public View getView( int position, View convertView, ViewGroup parent ) 
	    {
			LinearLayout v=(LinearLayout)convertView;

			if( convertView == null ) // if it's not recycled, initialize some attributes
			{ 
				LayoutInflater vi=(LayoutInflater)getBaseContext().getSystemService( Context.LAYOUT_INFLATER_SERVICE );
				v=(LinearLayout)vi.inflate( R.layout.face, null );
			}
			
			NamedFace person=_faces.get( position );

			ImageView face=(ImageView)v.findViewById( R.id.row_image );
			face.setImageBitmap( person.bitmap );
			face.setScaleType( ScaleType.CENTER_CROP );

			TextView name=(TextView)v.findViewById( R.id.row_text );
			name.setText( person.name );
			
			return v;
	    }

		public Object getItem( int position )
		{
			return _faces.get( position );
		}

		public long getItemId( int position )
		{
			return position;
		}
	}	
}
