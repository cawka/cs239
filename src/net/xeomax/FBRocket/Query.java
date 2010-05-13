package net.xeomax.FBRocket;

import android.net.Uri;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.TreeMap;

class Query extends TreeMap<String, String>
{
	private static final long	serialVersionUID	=1L;
	private Facebook			facebook;

	public Query( Facebook facebook, String method )
	{
		this.facebook=facebook;
		put( "format", "JSON" );
		put( "method", method );
		put( "api_key", facebook.getAPIKey( ) );
		put( "call_id", String.valueOf( System.currentTimeMillis( ) ) );
		put( "session_key", facebook.getSessionKey( ) );
		put( "v", "1.0" );
	}

	public String toString( )
	{
		String data="";
		for( String key : keySet( ) )
		{
			data=data + key + "=" + Uri.encode( (String)get( key ) ) + "&";
		}
		return data;
	}

	public void sign( )
	{
		put( "sig", generateSignature( this.facebook.getSecretKey( ) ) );
	}

	public String generateSignature( String secretKey )
	{
		String queryString="";
		for( String key : keySet( ) )
		{
			queryString=queryString + key + "=" + (String)get( key );
		}
		return md5( queryString + secretKey );
	}

	private static String md5( String string )
	{
		try
		{
			MessageDigest m=MessageDigest.getInstance( "MD5" );
			m.update( string.getBytes( ), 0, string.length( ) );
			return new BigInteger( 1, m.digest( ) ).toString( 16 );
		} catch( NoSuchAlgorithmException e )
		{
			e.printStackTrace( );
		}
		return "";
	}
}
