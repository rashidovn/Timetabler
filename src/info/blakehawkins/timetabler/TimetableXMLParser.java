package info.blakehawkins.timetabler;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.util.Log;
import android.util.Pair;
import android.util.Xml;

public class TimetableXMLParser {
	private static final String CLASS_NAME = "TimetableXMLParser";

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

	private static Pair<String, Integer> parseTimeCode(String startTime) {
		int s = 0;
		String q = "";
		if (startTime.equals("09:00")) {
			s = 9;
			q = "09:00 - 9:50";
		} else if (startTime.equals("10:00")) {
			s = 10;
			q = "10:00 - 10:50";
		} else if (startTime.equals("11:10")) {
			s = 11;
			q = "11:10 - 12:00";
		} else if (startTime.equals("12:10")) {
			s = 12;
			q = "12:10 - 13:00";
		} else if (startTime.equals("13:10")) {
			s = 13;
			q = "13:10 - 14:00";
		} else if (startTime.equals("14:10")) {
			s = 14;
			q = "14:10 - 15:00";
		} else if (startTime.equals("15:10")) {
			s = 15;
			q = "15:10 - 16:00";
		} else if (startTime.equals("16:10")) {
			s = 16;
			q = "16:10 - 17:00";
		} else if (startTime.equals("17:10")) {
			s = 17;
			q = "17:10 - 18:00";
		}
		return new Pair<String, Integer>(q, s);
	}

	private static String readText(XmlPullParser psr)
			throws XmlPullParserException, IOException {
		String result = "";
		if (psr.next() == XmlPullParser.TEXT) {
			result = psr.getText();
			psr.nextTag();
		}
		return result;
	}

	private static String readCourse(XmlPullParser psr) throws IOException,
			XmlPullParserException {
		psr.require(XmlPullParser.START_TAG, null, "course");
		String name = readText(psr);
		psr.require(XmlPullParser.END_TAG, null, "course");
		return name;
	}

	private static int readYear(XmlPullParser psr) throws IOException,
			XmlPullParserException {
		psr.require(XmlPullParser.START_TAG, null, "year");
		String year = readText(psr);
		psr.require(XmlPullParser.END_TAG, null, "year");
		return Integer.parseInt(year);
	}

	private static String readRoom(XmlPullParser psr) throws IOException,
			XmlPullParserException {
		psr.require(XmlPullParser.START_TAG, null, "room");
		String room = readText(psr);
		psr.require(XmlPullParser.END_TAG, null, "room");
		return room;
	}

	private static String readBuilding(XmlPullParser psr) throws IOException,
			XmlPullParserException {
		psr.require(XmlPullParser.START_TAG, null, "building");
		String building = readText(psr);
		psr.require(XmlPullParser.END_TAG, null, "building");
		return building;
	}

	private static Pair<String, String> parseVenue(XmlPullParser psr)
			throws IOException, XmlPullParserException {
		psr.require(XmlPullParser.START_TAG, null, "venue");
		String room = null, building = null;
		while (psr.next() != XmlPullParser.END_TAG) {
			if (psr.getEventType() != XmlPullParser.START_TAG) {
				continue;
			}
			String n = psr.getName();
			if (n.equals("room")) {
				Log.v(CLASS_NAME, "Reading room at " + psr.getLineNumber());
				room = readRoom(psr);
			} else if (n.equals("building")) {
				Log.v(CLASS_NAME, "Reading building at " + psr.getLineNumber());
				building = readBuilding(psr);
			} else {
				Log.w(CLASS_NAME, "Unexpected " + n);
				skip(psr);
			}
		}
		return new Pair<String, String>(room, building);
	}

	private static String parseComment(XmlPullParser psr) throws IOException,
			XmlPullParserException {
		psr.require(XmlPullParser.START_TAG, null, "comment");
		String comment = readText(psr);
		psr.require(XmlPullParser.END_TAG, null, "comment");
		return comment;
	}

	private static void parseLecture(XmlPullParser psr,
			ArrayList<Lecture> lectures, int sem, String day, String tString,
			int time) throws IOException, XmlPullParserException {
		psr.require(XmlPullParser.START_TAG, null, "lecture");
		String acronym = null, building = null, comment = null, room = null;
		boolean year1 = false, year2 = false, year3 = false, year4 = false, year5 = false;
		while (psr.next() != XmlPullParser.END_TAG) {
			if (psr.getEventType() != XmlPullParser.START_TAG) {
				continue;
			}
			String n = psr.getName();
			if (n.equals("course")) {
				Log.v(CLASS_NAME, "Reading course at " + psr.getLineNumber());
				acronym = readCourse(psr);
			} else if (n.equals("years")) {
				Log.v(CLASS_NAME, "Parsing years at " + psr.getLineNumber());
				psr.require(XmlPullParser.START_TAG, null, "years");
				while (psr.next() != XmlPullParser.END_TAG) {
					if (psr.getEventType() != XmlPullParser.START_TAG) {
						continue;
					}
					String q = psr.getName();
					if (q.equals("year")) {
						Log.v(CLASS_NAME,
								"Reading year at " + psr.getLineNumber());
						int y = readYear(psr);
						if (y == 1) {
							year1 = true;
						} else if (y == 2) {
							year2 = true;
						} else if (y == 3) {
							year3 = true;
						} else if (y == 4) {
							year4 = true;
						} else if (y == 5) {
							year5 = true;
						}
					} else {
						Log.w(CLASS_NAME, "Expected year but found " + q);
						skip(psr);
					}
				}
			} else if (n.equals("venue")) {
				Log.v(CLASS_NAME, "Parsing venue at " + psr.getLineNumber());
				Pair<String, String> rbdg = parseVenue(psr);
				room = rbdg.first;
				building = rbdg.second;
			} else if (n.equals("comment")) {
				Log.v(CLASS_NAME, "Parsing comment at " + psr.getLineNumber());
				comment = parseComment(psr);
			} else {
				Log.w(CLASS_NAME, "Unexpected " + n);
				skip(psr);
			}
		}
		Lecture l = new Lecture(day, acronym, room, building, comment, tString,
				sem, time, year1, year2, year3, year4, year5);
		lectures.add(l);
	}

	private static void parseTime(XmlPullParser psr,
			ArrayList<Lecture> lectures, int sem, String day)
			throws IOException, XmlPullParserException {
		psr.require(XmlPullParser.START_TAG, null, "time");
		Log.v(CLASS_NAME, "Parsing time code at " + psr.getLineNumber());
		// int time = parseTimeCode(psr.getAttributeValue(null, "start"));
		Pair<String, Integer> tandtstring = parseTimeCode(psr
				.getAttributeValue(null, "start"));
		int time = tandtstring.second;
		String tString = tandtstring.first;
		Log.v(CLASS_NAME,
				time + " while parsing time code at " + psr.getLineNumber());
		while (psr.next() != XmlPullParser.END_TAG) {
			if (psr.getEventType() != XmlPullParser.START_TAG) {
				continue;
			}
			String name = psr.getName();
			if (name.equals("lecture")) {
				Log.v(CLASS_NAME, "Parsing lecture at " + psr.getLineNumber());
				parseLecture(psr, lectures, sem, day, tString, time);
			} else {
				Log.w(CLASS_NAME, "Expected lecture but found " + name);
				skip(psr);
			}
		}
	}

	private static void parseDay(XmlPullParser psr,
			ArrayList<Lecture> lectures, int sem) throws IOException,
			XmlPullParserException {
		psr.require(XmlPullParser.START_TAG, null, "day");
		String day = psr.getAttributeValue(null, "name");
		while (psr.next() != XmlPullParser.END_TAG) {
			if (psr.getEventType() != XmlPullParser.START_TAG) {
				continue;
			}
			String name = psr.getName();
			if (name.equals("time")) {
				Log.v(CLASS_NAME, "Parsing time at " + psr.getLineNumber());
				parseTime(psr, lectures, sem, day);
			} else {
				Log.w(CLASS_NAME, "Expected time but found " + name);
				skip(psr);
			}
		}
	}

	private static void parseWeek(XmlPullParser psr,
			ArrayList<Lecture> lectures, int sem) throws IOException,
			XmlPullParserException {
		psr.require(XmlPullParser.START_TAG, null, "week");
		while (psr.next() != XmlPullParser.END_TAG) {
			if (psr.getEventType() != XmlPullParser.START_TAG) {
				continue;
			}
			String name = psr.getName();
			if (name.equals("day")) {
				Log.v(CLASS_NAME, "Parsing day at " + psr.getLineNumber());
				parseDay(psr, lectures, sem);
			} else {
				Log.w(CLASS_NAME, "Expected day but found " + name);
				skip(psr);
			}
		}
	}

	private static void parseSemester(XmlPullParser psr,
			ArrayList<Lecture> lectures) throws IOException,
			XmlPullParserException {
		psr.require(XmlPullParser.START_TAG, null, "semester");
		int sem = Integer.parseInt(psr.getAttributeValue(null, "number"));
		while (psr.next() != XmlPullParser.END_TAG) {
			if (psr.getEventType() != XmlPullParser.START_TAG) {
				continue;
			}
			String name = psr.getName();
			if (name.equals("week")) {
				Log.v(CLASS_NAME, "Parsing week at " + psr.getLineNumber());
				parseWeek(psr, lectures, sem);
			} else {
				Log.w(CLASS_NAME, "Expected week but encountered " + name);
				skip(psr);
			}
		}
	}

	private static ArrayList<Lecture> readFeed(XmlPullParser psr)
			throws IOException, XmlPullParserException {
		ArrayList<Lecture> lectures = new ArrayList<Lecture>();
		psr.require(XmlPullParser.START_TAG, null, "timetable");
		Log.v(CLASS_NAME, "Requierd Timetable at " + psr.getLineNumber());
		while (psr.next() != XmlPullParser.END_TAG) {
			if (psr.getEventType() != XmlPullParser.START_TAG) {
				continue;
			}
			String name = psr.getName();
			if (name.equals("semester")) {
				Log.v(CLASS_NAME, "Parsing semester at " + psr.getLineNumber());
				parseSemester(psr, lectures);
			} else {
				Log.w(CLASS_NAME, "Expected semester but encountered " + name);
				skip(psr);
			}
		}
		return lectures;
	}

	public static ArrayList<Lecture> parse(InputStream in) throws IOException,
			XmlPullParserException {
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
