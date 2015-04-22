/**
 * Ajay Vijayakumaran Nair
 * Ayang
 * Nachiket Doke
 */
package edu.uncc.mad.memorygame;

import android.app.Application;
import android.util.Log;

import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParsePush;
import com.parse.SaveCallback;

public class ParseApplication extends Application {

	@Override
	public void onCreate() {
		super.onCreate();

		// Initialize Crash Reporting.
		// ParseCrashReporting.enable(this);

		// Enable Local Datastore.
		// Parse.enableLocalDatastore(this);

		// Add your initialization code here
		Parse.initialize(this, "lahvjzArnTbevwRyKc34GHo6PnzSEu5xM0Z9zxVt", "OvCSe0djd6Y2mEyHB6JzhlXgOOgOcde2IZzmS5CB");

		// ParseUser.enableAutomaticUser();
		ParseACL defaultACL = new ParseACL();
		// Optionally enable public read access.
		// defaultACL.setPublicReadAccess(true);
		// ParseUser.getCurrentUser().saveInBackground();
		ParseACL.setDefaultACL(defaultACL, true);

		ParsePush.subscribeInBackground("", new SaveCallback() {
			@Override
			public void done(ParseException e) {
				if (e == null) {
					Log.d(MemoryGame.LOGGING_KEY, "successfully subscribed to the broadcast channel.");
				} else {
					Log.e(MemoryGame.LOGGING_KEY, "failed to subscribe for push", e);
				}
			}
		});
	}
}
