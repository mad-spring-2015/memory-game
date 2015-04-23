package edu.uncc.mad.memorygame;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

public class OneOnOneActivity extends Activity {

	boolean isMeInitiator;
	String gameInstanceId;
	ParseUser opponent;

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
		this.isMeInitiator = intent.getBooleanExtra("isMeInitiator", false);
		this.gameInstanceId = intent.getStringExtra("gameInstanceID");
		ParseQuery<ParseUser> opponentQuery = ParseUser.getQuery();
		opponentQuery.whereEqualTo("username", opponent);
		opponentQuery.findInBackground(new FindCallback<ParseUser>() {

			@Override
			public void done(List<ParseUser> users, ParseException e) {
				if (e != null) {
					Log.e(MemoryGame.LOGGING_KEY, "error fetching opponent", e);
					return;
				}
				opponent = users.get(0);
				// TODO start game
			}
		});
		((TextView) findViewById(R.id.textViewOpponentTxt)).setText(opponentUsername);
	}
}
