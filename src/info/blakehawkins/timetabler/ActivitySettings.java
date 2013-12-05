package info.blakehawkins.timetabler;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

public class ActivitySettings extends PreferenceActivity {
	public static final String DEFAULT_URI = "http://www.inf.ed.ac.uk/teaching/courses/selp/xml/";

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
				SharedPreferences sp = PreferenceManager
						.getDefaultSharedPreferences(getApplicationContext());
				SharedPreferences.Editor e = sp.edit();
				e.putString("pref_xmlUri", DEFAULT_URI);
				e.commit();
				EditTextPreference other = (EditTextPreference) findPreference("pref_xmlUri");
				other.setText(DEFAULT_URI);
				return false;
			}

		});
	}
}
