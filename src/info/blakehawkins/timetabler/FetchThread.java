package info.blakehawkins.timetabler;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.util.Log;
import android.widget.Toast;

/**
 * Here is a special class which extends Thread. It is its own class because we
 * need a custom constructor for it which retains its parent. The parent data is
 * necessary so we can call a Toast message after the fetching is complete.
 **/
public class FetchThread extends Thread implements Runnable {
	private static final String CLASS_NAME = "FetchThread",
			COURSES_NAME = "courses.xml", TIMETABLE_NAME = "timetable.xml",
			VENUES_NAME = "venues.xml";

	private ActivityMainViewer parent;

	/**
	 * Custom constructor which allows us to save parent data
	 **/
	public FetchThread(ActivityMainViewer parent) {
		this.parent = parent;
	}

	/**
	 * Method for requesting data from a URI and then overwriting it on the
	 * local device.
	 */
	private void overwriteFile(String file) {
		InputStream stream = null;
		try {
			String address = PreferencesManager.getXmlUri(parent
					.getApplicationContext()) + file;
			URL url = new URL(address);
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setReadTimeout(10000);
			con.setConnectTimeout(15000);
			con.setRequestMethod("GET");
			con.setDoInput(true);
			con.connect();
			stream = con.getInputStream();
			XMLManager.overwriteXml(parent, stream, file);
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

	/**
	 * Implemented run method, which requests an overwrite of all 3 xml files
	 * and then sends a toast to the parent UI afterwards.
	 */
	@Override
	public void run() {
		// Overwrite each file
		overwriteFile(COURSES_NAME);
		overwriteFile(TIMETABLE_NAME);
		overwriteFile(VENUES_NAME);

		// Log afterwards, and write a toast for the client to see.
		parent.runOnUiThread(new Runnable() {
			public void run() {
				Toast.makeText(
						parent.getApplicationContext(),
						parent.getString(R.string.toast_xml_fetched_remote_text),
						Toast.LENGTH_LONG).show();
				parent.refreshActivityContents();
			}
		});
		Log.v(CLASS_NAME, "Asynchronous XML requests finished.");
	}
}
