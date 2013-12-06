package info.blakehawkins.timetabler;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.util.Log;

public class ActivitySettings extends PreferenceActivity implements
		OnSharedPreferenceChangeListener {
	private static final String CLASS_NAME = "ActivitySettings";
	public static final String DEFAULT_URI = "http://www.inf.ed.ac.uk/teaching/courses/selp/xml/",
			KEY_PREF_XML_URI = "pref_xmlUri";

	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Why is this deprecated.. it's in the tutorial
		// http://developer.android.com/guide/topics/ui/settings.html
		addPreferencesFromResource(R.xml.preferences);
		Preference button = (Preference) findPreference("pref_resetXmlUri");
		button.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

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

	@SuppressWarnings("deprecation")
	public void onResume(Bundle savedInstanceState) {
		super.onResume();
		SharedPreferences sp = getPreferenceScreen()
				.getSharedPreferences();
		sp.registerOnSharedPreferenceChangeListener(this);
	}
	
	@SuppressWarnings("deprecation")
	@Override
	protected void onPause() {
	    super.onPause();
	    // Unregister the listener whenever a key changes
	    getPreferenceScreen().getSharedPreferences()
	            .unregisterOnSharedPreferenceChangeListener(this);
	}
	
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
