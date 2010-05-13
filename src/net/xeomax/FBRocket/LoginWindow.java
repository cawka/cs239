package net.xeomax.FBRocket;

import android.app.Activity;
import android.net.Uri;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import java.util.HashMap;
import org.json.JSONException;
import org.json.JSONObject;

class LoginWindow
{
	private FBRocket		fbRocket;
	private LoginListener	callback;
	private int				returnWindow;
	private WebView			wv;

	public LoginWindow( FBRocket fbRocket, LoginListener callback,
			int returnWindow )
	{
		this.fbRocket=fbRocket;
		this.callback=callback;
		this.returnWindow=returnWindow;

		this.wv=new WebView( fbRocket.getActivity( ) );
		fbRocket.getActivity( ).setContentView( this.wv );
		this.wv.getSettings( ).setAllowFileAccess( true );
		this.wv.getSettings( ).setJavaScriptEnabled( true );
		this.wv.setWebViewClient( new CustomWebViewClient( ) );

		this.wv
				.loadUrl( "http://www.facebook.com/login.php?api_key="
						+ fbRocket.getAPIKey( )
						+ "&connect_display=popup&v=1.0&next=http://www.facebook.com/connect/login_success.html&cancel_url=http://www.facebook.com/connect/login_failure.html&fbconnect=true&return_session=true&req_perms=read_stream,publish_stream,offline_access" );
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
				values.put( kvSplit[0], kvSplit[1] );
			}
		}
		return values;
	}

	public void deregisterWV( )
	{
		this.wv.setWebViewClient( new WebViewClient( ) );
	}

	public static String getURLOnly( String url )
	{
		return url.split( "\\?" )[0];
	}

	private class CustomWebViewClient extends WebViewClient
	{
		private CustomWebViewClient( )
		{
		}

		public void onPageFinished( WebView view, String url )
		{
			if( LoginWindow.getURLOnly( url ).equals(
					"http://www.facebook.com/connect/login_failure.html" ) )
			{
				LoginWindow.this.callback.onLoginFail( );
				LoginWindow.this.fbRocket.getActivity( ).setContentView(
						LoginWindow.this.returnWindow );
			} else if( LoginWindow.getURLOnly( url ).equals(
					"http://www.facebook.com/connect/login_success.html" ) )
			{
				LoginWindow.this.deregisterWV( );
				HashMap values=LoginWindow.parseQueryString( url );
				String sessionJSONString=Uri.decode( (String)values
						.get( "session" ) );
				try
				{
					JSONObject sessionJSON=new JSONObject( sessionJSONString );
					String sessionKey=sessionJSON.get( "session_key" )
							.toString( );
					String secretKey=sessionJSON.get( "secret" ).toString( );
					String uid=sessionJSON.get( "uid" ).toString( );
					Facebook facebook=new Facebook( LoginWindow.this.fbRocket,
							sessionKey, secretKey, uid );
					LoginWindow.this.callback.onLoginSuccess( facebook );
					LoginWindow.this.fbRocket.getActivity( ).setContentView(
							LoginWindow.this.returnWindow );
				} catch( JSONException e )
				{
					e.printStackTrace( );
				}
			}
		}
	}
}