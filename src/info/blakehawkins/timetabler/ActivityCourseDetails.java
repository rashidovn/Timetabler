package info.blakehawkins.timetabler;

import java.io.IOException;

import org.xmlpull.v1.XmlPullParserException;

import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
//import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ScrollView;
import android.widget.TextView;

/**
 * Acitivity class which contains course details. Course details can be
 * requested by the main viewer via a specific lecture (in which case the course
 * details will include infomation about that lecture), or via the search
 * function, in which case only the broad course details will be included.
 **/
public class ActivityCourseDetails extends ActionBarActivity implements
		Button.OnClickListener {
	private static final String CLASS_NAME = "ActivityCourseDetails";

	/**
	 * Inherited onCreate method which builds the primary viewgroup
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Enable 'home up' characteristic
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		// Get the course acronym and lecture start time, supplied by the main
		// viewer as extra data in the intent. Note that the start time data
		// provided may be 0 (no data).
		Intent i = getIntent();
		String acnm = i.getStringExtra(ActivityMainViewer.COURSE_INTENT_KEY);
		Integer sTime = i.getIntExtra(ActivityMainViewer.TIME_INTENT_KEY, 0);
		Log.v(CLASS_NAME, "Acronym gotten: " + acnm + "; start time gotten: "
				+ String.valueOf(sTime));

		// Build new content view from intent's data:
		try {
			// If the XML data is malformed, there may be a lecture which has no
			// course associated with it, in which case c will be null (we check
			// for this later, just in case :) )
			Course c = XMLManager.getCourseFromAcronym(this, acnm);

			// If the start time provided is "real" information, we also get
			// a lecture with which to build the content.
			Lecture l = null;
			if (sTime != 0) {
				l = XMLManager.getLectureFromAcronymTimePair(this, acnm, sTime);
				Log.v(CLASS_NAME, "Lecture data gotten");
			}
			// Provide both the course and lecture to the build method, which
			// will check the contents of l before proceeding.
			setContentView(buildScrollView(c, l));
		} catch (IOException e) {
			e.printStackTrace();
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Method used for building the main scroll view in the activity
	 **/
	private View buildScrollView(Course c, Lecture l) {
		ScrollView scroll = new ScrollView(this);
		scroll.addView(buildLinearLayout(c, l));
		return scroll;
	}

	/**
	 * Auxillary method for building a simple TextView
	 **/
	private View buildSimpleTextView(String text) {
		TextView t = new TextView(this);
		t.setText(text);
		return t;
	}

	/**
	 * Auxillary method for building a simple Buton (View) The click listener is
	 * also applied by default for all buttons (!)
	 **/
	private View buildSimpleButton(String text, Object tag) {
		Button b = new Button(this);
		b.setText(text);
		b.setTag(tag);
		b.setOnClickListener(this);
		return b;
	}

	/**
	 * Auxillary method used to build a textview which displays the applicable
	 * years, given the relevent lecture, since Timetable contains further year
	 * data than Course does.
	 **/
	private View buildApplicableYearsTextView(Lecture l) {
		Resources res = getResources();
		String yrs = "";
		if (l.year1) {
			yrs = yrs.concat("[" + res.getString(R.string.yr1) + "]");
		}
		if (l.year2) {
			yrs = yrs.concat("[" + res.getString(R.string.yr2) + "]");
		}
		if (l.year3) {
			yrs = yrs.concat("[" + res.getString(R.string.yr3) + "]");
		}
		if (l.year4) {
			yrs = yrs.concat("[" + res.getString(R.string.yr4) + "]");
		}
		if (l.year5) {
			yrs = yrs.concat("[" + res.getString(R.string.yr5) + "]");
		}
		return buildSimpleTextView(res.getString(R.string.applicable_years)
				+ ": " + yrs);
	}

	/**
	 * Auxillary method used to build a textview which displays the applicable
	 * departments (parts of school of informatics), given the relevent course
	 **/
	private View buildApplicableDepartmentTextView(Course c) {
		Resources res = getResources();
		String deps = "";
		if (c.ai) {
			deps = deps.concat("[" + res.getString(R.string.ai) + "]");
		}
		if (c.cg) {
			deps = deps.concat("[" + res.getString(R.string.cg) + "]");
		}
		if (c.se) {
			deps = deps.concat("[" + res.getString(R.string.se) + "]");
		}
		if (c.cs) {
			deps = deps.concat("[" + res.getString(R.string.cs) + "]");
		}
		return buildSimpleTextView(res.getString(R.string.apdep) + ": " + deps);
	}

	/**
	 * Builds the linear layout which contains all the contents of the Course
	 * Details activity. The LinearLayout is returned to the parent ScrollView.
	 */
	@SuppressWarnings("deprecation")
	private View buildLinearLayout(Course c, Lecture l) {
		// Get Resources object to a handle to improve readability
		Resources res = getResources();

		// Initialize layout
		LinearLayout layout = new LinearLayout(this);
		LayoutParams standardParams = new LinearLayout.LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		layout.setLayoutParams(standardParams);
		layout.setOrientation(LinearLayout.VERTICAL);

		// Add coursename - we check if c is null or not in case of an unusual
		// XML malformation
		if (c != null) {
			layout.addView(buildSimpleTextView(c.name));
		} else {
			layout.addView(buildSimpleTextView(res
					.getString(R.string.no_course_name)));
		}
		try {
			// Fetch venue data if a lecture was given. Add auxillary views only
			// if lecture/venue data is known
			Venue ven = null;
			if (l != null) {
				ven = XMLManager.getVenueFromBuilding(this, l.building);

				// Add location view
				layout.addView(buildSimpleTextView(ven.description));

				// Add room view
				layout.addView(buildSimpleTextView(res.getString(R.string.room)
						+ " " + l.room));

				// Add applicable years view
				layout.addView(buildApplicableYearsTextView(l));
			}

			if (c != null) {
				// Add EUCLID view
				layout.addView(buildSimpleTextView(res
						.getString(R.string.euclid) + ": " + c.euclid));

				// Add Applicable Department View
				layout.addView(buildApplicableDepartmentTextView(c));

				// Add Course Level view
				layout.addView(buildSimpleTextView(res
						.getString(R.string.course_level) + ": " + c.level));

				// Add Points view
				layout.addView(buildSimpleTextView(res.getString(R.string.pts)
						+ ": " + c.points));

				// Add Lecturer view
				layout.addView(buildSimpleTextView(res.getString(R.string.lec)
						+ ": " + c.lecturer));

				// Course Webpage Button
				layout.addView(buildSimpleButton(
						res.getString(R.string.course_webpage), c.url));
			}

			// We check for venue again down here to keep buttons together
			if (ven != null) {
				// Add Map button
				layout.addView(buildSimpleButton(
						res.getString(R.string.view_map), ven.mapUri));
			}

			// Add DRPS link button
			if (c != null) {
				layout.addView(buildSimpleButton(res.getString(R.string.drps),
						c.drps));
			}

			// Add Checkbox to include in Timetable
			if (c != null) {
				CheckBox check = new CheckBox(this);
				check.setText(res.getString(R.string.show_in_timetabler));
				check.setChecked(PreferencesManager.isCourseEnabled(this, c));
				check.setOnClickListener(this);
				check.setTag(c.acronym);
				layout.addView(check);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		}
		return layout;
	}

	/**
	 * Click handler for both buttons and checkboxes. Sends the client to the
	 * web browser if they clicked a button, and sends a message to the
	 * preferences manager if they clicked a checkbox.
	 **/
	@Override
	public void onClick(View arg0) {
		// In both cases, arg0's tag is a string
		String s = (String) arg0.getTag();

		// If the string is an HTTP address, we open it in the browser.
		// Otherwise, we enable/disable the course from view in Timetabler.
		if (s.startsWith("http://") || s.startsWith("https://")) {
			Intent browserInt = new Intent(Intent.ACTION_VIEW,
					Uri.parse((String) arg0.getTag()));
			startActivity(browserInt);
		} else {
			try {
				Course c = XMLManager.getCourseFromAcronym(this, s);
				PreferencesManager.setCourseEnabled(this, c,
						((CheckBox) arg0).isChecked());
			} catch (IOException e) {
				e.printStackTrace();
			} catch (XmlPullParserException e) {
				e.printStackTrace();
			}

		}
	}
}
