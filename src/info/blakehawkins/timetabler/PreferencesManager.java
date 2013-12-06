package info.blakehawkins.timetabler;

import java.util.ArrayList;
import java.util.Calendar;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * Static class which delegates preferences access, essentially just a facade
 * for the local file system.
 */
public class PreferencesManager {
	private static final String CLASS_NAME = "PreferencesManager";
	
	/**
	 * Checks today's date and returns the corresponding semester as an integer
	 */
	private static int getSemesterFromDate() {
		Calendar c = Calendar.getInstance();
		int month = c.get(Calendar.MONTH);
		if (month == Calendar.JANUARY || month == Calendar.FEBRUARY
				|| month == Calendar.MARCH || month == Calendar.APRIL
				|| month == Calendar.MAY || month == Calendar.JUNE) {
			return 2;
		} else {
			return 1;
		}
	}
	
	/**
	 * Checks a date given in milliseconds and returns the correspoding
	 * semester as an integer
	 */
	private static int getSemesterFromMillis(long m) {
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(m);
		int month = c.get(Calendar.MONTH);
		if (month == Calendar.JANUARY || month == Calendar.FEBRUARY
				|| month == Calendar.MARCH || month == Calendar.APRIL
				|| month == Calendar.MAY) {
			return 2;
		} else {
			return 1;
		}
	}
	
	/**
	 * Slightly comical method name which just returns a date from 1990 in
	 * milliseconds.
	 */
	private static long getLongTimeAgo() {
		Calendar c = Calendar.getInstance();
		c.set(Calendar.YEAR, 1990);
		return c.getTimeInMillis();
	}
	
	/**
	 * Method which returns 'now' in milliseconds
	 */
	private static long getNow() {
		Calendar c = Calendar.getInstance();
		return c.getTimeInMillis();
	}
	
	/**
	 * Public method for getting the logical semester depending on preferences
	 * and stored values.
	 */
	public static int getSemester(Context cxt) {
		SharedPreferences sp = cxt.getSharedPreferences(
				cxt.getString(R.string.preference_file_key),
				Context.MODE_PRIVATE);
		SharedPreferences.Editor e = sp.edit();
		if (sp.contains("pref_autoSemester") == false) {
			e.putBoolean("pref_autoSemester", true);
		}
		if (sp.contains("semester") == false) {
			e.putInt("semester", getSemesterFromDate());
		}
		if (sp.contains("lastAccessed") == false) {
			e.putLong("lastAccessed", getLongTimeAgo());
		}
		e.commit();
		if (sp.getBoolean("pref_autoSemester", true)) {
			int now = getSemesterFromDate();
			int then = getSemesterFromMillis(sp.getLong("lastAccessed", 0));
			if (now != then) {
				e.putInt("semester", now);
				e.putLong("lastAccessed", getNow());
				e.commit();
				return now;
			} else {
				return sp.getInt("semester", 1);
			}
		} else {
			return sp.getInt("semester", 1);
		}
	}
	
	/**
	 * Stores a course as enabled or not to disk.
	 */
	public static void setCourseEnabled(Context cxt, Course course,
			boolean enabled) {
		SharedPreferences sp = cxt.getSharedPreferences(
				cxt.getString(R.string.preference_file_key),
				Context.MODE_PRIVATE);
		SharedPreferences.Editor e = sp.edit();
		e.putBoolean(course.acronym, enabled);
		e.commit();
	}

	/**
	 * Checks if a course is enabled or not 
	 */
	public static Boolean isCourseEnabled(Context cxt, Lecture lecture) {
		SharedPreferences sp = cxt.getSharedPreferences(
				cxt.getString(R.string.preference_file_key),
				Context.MODE_PRIVATE);
		SharedPreferences.Editor e = sp.edit();
		boolean f = false;
		if (sp.contains(lecture.acronym)) {
			f = sp.getBoolean(lecture.acronym, false);
		} else {
			e.putBoolean(lecture.acronym, false);
		}
		e.commit();
		return f;
	}
	
	/**
	 * Returns the filter state of the given ID
	 */
	public static boolean getFilterState(Context cxt, String id) {
		SharedPreferences sp = cxt.getSharedPreferences(
				cxt.getString(R.string.preference_file_key),
				Context.MODE_PRIVATE);
		return sp.getBoolean(id, true);
	}
	
	/**
	 * Gets all filter states
	 */
	public static ArrayList<Boolean> getFilterStates(Context cxt) {
		SharedPreferences sp = cxt.getSharedPreferences(
				cxt.getString(R.string.preference_file_key),
				Context.MODE_PRIVATE);
		ArrayList<Boolean> filters = new ArrayList<Boolean>();
		filters.add(sp.getBoolean("y1", true));
		filters.add(sp.getBoolean("y2", true));
		filters.add(sp.getBoolean("y3", true));
		filters.add(sp.getBoolean("y4", true));
		filters.add(sp.getBoolean("y5", true));
		filters.add(sp.getBoolean("sem1", true));
		filters.add(sp.getBoolean("sem2", true));
		return filters;
	}
	
	/**
	 * Saves all filter states
	 */
	public static void saveFilterStates(Context cxt, boolean y1, boolean y2,
			boolean y3, boolean y4, boolean y5, boolean sem1, boolean sem2) {
		SharedPreferences sp = cxt.getSharedPreferences(
				cxt.getString(R.string.preference_file_key),
				Context.MODE_PRIVATE);
		SharedPreferences.Editor e = sp.edit();
		e.putBoolean("y1", y1);
		e.putBoolean("y2", y2);
		e.putBoolean("y3", y3);
		e.putBoolean("y4", y4);
		e.putBoolean("y5", y5);
		e.putBoolean("sem1", sem1);
		e.putBoolean("sem2", sem2);
		e.commit();
	}

	/**
	 * Gets the stored XML URI which can be set in the settings activity.
	 */
	public static String getXmlUri(Context cxt) {
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(cxt);
		String uri = sp.getString(ActivitySettings.KEY_PREF_XML_URI,
				ActivitySettings.DEFAULT_URI);
		Log.v(CLASS_NAME, uri);
		return uri;
	}
	
	/**
	 * Switches the currently enabled semester
	 */
	public static void swapSemester(Context cxt) {
		SharedPreferences sp = cxt.getSharedPreferences(
				cxt.getString(R.string.preference_file_key),
				Context.MODE_PRIVATE);
		SharedPreferences.Editor e = sp.edit();
		int before = sp.getInt("semester", 1);
		if (before == 1) {
			e.putInt("semester", 2);
		} else {
			e.putInt("semester", 1);
		}
		e.commit();
	}
	
	/**
	 * Checks if a specific course is enabled
	 */
	public static Boolean isCourseEnabled(Context cxt, Course course) {
		SharedPreferences sp = cxt.getSharedPreferences(
				cxt.getString(R.string.preference_file_key),
				Context.MODE_PRIVATE);
		SharedPreferences.Editor e = sp.edit();
		boolean f = false;
		if (sp.contains(course.acronym)) {
			f = sp.getBoolean(course.acronym, false);
		} else {
			e.putBoolean(course.acronym, false);
		}
		e.commit();
		return f;
	}
}
