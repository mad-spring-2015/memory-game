package edu.uncc.mad.memorygame;

import java.util.ArrayList;
import java.util.List;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

public class LeaderboardActivity extends Activity {

	private List<ParseUser> users = new ArrayList<ParseUser>();
	private LeaderboardListAdapter adapter;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_leaderboard);
		ListView lv = (ListView) findViewById(R.id.listViewLb);
		adapter = new LeaderboardListAdapter(this, R.layout.leaderboard_list_item, users);
		lv.setAdapter(adapter);
		ParseQuery<ParseUser> topUsers = ParseUser.getQuery();
		topUsers.orderByDescending("score");
		topUsers.findInBackground(new FindCallback<ParseUser>() {
			
			@Override
			public void done(List<ParseUser> users, ParseException e) {
				if(e != null){
					Log.e(MemoryGame.LOGGING_KEY, "Error fetching users", e);
					return;
				}
				LeaderboardActivity.this.users.addAll(users);
				adapter.notifyDataSetChanged();
			}
		});
	}
}
