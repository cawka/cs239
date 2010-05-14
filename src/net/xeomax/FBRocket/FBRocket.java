package net.xeomax.FBRocket;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;
import java.io.PrintStream;

public class FBRocket
{
	protected static final String	TAG	="FBRocket";
	
	private String					apiKey;
	private String					appName;
	private Context					context;
	private LoginListener 			callback;
	
	private Facebook 				facebook=null;
	
	public FBRocket( Context context, String appName, String apiKey, LoginListener callback )
	{
		this.context=context;
		this.appName=appName;
		this.apiKey=apiKey;
		this.callback=callback;
	}

	public void requestLoginActivity( Activity activity, int requestCode )
	{
		Intent i=new Intent( "net.xeomax.FBRocket.Login" );
		i.putExtra( "api_key", this.apiKey );
		
		activity.startActivityForResult( i, requestCode );
	}
	
	public void onLoginActivityReturn( int resultCode, Intent data )
	{
		if( data!=null && resultCode==LoginWindow.LOGIN_SUCCESS )
		{
			this.facebook=new Facebook( 
					this.apiKey,
					data.getExtras( ).getString("sessionKey"),
					data.getExtras( ).getString("secret"),
					data.getExtras( ).getString("uid") 
			);

			Log.v( TAG, ">"+apiKey+", "+data.getExtras( ).getString("sessionKey")+", "+data.getExtras( ).getString("secret")+", "+data.getExtras( ).getString("uid") );

			// save preferences
			SharedPreferences.Editor preferences=this.context.getSharedPreferences( this.appName, Activity.MODE_PRIVATE ).edit( );

			preferences.putString( "sessionKey", data.getExtras( ).getString("sessionKey") );
			preferences.putString( "secret", data.getExtras( ).getString("secret") );
			preferences.putString( "uid", data.getExtras( ).getString("uid") );
			preferences.commit( );
			
			this.callback.onLoginSuccess( );
		}
		else
		{
			this.facebook=null;
			this.callback.onLoginFail( );
		}
	}

	public boolean trySavedLogin( )
	{
		if( this.facebook!=null ) return true;
		
		SharedPreferences preferences=this.context.getSharedPreferences( this.appName, Activity.MODE_PRIVATE );

		String sessionKey=preferences.getString( "sessionKey", "" );
		String secret    =preferences.getString( "secret",     "" );
		String uid		 =preferences.getString( "uid", "" );

		Log.v( TAG, "<"+apiKey+", "+sessionKey+", "+secret+", "+uid );
		
		if( this.apiKey.equals("") ||
				sessionKey.equals("") ||
				secret.equals("") ||
				uid.equals("")
		)
		{
			// don't even need to try 
			return false; //nothing
		}

		this.facebook=new Facebook( this.apiKey, sessionKey, secret, uid );
		try
		{
			if( this.facebook.sessionIsValid( ) )
			{
				Log.v( TAG, "Successfully reusing existing login information" );
				this.callback.onLoginSuccess( );
				return true;
			}
		} 
		catch( ServerErrorException e )
		{
			e.printStackTrace( );
		}
		
		return false;
	}
	
	public void logout( )
	{
		if( this.facebook!=null )
		{
			try
			{
				this.facebook.logout( );
			} 
			catch( ServerErrorException e )
			{
				// ignore all errors
			}
		}
		
		// reset preferences
		SharedPreferences.Editor preferences=this.context.getSharedPreferences( this.appName, Activity.MODE_PRIVATE ).edit( );

		preferences.remove( "sessionKey" );
		preferences.remove( "secret");
		preferences.remove( "uid" );
	}
	
	public String getAPIKey( )
	{
		return this.apiKey;
	}

	public String getAppName( )
	{
		return this.appName;
	}
	
	public Facebook getFacebook( )
	{
		return this.facebook;
	}

	public void displayDialog( String message )
	{
		AlertDialog.Builder dlgAlert=new AlertDialog.Builder( this.context );
		dlgAlert.setMessage( message );
		dlgAlert.create( ).show( );
	}

	public void displayToast( String message )
	{
		Toast t=Toast.makeText( this.context, message, 0 );
		t.show( );
	}
}
