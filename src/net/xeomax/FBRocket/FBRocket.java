package net.xeomax.FBRocket;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Toast;
import java.io.PrintStream;

public class FBRocket
{
	protected static final String	TAG	="FBRocket";
	private String					apiKey;
	private String					appName;
	private Activity				activity;

	public static int FBROCKET_LOGIN_RESULT = 1;
	
	public FBRocket( Activity activity, String appName, String apiKey )
	{
		this.activity=activity;
		this.appName=appName;
		this.apiKey=apiKey;
	}

//	public void login( LoginListener callback, int returnWindow )
//	{
////		new LoginWindow( this, callback, returnWindow );
//	}
//
//	public void login( int returnWindow )
//	{
////		new LoginWindow( this, (LoginListener)this.activity, returnWindow );
//	}
	
	public void login( int requestCode )
	{
		Intent i=new Intent( "net.xeomax.FBRocket.Login" );
		activity.startActivityForResult( i, requestCode );
	}
	
	public void onLoginActivityReturn( int resultCode, Intent data )
	{
		
	}

	protected Activity getActivity( )
	{
		return this.activity;
	}

	public String getAPIKey( )
	{
		return this.apiKey;
	}

	public String getAppName( )
	{
		return this.appName;
	}

	public void displayDialog( String message )
	{
		AlertDialog.Builder dlgAlert=new AlertDialog.Builder( this.activity );
		dlgAlert.setMessage( message );
		dlgAlert.create( ).show( );
	}

	public void displayToast( String message )
	{
		Toast t=Toast.makeText( this.activity, message, 0 );
		t.show( );
	}

	public void loadFacebook( LoginListener callback )
	{
		SharedPreferences preferences=this.activity.getSharedPreferences(
				this.appName, 0 );
		String sessionKey=preferences.getString( "sessionKey", "" );
		String secretKey=preferences.getString( "secretKey", "" );
		String uid=preferences.getString( "uid", "" );
		Facebook facebook=new Facebook( this, sessionKey, secretKey, uid );
		try
		{
			if( facebook.sessionIsValid( ) )
				callback.onLoginSuccess( facebook );
			else
				callback.onLoginFail( );
		} catch( ServerErrorException e )
		{
			e.printStackTrace( );
			callback.onLoginFail( );
		}
	}

	public void loadFacebook( )
	{
		loadFacebook( (LoginListener)this.activity );
	}

	public boolean existsSavedFacebook( )
	{
		SharedPreferences preferences=this.activity.getSharedPreferences(
				this.appName, 0 );
		return !preferences.getString( "sessionKey", "" ).equals( "" );
	}

	public static void main( String[] args )
	{
		System.out
				.println( "FBRocket is a library designed for the Android platform and is not usable as a stand-alone product.\nSee http://www.xeomax.net/fbrocket for further information. \nThe program will now terminate." );
	}
}
