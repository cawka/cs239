package net.xeomax.FBRocket;

import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

@SuppressWarnings( "unchecked" )
class QueryProcessor
{
	public static boolean getBoolean( Query query ) throws ServerErrorException
	{
		String serverResponse=HTTPManager.getResponse( query );
		errorCheck( serverResponse );
		return Boolean.parseBoolean( serverResponse.replaceAll( "\"", "" ) );
	}

	public static String getRawString( Query query )
			throws ServerErrorException
	{
		String serverResponse=HTTPManager.getResponse( query );
		errorCheck( serverResponse );
		if( getJSONType( serverResponse ) == JSONType.STRING )
		{
			return serverResponse.replaceAll( "\"", "" );
		}
		return serverResponse;
	}

	public static List<Status> getStatusList( Query query )
			throws ServerErrorException
	{
		String serverResponse=HTTPManager.getResponse( query );
		errorCheck( serverResponse );
		List statusList=new ArrayList( );
		try
		{
			ServerArray jsonArray=new ServerArray( serverResponse );
			for( int i=0; i < jsonArray.length( ); ++i )
				statusList.add( jsonArray.getStatus( i ) );
		} catch( JSONException e )
		{
			e.printStackTrace( );
		}
		return statusList;
	}

	public static List<String> getStringList( Query query )
			throws ServerErrorException
	{
		String serverResponse=HTTPManager.getResponse( query );
		errorCheck( serverResponse );
		List stringList=new ArrayList( );
		try
		{
			JSONArray jsonArray=new JSONArray( serverResponse );
			for( int i=0; i < jsonArray.length( ); ++i )
				stringList.add( jsonArray.get( i ).toString( ).replaceAll(
						"\"", "" ) );
		} catch( JSONException e )
		{
			e.printStackTrace( );
		}
		return stringList;
	}
	
	public static List<String> getSrcList( Query query, String field ) throws ServerErrorException
	{
		String serverResponse=HTTPManager.getResponse( query );
		errorCheck( serverResponse );
		List stringList=new ArrayList( );
		try
		{
			JSONArray jsonArray=new JSONArray( serverResponse );
			for( int i=0; i < jsonArray.length( ); ++i )
				stringList.add( jsonArray.getJSONObject( i ).get( field ).toString( ).replaceAll( "\"", "" ) );
		} 
		catch( JSONException e )
		{
			e.printStackTrace( );
		}
		return stringList;
	}

	public static Friend getFriend( Query query ) throws ServerErrorException
	{
		String serverResponse=HTTPManager.getResponse( query );
		errorCheck( serverResponse );
		try
		{
			ServerArray serverArray=new ServerArray( serverResponse );
			return serverArray.getFriend( 0 );
		} catch( JSONException e )
		{
			e.printStackTrace( );
		}
		return null;
	}

	public static List<Friend> getFriendList( Query query )
			throws ServerErrorException
	{
		String serverResponse=HTTPManager.getResponse( query );
		errorCheck( serverResponse );
		List friendList=new ArrayList( );
		try
		{
			ServerArray serverArray=new ServerArray( serverResponse );
			for( int i=0; i < serverArray.length( ); ++i )
				friendList.add( serverArray.getFriend( i ) );
		} catch( JSONException e )
		{
			e.printStackTrace( );
		}
		return friendList;
	}

	public static void errorCheck( String serverResponse )
			throws ServerErrorException
	{
		if( getJSONType( serverResponse ) != JSONType.OBJECT )
			return;
		try
		{
			JSONObject json=new JSONObject( serverResponse );
			if( !json.has( "error_code" ) )
				return;
			throw new ServerErrorException(
					json.get( "error_code" ).toString( ), json
							.get( "error_msg" ).toString( ) );
		} catch( JSONException e )
		{
			e.printStackTrace( );
		}
	}

	public static JSONType getJSONType( String string )
	{
		if( string.startsWith( "[" ) )
			return JSONType.ARRAY;
		if( string.startsWith( "\"" ) )
			return JSONType.STRING;
		if( string.startsWith( "{" ) )
			return JSONType.OBJECT;
		return JSONType.UNKNOWN;
	}

	public static enum JSONType
	{
		OBJECT, ARRAY, STRING, UNKNOWN;
	}
}
