package info.blakehawkins.timetabler;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class MainViewer extends ActionBarActivity {
	private static final String CLASS_NAME = "MainViewer";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_viewer);
		Log.v(CLASS_NAME, "Activity Created");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main_viewer, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_selCourses:
			selectCourses();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	public void selectCourses() {
		Intent intent = new Intent(this, SelectCourses.class);
		startActivity(intent);
	}

	public void onStart() {
		super.onStart();
		Log.v(CLASS_NAME,"onStart() occured.");
		XMLManager.verifyXMLIntegrity(this);
	}
}
