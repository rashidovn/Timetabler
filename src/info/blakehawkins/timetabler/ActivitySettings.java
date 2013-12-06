package info.blakehawkins.timetabler;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.util.Log;

/**
 * Settings activity which extends PreferenceActivity. Note that
 * PreferenceActivity and ActionBarActivity are not related. The result is that
 * in Gingerbread, Settings has no action bar. On the bright side, there are no
 * (functional) features missing anyway.
 */
public class ActivitySettings extends PreferenceActivity implements
		OnSharedPreferenceChangeListener {
	private static final String CLASS_NAME = "ActivitySettings";
	public static final String DEFAULT_URI = "http://www.inf.ed.ac.uk/teaching/courses/selp/xml/",
			KEY_PREF_XML_URI = "pref_xmlUri";
	
	/**
	 * Inherited onCreate method, which we use to register clicks to the 'reset
	 * xml uri' button, so that we can change the value of the xml uri
	 * programmatically.
	 */
	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Why is this deprecated.. it's in the tutorial
		// http://developer.android.com/guide/topics/ui/settings.html
		addPreferencesFromResource(R.xml.preferences);
		Preference button = (Preference) findPreference("pref_resetXmlUri");
		button.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
			
			/**
			 * Anonymous inner method which registers a click listener for the
			 * 'reset xml uri' button
			 */
			@Override
			public boolean onPreferenceClick(Preference arg0) {
				SharedPreferences sp = getSharedPreferences(
						getString(R.string.preference_file_key),
						Context.MODE_PRIVATE);
				SharedPreferences.Editor e = sp.edit();
				e.putString("pref_xmlUri", DEFAULT_URI);
				e.commit();
				EditTextPreference other = (EditTextPreference) findPreference("pref_xmlUri");
				other.setText(DEFAULT_URI);
				return false;
			}

		});
	}
	
	/**
	 * Inherited onResume method in which we register a preferences listener
	 */
	@SuppressWarnings("deprecation")
	public void onResume(Bundle savedInstanceState) {
		super.onResume();
		SharedPreferences sp = getPreferenceScreen()
				.getSharedPreferences();
		sp.registerOnSharedPreferenceChangeListener(this);
	}
	
	/**
	 * Inherited onPause method which unregisters the listener. I was told the
	 * listener would be garbage collected and simultaneously shown this code
	 * (StackOverflow) so I'm not even sure if this is necessary.
	 */
	@SuppressWarnings("deprecation")
	@Override
	protected void onPause() {
	    super.onPause();
	    // Unregister the listener whenever a key changes
	    getPreferenceScreen().getSharedPreferences()
	            .unregisterOnSharedPreferenceChangeListener(this);
	}
	
	/**
	 * The implemented listener which writes to the shared preferences.
	 * I build this while debugging an issue with defaultSharedPreferences vs
	 * sharedPreferences. I'm pretty sure this method is supeflous (dead code)
	 * but I'm scared to change it.
	 */
	@Override
	public void onSharedPreferenceChanged(SharedPreferences pref, String key) {
		Log.v(CLASS_NAME, "onPreferenceChange!");
		if (key.equals(KEY_PREF_XML_URI)) {
			SharedPreferences sp = getSharedPreferences(
					getString(R.string.preference_file_key),
					Context.MODE_PRIVATE);
			SharedPreferences.Editor e = sp.edit();
			e.putString("pref_xmlUri",
					pref.getString("pref_xmlUri", DEFAULT_URI));
			e.commit();
			Log.v(CLASS_NAME,
					"Wrote " + pref.getString("pref_xmlUri", DEFAULT_URI));
		}
		return;
	}
}
