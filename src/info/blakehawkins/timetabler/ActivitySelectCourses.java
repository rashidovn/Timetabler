package info.blakehawkins.timetabler;

import java.io.IOException;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParserException;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ScrollView;

/**
 * Activity for selecting which courses should be displayed in timetabler.
 * Options menu contains a filter courses link, which lets the user filter
 * courses by semester and year. Year searching is as inclusive as possible.
 */
public class ActivitySelectCourses extends ActionBarActivity {
	private static final String CLASS_NAME = "ActivitySelectCourses";

	private ArrayList<Pair<CheckBox, Course>> cbcs = null;

	/**
	 * Inherited onPause method, during which we send data to the preferences
	 * manager.
	 */
	@Override
	public void onPause() {
		super.onPause();
		for (Pair<CheckBox, Course> p : this.cbcs) {
			PreferencesManager.setCourseEnabled(this, p.second,
					p.first.isChecked());
		}
	}

	/**
	 * Auxillary method for refreshing the activity contents. Useful in case we
	 * want to refresh the contents at diferent times.
	 */
	private void refreshActivityContents() {
		cbcs = new ArrayList<Pair<CheckBox, Course>>();

		// Fill activity with checkboxes that correspond to courses
		ArrayList<CheckBox> boxes = new ArrayList<CheckBox>();
		Log.v(CLASS_NAME, "Getting filter states");
		ArrayList<Boolean> filters = PreferencesManager.getFilterStates(this);
		boolean[] years = new boolean[5];
		for (int x = 0; x < 5; x++) {
			years[x] = filters.get(x);
		}
		boolean[] sems = new boolean[2];
		sems[0] = filters.get(5);
		sems[1] = filters.get(6);
		buildCheckboxes(boxes, years, sems);

		// Display the checkboxes inside a LinearLayout, inside a ScrollView
		ScrollView sv = new ScrollView(this);
		LinearLayout ly = new LinearLayout(this);
		@SuppressWarnings("deprecation")
		LayoutParams lp = new LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.FILL_PARENT);
		ly.setOrientation(LinearLayout.VERTICAL);
		ly.setLayoutParams(lp);
		sv.addView(ly);
		for (CheckBox c : boxes) {
			ly.addView(c);
		}
		setContentView(sv);
	}

	/**
	 * Auxillary method used to fill boxes with all applicable checkbox views
	 */
	private void buildCheckboxes(ArrayList<CheckBox> boxes, boolean[] years,
			boolean[] sems) {
		try {
			ArrayList<Course> names = XMLManager.getCourses(this);
			Log.v(CLASS_NAME, String.valueOf(names.size()) + " courses gotten");
			for (Course q : names) {
				conditionallyBuildCheckBox(boxes, years, sems, q);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Auxillary method which builds an individual checkbox (if applicable)
	 */
	private void conditionallyBuildCheckBox(ArrayList<CheckBox> boxes,
			boolean[] years, boolean[] sems, Course q) throws IOException,
			XmlPullParserException {
		Log.v(CLASS_NAME, "Checking year " + String.valueOf(q.year - 1)
				+ " and semester " + String.valueOf(q.semester - 1));
		Lecture l = XMLManager.getLectureFromAcronym(this, q.acronym);

		// As long as we have a lecture that fits the descccription, and the
		// course fits the given filters, build it
		if (l != null
				&& ((years[0] && l.year1) || (years[1] && l.year2)
						|| (years[2] && l.year3) || (years[3] && l.year4) || (years[4] && l.year5))) {
			if (q.semester == -1 || sems[q.semester - 1]) {
				CheckBox c = new CheckBox(getApplicationContext());
				Pair<CheckBox, Course> p = new Pair<CheckBox, Course>(c, q);
				this.cbcs.add(p);
				c.setText(q.name);
				c.setTextColor(Color.BLACK);
				c.setChecked(PreferencesManager.isCourseEnabled(this, q));
				boxes.add(c);
			}
		}
	}

	/**
	 * Inherited onCreate method
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_select_courses);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		refreshActivityContents();
	}

	/**
	 * Inherited onStart method
	 */
	@Override
	protected void onStart() {
		super.onStart();
		refreshActivityContents();
	}

	/**
	 * Auxillary method for opening the filter courses activity
	 */
	private void filterCourses() {
		Intent i = new Intent(this, ActivityFilterCourses.class);
		startActivity(i);
	}

	/**
	 * Inherited onOptionsItemSelected method
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_filter_courses:
			filterCourses();
			return true;
		case R.id.action_settings:
			settings();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	/**
	 * Auxillary method for opening the settings activity
	 */
	public void settings() {
		Intent intent = new Intent(this, ActivitySettings.class);
		startActivity(intent);
	}

	/**
	 * Inherited onCreateOptionsMenu activity, since we build the menu from XML
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.select_courses, menu);
		return true;
	}

}
