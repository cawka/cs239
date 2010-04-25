package com.cawka.FriendDetector;

import com.cawka.FriendDetector.R;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;

public class Settings extends PreferenceActivity 
{
	private static final String KEY_TEST = "test";
	
	
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource( R.xml.settings );

        initToggles();
//        mAirplaneModePreference = (CheckBoxPreference) findPreference(KEY_TOGGLE_AIRPLANE);
    }
    
    /**
     * Invoked on each preference click in this hierarchy, overrides
     * PreferenceActivity's implementation.  Used to make sure we track the
     * preference click events.
     */
    public boolean onPreferenceTreeClick( PreferenceScreen preferenceScreen, Preference preference ) 
    {
//        if ( (preference == mAirplaneModePreference) &&
//                (Boolean.parseBoolean(
//                    SystemProperties.get(TelephonyProperties.PROPERTY_INECM_MODE))) ) {
//            // In ECM mode launch ECM app dialog
//            startActivityForResult(
//                new Intent(TelephonyIntents.ACTION_SHOW_NOTICE_ECM_BLOCK_OTHERS, null),
//                REQUEST_CODE_EXIT_ECM);
//
//            return true;
//        }
//        else {
//            // Let the intents be launched by the Preference manager
//            return false;
//        }
    	
    	return true;
    }
    
    private void initToggles() 
    {
        
//        Preference airplanePreference = findPreference(KEY_TOGGLE_AIRPLANE);
//        Preference wifiPreference = findPreference(KEY_TOGGLE_WIFI);
//        Preference btPreference = findPreference(KEY_TOGGLE_BLUETOOTH);
//        Preference wifiSettings = findPreference(KEY_WIFI_SETTINGS);
//        Preference vpnSettings = findPreference(KEY_VPN_SETTINGS);

//        IBinder b = ServiceManager.getService(BluetoothAdapter.BLUETOOTH_SERVICE);
//        if (b == null) {
//            // Disable BT Settings if BT service is not available.
//            Preference btSettings = findPreference(KEY_BT_SETTINGS);
//            btSettings.setEnabled(false);
//        }
//
//        mWifiEnabler = new WifiEnabler(
//                this, (WifiManager) getSystemService(WIFI_SERVICE),
//                (CheckBoxPreference) wifiPreference);
//        mAirplaneModeEnabler = new AirplaneModeEnabler(
//                this, (CheckBoxPreference) airplanePreference);
//        mBtEnabler = new BluetoothEnabler(this, (CheckBoxPreference) btPreference);
//
//        // manually set up dependencies for Wifi if its radio is not toggleable in airplane mode
//        String toggleableRadios = Settings.System.getString(getContentResolver(),
//                Settings.System.AIRPLANE_MODE_TOGGLEABLE_RADIOS);
//        if (toggleableRadios == null || !toggleableRadios.contains(Settings.System.RADIO_WIFI)) {
//            wifiPreference.setDependency(airplanePreference.getKey());
//            wifiSettings.setDependency(airplanePreference.getKey());
//            vpnSettings.setDependency(airplanePreference.getKey());
//        }
    }    
}
