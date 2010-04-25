package com.cawka.FriendDetector;

import com.cawka.FriendDetector.R;

import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;
import android.preference.Preference.OnPreferenceChangeListener;
import android.text.method.DigitsKeyListener;

public class Settings extends PreferenceActivity
	implements OnPreferenceChangeListener
{
	public static final String KEY_LOCAL_ENABLED   = "local_enabled";
	public static final String KEY_REMOTE_ENABLED  = "remote_enabled";
	public static final String KEY_HOSTNAME = "remote_hostname";
	public static final String KEY_PORT 	= "remote_port";
	public static final String KEY_TIMEOUT  = "remote_timeout";
	
//	private CheckBoxPreference _enabled;
	private EditTextPreference _hostname;
	private EditTextPreference _port;
	private EditTextPreference _timeout;
	
    protected void onCreate( Bundle savedInstanceState ) 
    {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource( R.xml.settings );

//        _enabled =(CheckBoxPreference)findPreference( KEY_ENABLED );
        _hostname=(EditTextPreference)findPreference( KEY_HOSTNAME );
        _port    =(EditTextPreference)findPreference( KEY_PORT );
        _timeout =(EditTextPreference)findPreference( KEY_TIMEOUT );
        
        _hostname.setOnPreferenceChangeListener( this );
        _port    .setOnPreferenceChangeListener( this );
        _timeout .setOnPreferenceChangeListener( this );
        
        PreferenceScreen prefSet = getPreferenceScreen();
        _hostname.setSummary( prefSet.getSharedPreferences().getString(KEY_HOSTNAME,"") );
        _port    .setSummary( prefSet.getSharedPreferences().getString(KEY_PORT, "55436") );
        _timeout .setSummary( prefSet.getSharedPreferences().getString(KEY_TIMEOUT,"2000") );
        
        _port    .getEditText().setKeyListener( DigitsKeyListener.getInstance(false,true) ); 
        _timeout .getEditText().setKeyListener( DigitsKeyListener.getInstance(false,true) ); 
        
//        initToggles();
////        mAirplaneModePreference = (CheckBoxPreference) findPreference(KEY_TOGGLE_AIRPLANE);
//        
//        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener( this );
    }

//    protected void updateEditTextSettings( EditTextPreference preference, String newValue )
//    {
//    
//    }

	public boolean onPreferenceChange( Preference preference, Object newValue ) 
	{
		if( preference==_hostname ||
			preference==_port ||
			preference==_timeout )
		{
			((EditTextPreference)preference).setSummary( (String)newValue );
			return true;
		}
		return false;
	}
    
    /**
     * Invoked on each preference click in this hierarchy, overrides
     * PreferenceActivity's implementation.  Used to make sure we track the
     * preference click events.
     */
//    public boolean onPreferenceTreeClick( PreferenceScreen preferenceScreen, Preference preference ) 
//    {
////        if ( (preference == mAirplaneModePreference) &&
////                (Boolean.parseBoolean(
////                    SystemProperties.get(TelephonyProperties.PROPERTY_INECM_MODE))) ) {
////            // In ECM mode launch ECM app dialog
////            startActivityForResult(
////                new Intent(TelephonyIntents.ACTION_SHOW_NOTICE_ECM_BLOCK_OTHERS, null),
////                REQUEST_CODE_EXIT_ECM);
////
////            return true;
////        }
////        else {
////            // Let the intents be launched by the Preference manager
////            return false;
////        }
//    	
//    	return true;
//    }
    
//    private void initToggles() 
//    {
//        CheckBoxPreference test = (CheckBoxPreference)findPreference( KEY_TEST );
//        
//        test.setChecked( getPreferenceScreen().getSharedPreferences().getBoolean("test", false) );
//        
//
////        Preference airplanePreference = findPreference(KEY_TOGGLE_AIRPLANE);
////        Preference wifiPreference = findPreference(KEY_TOGGLE_WIFI);
////        Preference btPreference = findPreference(KEY_TOGGLE_BLUETOOTH);
////        Preference wifiSettings = findPreference(KEY_WIFI_SETTINGS);
////        Preference vpnSettings = findPreference(KEY_VPN_SETTINGS);
//
////        IBinder b = ServiceManager.getService(BluetoothAdapter.BLUETOOTH_SERVICE);
////        if (b == null) {
////            // Disable BT Settings if BT service is not available.
////            Preference btSettings = findPreference(KEY_BT_SETTINGS);
////            btSettings.setEnabled(false);
////        }
////
////        mWifiEnabler = new WifiEnabler(
////                this, (WifiManager) getSystemService(WIFI_SERVICE),
////                (CheckBoxPreference) wifiPreference);
////        mAirplaneModeEnabler = new AirplaneModeEnabler(
////                this, (CheckBoxPreference) airplanePreference);
////        mBtEnabler = new BluetoothEnabler(this, (CheckBoxPreference) btPreference);
////
////        // manually set up dependencies for Wifi if its radio is not toggleable in airplane mode
////        String toggleableRadios = Settings.System.getString(getContentResolver(),
////                Settings.System.AIRPLANE_MODE_TOGGLEABLE_RADIOS);
////        if (toggleableRadios == null || !toggleableRadios.contains(Settings.System.RADIO_WIFI)) {
////            wifiPreference.setDependency(airplanePreference.getKey());
////            wifiSettings.setDependency(airplanePreference.getKey());
////            vpnSettings.setDependency(airplanePreference.getKey());
////        }
//    }
//
//
//	public void onSharedPreferenceChanged( SharedPreferences sharedPreferences,
//			String key ) 
//	{
//		sharedPreferences.
//		
//	}    
}
