package net.xeomax.FBRocket;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import java.util.HashMap;
import org.json.JSONException;
import org.json.JSONObject;

import com.cawka.FriendDetector.R;

@SuppressWarnings( "unchecked" )
public class LoginWindow extends Activity
{
	private static final String TAG = "net.xeomax.FBRocket.LoginWindow";
	
	private WebView		wv;
	private String  	apiKey="";
	
	public static final int LOGIN_SUCCESS = 10;
	public static final int LOGIN_FAILURE = 11;

	protected void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		
		this.wv=new WebView( this );
		setContentView( this.wv );
	
//		this.wv.setBackgroundResource( R.drawable.background ); //hack
		
		this.wv.getSettings( ).setAllowFileAccess( true );
		this.wv.getSettings( ).setJavaScriptEnabled( true );
		this.wv.setWebViewClient( new CustomWebViewClient( ) );
	}
	
	protected void onStart( )
	{
		super.onStart( );
		
		this.apiKey=getIntent().getExtras( ).getString( "api_key" );
		if( this.apiKey.equals("") )
		{
			Log.v( TAG, "API_KEY has not been specified" );
			Toast.makeText( this, "API_KEY has not been specified", Toast.LENGTH_LONG ).show( );
			
			setResult( LOGIN_FAILURE );
			finish( );
		}
			
		this.wv
				.loadUrl( "http://www.facebook.com/login.php?api_key="
						+ apiKey
						+ "&connect_display=popup&" +
								"v=1.0&" +
								"next=http://www.facebook.com/connect/login_success.html&" +
								"cancel_url=http://www.facebook.com/connect/login_failure.html&" +
								"fbconnect=true&" +
								"return_session=true&" +
								"req_perms=read_stream,publish_stream,offline_access" );
	}

	public static HashMap<String, String> parseQueryString( String url )
	{
		HashMap values=new HashMap( );
		if( url.contains( "?" ) )
		{
			String queryString=url.split( "\\?" )[1];
			String[] kvPairs=queryString.split( "&" );
			for( String kvPair : kvPairs )
			{
				String[] kvSplit=kvPair.split( "=" );
				if( kvSplit.length>1 ) values.put( kvSplit[0], kvSplit[1] );
			}
		}
		return values;
	}

	public static String getURLOnly( String url )
	{
		return url.split( "\\?" )[0];
	}

	private class CustomWebViewClient extends WebViewClient
	{
		public void onPageFinished( WebView view, String url )
		{
			if( LoginWindow.getURLOnly( url ).equals(
					"http://www.facebook.com/connect/login_failure.html" ) )
			{
				setResult( LOGIN_FAILURE );
				finish( );			
			}
			else if( LoginWindow.getURLOnly( url ).equals(
					"http://www.facebook.com/connect/login_success.html" ) )
			{
				Log.v( TAG, "Real login success" );
				
				HashMap values=LoginWindow.parseQueryString( url );
				String sessionJSONString=Uri.decode( (String)values
						.get( "session" ) );
				try
				{
					JSONObject sessionJSON=new JSONObject( sessionJSONString );
					String sessionKey=sessionJSON.get( "session_key" )
							.toString( );
					String secret=sessionJSON.get( "secret" ).toString( );
					String uid=sessionJSON.get( "uid" ).toString( );
					
					Intent data=new Intent();
					data.putExtra( "sessionKey", sessionKey );
					data.putExtra( "secret",     secret );
					data.putExtra( "uid",        uid );
					
					setResult( LOGIN_SUCCESS, data );
					finish( );
					return;
				} 
				catch( JSONException e )
				{
					Log.e( TAG, Log.getStackTraceString(e) );
					
					setResult( LOGIN_FAILURE );
					finish( );			
				}
			}
		}
	}
}
