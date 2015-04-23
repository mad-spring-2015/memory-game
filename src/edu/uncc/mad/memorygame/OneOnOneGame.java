package edu.uncc.mad.memorygame;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

public class OneOnOneGame {
	private static final String ORDERING_DELIMITER = ",";
	private OneOnOneActivity activity;
	private int score = 0;
	private List<ParseObject> levels;
	private int currentLevel = 1;
	private String currentLevelOrdering ;
	private Random colorRandomizer = new Random();
	private String[] availableColors;
	private StringBuilder playerPattern;
	private boolean patternClickEnabled;

	public OneOnOneGame(OneOnOneActivity activity) {
		this.activity = activity;
		this.availableColors = activity.getResources().getStringArray(R.array.availableColors);
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
		startLevel(levels.get(currentLevel));
	}

	public void startLevel(ParseObject level) {
		playerPattern = new StringBuilder();
		patternClickEnabled = false;
		currentLevelOrdering = level.getString(getString(R.string.parse_field_1on1_level_ordering));
		draw(level);
	}

	private void draw(ParseObject level) {
		((TextView) activity.findViewById(R.id.textViewLevel)).setText("Level : " + currentLevel);
		((TextView) activity.findViewById(R.id.textViewScore)).setText("Score : " + score);
		GridView gv = (GridView) activity.findViewById(R.id.gridView1on1);
		int noOfComponents = level.getInt(getString(R.string.parse_field_1on1_level_noOfComp));
		gv.setNumColumns((int) Math.sqrt(noOfComponents));
		List<Circle> components = new ArrayList<Circle>();
		for (int i = 0; i < noOfComponents; i++) {
			components.add(new Circle(activity, getRandomColor()));
		}
		gv.setAdapter(new CircleAdapter(components));
		gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if (!patternClickEnabled) {
					return;
				}
				boolean resume = recordPattern(position);
				if (!resume) {
					//TODO end timer
					if (playerPattern.toString().equals(currentLevelOrdering)) {
						// you are good. got to next level
						gotoNextLevel();
					} else {
						levelFail();
					}
				}
			}
		});
	}

	protected void levelFail() {
		// TODO Auto-generated method stub
		
	}

	protected void gotoNextLevel() {
		//TODO calculate score
		this.currentLevel +=1;
		startLevel(this.levels.get(currentLevel));
		
	}

	private int getRandomColor() {
		int idx = this.colorRandomizer.nextInt(availableColors.length);
		String randomColor = availableColors[idx];
		int color, red, green, blue;
		color = (int) Long.parseLong(randomColor, 16);
		red = (color >> 16) & 0xFF;
		green = (color >> 8) & 0xFF;
		blue = (color >> 0) & 0xFF;
		return Color.rgb(red, green, blue);
	}

	/**
	 * Record the player pattern one click at a time
	 * 
	 * @param position
	 * @return whether to continue recording or not
	 */
	private boolean recordPattern(int position) {
		playerPattern.append((playerPattern.length() == 0) ? (position + "") : (ORDERING_DELIMITER + position));
		if (playerPattern.length() >= this.currentLevelOrdering.length()) {
			return false;
		}
		return true;
	}

	public void enableClicks() {
		this.patternClickEnabled = true;
	}

	public void disableClicks() {
		this.patternClickEnabled = false;
	}

	private String getString(int resId) {
		return activity.getString(resId);
	}
}
