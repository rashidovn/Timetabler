package info.blakehawkins.timetabler;

import java.io.IOException;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParserException;

import android.annotation.SuppressLint;
import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.Context;
import android.content.Intent;
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
import android.widget.Toast;
import android.widget.TableRow.LayoutParams;
import android.widget.TextView;

/**
 * Main Viewer activity, which is the launcher activity. Displays lecture
 * details about selected courses for the active semester. Lectures can be
 * selected to display additional details.
 **/
public class ActivityMainViewer extends ActionBarActivity implements
		Button.OnClickListener {
	private static final String CLASS_NAME = "ActivityMainViewer";

	public static final String TIME_INTENT_KEY = "info.blakehawkins.timetabler.time",
			COURSE_INTENT_KEY = "info.blakehawkins.timetabler.course";

	/**
	 * Method which builds the viewgroup for the activity. We abstract it so
	 * that it could be called from different states. For example, the view is
	 * refreshed when the user swaps semester in the action bar.
	 */
	@SuppressWarnings("deprecation")
	protected void refreshActivityContents() {
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
					if (c != null) {
						lectureNameBtn.setText(c.name);
					} else {
						lectureNameBtn.setText(R.string.no_course_name);
					}
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

	@SuppressLint("NewApi")
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main_viewer, menu);
		// Associated searchable configurations with the searchview
		Log.v(CLASS_NAME, "Menu Inflated");
		// if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
		SearchManager sMan = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
		Log.v(CLASS_NAME, "Search manager acquired");
		MenuItem searchItem = menu.findItem(R.id.search);
		if (searchItem == null) {
			Log.v(CLASS_NAME, "searchItem null");
		}
		Log.v(CLASS_NAME, searchItem.toString());
		SearchView sView = (SearchView) MenuItemCompat
				.getActionView(searchItem);
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
		// }
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

	/**
	 * Auxillary method used to fetch and refresh the client's XML data from the
	 * URI given in settings. By default it will fetch from the static URI
	 * provided in the SELP course descriptor.
	 **/
	private void refreshXML() {
		// Build a new thread to handle the request, since fetching files from
		// a remove server shouldn't block screen drawing.
		Toast.makeText(
				this,
				getString(R.string.xml_requested_from) + ": "
						+ PreferencesManager.getXmlUri(this), Toast.LENGTH_LONG)
				.show();
		FetchThread ft = new FetchThread(this);
		ft.start();
	}

	/**
	 * Auxillary method used to start ActivitySettings
	 **/
	private void settings() {
		Intent intent = new Intent(this, ActivitySettings.class);
		startActivity(intent);
	}

	/**
	 * Auxillary method used to start ActivitySelectCourses
	 **/
	private void selectCourses() {
		Intent intent = new Intent(this, ActivitySelectCourses.class);
		startActivity(intent);
	}

	/**
	 * Inherited onStart method, which occurs after the activity is created, but
	 * also after the activity is stopped (onStop) and restarted. It is
	 * important to call refreshActivityContents() here rather than onCreate()
	 * so that when MainViewer is *returned to* from another activity, it will
	 * refresh its contents in case anything has been changed.
	 **/
	public void onStart() {
		super.onStart();
		XMLManager.verifyXMLIntegrity(this);
		refreshActivityContents();
	}

	/**
	 * Implemented onClick handler, which registers the clicking of a link in
	 * the main viewer, and opens the course/lecture details in the
	 * CourseDetails activity.
	 */
	@Override
	public void onClick(View arg0) {
		Intent intent = new Intent(this, ActivityCourseDetails.class);

		// Attach course acronym and lecture start time to the intent, so
		// CourseDetails activity can fetch the proper lecture
		intent.putExtra(COURSE_INTENT_KEY,
				(String) arg0.getTag(R.string.COURSE_TAG_ID));
		intent.putExtra(TIME_INTENT_KEY,
				(Integer) arg0.getTag(R.string.TIME_TAG_ID));
		startActivity(intent);
	}
}
