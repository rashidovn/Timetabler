package info.blakehawkins.timetabler;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.util.Log;
import android.util.Xml;

/**
 * Parser otherwise equivalent to CoursesXMLParser
 */
public class VenuesXMLParser {
	private static final String CLASS_NAME = "VenuesXMLParser";
	
	/**
	 * Reads text at the carat
	 */
	private static String readText(XmlPullParser psr)
			throws XmlPullParserException, IOException {
		String result = "";
		if (psr.next() == XmlPullParser.TEXT) {
			result = psr.getText();
			psr.nextTag();
		}
		return result;
	}
	
	/**
	 * Reads a text tag of arbitrary tag name at the carat
	 */
	private static String readTextTag(XmlPullParser psr, String tagName)
			throws XmlPullParserException, IOException {
		psr.require(XmlPullParser.START_TAG, null, tagName);
		String url = readText(psr);
		psr.require(XmlPullParser.END_TAG, null, tagName);
		return url;
	}
	
	/**
	 * Reads a building at the carat
	 */
	private static Venue readBuilding(XmlPullParser psr)
			throws XmlPullParserException, IOException {
		psr.require(XmlPullParser.START_TAG, null, "building");
		String name = null, description = null, map = null;
		while (psr.next() != XmlPullParser.END_TAG) {
			if (psr.getEventType() != XmlPullParser.START_TAG) {
				continue;
			}
			String n = psr.getName();
			if (n.equals("name")) {
				name = readTextTag(psr, "name");
			} else if (n.equals("description")) {
				description = readTextTag(psr, "description");
			} else if (n.equals("map")) {
				map = readTextTag(psr, "map");
			} else {
				Log.v(CLASS_NAME, "Encountered unusual name: " + n);
			}
		}
		return new Venue(name, description, map);
	}
	
	/**
	 * Skips a tag and its children
	 */
	private static void skip(XmlPullParser psr) throws XmlPullParserException,
			IOException {
		if (psr.getEventType() != XmlPullParser.START_TAG) {
			throw new IllegalStateException();
		}
		int depth = 1;
		while (depth != 0) {
			switch (psr.next()) {
			case XmlPullParser.END_TAG:
				depth--;
				break;
			case XmlPullParser.START_TAG:
				depth++;
				break;
			}
		}
	}
	
	/**
	 * Reads a feed at the carat
	 */
	private static ArrayList<Venue> readFeed(XmlPullParser psr)
			throws XmlPullParserException, IOException {
		ArrayList<Venue> venues = new ArrayList<Venue>();
		psr.require(XmlPullParser.START_TAG, null, "venues");
		while (psr.next() != XmlPullParser.END_TAG) {
			if (psr.getEventType() != XmlPullParser.START_TAG) {
				continue;
			}
			String name = psr.getName();
			if (name.equals("building")) {
				venues.add(readBuilding(psr));
			} else {
				skip(psr);
			}
		}
		return venues;
	}
	
	/**
	 * Public method for parsing a venues.xml file
	 */
	public static ArrayList<Venue> parse(InputStream in)
			throws XmlPullParserException, IOException {
		try {
			XmlPullParser psr = Xml.newPullParser();
			psr.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
			psr.setInput(in, null);
			psr.nextTag();
			return readFeed(psr);
		} finally {
			in.close();
		}
	}
}
