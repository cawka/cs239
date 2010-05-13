package com.cawka.FriendDetector;

import java.io.IOException;

import com.cawka.FriendDetector.detector.eigenfaces.Eigenface.NamedFace;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;

public class FBDBHandler extends SQLiteOpenHelper
{
	private static final String DB_NAME="fb";
	private static final String TABLE  ="fb";
	
	private static final String INIT_TABLE =  
		"CREATE TABLE "+TABLE+"( " +
			"id VARCHAR(255) NOT NULL, " +
			"name VARCHAR(255) NOT NULL" +
			" "+
	    ")";
	
	private static final String COLUMNS[] = { "id", "name" };
	private static final int INDEX_ID 		= 0;
	private static final int INDEX_NAME 	= 1;
	
	private static final String DROP_TABLE = 
		"DROP TABLE IF EXISTS "+TABLE;
	
	private static final String TAG="FriendDetector.FBDBHandler";
	
	/////////////////////////////////////////////////////////////////
		
	public FBDBHandler( Context context ) 
	{
		super( context, DB_NAME, null, 1 );
		getWritableDatabase( ).close( ); // to initialize database if necessary
	}

	public void onCreate( SQLiteDatabase db ) //called when database is first created
	{
		db.execSQL( INIT_TABLE );
	}

	public void onUpgrade( SQLiteDatabase db, int oldVersion, int newVersion ) 
	{
		Log.i( TAG, "Request to upgrade database. Currently all data is deleted and table are recreated" );
		
		db.execSQL( DROP_TABLE );
		db.execSQL( INIT_TABLE );
	}
	
	/////////////////////////////////////////////////////////////////
  
	public NamedFace getNamedFace( String uid ) throws IOException
	{
		String status=Environment.getExternalStorageState( );
        if( !status.equals(Environment.MEDIA_MOUNTED) ) 
        {
        	throw new IOException( "sdcard should be mounted!!!" );
        }		
		
		NamedFace ret=null;
		
		SQLiteDatabase db=getReadableDatabase( );
		Cursor cursor=db.query( TABLE, COLUMNS, "id=?", new String[]{uid}, null, null, null );
		if( cursor.moveToFirst() )
		{
			ret=new NamedFace( uid, cursor.getString(1), "facebook" );
		}
		
		cursor.close( );
		db.close( );
		return ret;
	}
	
	public void saveNamedFace( NamedFace face ) throws IOException
	{
		SQLiteDatabase db=getWritableDatabase( );
		
		ContentValues values = new ContentValues();
		values.put( "id", 	face.id );
		values.put( "name", face.name );
		
		db.insert( TABLE, null, values );
		db.close( );		
	}
}
