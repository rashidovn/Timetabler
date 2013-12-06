package info.blakehawkins.timetabler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

import org.xmlpull.v1.XmlPullParserException;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;

/**
 * Activity for displaying the results built by the search function; searches
 * all courses for course names and acronyms that contain the search pattern.
 **/
public class ActivitySearchResults extends ActionBarActivity implements
		OnClickListener {
	private static final String CLASS_NAME = "ActivitySearchResults";

	/**
	 * Inherited method which builds the default content and handles the intent
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search_results);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		handleIntent(getIntent());
	}

	/**
	 * The intent should in theory be ACTION_SEARCH since it's the only way my
	 * app calls this activity.
	 */
	@Override
	protected void onNewIntent(Intent in) {
		handleIntent(getIntent());
	}

	/**
	 * Auxillary method used to buld the linearlayout contained in the scroll
	 * view after the intent has been recognized.
	 */
	private LinearLayout buildLinearLayout(Intent in)
			throws XmlPullParserException, IOException {
		ArrayList<Course> courses = XMLManager.getCourses(this);
		String qu = in.getStringExtra(SearchManager.QUERY);
		LinearLayout layout = new LinearLayout(this);
		layout.setOrientation(LinearLayout.VERTICAL);
		// Check each course in the list to see if it might match the search
		// string, and build the linearlayout accordingly
		for (Course c : courses) {
			boolean hasName = c.name.toLowerCase(Locale.getDefault()).contains(
					qu.toLowerCase(Locale.getDefault()));
			boolean hasAcronym = c.acronym.toLowerCase(Locale.getDefault()).contains(
					qu.toLowerCase(Locale.getDefault()));
			if (hasName || hasAcronym) {
				Button b = new Button(this);
				b.setTag(R.string.COURSE_TAG_ID, c.acronym);
				b.setOnClickListener(this);
				b.setText(c.name);
				layout.addView(b);
			}
		}
		return layout;
	}

	/**
	 * Now that we've verified the intent as ACTION_SEARCH, we build the new
	 * content view programmatically.
	 */
	private void handleIntent(Intent in) {
		if (Intent.ACTION_SEARCH.equals(in.getAction())) {
			try {
				ScrollView scroll = new ScrollView(this);
				scroll.addView(buildLinearLayout(in));
				setContentView(scroll);
			} catch (XmlPullParserException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
	}
	
	/**
	 * Inherited onClick method which starts ActivityCourseDetails to display
	 * data about the course chosen. Since search results don't return lecture
	 * data, the result will be an ActivityCourseDetails with only course data
	 */
	@Override
	public void onClick(View arg0) {
		Intent intent = new Intent(this, ActivityCourseDetails.class);
		Log.v(CLASS_NAME, "Starting ActivityCourseDetails without time data");
		intent.putExtra(ActivityMainViewer.COURSE_INTENT_KEY,
				(String) arg0.getTag(R.string.COURSE_TAG_ID));
		startActivity(intent);
	}
}
