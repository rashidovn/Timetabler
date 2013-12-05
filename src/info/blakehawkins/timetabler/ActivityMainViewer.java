package info.blakehawkins.timetabler;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParserException;

import android.annotation.SuppressLint;
import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TableRow.LayoutParams;
import android.widget.TextView;

public class ActivityMainViewer extends ActionBarActivity implements
		Button.OnClickListener {
	private static final String CLASS_NAME = "ActivityMainViewer";
	public static final String TIME_INTENT_KEY = "info.blakehawkins.timetabler.time",
			COURSE_INTENT_KEY = "info.blakehawkins.timetabler.course";

	@SuppressWarnings("deprecation")
	private void refreshActivityContents() {
		// Initialize scrollview
		ScrollView scrollView = new ScrollView(this);
		FrameLayout.LayoutParams scrollParams = new ScrollView.LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		scrollView.setLayoutParams(scrollParams);
		// Initialize table
		TableLayout table = new TableLayout(this);
		LayoutParams tableParams = new LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.WRAP_CONTENT);
		table.setLayoutParams(tableParams);
		// Initialize generic row params
		LayoutParams genericRowParams = new TableRow.LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		Log.v(CLASS_NAME, "onResume occured, next will get lectures...");
		try {
			ArrayList<Lecture> lectures = XMLManager.getLectures(this);
			Log.v(CLASS_NAME, "Lectures gotten. Count: " + lectures.size());
			String day = null;
			int semester = PreferencesManager.getSemester(this);
			TextView semesterTextView = new TextView(this);
			TableRow rowForSemester = new TableRow(this);
			rowForSemester.setLayoutParams(genericRowParams);
			LayoutParams rowForSemesterParams = new LayoutParams(
					LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
			LayoutParams semesterTextParams = new LayoutParams(
					LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT,
					(float) 1);
			rowForSemesterParams.gravity = Gravity.CENTER_HORIZONTAL;
			semesterTextView.setText("Semester " + String.valueOf(semester));
			semesterTextView.setTextSize(20);
			rowForSemester.setLayoutParams(rowForSemesterParams);
			semesterTextView.setLayoutParams(semesterTextParams);
			rowForSemester.addView(semesterTextView);
			table.addView(rowForSemester);
			Log.v(CLASS_NAME, "For lectures:...");
			for (Lecture l : lectures) {
				if (PreferencesManager.isCourseEnabled(this, l)
						&& l.semester == semester) {
					Log.v(CLASS_NAME, "Found relevent course...");
					if (l.day.equals(day) == false || day == null) {
						Log.v(CLASS_NAME, "Found new day...");
						day = l.day;
						TableRow dayRow = new TableRow(this);
						dayRow.setLayoutParams(genericRowParams);
						TextView dayView = new TextView(this);
						dayView.setText("     " + day);
						dayView.setPadding(0, 10, 0, 0);
						LayoutParams dayParams = new LayoutParams(
								LayoutParams.FILL_PARENT,
								LayoutParams.WRAP_CONTENT);
						dayView.setLayoutParams(dayParams);
						dayRow.addView(dayView);
						table.addView(dayRow);
						Log.v(CLASS_NAME, "Finished new day...");
					}
					TextView timeView = new TextView(this);
					LayoutParams timeParams = new LayoutParams(10,
							LayoutParams.WRAP_CONTENT);
					timeView.setLayoutParams(timeParams);
					Course c = XMLManager.getCourseFromAcronym(this, l.acronym);
					TableRow lectureRow = new TableRow(this);
					lectureRow.setLayoutParams(genericRowParams);
					Button lectureNameBtn = new Button(this);
					lectureNameBtn.setOnClickListener(this);
					lectureNameBtn.setTag(R.string.TIME_TAG_ID, l.startTime);
					lectureNameBtn.setTag(R.string.COURSE_TAG_ID, l.acronym);
					lectureNameBtn.setText(c.name);
					LayoutParams buttonParams = new LayoutParams(
							LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
					buttonParams.weight = 1;
					timeView.setText(l.time);
					lectureNameBtn.setLayoutParams(buttonParams);
					lectureRow.addView(timeView);
					lectureRow.addView(lectureNameBtn);
					table.addView(lectureRow);
					Log.v(CLASS_NAME, "Finished relevent course...");
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		}
		scrollView.addView(table);
		setContentView(scrollView);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// setContentView(R.layout.activity_main_viewer);
		Log.v(CLASS_NAME, "Activity Created");
		refreshActivityContents();
	}

	@SuppressLint("NewApi")
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main_viewer, menu);
		// Associated searchable configurations with the searchview
		Log.v(CLASS_NAME, "Menu Inflated");
		//if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			SearchManager sMan = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
			Log.v(CLASS_NAME, "Search manager acquired");
			MenuItem searchItem = menu.findItem(R.id.search);
			if (searchItem == null) {
				Log.v(CLASS_NAME, "searchItem null");
			}
			Log.v(CLASS_NAME,searchItem.toString());
			SearchView sView = (SearchView) MenuItemCompat.getActionView(searchItem);
			if (sView == null) {
				Log.v(CLASS_NAME, "sView Null!");
			}
			Log.v(CLASS_NAME, "Search view acquired");
			SearchableInfo info = sMan.getSearchableInfo(getComponentName());
			Log.v(CLASS_NAME, "Search info acquired: " + info.toString());
			sView.setSearchableInfo(info);
			Log.v(CLASS_NAME, "Searchable Info set");
			sView.setIconifiedByDefault(true);
			Log.v(CLASS_NAME, "Iconified by default set");
		//}
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.search:
			onSearchRequested();
			return true;
		case R.id.action_selCourses:
			selectCourses();
			return true;
		case R.id.action_settings:
			settings();
			return true;
		case R.id.action_refreshXML:
			refreshXML();
			return true;
		case R.id.action_swapSem:
			swapSemester();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	public void swapSemester() {
		PreferencesManager.swapSemester(this);
		refreshActivityContents();
	}

	public void refreshXML() {
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				String[] files = new String[3];
				files[0] = "courses.xml";
				files[1] = "timetable.xml";
				files[2] = "venues.xml";
				for (String file : files) {
					InputStream stream = null;
					try {
						String address = PreferencesManager
								.getXmlUri(getApplicationContext()) + file;
						URL url = new URL(address);
						HttpURLConnection con = (HttpURLConnection) url
								.openConnection();
						con.setReadTimeout(10000);
						con.setConnectTimeout(15000);
						con.setRequestMethod("GET");
						con.setDoInput(true);
						con.connect();
						stream = con.getInputStream();
						XMLManager.overwriteXml(getApplicationContext(),
								stream, file);
						Log.v(CLASS_NAME, file + " overwritten.");
					} catch (MalformedURLException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					} finally {
						if (stream != null) {
							try {
								stream.close();
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					}
				}
				Log.v(CLASS_NAME, "Asynchronous XML requests finished.");
			}
		});
		thread.start();
	}

	public void settings() {
		Intent intent = new Intent(this, ActivitySettings.class);
		startActivity(intent);
	}

	public void selectCourses() {
		Intent intent = new Intent(this, ActivitySelectCourses.class);
		startActivity(intent);
	}

	public void onStart() {
		super.onStart();
		Log.v(CLASS_NAME, "onStart() occured.");
		XMLManager.verifyXMLIntegrity(this);
		refreshActivityContents();
	}

	@Override
	public void onClick(View arg0) {
		Intent intent = new Intent(this, ActivityCourseDetails.class);
		Log.v(CLASS_NAME,
				"Sending "
						+ String.valueOf((Integer) arg0
								.getTag(R.string.TIME_TAG_ID)) + " "
						+ (String) arg0.getTag(R.string.COURSE_TAG_ID));
		intent.putExtra(COURSE_INTENT_KEY,
				(String) arg0.getTag(R.string.COURSE_TAG_ID));
		intent.putExtra(TIME_INTENT_KEY,
				(Integer) arg0.getTag(R.string.TIME_TAG_ID));
		startActivity(intent);
	}
}
