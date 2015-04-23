package edu.uncc.mad.memorygame;

import java.util.List;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import android.app.Activity;
import android.util.Log;

public class OneOnOneGame {
	private OneOnOneActivity activity;
	private int score;
	private List<ParseObject> levels;
	private int currentLevel = 1;

	public OneOnOneGame(OneOnOneActivity activity) {
		this.activity = activity;
	}

	public void init() {
		
		ParseQuery<ParseObject> levelQuery = ParseQuery.getQuery(getString(R.string.parse_class_1on1_level));
		levelQuery.findInBackground(new FindCallback<ParseObject>() {

			@Override
			public void done(List<ParseObject> levels, ParseException e) {
				if (levels != null) {
					OneOnOneGame.this.levels = levels;
					postInit();
				} else {
					Log.d(MemoryGame.LOGGING_KEY, "query level dint work", e);
				}
			}
		});
	}

	/**
	 * Wrapper for all work that needs to be done post initialization
	 */
	public void postInit() {

	}

	private String getString(int resId) {
		return activity.getString(resId);
	}
}
