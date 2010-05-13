package net.xeomax.FBRocket;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import java.util.List;

public class Facebook
{
	private FBRocket	fbRocket;
	private String		sessionKey;
	private String		secretKey;
	private String		uid;

	protected Facebook( FBRocket fbRocket, String sessionKey, String secretKey,
			String uid )
	{
		this.fbRocket=fbRocket;
		this.sessionKey=sessionKey;
		this.secretKey=secretKey;
		this.uid=uid;
	}

	public String getAPIKey( )
	{
		return this.fbRocket.getAPIKey( );
	}

	public String getSessionKey( )
	{
		return this.sessionKey;
	}

	public String getSecretKey( )
	{
		return this.secretKey;
	}

	public String getUid( )
	{
		return this.uid;
	}

	public void save( )
	{
		SharedPreferences preferences=this.fbRocket.getActivity( )
				.getSharedPreferences( this.fbRocket.getAppName( ), 0 );
		SharedPreferences.Editor editor=preferences.edit( );
		editor.putString( "sessionKey", this.sessionKey );
		editor.putString( "secretKey", this.secretKey );
		editor.putString( "uid", this.uid );
		editor.commit( );
	}

	public boolean sessionIsValid( ) throws ServerErrorException
	{
		try
		{
			getStatus( );
			return true;
		} catch( ServerErrorException e )
		{
			if( e.notLoggedIn( ) )
				return false;
			throw e;
		}
	}

	public String createNote( String title, String content )
			throws ServerErrorException
	{
		Query query=new Query( this, "notes.create" );
		query.put( "title", title );
		query.put( "content", content );
		query.sign( );
		return QueryProcessor.getRawString( query );
	}

	public String createToken( ) throws ServerErrorException
	{
		Query query=new Query( this, "auth.createToken" );
		query.sign( );
		return QueryProcessor.getRawString( query );
	}

	public boolean editNote( String id, String title, String content )
			throws ServerErrorException
	{
		Query query=new Query( this, "notes.edit" );
		query.put( "note_id", id );
		query.put( "title", title );
		query.put( "content", content );
		query.sign( );
		return QueryProcessor.getBoolean( query );
	}

	public boolean expireSession( ) throws ServerErrorException
	{
		Query query=new Query( this, "auth.expireSession" );
		query.sign( );
		return QueryProcessor.getBoolean( query );
	}

	public boolean logout( ) throws ServerErrorException
	{
		return expireSession( );
	}

	public String fqlQuery( String fqlQuery ) throws ServerErrorException
	{
		Query query=new Query( this, "fql.query" );
		query.put( "query", fqlQuery );
		query.sign( );
		return QueryProcessor.getRawString( query );
	}

	public List<String> getFriendUIDs( ) throws ServerErrorException
	{
		Query query=new Query( this, "friends.get" );
		query.sign( );
		return QueryProcessor.getStringList( query );
	}

	public Friend getFriend( String uid ) throws ServerErrorException
	{
		Query query=new Query( this, "fql.query" );
		String fqlQuery="SELECT uid, name, pic, profile_update_time, timezone, birthday_date, status, online_presence, locale, profile_url, website, is_blocked FROM user WHERE uid="
				+ uid;
		query.put( "query", fqlQuery );
		query.sign( );
		return QueryProcessor.getFriend( query );
	}

	public List<Friend> getFriends( String[] uids ) throws ServerErrorException
	{
		String whereClause="";
		for( int i=0; i < uids.length - 1; ++i )
		{
			whereClause=whereClause + "uid = " + uids[i] + " OR ";
		}
		whereClause=whereClause + "uid = " + uids[( uids.length - 1 )];

		Query query=new Query( this, "fql.query" );
		String fqlQuery="SELECT uid, name, pic, profile_update_time, timezone, birthday_date, status, online_presence, locale, profile_url, website, is_blocked FROM user WHERE "
				+ whereClause;
		query.put( "query", fqlQuery );
		query.sign( );
		return QueryProcessor.getFriendList( query );
	}

	public String getSession( ) throws ServerErrorException
	{
		Query query=new Query( this, "auth.getSession" );
		query.put( "auth_token", createToken( ) );

		query.sign( );
		return QueryProcessor.getRawString( query );
	}

	public List<Status> getStatus( int limit ) throws ServerErrorException
	{
		Query query=new Query( this, "status.get" );
		query.put( "limit", limit );
		query.sign( );
		return QueryProcessor.getStatusList( query );
	}

	public Status getStatus( ) throws ServerErrorException
	{
		return (Status)getStatus( 1 ).get( 0 );
	}

	public List<Status> getAllStatus( ) throws ServerErrorException
	{
		Query query=new Query( this, "status.get" );
		query.sign( );
		return QueryProcessor.getStatusList( query );
	}

	public boolean setStatus( String newStatus ) throws ServerErrorException
	{
		Query query=new Query( this, "status.set" );
		query.put( "status", newStatus );
		query.sign( );
		return QueryProcessor.getBoolean( query );
	}
}
