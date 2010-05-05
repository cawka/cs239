package com.cawka.FriendDetector.settings;


import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import com.cawka.FriendDetector.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;
import android.preference.Preference.OnPreferenceChangeListener;
import android.text.method.DigitsKeyListener;
import android.widget.Toast;

public class Server extends PreferenceActivity
	implements OnPreferenceChangeListener
{
	public static final String KEY_ENABLED 	= "enabled";
	public static final String KEY_TYPE 	= "type"; 
	public static final String KEY_HOSTNAME = "hostname";
	public static final String KEY_PORT 	= "port";
	public static final String KEY_TIMEOUT  = "timeout";
	
	private static final String KEY_DELETE_CATEGORY = "delete_category";
	private static final String KEY_DELETE_SERVER = "delete";
	private static final String KEY_SAVE = "save";
	
	private CheckBoxPreference _enabled;
	private ListPreference     _type;
	private EditTextPreference _hostname;
	private EditTextPreference _port;
	private EditTextPreference _timeout;
	
	private Preference 		  _delete_server;
	private Preference 		  _save;
	
	public static final int LOCAL  = 0;
	public static final int REMOTE = 1;
	
	public static final int	RESULT_DELETE_DETECTOR = 10;
	
	private Server.Config _config;
	
    protected void onCreate( Bundle savedInstanceState ) 
    {
        super.onCreate( savedInstanceState );

        addPreferencesFromResource( R.xml.server );
       
        if( savedInstanceState!=null )
        	_config=(Server.Config)savedInstanceState.getSerializable( "config" );
        
        if( _config==null )
        	_config=(Server.Config)getIntent().getSerializableExtra( "config" );

        _enabled =(CheckBoxPreference)findPreference( KEY_ENABLED );
        _type    =(ListPreference)    findPreference( KEY_TYPE );
        _hostname=(EditTextPreference)findPreference( KEY_HOSTNAME );
        _port    =(EditTextPreference)findPreference( KEY_PORT );
        _timeout =(EditTextPreference)findPreference( KEY_TIMEOUT );
        
        _delete_server=findPreference( KEY_DELETE_SERVER );
        _save         =findPreference( KEY_SAVE );
        if( _config.id<0 ) 
        {
        	((PreferenceCategory)findPreference(KEY_DELETE_CATEGORY)).removeAll( );
        	((PreferenceCategory)findPreference(KEY_DELETE_CATEGORY)).setEnabled( false );
        }
        
        _enabled .setOnPreferenceChangeListener( this );
        _type    .setOnPreferenceChangeListener( this );
        _hostname.setOnPreferenceChangeListener( this );
        _port    .setOnPreferenceChangeListener( this );
        _timeout .setOnPreferenceChangeListener( this );
        
        _enabled.setChecked( _config.enabled );
        _enabled .setSummary( _config.enabled?"Enabled":"Disabled" );
        
        _type    .setValueIndex( _config.type );
        _type    .setSummary( _config.type==0?"Local":"Remote" );
         
        _hostname.setText( _config.hostname );
        _hostname.setSummary( _config.hostname );
    	_hostname.setEnabled( _config.type==REMOTE );
        
        _port    .setText( Integer.toString(_config.port) );
        _port    .setSummary( Integer.toString(_config.port) );
    	_port    .setEnabled( _config.type==REMOTE );
      
        _timeout .setText( Integer.toString(_config.timeout) );
        _timeout .setSummary( Integer.toString(_config.timeout) );
        _timeout .setEnabled( _config.type==REMOTE );
        
        _port    .getEditText().setKeyListener( DigitsKeyListener.getInstance(false,true) ); 
        _timeout .getEditText().setKeyListener( DigitsKeyListener.getInstance(false,true) ); 
    }

	public boolean onPreferenceChange( Preference preference, Object newValue ) 
	{
		if( preference==_hostname ||
			preference==_port ||
			preference==_timeout
			)
		{
			((EditTextPreference)preference).setSummary( (String)newValue );
		}
		
		if( preference==_enabled )
		{
			_config.enabled=(Boolean)newValue;
			_enabled .setSummary( _config.enabled?"Enabled":"Disabled" );
		}
		else if( preference==_type )
		{
			_config.type=Integer.parseInt( (String)newValue );
			_type    .setSummary( _config.type==0?"Local":"Remote" );
			
			_hostname.setEnabled( _config.type==REMOTE );
			_port    .setEnabled( _config.type==REMOTE );
			_timeout .setEnabled( _config.type==REMOTE );
		}
		else if( preference==_hostname )
		{
			_config.hostname=(String)newValue;
		}
		else if( preference==_port )
		{
			_config.port=Integer.parseInt( (String)newValue );
		}
		else if( preference==_timeout )
		{
			_config.timeout=Integer.parseInt( (String)newValue );
		}
		
		return true;
	}

    public boolean onPreferenceTreeClick( PreferenceScreen preferenceScreen, Preference preference ) 
    {
        super.onPreferenceTreeClick( preferenceScreen, preference );

        if( preference==_delete_server ) 
        {
        	new AlertDialog.Builder( this )
	            .setMessage( "Are you sure?" )
	            .setPositiveButton("Yup",  new DialogInterface.OnClickListener(){
					public void onClick( DialogInterface dialog, int which )
					{
			    		getIntent().putExtra( "config", _config );
			    		setResult( RESULT_DELETE_DETECTOR, getIntent() );
			    		
			    		finish( );
					}
				} )
	            .setNegativeButton("Nope", new DialogInterface.OnClickListener(){
					public void onClick( DialogInterface dialog, int which )
					{
					}
				} )
	            .show();
        }
        else if( preference==_save )
        {
    		getIntent().putExtra( "config", _config );
    		setResult( Activity.RESULT_OK, getIntent() );
    		
    		finish( );
        }
        
        return true;
    }	
	
	//////////////////////////////////////////////////////////////////////////////////
	
	public static class Config implements Serializable
	{
		// Determines if a de-serialized file is compatible with this class
		private static final long serialVersionUID = -9122704002090808233L;

		public long		id;
		public int 		type;
		public String	hostname;
		public int 		port;
		public boolean 	enabled;
		public int 		timeout;
		
		public Config( ) //default config
		{
			this( -1, Server.LOCAL, "", 55436, true, 10000 );
		}
		
		public Config( int _id, int _type, String _hostname, int _port, boolean _enabled, int _timeout )
		{
			id		=_id;
			type	=_type;
			hostname=_hostname;
			port	=_port;
			enabled	=_enabled;
			timeout	=_timeout;
		}
		
		 private void writeObject( ObjectOutputStream out ) throws IOException
		 {
			out.writeLong( id );
			out.writeInt( type );
			out.writeUTF( hostname );
			out.writeInt( port );
			out.writeBoolean( enabled );
			out.writeInt( timeout );
		 }

		 private void readObject( ObjectInputStream in ) throws IOException, ClassNotFoundException 
		 {
			id		=in.readLong( );
			type	=in.readInt( );
			hostname=in.readUTF( );
			port	=in.readInt( );
			enabled	=in.readBoolean( );
			timeout	=in.readInt( );
		 }
	}
}

