package edu.uncc.mad.memorygame;

import java.util.List;

import android.content.Context;
import android.util.Log;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

public class Game {
	private String ordering;
	private int noOfComponents;
	private int defaultSpeed;
	private Context context;
	
	public Game(Context context) {
		super();
		this.context = context;
	}
	public void init(){
		ParseQuery<ParseObject> levelQuery = ParseQuery.getQuery(getString(R.string.parse_class_level));
		String key = getString(R.string.parse_field_level_levelno);
		levelQuery.whereEqualTo(key, getLevelNo());
		levelQuery.findInBackground(new FindCallback<ParseObject>() {
			
			@Override
			public void done(List<ParseObject> level, ParseException e) {
				if(level != null){
					
				} else{
					Log.d(MemoryGame.LOGGING_KEY, e.getMessage());
				}
			}
		});
	}
	public void draw() {
		
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
	private String getString(int resId){
		return context.getString(resId);
	}
	public int getLevelNo() {
		return ParseUser.getCurrentUser().getInt(getString(R.string.parse_field_user_level));
	}
	public int getScore(){
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
