package edu.uncc.mad.memorygame.oneonone;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import edu.uncc.mad.memorygame.MemoryGame;
import edu.uncc.mad.memorygame.R;

public class OneOnOneActivity extends Activity {

	boolean isMeInitiator;
	String gameInstanceId;
	ParseUser opponent;
	ParseObject gameInstance;
	OneOnOneGame game;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_one_on_one);
		Intent intent = getIntent();
		String opponentUsername = intent.getStringExtra("opponent");
		if (opponentUsername == null) {
			Log.e(MemoryGame.LOGGING_KEY, "No opponent");
			return;
		}
		game = new OneOnOneGame(this);

		this.isMeInitiator = intent.getBooleanExtra("isMeInitiator", false);
		this.gameInstanceId = intent.getStringExtra("gameInstanceID");
		ParseQuery<ParseObject> gameInstanceQuery = ParseQuery.getQuery(getString(R.string.parse_class_1on1_game));
		gameInstanceQuery.whereEqualTo("objectId", gameInstanceId);
		final String userToInclude = isMeInitiator ? getString(R.string.parse_field_1on1_game_userB)
				: getString(R.string.parse_field_1on1_game_userA);
		gameInstanceQuery.include(userToInclude);
		gameInstanceQuery.findInBackground(new FindCallback<ParseObject>() {

			@Override
			public void done(List<ParseObject> objects, ParseException e) {
				if (e == null) {
					gameInstance = objects.get(0);
					opponent = gameInstance.getParseUser(userToInclude);
					game.init();
				} else {
					Log.e(MemoryGame.LOGGING_KEY, "error fetching game instance", e);
					return;
				}

			}
		});
		/*
		 * ParseQuery<ParseUser> opponentQuery = ParseUser.getQuery();
		 * opponentQuery.whereEqualTo("username", opponent);
		 * opponentQuery.findInBackground(new FindCallback<ParseUser>() {
		 * 
		 * @Override public void done(List<ParseUser> users, ParseException e) {
		 * if (e != null) { Log.e(MemoryGame.LOGGING_KEY,
		 * "error fetching opponent", e); return; } opponent = users.get(0); //
		 * TODO start game } });
		 */
		((TextView) findViewById(R.id.textViewOpponentTxt)).setText(opponentUsername);
	}
}
