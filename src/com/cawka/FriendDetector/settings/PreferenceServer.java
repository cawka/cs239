package com.cawka.FriendDetector.settings;

import android.content.Context;
import android.preference.Preference;

public class PreferenceServer extends Preference 
{
	private Server.Config _config;
	
	public PreferenceServer( Context context, Server.Config config )
	{
		super( context );
		setConfig( config );
	}
	
	public Server.Config getConfig( )
	{
		return _config;
	}
	
	public void setConfig( Server.Config config )
	{
		_config=config;
		
		String extra="";
		if( !_config.enabled ) extra=" (disabled)";
		
		switch( _config.type )
		{
		case Server.LOCAL:
			setTitle( "Local"+extra );
			break;
		case Server.REMOTE:
			setTitle( "Remote"+extra );
			setSummary( "detector://"+_config.hostname+":"+_config.port );
			break;
		}
	}
}
