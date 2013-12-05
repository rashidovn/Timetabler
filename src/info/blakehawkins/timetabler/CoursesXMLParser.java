package info.blakehawkins.timetabler;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.util.Xml;

public class CoursesXMLParser {
	private static String readText(XmlPullParser psr) throws XmlPullParserException,
			IOException {
		String result = "";
		if (psr.next() == XmlPullParser.TEXT) {
			result = psr.getText();
			psr.nextTag();
		}
		return result;
	}

	private static String readUrl(XmlPullParser psr) throws XmlPullParserException,
			IOException {
		psr.require(XmlPullParser.START_TAG, null, "url");
		String url = readText(psr);
		psr.require(XmlPullParser.END_TAG, null, "url");
		return url;
	}

	private static String readName(XmlPullParser psr) throws XmlPullParserException,
			IOException {
		psr.require(XmlPullParser.START_TAG, null, "name");
		String name = readText(psr);
		psr.require(XmlPullParser.END_TAG, null, "name");
		return name;
	}

	private static String readDrps(XmlPullParser psr) throws XmlPullParserException,
			IOException {
		psr.require(XmlPullParser.START_TAG, null, "drps");
		String drps = readText(psr);
		psr.require(XmlPullParser.END_TAG, null, "drps");
		return drps;
	}

	private static String readEuclid(XmlPullParser psr) throws XmlPullParserException,
			IOException {
		psr.require(XmlPullParser.START_TAG, null, "euclid");
		String euclid = readText(psr);
		psr.require(XmlPullParser.END_TAG, null, "euclid");
		return euclid;
	}

	private static String readAcronym(XmlPullParser psr)
			throws XmlPullParserException, IOException {
		psr.require(XmlPullParser.START_TAG, null, "acronym");
		String acronym = readText(psr);
		psr.require(XmlPullParser.END_TAG, null, "acronym");
		return acronym;
	}

	private static String readLecturer(XmlPullParser psr)
			throws XmlPullParserException, IOException {
		psr.require(XmlPullParser.START_TAG, null, "lecturer");
		String lecturer = readText(psr);
		psr.require(XmlPullParser.END_TAG, null, "lecturer");
		return lecturer;
	}

	private static boolean readAi(XmlPullParser psr) throws XmlPullParserException,
			IOException {
		psr.require(XmlPullParser.START_TAG, null, "ai");
		String ai = readText(psr);
		psr.require(XmlPullParser.END_TAG, null, "ai");
		if (ai.equals("AI")) {
			return true;
		}
		return false;
	}

	private static boolean readCg(XmlPullParser psr) throws XmlPullParserException,
			IOException {
		psr.require(XmlPullParser.START_TAG, null, "cg");
		String cg = readText(psr);
		psr.require(XmlPullParser.END_TAG, null, "cg");
		if (cg.equals("CG")) {
			return true;
		}
		return false;
	}

	private static boolean readCs(XmlPullParser psr) throws XmlPullParserException,
			IOException {
		psr.require(XmlPullParser.START_TAG, null, "cs");
		String cs = readText(psr);
		psr.require(XmlPullParser.END_TAG, null, "cs");
		if (cs.equals("CS")) {
			return true;
		}
		return false;
	}

	private static boolean readSe(XmlPullParser psr) throws XmlPullParserException,
			IOException {
		psr.require(XmlPullParser.START_TAG, null, "se");
		String se = readText(psr);
		psr.require(XmlPullParser.END_TAG, null, "se");
		if (se.equals("SE")) {
			return true;
		}
		return false;
	}

	private static int readLevel(XmlPullParser psr) throws XmlPullParserException,
			IOException {
		psr.require(XmlPullParser.START_TAG, null, "level");
		String level = readText(psr);
		psr.require(XmlPullParser.END_TAG, null, "level");
		return Integer.parseInt(level);
	}

	private static int readPoints(XmlPullParser psr) throws XmlPullParserException,
			IOException {
		psr.require(XmlPullParser.START_TAG, null, "points");
		String points = readText(psr);
		psr.require(XmlPullParser.END_TAG, null, "points");
		return Integer.parseInt(points);
	}

	private static int readYear(XmlPullParser psr) throws XmlPullParserException,
			IOException {
		psr.require(XmlPullParser.START_TAG, null, "year");
		String year = readText(psr);
		psr.require(XmlPullParser.END_TAG, null, "year");
		return Integer.parseInt(year);
	}

	private static int readSemester(XmlPullParser psr) throws XmlPullParserException,
			IOException {
		psr.require(XmlPullParser.START_TAG, null, "deliveryperiod");
		String deliveryperiod = readText(psr);
		psr.require(XmlPullParser.END_TAG, null, "deliveryperiod");
		if (deliveryperiod.equals("S1")) {
			return 1;
		} else if (deliveryperiod.equals("S2")) {
			return 2;
		}
		return -1;
	}

	private static Course readCourse(XmlPullParser psr) throws XmlPullParserException,
			IOException {
		psr.require(XmlPullParser.START_TAG, null, "course");
		String url = null, name = null, drps = null, euclid = null, acronym = null, lecturer = null;
		boolean ai = false, cg = false, cs = false, se = false;
		int level = -1, points = -1, year = -1, semester = -1;
		while (psr.next() != XmlPullParser.END_TAG) {
			if (psr.getEventType() != XmlPullParser.START_TAG) {
				continue;
			}
			String n = psr.getName();
			if (n.equals("url")) {
				url = readUrl(psr);
			} else if (n.equals("name")) {
				name = readName(psr);
			} else if (n.equals("drps")) {
				drps = readDrps(psr);
			} else if (n.equals("euclid")) {
				euclid = readEuclid(psr);
			} else if (n.equals("acronym")) {
				acronym = readAcronym(psr);
			} else if (n.equals("lecturer")) {
				lecturer = readLecturer(psr);
			} else if (n.equals("ai")) {
				ai = readAi(psr);
			} else if (n.equals("cg")) {
				cg = readCg(psr);
			} else if (n.equals("cs")) {
				cs = readCs(psr);
			} else if (n.equals("se")) {
				se = readSe(psr);
			} else if (n.equals("level")) {
				level = readLevel(psr);
			} else if (n.equals("points")) {
				points = readPoints(psr);
			} else if (n.equals("year")) {
				year = readYear(psr);
			} else if (n.equals("deliveryperiod")) {
				semester = readSemester(psr);
			}
		}
		return new Course(url, name, drps, euclid, acronym, lecturer, ai, cg,
				cs, se, level, points, year, semester);
	}

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

	private static ArrayList<Course> readFeed(XmlPullParser psr)
			throws XmlPullParserException, IOException {
		ArrayList<Course> courses = new ArrayList<Course>();
		psr.require(XmlPullParser.START_TAG, null, "list");
		while (psr.next() != XmlPullParser.END_TAG) {
			if (psr.getEventType() != XmlPullParser.START_TAG) {
				continue;
			}
			String name = psr.getName();
			if (name.equals("course")) {
				courses.add(readCourse(psr));
			} else {
				skip(psr);
			}
		}
		return courses;
	}

	public static ArrayList<Course> parse(InputStream in)
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
