package com.cawka.FriendDetector.settings;

import java.util.LinkedList;
import java.util.List;

import com.cawka.FriendDetector.Main;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

public class DBHandle extends SQLiteOpenHelper
{
	private static final String DB_NAME="detectors";
	private static final String TABLE  ="detectors";
	
	private static final String INIT_TABLE =  
		"CREATE TABLE "+TABLE+"( " +
			"id INTEGER PRIMARY KEY AUTOINCREMENT, " +
			"type INTEGER NOT NULL, "+
			"hostname VARCHAR(255) NOT NULL, " +
	        "port INT NOT NULL, " +
	        "enabled BOOLEAN NOT NULL," +
	        "enabled_recognizer BOOLEAN NOT NULL," +
	        "timeout INTEGER NOT NULL" +
	    ")";
	
	private static final String	COLUMNS[]					= { 
			"id",
			Server.KEY_TYPE, Server.KEY_HOSTNAME, Server.KEY_PORT,
			Server.KEY_ENABLED, Server.KEY_ENABLED_RECOGNIZER,
			Server.KEY_TIMEOUT								};
	
	private static final int INDEX_ID 		= 0;
	private static final int INDEX_TYPE 	= 1;
	private static final int INDEX_SERVER 	= 2;
	private static final int INDEX_PORT 	= 3;
	private static final int INDEX_ENABLED 	= 4;
	private static final int INDEX_ENABLED_RECOGNIZER 	= 5;
	private static final int INDEX_TIMEOUT 	= 6;
	
	private static final String DROP_TABLE = 
		"DROP TABLE IF EXISTS "+TABLE;
	
	private static final String TAG="FriendDetector.DBHandle";
	
	/////////////////////////////////////////////////////////////////
		
	public DBHandle( Context context ) 
	{
		super( context, DB_NAME, null, 5 );
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
    
    public List<Server.Config> getAllConfigs( )
    {
    	SQLiteDatabase db=getReadableDatabase( );
    	Cursor cursor=db.query( TABLE, COLUMNS, null, null, null, null, "id" );
    	
    	LinkedList<Server.Config> ret=new LinkedList<Server.Config>( );
    	
    	cursor.moveToFirst( );
    	while( !cursor.isAfterLast() )
    	{
    		ret.add( DBHandle.createConfig(cursor) );
    		cursor.moveToNext( );
    	}
    	cursor.close( );
    	db.close( );
    	
    	return ret;
    }
    
    private static Server.Config createConfig( Cursor row )
    {
    	return new Server.Config( 
    		row.getInt(INDEX_ID),
    		row.getInt(INDEX_TYPE),
    		row.getString(INDEX_SERVER),
    		row.getInt(INDEX_PORT),
    		row.getInt(INDEX_ENABLED)==1,
    		row.getInt(INDEX_ENABLED_RECOGNIZER)==1,
    		row.getInt(INDEX_TIMEOUT)
    	);
    }
	
    public Server.Config getConfig( long server_id )
    {
    	SQLiteDatabase db=getReadableDatabase( );
    	Cursor cursor=db.query( TABLE, COLUMNS, 
    			"id=?", new String[]{ new Long(server_id).toString() }, 
    			null, null, null );

    	Server.Config ret;
    	
    	cursor.moveToFirst( );
    	if( !cursor.isAfterLast() )
    		ret=DBHandle.createConfig( cursor );
    	else
    		return new Server.Config( ); //use default settings

    	cursor.close( );
		db.close( );
		return ret;
    }

    public Server.Config add( Server.Config config )
    {
    	SQLiteDatabase db=getWritableDatabase( );
    	
		ContentValues values = new ContentValues();
        values.put(Server.KEY_TYPE, 	config.type );
        values.put(Server.KEY_HOSTNAME, config.hostname );
        values.put(Server.KEY_PORT, 	config.port );
        values.put(Server.KEY_ENABLED, 	config.enabled );
        values.put(Server.KEY_ENABLED_RECOGNIZER, 	config.enabled_recognizer );
        values.put(Server.KEY_TIMEOUT, 	config.timeout );
        
        config.id=db.insert( TABLE, null, values );
        db.close( );
        return config;
    }

    public void delete( long server_id ) 
    {
    	SQLiteDatabase db=getWritableDatabase( );
    	
    	db.delete( TABLE, "id=?", new String[]{ new Long(server_id).toString() } );
    	db.close( );
	}
    
	public void update( Server.Config config )
	{
		SQLiteDatabase db=getWritableDatabase( );
		
		ContentValues values = new ContentValues();
        values.put(Server.KEY_TYPE, 	config.type );
        values.put(Server.KEY_HOSTNAME, config.hostname );
        values.put(Server.KEY_PORT, 	config.port );
        values.put(Server.KEY_ENABLED, 	config.enabled );
        values.put(Server.KEY_ENABLED_RECOGNIZER, 	config.enabled_recognizer );
        values.put(Server.KEY_TIMEOUT, 	config.timeout );

        db.update( TABLE, values, "id=?", new String[]{ new Long(config.id).toString() } );
        db.close( );
	}
}
