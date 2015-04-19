package edu.uncc.mad.memorygame;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

public class Game {
	private static final String ORDERING_DELIMITER = ",";
	private String ordering;
	private int noOfComponents;
	private int defaultSpeed;
	private Activity activity;
	private List<Circle> components = new ArrayList<Circle>();
	private String[] availableColors;
	private Random colorRandomizer = new Random();
	private StringBuilder playerPattern = new StringBuilder();
	private boolean patternClickEnabled = false;
	/**
	 * Maximum allowed time for current level
	 */
	private long timeinMillSec;
	/**
	 * Holds the maximum points possible for the level Any user, practically,
	 * will not be able to score maximum
	 */
	private long points;
	private Timer timer;

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
					Game.this.timeinMillSec = level.getInt(getString(R.string.parse_field_level_time)) * 1000;
					Game.this.points = level.getInt(getString(R.string.parse_field_level_points));
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
		timer = new Timer(activity, timeinMillSec);
		draw();
	}

	/**
	 * Draws the game screen based on level
	 */
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
				if (!patternClickEnabled) {
					return;
				}
				boolean resume = recordPattern(position);
				if (!resume) {
					timer.endTimer();
					if (playerPattern.toString().equals(ordering)) {
						// you are good. got to next level
						gotoNextLevel();
					} else {
						restartLevel();
					}
				}
			}
		});
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
		if (playerPattern.length() >= this.ordering.length()) {
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

	public void playPattern() {
		AnimatorSet animatorSet = new AnimatorSet();
		animatorSet.addListener(new AnimatorListener() {

			@Override
			public void onAnimationStart(Animator animation) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationRepeat(Animator animation) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationEnd(Animator animation) {
				enableClicks();
				timer.startTimer();
			}

			@Override
			public void onAnimationCancel(Animator animation) {
				// TODO Auto-generated method stub

			}
		});
		List<Animator> animators = new ArrayList<Animator>();
		for (String sIdx : ordering.split(ORDERING_DELIMITER)) {
			int idx = Integer.valueOf(sIdx);
			ObjectAnimator animator = ObjectAnimator.ofInt(components.get(idx), "transparency", Circle.DEFAULT_ALPHA, 255);
			animator.setRepeatCount(1);
			animator.setRepeatMode(Animation.REVERSE);
			animator.setDuration(defaultSpeed);
			animators.add(animator);
		}
		animatorSet.playSequentially(animators);
		animatorSet.start();
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

	/**
	 * Starts the game with the animations
	 */
	public void begin() {
		activity.findViewById(R.id.buttonPlay).setEnabled(false);
		playPattern();
	}

	/**
	 * Stop all game related functionalities here
	 */
	public void end(){
		timer.endTimer();
	}
	private void gotoNextLevel() {
		ParseUser user = ParseUser.getCurrentUser();
		int currentLevel = getLevelNo();
		int currentScore = getScore();
		user.put(getString(R.string.parse_field_user_level), currentLevel + 1);
		user.put(getString(R.string.parse_field_user_score),
				currentScore + Score.calcScore( timer.getTime(), this.timeinMillSec, this.points));
		user.saveInBackground(new SaveCallback() {

			@Override
			public void done(ParseException arg0) {
				((Button) activity.findViewById(R.id.buttonPlay)).setVisibility(View.GONE);
				((Button) activity.findViewById(R.id.buttonRestart)).setVisibility(View.VISIBLE);
			}
		});
		Toast.makeText(activity, "yo are good", Toast.LENGTH_SHORT).show();

	}

	private void restartLevel() {
		((Button) activity.findViewById(R.id.buttonPlay)).setVisibility(View.GONE);
		Button btn = (Button) activity.findViewById(R.id.buttonRestart);
		btn.setText(getString(R.string.btn_txt_retry));
		btn.setVisibility(View.VISIBLE);
	}
}
