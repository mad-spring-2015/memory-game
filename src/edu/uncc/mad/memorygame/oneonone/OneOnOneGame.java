package edu.uncc.mad.memorygame.oneonone;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.Timer;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.graphics.Color;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import edu.uncc.mad.memorygame.CircleAdapter;
import edu.uncc.mad.memorygame.MemoryGame;
import edu.uncc.mad.memorygame.R;
import edu.uncc.mad.memorygame.Score;
import edu.uncc.mad.memorygame.ui.Circle;

public class OneOnOneGame {
	private static final int GAME_SYNC_INTERVAL = 5000;
	private static final String ORDERING_DELIMITER = ",";
	private OneOnOneActivity activity;
	private int score = 0;
	private List<ParseObject> levels;
	private int currentLevel = 0;
	private String currentLevelOrdering;
	private Random colorRandomizer = new Random();
	private String[] availableColors;
	private StringBuilder playerPattern;
	private boolean patternClickEnabled;
	private Date levelStartTime;
	private Date levelEndTime;
	private List<Circle> components;
	private TextView meScoreView;
	private String meScore;
	private String meStatus;
	private String opScore;
	private String opStatus;
	private String meUser;
	private String opUser;
	private TextView opScoreView;
	private static OneOnOneGame instance;
	private Timer gameSync;

	public ParseObject getGameInstance() {
		return activity.gameInstance;
	}

	public static OneOnOneGame getCurrentGame() {
		return instance;
	}

	public OneOnOneGame(OneOnOneActivity activity) {
		this.activity = activity;
		this.availableColors = activity.getResources().getStringArray(R.array.availableColors);
		this.meScoreView = (TextView) activity.findViewById(R.id.textViewMeScore);
		this.opScoreView = (TextView) activity.findViewById(R.id.textViewOpponentScore);
		instance = this;

	}

	public void init() {

		if (!activity.isMeInitiator) {
			meScore = getString(R.string.parse_field_1on1_game_scoreB);
			meUser = getString(R.string.parse_field_1on1_game_userB);
			meStatus = getString(R.string.parse_field_1on1_game_statusB);
			opScore = getString(R.string.parse_field_1on1_game_scoreA);
			opUser = getString(R.string.parse_field_1on1_game_userA);
			opStatus = getString(R.string.parse_field_1on1_game_statusA);
		} else {
			meScore = getString(R.string.parse_field_1on1_game_scoreA);
			meUser = getString(R.string.parse_field_1on1_game_userA);
			meStatus = getString(R.string.parse_field_1on1_game_statusA);
			opScore = getString(R.string.parse_field_1on1_game_scoreB);
			opUser = getString(R.string.parse_field_1on1_game_userB);
			opStatus = getString(R.string.parse_field_1on1_game_statusB);
		}
		GameInstanceHandler.updateMyGameStatus(OneOnOneGameStatus.PLAYING);
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

	public String getOpScore() {
		return opScore;
	}

	public String getOpStatus() {
		return opStatus;
	}

	/**
	 * Wrapper for all work that needs to be done post initialization
	 */
	public void postInit() {
		startLevel(levels.get(currentLevel));
		GameSyncTask gameSyncTask = new GameSyncTask();
		gameSync = new Timer("GameSyncTask");
		gameSync.scheduleAtFixedRate(gameSyncTask, 0, GAME_SYNC_INTERVAL);
	}

	public void startLevel(ParseObject level) {
		playerPattern = new StringBuilder();
		patternClickEnabled = false;
		currentLevelOrdering = level.getString(getString(R.string.parse_field_1on1_level_ordering));
		draw(level);
		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				playPattern();
			}
		}, 1000);
	}

	private void draw(ParseObject level) {
		((TextView) activity.findViewById(R.id.textViewMeScore)).setText("" + score);
		GridView gv = (GridView) activity.findViewById(R.id.gridView1on1);
		int noOfComponents = level.getInt(getString(R.string.parse_field_1on1_level_noOfComp));
		gv.setNumColumns((int) Math.sqrt(noOfComponents));
		components = new ArrayList<Circle>();
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
					levelEndTime = new Date();
					if (playerPattern.toString().equals(currentLevelOrdering)) {
						gotoNextLevel();
					} else {
						levelFail();
					}
				}
			}
		});
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
				levelStartTime = new Date();
			}

			@Override
			public void onAnimationCancel(Animator animation) {
				// TODO Auto-generated method stub

			}
		});
		List<Animator> animators = new ArrayList<Animator>();
		for (String sIdx : currentLevelOrdering.split(ORDERING_DELIMITER)) {
			int idx = Integer.valueOf(sIdx);
			ObjectAnimator animator = ObjectAnimator.ofInt(components.get(idx), "transparency", Circle.DEFAULT_ALPHA,
					255);
			animator.setRepeatCount(1);
			animator.setRepeatMode(Animation.REVERSE);
			int defaultSpeed = levels.get(currentLevel).getInt(getString(R.string.parse_field_1on1_level_speed));
			animator.setDuration(defaultSpeed);
			animators.add(animator);
		}
		animatorSet.playSequentially(animators);
		animatorSet.start();
	}

	/**
	 * Called when player fails to complete the current level
	 */
	protected void levelFail() {
		GameInstanceHandler.updateMyGameStatusAndShowMsg(OneOnOneGameStatus.DONE);
	}

	protected void gotoNextLevel() {
		score += Score.calcOneOnOneScore(levelEndTime.getTime() - levelStartTime.getTime(), levels.get(currentLevel)
				.getInt(getString(R.string.parse_field_1on1_level_points)));
		meScoreView.setText(score + "");
		// Save score in game instance
		GameInstanceHandler.saveScore(score);
		this.currentLevel += 1;
		if (this.currentLevel == levels.size()) {
			gameFinish();
		} else {
			startLevel(this.levels.get(currentLevel));
		}

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

	public void pauseGame() {
		gameSync.cancel();
	}

	public void resumeGame() {
		GameSyncTask gameSyncTask = new GameSyncTask();
		gameSync.scheduleAtFixedRate(gameSyncTask, 0, GAME_SYNC_INTERVAL);
	}

	public void gameFinish() {
		if (isPlaying()) {
			gameForfeit();
		}

	}

	public void gameForfeit() {
		GameInstanceHandler.updateMyGameStatus(OneOnOneGameStatus.EXITED);
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

	public boolean isPlaying() {
		if (currentLevel <= levels.size()) {
			return true;
		}
		return false;
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

	public TextView getScoreView() {
		return meScoreView;
	}

	/**
	 * String for the field that represents current player's score
	 * 
	 * @return
	 */
	public String getMeScore() {
		return meScore;
	}

	public String getMeStatus() {
		return meStatus;
	}

	public OneOnOneActivity getActivity() {
		return activity;
	}

	public TextView getOpScoreView() {
		return opScoreView;
	}
}
