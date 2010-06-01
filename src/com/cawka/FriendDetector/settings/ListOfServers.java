
package com.cawka.FriendDetector.settings;

import com.cawka.FriendDetector.R;
import com.cawka.FriendDetector.detector.eigenfaces.DBHandleEigen;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;
import android.preference.Preference.OnPreferenceChangeListener;
import android.text.method.DigitsKeyListener;
import android.widget.Toast;

public class ListOfServers extends PreferenceActivity implements OnPreferenceChangeListener
{
	private static final String KEY_ADD  = "add_server";
	private static final String KEY_LIST = "list_of_servers";
	private static final String KEY_RESET = "reset_eignefaces";
	
	private static final int    UPDATE_SERVER_INFO = 1;

	//////////////////////////////////////////////////////////////////
	
	private DBHandle	_dbHandler;
	
	private Preference	_add_server;
	private PreferenceCategory	_list_of_servers;
	private EditTextPreference _max_size;
//	private Preference  _reset_eignefaces;
	

    public void onCreate( Bundle savedInstanceState ) 
    {
        super.onCreate( savedInstanceState );
        addPreferencesFromResource( R.xml.list_of_servers );
        getListView( ).setBackgroundResource( R.drawable.background );
        
        _add_server=findPreference( KEY_ADD );
        
        _list_of_servers=(PreferenceCategory)findPreference( KEY_LIST );
        _list_of_servers.setOrderingAsAdded( true );
        
        PreferenceScreen prefSet = getPreferenceScreen();
//        _reset_eignefaces=findPreference( KEY_RESET );
        
        _max_size=(EditTextPreference)findPreference( "max_size" );
        _max_size.setText( prefSet.getSharedPreferences().getString("max_size","300") );
        _max_size.setSummary( prefSet.getSharedPreferences().getString("max_size","300") );
        _max_size.getEditText().setKeyListener( DigitsKeyListener.getInstance(false,true) ); 
        
        _max_size.setOnPreferenceChangeListener( this );
        
        _dbHandler = new DBHandle( this );
        displayAllServers( ); 
    }

    private void displayAllServers( )
    {
    	for( Server.Config config : _dbHandler.getAllConfigs() )
    	{
    		_list_of_servers.addPreference( new PreferenceServer(this, config) );
    	}
    }

    public boolean onPreferenceTreeClick( PreferenceScreen preferenceScreen, Preference preference ) 
    {
        super.onPreferenceTreeClick( preferenceScreen, preference );

        if( preference==_add_server ) 
        {
        	Intent i=new Intent( "com.cawka.FriendDetector.settings.Server" );
        	i.putExtra( "config", new Server.Config() );
        	startActivityForResult( i, UPDATE_SERVER_INFO );
        }
        else if( preference.getClass( ).equals(PreferenceServer.class) )
        {
        	Intent i=new Intent( "com.cawka.FriendDetector.settings.Server" );
        	i.putExtra( "config", ((PreferenceServer)preference).getConfig() );
        	startActivityForResult( i, UPDATE_SERVER_INFO );
        }
//        else if( preference==_reset_eignefaces )
//        {
//        	DBHandleEigen db=new DBHandleEigen( this );
//        	db.deleteAll( );
//        }
        
        return true;
    }
   
	public boolean onPreferenceChange( Preference preference, Object newValue )
	{
		if( preference == _max_size )
		{
			( (EditTextPreference)preference ).setSummary( (String)newValue );
			return true;
		}
		return false;
	}
    
	protected void onActivityResult( int requestCode, int resultCode,
			Intent data )
	{
		if( requestCode==UPDATE_SERVER_INFO )
		{
//			Toast.makeText(  this, (data!=null)?"OK":"not ok", Toast.LENGTH_SHORT ).show( );
			if( resultCode==Activity.RESULT_OK )
			{
				Server.Config config=(Server.Config)data.getSerializableExtra( "config" );
				if( config.id<0 )
				{
					config=_dbHandler.add( config );
					_list_of_servers.addPreference( new PreferenceServer(this, config) );
				}
				else
				{
					_dbHandler.update( config );
					PreferenceServer pref=findServer( config.id );
					if( pref!=null ) pref.setConfig( config );
				}
			}
			else if( resultCode==Server.RESULT_DELETE_DETECTOR )
			{
				Server.Config config=(Server.Config)data.getSerializableExtra( "config" );
				if( config.id>=0 )
				{
					_dbHandler.delete( config.id );
					PreferenceServer pref=findServer( config.id );
					_list_of_servers.removePreference( pref );
				}
			}
		} 
	}
	
	private PreferenceServer findServer( long server_id )
	{
		for( int i=0; i<_list_of_servers.getPreferenceCount(); i++ )
		{
			if( _list_of_servers.getPreference(i).getClass( ).equals(PreferenceServer.class) )
			{
				PreferenceServer pref=(PreferenceServer)_list_of_servers.getPreference(i);
				if( pref.getConfig().id==server_id ) return pref;
			}
		}
		
		return null;
	}
}

