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

public class ActivitySearchResults extends ActionBarActivity implements
		OnClickListener {
	private static final String CLASS_NAME = "ActivitySearchResults";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search_results);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		handleIntent(getIntent());
	}

	@Override
	protected void onNewIntent(Intent in) {
		handleIntent(getIntent());
	}

	private void handleIntent(Intent in) {
		if (Intent.ACTION_SEARCH.equals(in.getAction())) {
			try {
				ArrayList<Course> courses = XMLManager.getCourses(this);
				String qu = in.getStringExtra(SearchManager.QUERY);
				ScrollView scroll = new ScrollView(this);
				LinearLayout layout = new LinearLayout(this);
				layout.setOrientation(LinearLayout.VERTICAL);
				for (Course c : courses) {
					if (c.name.toLowerCase(Locale.getDefault()).contains(
							qu.toLowerCase(Locale.getDefault()))
							|| c.acronym
									.toLowerCase(Locale.getDefault())
									.contains(
											qu.toLowerCase(Locale.getDefault()))) {
						Button b = new Button(this);
						b.setTag(R.string.COURSE_TAG_ID, c.acronym);
						b.setOnClickListener(this);
						b.setText(c.name);
						layout.addView(b);
					}
				}
				scroll.addView(layout);
				setContentView(scroll);
			} catch (XmlPullParserException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
	}

	@Override
	public void onClick(View arg0) {
		Intent intent = new Intent(this, ActivityCourseDetails.class);
		Log.v(CLASS_NAME,"Starting ActivityCourseDetails without time data");
		intent.putExtra(ActivityMainViewer.COURSE_INTENT_KEY,
				(String) arg0.getTag(R.string.COURSE_TAG_ID));
		startActivity(intent);
	}
}
