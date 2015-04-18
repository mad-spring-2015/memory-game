package edu.uncc.mad.memorygame;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import android.app.Activity;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

public class Game {
	private String ordering;
	private int noOfComponents;
	private int defaultSpeed;
	private Activity activity;
	private List<Circle> components = new ArrayList<Circle>();
	private String[] availableColors;
	private Random colorRandomizer = new Random();

	public Game(Activity activity) {
		super();
		this.activity = activity;
		this.availableColors = activity.getResources().getStringArray(R.array.availableColors);
	}

	/**
	 * Initialize level details. On success, the game variables will be properly
	 * initialized
	 */
	public void init() {
		ParseQuery<ParseObject> levelQuery = ParseQuery.getQuery(getString(R.string.parse_class_level));
		String key = getString(R.string.parse_field_level_levelno);
		levelQuery.whereEqualTo(key, getLevelNo());
		levelQuery.findInBackground(new FindCallback<ParseObject>() {

			@Override
			public void done(List<ParseObject> levels, ParseException e) {
				if (levels != null) {
					ParseObject level = levels.get(0);
					Game.this.ordering = level.getString(getString(R.string.parse_field_level_ordering));
					Game.this.noOfComponents = level.getInt(getString(R.string.parse_field_level_noOfComp));
					Game.this.defaultSpeed = level.getInt(getString(R.string.parse_field_level_speed));
					postInit();
				} else {
					Log.d(MemoryGame.LOGGING_KEY, "query level dint work", e);
				}
			}
		});
	}

	public void postInit() {
		draw();
	}

	public void draw() {
		((TextView) activity.findViewById(R.id.textViewLevel)).setText("Level : " + getLevelNo());
		((TextView) activity.findViewById(R.id.textViewScore)).setText("Score : " + getScore());
		GridView gv = (GridView) activity.findViewById(R.id.gridViewGame);
		gv.setNumColumns((int) Math.sqrt(this.noOfComponents));
		for (int i = 0; i < this.noOfComponents; i++) {
			this.components.add(new Circle(activity, getRandomColor()));
		}
		gv.setAdapter(new CircleAdapter(components));
		gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Toast.makeText(activity, ((Circle)view).getColor() + "", Toast.LENGTH_SHORT).show();
			}
		});
	}

	private int getRandomColor(){
		int idx = this.colorRandomizer.nextInt(availableColors.length);
		String randomColor = availableColors[idx];
		int color, red, green, blue;
		color = (int) Long.parseLong(randomColor, 16);
		red = (color >> 16 ) & 0xFF;
		green = (color >> 8) & 0xFF;
		blue = (color >> 0) & 0xFF;
		return Color.rgb(red, green, blue);
	}

	public void showScore() {

	}

	public void startTimer() {

	}

	public void enableClicks() {

	}

	public void disableClicks() {

	}

	public void saveScore() {

	}

	public void playPattern() {

	}

	private String getString(int resId) {
		return activity.getString(resId);
	}

	public int getLevelNo() {
		return ParseUser.getCurrentUser().getInt(getString(R.string.parse_field_user_level));
	}

	public int getScore() {
		return ParseUser.getCurrentUser().getInt(getString(R.string.parse_field_user_score));
	}

	public String getOrdering() {
		return ordering;
	}

	public void setOrdering(String ordering) {
		this.ordering = ordering;
	}

	public int getNoOfComponents() {
		return noOfComponents;
	}

	public void setNoOfComponents(int noOfComponents) {
		this.noOfComponents = noOfComponents;
	}

	public int getDefaultSpeed() {
		return defaultSpeed;
	}

	public void setDefaultSpeed(int defaultSpeed) {
		this.defaultSpeed = defaultSpeed;
	}

}
