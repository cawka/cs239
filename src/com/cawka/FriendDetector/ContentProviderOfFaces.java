package com.cawka.FriendDetector;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;


public class ContentProviderOfFaces extends ContentProvider
{
	private static final int    FACES_CODE = 1;
	private static final String PATH       = "faces";

	public static final String AUTHORITY = "com.cawka.FriendDetector";
    public static final Uri BASE_URI = Uri.parse("content://" + AUTHORITY);
    public static final Uri FACES_URI = Uri.withAppendedPath( BASE_URI, "faces" );
    public static final Uri NAMES_URI = Uri.withAppendedPath( BASE_URI, "names" );	
	
    
    private static final String TAG = "ContentProviderOfFaces";
    
	private static final UriMatcher _uriMatcher;

	//////////////////////////////////////////////////////////////////////
	
	public int delete( Uri uri, String selection, String[] selectionArgs )
	{
		return 0;
	}

	public String getType( Uri uri )
	{
		switch( _uriMatcher.match(uri) )
		{
		case FACES_CODE:
			return "image/jpeg";
		default:
			throw new IllegalArgumentException( "Unknown URI "+uri );
		}
	}

	public Uri insert( Uri uri, ContentValues values )
	{
		throw new UnsupportedOperationException( "Not supported by this provider" );
	}

	public boolean onCreate( )
	{
		return true;
	}

	public Cursor query( Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder )
	{
		return null;
	}

	public int update( Uri uri, ContentValues values, String selection,
			String[] selectionArgs )
	{
		throw new UnsupportedOperationException( "Not supported by this provider" );
	}
	
	
	static 
	{
		_uriMatcher=new UriMatcher( UriMatcher.NO_MATCH );
		_uriMatcher.addURI( AUTHORITY, PATH, FACES_CODE );
	}

}
