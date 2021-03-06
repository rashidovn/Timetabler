package info.blakehawkins.timetabler;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParserException;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;
import android.widget.Toast;

/**
 * Facade for managing all interactions with XML data
 */
public class XMLManager {
	private static final String CLASS_NAME = "XMLManager";

	private static final String COURSES_FILENAME = "courses.xml";
	private static final String TIMETABLE_FILENAME = "timetable.xml";
	private static final String VENUES_FILENAME = "venues.xml";

	private static ArrayList<Course> coursesCache = null;
	private static ArrayList<Lecture> lecturesCache = null;
	private static ArrayList<Venue> venuesCache = null;
	private static boolean useCoursesCache = false;
	private static boolean useLecturesCache = false;
	private static boolean useVenuesCache = false;
	
	/**
	 * Method which checks if a file exists in the files directory, useful
	 * since by default the application only contains files in asset block
	 */
	private static boolean fileExistsInFilesDir(String fn, Context cxt) {
		File f = new File(cxt.getApplicationContext().getFilesDir(), fn);
		return f.exists();
	}
	
	/**
	 * Generic method for copying a file from an in to an out stream
	 */
	private static void copyFile(InputStream in, OutputStream out)
			throws IOException {
		byte[] buf = new byte[1024];
		int len;
		while ((len = in.read(buf)) > 0) {
			out.write(buf, 0, len);
		}
	}
	
	/**
	 * Helper method for copying a file from an in stream to an out File
	 */
	private static void copyFile(InputStream in, File dst) throws IOException {
		OutputStream out = new FileOutputStream(dst);
		copyFile(in, out);
		out.close();
	}
	
	/**
	 * Method for overwriting a file in the files directory
	 */
	public static void overwriteXml(Context cxt, InputStream in, String file)
			throws IOException {
		useCoursesCache = false;
		useLecturesCache = false;
		useVenuesCache = false;
		File filesDir = cxt.getApplicationContext().getFilesDir();
		copyFile(in, new File(filesDir, file));
	}

	/**
	 * Method for copying XML from assets (most likely only runs once)
	 */
	private static void copyXMLFromAssets(Context cxt) {
		AssetManager am = cxt.getAssets();
		File filesDir = cxt.getApplicationContext().getFilesDir();
		File fC = new File(filesDir, COURSES_FILENAME), fT = new File(filesDir,
				TIMETABLE_FILENAME), fV = new File(filesDir, VENUES_FILENAME);

		try {
			InputStream sC = am.open(COURSES_FILENAME), sT = am
					.open(TIMETABLE_FILENAME), sV = am.open(VENUES_FILENAME);
			copyFile(sC, fC);
			copyFile(sT, fT);
			copyFile(sV, fV);
			sC.close();
			sT.close();
			sV.close();
		} catch (IOException e) {
			e.printStackTrace();
			Log.e(CLASS_NAME, "Unable to open file in assets");
		}
	}
	
	/**
	 * Method for getting a course given its acronym as a string. Uses a linear
	 * search which is okay for the reasonable size of courses
	 */
	public static Course getCourseFromAcronym(Context cxt, String acnm)
			throws IOException, XmlPullParserException {
		ArrayList<Course> courses = getCourses(cxt);
		for (Course c : courses) {
			if (c.acronym.equals(acnm)) {
				return c;
			}
		}
		Log.w(CLASS_NAME, "Unable to find course with acronym " + acnm);
		return null;
	}
	
	/**
	 * Linearly searchs for "any" lecture that contains the course acronym.
	 * Useful because lectures actually contain broad data as well.
	 */
	public static Lecture getLectureFromAcronym(Context cxt, String acnm)
			throws IOException, XmlPullParserException {
		ArrayList<Lecture> lecs = getLectures(cxt);
		for (Lecture l : lecs) {
			if (l.acronym.equals(acnm)) {
				return l;
			}
		}
		Log.w(CLASS_NAME, "Unable to find lecture with acronym " + acnm);
		return null;
	}
	
	/**
	 * Gets a specific lecture from a course acronym, time pair (linear search)
	 */
	public static Lecture getLectureFromAcronymTimePair(Context cxt,
			String acnm, Integer sTime) throws IOException,
			XmlPullParserException {
		ArrayList<Lecture> lecs = getLectures(cxt);
		for (Lecture l : lecs) {
			if (l.acronym.equals(acnm) && l.startTime == sTime) {
				return l;
			}
		}
		Log.w(CLASS_NAME, "Unable to find lecture with acronym, start time: "
				+ acnm + " " + String.valueOf(sTime));
		return null;
	}

	/**
	 * Used to get a list of courses from courses.xml
	 **/
	public static ArrayList<Course> getCourses(Context cxt)
			throws XmlPullParserException, IOException {
		if (useCoursesCache) {
			return coursesCache;
		}
		File f = new File(cxt.getApplicationContext().getFilesDir(),
				COURSES_FILENAME);
		InputStream in = new FileInputStream(f);
		ArrayList<Course> results = CoursesXMLParser.parse(in);
		coursesCache = results;
		useCoursesCache = true;
		return results;
	}
	
	/**
	 * Used to get a list of lectures
	 */
	public static ArrayList<Lecture> getLectures(Context cxt)
			throws XmlPullParserException, IOException {
		if ( useLecturesCache) {
			return lecturesCache;
		}
		File f = new File(cxt.getApplicationContext().getFilesDir(),
				TIMETABLE_FILENAME);
		InputStream in = new FileInputStream(f);
		ArrayList<Lecture> results = TimetableXMLParser.parse(in);
		lecturesCache=results;
		useLecturesCache = true;
		return results;
	}
	
	/**
	 * Used to get a list of venues
	 */
	public static ArrayList<Venue> getVenues(Context cxt)
			throws XmlPullParserException, IOException {
		if ( useVenuesCache) {
			return venuesCache;
		}
		File f = new File(cxt.getApplicationContext().getFilesDir(),
				VENUES_FILENAME);
		InputStream in = new FileInputStream(f);
		ArrayList<Venue> results = VenuesXMLParser.parse(in);
		venuesCache=results;
		useVenuesCache=true;
		return results;
	}
	
	/**
	 * Linear search for a venue given a building name code
	 */
	public static Venue getVenueFromBuilding(Context cxt, String name)
			throws XmlPullParserException, IOException {
		ArrayList<Venue> venues = getVenues(cxt);
		for (Venue v : venues) {
			if (v.name.equals(name)) {
				return v;
				
			}
		}
		Log.w(CLASS_NAME, "Venue not found with name " + name);
		return null;
	}
	
	/**
	 * Method for verifying that XML files are in files directory. If not, the
	 * data is fetched from assets. This part should only occur once
	 */
	public static void verifyXMLIntegrity(Context cxt) {
		boolean c = fileExistsInFilesDir(COURSES_FILENAME, cxt);
		boolean t = fileExistsInFilesDir(TIMETABLE_FILENAME, cxt);
		boolean v = fileExistsInFilesDir(VENUES_FILENAME, cxt);
		if (c && t && v) {
			Log.v(CLASS_NAME, "XML Integrity verified Successfully");
		} else {
			copyXMLFromAssets(cxt);
			Toast.makeText(cxt, cxt.getString(R.string.toast_xml_copied_text),
					Toast.LENGTH_LONG).show();
			Log.v(CLASS_NAME, "XML Data copied from assets");
		}
	}
}
