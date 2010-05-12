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
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class Server extends PreferenceActivity
	implements OnPreferenceChangeListener
{
	public static final String KEY_ENABLED 	= "enabled";
	public static final String KEY_ENABLED_RECOGNIZER="enabled_recognizer";
	public static final String KEY_TYPE 	= "type"; 
	public static final String KEY_HOSTNAME = "hostname";
	public static final String KEY_PORT 	= "port";
	public static final String KEY_TIMEOUT  = "timeout";
	
	private static final String KEY_DELETE_CATEGORY = "delete_category";
	private static final String KEY_DELETE_SERVER = "delete";
	private static final String KEY_SAVE = "save";
	private static final String KEY_TRAINING_SET = "training_set";
	
	private CheckBoxPreference _enabled;
	private CheckBoxPreference _enabled_recognizer;
	private ListPreference     _type;
	private EditTextPreference _hostname;
	private EditTextPreference _port;
	private EditTextPreference _timeout;
	
//	private Preference 		  _delete_server;
//	private Preference 		  _save;
	private Preference		  _training_set;

	public static final int LOCAL  = 0;
	public static final int REMOTE = 1;
	
	public static final int	RESULT_DELETE_DETECTOR = 10;
	
	private static final int	MENU_DELETE	= 1;
//	private static final int    MENU_RESET  = 2;
	
	private Server.Config _config;
	
    protected void onCreate( Bundle savedInstanceState ) 
    {
        super.onCreate( savedInstanceState );

        addPreferencesFromResource( R.xml.server );
       
        getListView( ).setBackgroundResource( R.drawable.background );

        if( savedInstanceState!=null )
        	_config=(Server.Config)savedInstanceState.getSerializable( "config" );
        
        if( _config==null )
        	_config=(Server.Config)getIntent().getSerializableExtra( "config" );

        _enabled =(CheckBoxPreference)findPreference( KEY_ENABLED );
        _enabled_recognizer=
        		  (CheckBoxPreference)findPreference( KEY_ENABLED_RECOGNIZER );
        _type    =(ListPreference)    findPreference( KEY_TYPE );
        _hostname=(EditTextPreference)findPreference( KEY_HOSTNAME );
        _port    =(EditTextPreference)findPreference( KEY_PORT );
        _timeout =(EditTextPreference)findPreference( KEY_TIMEOUT );
        
        _training_set=findPreference( KEY_TRAINING_SET );
//        _delete_server=findPreference( KEY_DELETE_SERVER );
//        _save         =findPreference( KEY_SAVE );
//        if( _config.id<0 ) 
//        {
//        	((PreferenceCategory)findPreference(KEY_DELETE_CATEGORY)).removeAll( );
//        	((PreferenceCategory)findPreference(KEY_DELETE_CATEGORY)).setEnabled( false );
//        }
        
        _enabled .setOnPreferenceChangeListener( this );
        _enabled_recognizer
        		 .setOnPreferenceChangeListener( this );
        _type    .setOnPreferenceChangeListener( this );
        _hostname.setOnPreferenceChangeListener( this );
        _port    .setOnPreferenceChangeListener( this );
        _timeout .setOnPreferenceChangeListener( this );
        
        _enabled.setChecked( _config.enabled );
        _enabled .setSummary( _config.enabled?"Enabled":"Disabled" );

        _enabled_recognizer.setChecked( _config.enabled_recognizer );
        _enabled_recognizer .setSummary( _config.enabled_recognizer?"Enabled":"Disabled" );

        _type    .setValueIndex( _config.type );
        _type    .setSummary( _config.type==0?"Local":"Remote" );
         
        _hostname.setText( _config.hostname );
        _hostname.setSummary( _config.hostname );
        
        _port    .setText( Integer.toString(_config.port) );
        _port    .setSummary( Integer.toString(_config.port) );
      
        _timeout .setText( Integer.toString(_config.timeout) );
        _timeout .setSummary( Integer.toString(_config.timeout) );
        
        _port    .getEditText().setKeyListener( DigitsKeyListener.getInstance(false,true) ); 
        _timeout .getEditText().setKeyListener( DigitsKeyListener.getInstance(false,true) ); 
        
        updateDependencies( );
        setResult( Activity.RESULT_OK, getIntent() ); //always OK except when server is deleted
    }

    public boolean onCreateOptionsMenu( Menu menu )
    {
//    	menu.add(0, MENU_RESET,  0, "Reset training set" )
//    		.setIcon( R.drawable.ic_menu_refresh );
//    	
        menu.add(0, MENU_DELETE, 0, "Delete server")
        	.setIcon( android.R.drawable.ic_menu_delete );

    	return true;
    }
    
    private void updateDependencies( )
    {
    	_type.setEnabled( _config.enabled || _config.enabled_recognizer );
    	boolean ok=(_config.enabled || _config.enabled_recognizer) && _config.type==REMOTE;
    	_hostname.setEnabled( ok );
    	_port	 .setEnabled( ok );
    	_timeout .setEnabled( ok );    	
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
			
			updateDependencies( );
		}
		else if( preference==_enabled_recognizer )
		{
			_config.enabled_recognizer=(Boolean)newValue;
			_enabled_recognizer.setSummary( _config.enabled_recognizer?"Enabled":"Disabled" );
			
			updateDependencies( );
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

    public boolean onOptionsItemSelected( MenuItem item )
    {
    	switch( item.getItemId() ) 
    	{
	        case MENU_DELETE:
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
	            return true;
//	        case MENU_RESET:
//	        	return true;
    	}
    	
    	return false;
    }
    
    public boolean onPreferenceTreeClick( PreferenceScreen preferenceScreen, Preference preference ) 
    {
        super.onPreferenceTreeClick( preferenceScreen, preference );

    	if( preference==_training_set )
        {
    		Intent i=new Intent( ).setAction( "com.cawka.FriendDetector.Gallery" );
    		i.putExtra( "config", _config );
			startActivity( i );
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
		public boolean  enabled_recognizer;
		public int 		timeout;
		
		public Config( ) //default config
		{
			this( -1, Server.LOCAL, "", 55436, true, true, 10000 );
		}
		
		public Config( int _id, int _type, String _hostname, int _port, boolean _enabled, boolean _enabled_recognizer, int _timeout )
		{
			id		=_id;
			type	=_type;
			hostname=_hostname;
			port	=_port;
			enabled	=_enabled;
			enabled_recognizer=_enabled_recognizer;
			timeout	=_timeout;
		}
		
		 private void writeObject( ObjectOutputStream out ) throws IOException
		 {
			out.writeLong( id );
			out.writeInt( type );
			out.writeUTF( hostname );
			out.writeInt( port );
			out.writeBoolean( enabled );
			out.writeBoolean( enabled_recognizer );
			out.writeInt( timeout );
		 }

		 private void readObject( ObjectInputStream in ) throws IOException, ClassNotFoundException 
		 {
			id		=in.readLong( );
			type	=in.readInt( );
			hostname=in.readUTF( );
			port	=in.readInt( );
			enabled	=in.readBoolean( );
			enabled_recognizer=in.readBoolean( );
			timeout	=in.readInt( );
		 }
	}
}

