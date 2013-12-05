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

public class ActivitySelectCourses extends ActionBarActivity {
	private static final String CLASS_NAME = "ActivitySelectCourses";

	private ArrayList<Pair<CheckBox, Course>> cbcs = null;

	@Override
	public void onPause() {
		super.onPause();
		for (Pair<CheckBox, Course> p : this.cbcs) {
			PreferencesManager.setCourseEnabled(this, p.second,
					p.first.isChecked());
		}
	}

	private void refreshActivityContents() {
		cbcs = new ArrayList<Pair<CheckBox, Course>>();
		// Fill activity with checkboxes that correspond to courses
		ArrayList<CheckBox> boxes = new ArrayList<CheckBox>();
		Log.v(CLASS_NAME, "Getting filter states");
		ArrayList<Boolean> filters = PreferencesManager.getFilterStates(this);
		Log.v(CLASS_NAME, "Filter states gotten");
		boolean[] years = new boolean[5];
		for (int x = 0; x < 5; x++) {
			years[x] = filters.get(x);
		}
		boolean[] sems = new boolean[2];
		sems[0] = filters.get(5);
		sems[1] = filters.get(6);
		try {
			ArrayList<Course> names = XMLManager.getCourses(this);
			Log.v(CLASS_NAME, String.valueOf(names.size()) + " courses gotten");
			for (Course q : names) {
				Log.v(CLASS_NAME, "Checking year " + String.valueOf(q.year - 1)
						+ " and semester " + String.valueOf(q.semester - 1));
				// To make course filtering as INCLUSIVE as possible, uncomment
				// lines 58 and 60-63, and comment line 59. Warning: LAG
				// Lecture l = XMLManager.getLectureFromAcronym(this,
				// q.acronym);
				if (years[q.year - 1]) {
					// if (l!=null && ((years[0] && l.year1) || (years[1] &&
					// l.year2)
					// || (years[2] && l.year3) || (years[3] && l.year4)
					// || (years[4] && l.year5))) {
					if (q.semester == -1 || sems[q.semester - 1]) {
						CheckBox c = new CheckBox(getApplicationContext());
						Pair<CheckBox, Course> p = new Pair<CheckBox, Course>(
								c, q);
						this.cbcs.add(p);
						c.setText(q.name);
						c.setTextColor(Color.BLACK);
						c.setChecked(PreferencesManager
								.isCourseEnabled(this, q));
						boxes.add(c);
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		}
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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_select_courses);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		refreshActivityContents();
	}

	@Override
	protected void onStart() {
		super.onStart();
		refreshActivityContents();
	}

	private void filterCourses() {
		Intent i = new Intent(this, ActivityFilterCourses.class);
		startActivity(i);
	}

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

	public void settings() {
		Intent intent = new Intent(this, ActivitySettings.class);
		startActivity(intent);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.select_courses, menu);
		return true;
	}

}
