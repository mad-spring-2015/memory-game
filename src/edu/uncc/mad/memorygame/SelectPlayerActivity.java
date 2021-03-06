package edu.uncc.mad.memorygame;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SendCallback;

import edu.uncc.mad.memorygame.oneonone.WaitForOpponentAcceptTask;

public class SelectPlayerActivity extends Activity {

	private ParseObject gameInstance;
	private SelectPlayerListAdapter adapter;
	private List<ParseUser> users = new ArrayList<ParseUser>();
	private ProgressBar progressBar;
	private TextView statusTextView;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_select_player);
		ListView lv = (ListView) findViewById(R.id.listViewSelectPlayer);
		statusTextView = (TextView) findViewById(R.id.textViewSpStatus);
		progressBar = (ProgressBar) findViewById(R.id.progressBarSp);
		progressBar.setVisibility(View.VISIBLE);
		statusTextView.setText("Retreiving players..");
		adapter = new SelectPlayerListAdapter(this, R.layout.select_player_list_item, users);
		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				saveGameInstance(users.get(position));
				
			}
		});
		lv.setAdapter(adapter);
		ParseQuery<ParseUser> playersQuery = ParseUser.getQuery();
		playersQuery.whereNotEqualTo("username", ParseUser.getCurrentUser().getUsername());
		playersQuery.orderByAscending(getString(R.string.parse_field_user_firstname));
		playersQuery.findInBackground(new FindCallback<ParseUser>() {
			
			@Override
			public void done(List<ParseUser> users, ParseException e) {
				if(e != null){
					Log.e(MemoryGame.LOGGING_KEY, "Error fetching users", e);
					return;
				}
				progressBar.setVisibility(View.INVISIBLE);
				statusTextView.setText("Select a player to challenge.");
				SelectPlayerActivity.this.users.addAll(users);
				adapter.notifyDataSetChanged();
			}
		});
	}

	public void selectPlayerClicked(String opponentUsername) {

		ParseQuery<ParseUser> opponentQuery = ParseUser.getQuery();
		opponentQuery.whereEqualTo("username", opponentUsername);
		opponentQuery.getFirstInBackground(new GetCallback<ParseUser>() {

			@Override
			public void done(ParseUser opponent, ParseException e) {
				if (e != null) {
					Log.e(MemoryGame.LOGGING_KEY, "Couldnt retreive opponent", e);
					return;
				}
				saveGameInstance(opponent);
			}
		});

	}

	private void saveGameInstance(final ParseUser opponent) {
		gameInstance = new ParseObject(getString(R.string.parse_class_1on1_game));
		gameInstance.put(getString(R.string.parse_field_1on1_game_userA), ParseUser.getCurrentUser());
		gameInstance.put(getString(R.string.parse_field_1on1_game_userB), (opponent));
		gameInstance.put(getString(R.string.parse_field_1on1_game_scoreA), 0);
		gameInstance.put(getString(R.string.parse_field_1on1_game_scoreB), 0);
		ParseACL acl = new ParseACL(ParseUser.getCurrentUser());
		acl.setWriteAccess(opponent, true);
		acl.setReadAccess(opponent, true);
		gameInstance.setACL(acl);
		gameInstance.saveInBackground(new SaveCallback() {

			@Override
			public void done(ParseException e) {
				if (e != null) {
					Log.e(MemoryGame.LOGGING_KEY, "Couldn't save game instance", e);
					return;
				}
				pushChallenge(opponent.getUsername(), gameInstance.getObjectId());
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
			String me = ParseUser.getCurrentUser().getString(getString(R.string.parse_field_user_firstname));
			data.put("alert", me + " is challenging you");
			data.put("username", me);
			data.put("gameInstanceId", objectId);
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
					waitForOpponentToJoin(opponent, objectId);
				} else {
					Toast.makeText(getBaseContext(), "Error requesing challenge " + e.getMessage(), Toast.LENGTH_LONG)
							.show();
				}
			}
		});
	}

	private void waitForOpponentToJoin(final String opponent, final String objectId) {
		final ProgressDialog progress = new ProgressDialog(this);
		progress.setTitle("Waiting on Opponent");
		progress.setMessage("Waiting for opponent to accept the challenge!");
		progress.show();
		final CountDownTimer cdTimer = new WaitForOpponentAcceptTask(gameInstance, progress, this, opponent, objectId);
		cdTimer.start();

	}

	
}
