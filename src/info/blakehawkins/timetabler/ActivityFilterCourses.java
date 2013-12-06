package info.blakehawkins.timetabler;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.CheckBox;

/**
 * This is the filter courses activity, which can be opened via the Select
 * Courses activity. Filter courses is a statically built layout with checkboxes
 * for the various semesters and years stored in the Course data. By selecting
 * these filters, the Select Courses activity will change its contents.
 **/
public class ActivityFilterCourses extends ActionBarActivity {
	/**
	 * Inherited onPause method in which we save filter checkbox states using
	 * the PreferencesManager class
	 **/
	@Override
	protected void onPause() {
		super.onPause();
		CheckBox cbYear1 = (CheckBox) findViewById(R.id.year1);
		CheckBox cbYear2 = (CheckBox) findViewById(R.id.year2);
		CheckBox cbYear3 = (CheckBox) findViewById(R.id.year3);
		CheckBox cbYear4 = (CheckBox) findViewById(R.id.year4);
		CheckBox cbYear5 = (CheckBox) findViewById(R.id.year5);
		CheckBox cbSems1 = (CheckBox) findViewById(R.id.semester1);
		CheckBox cbSems2 = (CheckBox) findViewById(R.id.semester2);
		PreferencesManager.saveFilterStates(this, cbYear1.isChecked(),
				cbYear2.isChecked(), cbYear3.isChecked(), cbYear4.isChecked(),
				cbYear5.isChecked(), cbSems1.isChecked(), cbSems2.isChecked());
	}

	/**
	 * Inherited onCreate method; produces content statically using filter
	 * courses XML file; only fills in checkboxes programmatically (using
	 * preferences manager)
	 **/
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_filter_courses);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		// Set checkbox checked property by requesting filter states from
		// PreferencesManager class
		CheckBox cbYear1 = (CheckBox) findViewById(R.id.year1);
		CheckBox cbYear2 = (CheckBox) findViewById(R.id.year2);
		CheckBox cbYear3 = (CheckBox) findViewById(R.id.year3);
		CheckBox cbYear4 = (CheckBox) findViewById(R.id.year4);
		CheckBox cbYear5 = (CheckBox) findViewById(R.id.year5);
		CheckBox cbSems1 = (CheckBox) findViewById(R.id.semester1);
		CheckBox cbSems2 = (CheckBox) findViewById(R.id.semester2);
		cbYear1.setChecked(PreferencesManager.getFilterState(this, "y1"));
		cbYear2.setChecked(PreferencesManager.getFilterState(this, "y2"));
		cbYear3.setChecked(PreferencesManager.getFilterState(this, "y3"));
		cbYear4.setChecked(PreferencesManager.getFilterState(this, "y4"));
		cbYear5.setChecked(PreferencesManager.getFilterState(this, "y5"));
		cbSems1.setChecked(PreferencesManager.getFilterState(this, "sem1"));
		cbSems2.setChecked(PreferencesManager.getFilterState(this, "sem2"));
	}
}
