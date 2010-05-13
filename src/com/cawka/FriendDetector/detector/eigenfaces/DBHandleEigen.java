package com.cawka.FriendDetector.detector.eigenfaces;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import com.cawka.FriendDetector.Main;
import com.cawka.FriendDetector.detector.eigenfaces.Eigenface.NamedFace;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

public class DBHandleEigen extends SQLiteOpenHelper
{
	private static final String DB_NAME="eigen";
	private static final String TABLE  ="eigen";
	
	private static final String INIT_TABLE =  
		"CREATE TABLE "+TABLE+"( " +
			"id INTEGER PRIMARY KEY AUTOINCREMENT, " +
			"name VARCHAR(255) NOT NULL" +
			" "+
	    ")";
	
	private static final String COLUMNS[] = { "id", "name" };
	private static final int INDEX_ID 		= 0;
	private static final int INDEX_NAME 	= 1;
	
	private static final String DROP_TABLE = 
		"DROP TABLE IF EXISTS "+TABLE;
	
	private static final String TAG="FriendDetector.DBHandleEigen";
	
	/////////////////////////////////////////////////////////////////
		
	public DBHandleEigen( Context context ) 
	{
		super( context, DB_NAME, null, 1 );
		getWritableDatabase( ).close( ); // to initialize database if necessary
		
//		SQLiteDatabase db=getWritableDatabase( );
//		db.delete( TABLE, null, null );
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
    
//	public int getTrainSetSize( )
//	{
//		SQLiteDatabase db=getReadableDatabase( );
//		
//		Cursor cursor=db.query( TABLE, new String[]{"count(*)"}, null, null, null, null, null );
//		int ret=0;
//		if( cursor.moveToFirst() ) ret=cursor.getInt( 0 );
//		
//		cursor.close( );
//		db.close( );
//		
//		return ret;
//	}
		
//	public void requestFaces( )
//	{
//		_temp_db=getReadableDatabase( );
//		_temp_cursor=_temp_db.query( TABLE, COLUMNS, null, null, null, null, null );
//		_temp_cursor.moveToFirst( );
//	}
//	
//	public NamedFace getNextFace( )
//	{
//		if( _temp_db==null || _temp_cursor==null ) return null;
//		String status=Environment.getExternalStorageState( );
//        if( !status.equals(Environment.MEDIA_MOUNTED) ) return null;
//		
//		if( !_temp_cursor.isAfterLast( ) )
//		{
//			File filepath=new File( Environment.getExternalStorageDirectory()+"/friendDetector" );
//			File filename=new File( filepath, Long.toString(_temp_cursor.getLong(0))+".png" );
//
//			NamedFace ret=new NamedFace( BitmapFactory.decodeFile(filename.getAbsolutePath() ), _temp_cursor.getString(1) );
//			_temp_cursor.moveToNext( );
//			return ret;
//		}
//		else
//		{
//			_temp_cursor.close( );
//			_temp_db.close( );
//			return null;
//		}
//	}
	
	public List<NamedFace> getAllFaces( )
	{
		SQLiteDatabase _temp_db;
		Cursor		   _temp_cursor;

		_temp_db=getReadableDatabase( );
		_temp_cursor=_temp_db.query( TABLE, COLUMNS, null, null, null, null, "name,id" );
		_temp_cursor.moveToFirst( );
		
		List<NamedFace> ret=new LinkedList<NamedFace>( );
		
		String status=Environment.getExternalStorageState( );
		if( !status.equals(Environment.MEDIA_MOUNTED) ) return ret;
		
		while( !_temp_cursor.isAfterLast( ) )
		{
			File filepath=new File( Environment.getExternalStorageDirectory()+"/friendDetector" );
			File filename=new File( filepath, Long.toString(_temp_cursor.getLong(0))+".png" );
			if( !filename.exists() )
				filename=new File( filepath, Long.toString(_temp_cursor.getLong(0))+".jpeg" );

			ret.add( new NamedFace( 
					Long.toString(_temp_cursor.getLong(0)), 
					BitmapFactory.decodeFile(filename.getAbsolutePath() ), 
					_temp_cursor.getString(1) ) );
			_temp_cursor.moveToNext( );
		}
		
		_temp_cursor.close( );
		_temp_db.close( );
		
		return ret;		
	}

	public List<NamedFace> getFacesByName( String name )
	{
		SQLiteDatabase _temp_db;
		Cursor		   _temp_cursor;

		_temp_db=getReadableDatabase( );
		_temp_cursor=_temp_db.query( TABLE, COLUMNS, "name=?", new String[]{name}, null, null, null );
		_temp_cursor.moveToFirst( );
		
		List<NamedFace> ret=new LinkedList<NamedFace>( );
		
		while( !_temp_cursor.isAfterLast( ) )
		{
			File filepath=new File( Environment.getExternalStorageDirectory()+"/friendDetector" );
			File filename=new File( filepath, Long.toString(_temp_cursor.getLong(0))+".png" );
			if( !filename.exists() )
				filename=new File( filepath, Long.toString(_temp_cursor.getLong(0))+".jpeg" );

			ret.add( new NamedFace( 
					Long.toString(_temp_cursor.getLong(0)), 
					BitmapFactory.decodeFile(filename.getAbsolutePath()), 
					_temp_cursor.getString(1) ) );
			_temp_cursor.moveToNext( );
			
			_temp_cursor.moveToNext( );
		}
		
		_temp_cursor.close( );
		_temp_db.close( );
		
		return ret;		
	}
	
	public List<String> getNames( )
	{
		SQLiteDatabase _temp_db;
		Cursor		   _temp_cursor;

		_temp_db=getReadableDatabase( );
		_temp_cursor=_temp_db.query( TABLE, new String[]{"name"}, null, null, "name", null, null );
		_temp_cursor.moveToFirst( );
		
		List<String> ret=new LinkedList<String>( );
		
		while( !_temp_cursor.isAfterLast( ) )
		{
			ret.add( _temp_cursor.getString(0) );
			_temp_cursor.moveToNext( );
		}
		
		_temp_cursor.close( );
		_temp_db.close( );
		
		return ret;
	}
	
	
//    public List<Server.Config> getAllConfigs( )
//    {
//    	SQLiteDatabase db=getReadableDatabase( );
//    	Cursor cursor=db.query( TABLE, COLUMNS, null, null, null, null, "id" );
//    	
//    	LinkedList<Server.Config> ret=new LinkedList<Server.Config>( );
//    	
//    	cursor.moveToFirst( );
//    	while( !cursor.isAfterLast() )
//    	{
//    		ret.add( DBHandle.createConfig(cursor) );
//    		cursor.moveToNext( );
//    	}
//    	cursor.close( );
//    	db.close( );
//    	
//    	return ret;
//    }
//    
//    private static Server.Config createConfig( Cursor row )
//    {
//    	return new Server.Config( 
//    		row.getInt(INDEX_ID),
//    		row.getInt(INDEX_TYPE),
//    		row.getString(INDEX_SERVER),
//    		row.getInt(INDEX_PORT),
//    		row.getInt(INDEX_ENABLED)==1,
//    		row.getInt(INDEX_TIMEOUT)
//    	);
//    }
//	
//    public Server.Config getConfig( long server_id )
//    {
//    	SQLiteDatabase db=getReadableDatabase( );
//    	Cursor cursor=db.query( TABLE, COLUMNS, 
//    			"id=?", new String[]{ new Long(server_id).toString() }, 
//    			null, null, null );
//
//    	Server.Config ret;
//    	
//    	cursor.moveToFirst( );
//    	if( !cursor.isAfterLast() )
//    		ret=DBHandle.createConfig( cursor );
//    	else
//    		return new Server.Config( ); //use default settings
//
//    	cursor.close( );
//		db.close( );
//		return ret;
//    }

    public String add( String name )
    {
    	SQLiteDatabase db=getWritableDatabase( );
    	
		ContentValues values = new ContentValues();
        values.put( "name", name );
        
        long id=db.insert( TABLE, null, values );
        
        db.close( );
        return Long.toString( id );
    }

    public void deleteAll( ) 
    {
    	Log.v( TAG, "Purging local face database. Files are not deleted" );
    	SQLiteDatabase db=getWritableDatabase( );
    	
    	db.delete( TABLE, null, null );
    	db.close( );
	}
    
    public void delete( long id ) 
    {
    	Log.v( TAG, "Removing item "+Long.toString(id) );
    	SQLiteDatabase db=getWritableDatabase( );
    	
    	db.delete( TABLE, "id=?", new String[]{Long.toString(id)} );
    	db.close( );

		File filepath=new File( Environment.getExternalStorageDirectory()+"/friendDetector" );
		File filename=new File( filepath, Long.toString(id)+".png" );
		if( !filename.exists() )
			filename=new File( filepath, Long.toString(id)+".jpeg" );
		
		if( filename.exists() )
			filename.delete( );
    } 
    
//	public void update( Server.Config config )
//	{
//		SQLiteDatabase db=getWritableDatabase( );
//		
//		ContentValues values = new ContentValues();
//        values.put(Server.KEY_TYPE, 	config.type );
//        values.put(Server.KEY_HOSTNAME, config.hostname );
//        values.put(Server.KEY_PORT, 	config.port );
//        values.put(Server.KEY_ENABLED, 	config.enabled );
//        values.put(Server.KEY_TIMEOUT, 	config.timeout );
//
//        db.update( TABLE, values, "id=?", new String[]{ new Long(config.id).toString() } );
//        db.close( );
//	}
}
