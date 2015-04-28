package edu.uncc.mad.memorygame;

import java.util.List;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

public class LeaderboardActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_leaderboard);
		
		ParseQuery<ParseUser> topUsers = ParseUser.getQuery();
		topUsers.orderByDescending("score");
		topUsers.findInBackground(new FindCallback<ParseUser>() {
			
			@Override
			public void done(List<ParseUser> users, ParseException e) {
				if(e != null){
					Log.e(MemoryGame.LOGGING_KEY, "Error fetching users", e);
					return;
				}
				
			}
		});
	}
}
