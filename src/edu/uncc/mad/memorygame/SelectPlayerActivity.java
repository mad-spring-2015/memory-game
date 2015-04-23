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
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SendCallback;

public class SelectPlayerActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_select_player);
	}

	public void selectPlayerBtnClicked(View view) {
		TextView tv = (TextView) findViewById(R.id.editTextSelectPlayer);
		ParsePush parsePush = new ParsePush();
		ParseQuery<ParseInstallation> pQuery = ParseInstallation.getQuery();
		ParseQuery<ParseUser> userQuery = ParseUser.getQuery();
		final String opponent = tv.getText().toString();
		userQuery.whereEqualTo("username", opponent);
		pQuery.whereMatchesQuery("user", userQuery);
		parsePush.setQuery(pQuery);
		JSONObject data = new JSONObject();
		try {
			String me = ParseUser.getCurrentUser().getUsername();
			data.put("alert", me + " is challenging you");
			data.put("username", me);
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
					Intent intent = new Intent(SelectPlayerActivity.this,
							OneOnOneActivity.class);
					intent.putExtra("opponent", opponent);
					startActivity(intent);
				} else {
					Toast.makeText(getBaseContext(),
							"Error requesing challenge " + e.getMessage(),
							Toast.LENGTH_LONG).show();
				}
			}
		});
	}
}
