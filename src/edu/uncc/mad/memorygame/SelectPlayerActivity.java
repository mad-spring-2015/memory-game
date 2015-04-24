package edu.uncc.mad.memorygame;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SendCallback;

import edu.uncc.mad.memorygame.oneonone.OneOnOneActivity;

public class SelectPlayerActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_select_player);
	}

	public void selectPlayerBtnClicked(View view) {
		TextView tv = (TextView) findViewById(R.id.editTextSelectPlayer);
		final String opponent = tv.getText().toString();

		// Creating a game instance for sharing state
		final ParseObject gameInstance = new ParseObject(getString(R.string.parse_class_1on1_game));
		gameInstance.put(getString(R.string.parse_field_1on1_game_userA), ParseUser.getCurrentUser());
		gameInstance.put(getString(R.string.parse_field_1on1_game_userB), opponent);
		gameInstance.put(getString(R.string.parse_field_1on1_game_scoreA), 0);
		gameInstance.put(getString(R.string.parse_field_1on1_game_scoreB), 0);
		gameInstance.saveInBackground(new SaveCallback() {

			@Override
			public void done(ParseException e) {
				if (e != null) {
					Log.e(MemoryGame.LOGGING_KEY, "Couldn't save game instance", e);
					return;
				}
				pushChallenge(opponent, gameInstance.getObjectId());
			}
		});

	}

	private void pushChallenge(final String opponent, final String objectId) {
		ParsePush parsePush = new ParsePush();
		ParseQuery<ParseInstallation> pQuery = ParseInstallation.getQuery();
		ParseQuery<ParseUser> userQuery = ParseUser.getQuery();

		userQuery.whereEqualTo("username", opponent);
		pQuery.whereMatchesQuery("user", userQuery);
		parsePush.setQuery(pQuery);
		JSONObject data = new JSONObject();
		try {
			String me = ParseUser.getCurrentUser().getUsername();
			data.put("alert", me + " is challenging you");
			data.put("username", me);
			data.put("gameInstanceID", objectId);
			// data.put("uri", OneOnOneActivity.class);
		} catch (JSONException e) {
			Log.e(MemoryGame.LOGGING_KEY, "error parsing", e);
		}
		parsePush.setMessage("test");
		parsePush.setData(data);
		parsePush.sendInBackground(new SendCallback() {

			@Override
			public void done(ParseException e) {
				if (e == null) {
					Log.d(MemoryGame.LOGGING_KEY, "done pushing", e);
					startOneOnOne(opponent, objectId);
				} else {
					Toast.makeText(getBaseContext(), "Error requesing challenge " + e.getMessage(), Toast.LENGTH_LONG)
							.show();
				}
			}
		});
	}

	/**
	 * 
	 * @param opponent
	 * @param objectId
	 *            We'll need this at the opponent side to maintain shared state
	 */
	private void startOneOnOne(String opponent, String objectId) {
		Intent intent = new Intent(SelectPlayerActivity.this, OneOnOneActivity.class);
		intent.putExtra("opponent", opponent);
		intent.putExtra("isMeInitiator", true);
		intent.putExtra("gameInstanceId", objectId);
		startActivity(intent);
	}
}
