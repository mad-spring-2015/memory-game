package edu.uncc.mad.memorygame;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

public class StandingsActivity extends Activity {

	private TextView stTextViewStatus;
	private ProgressBar stProgressBarStatus;
	private ParseUser currentUser;
	private Standings1on1ListAdapter adapter;
	private List<ParseObject> results;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_standings);
		currentUser = ParseUser.getCurrentUser();
		results = new ArrayList<ParseObject>();
		adapter = new Standings1on1ListAdapter(this, R.layout.standings_list_item, results);
		ListView lv = (ListView) findViewById(R.id.listViewSt1on1);
		lv.setAdapter(adapter);

		((TextView) findViewById(R.id.textViewStFName)).setText(currentUser
				.getString(getString(R.string.parse_field_user_firstname)));
		((TextView) findViewById(R.id.textViewStLName)).setText(currentUser.getString("lastName"));
		((TextView) findViewById(R.id.textViewStLevel)).setText("Level : "
				+ currentUser.getInt(getString(R.string.parse_field_user_level)));
		((TextView) findViewById(R.id.textViewStScore)).setText(""
				+ currentUser.getInt(getString(R.string.parse_field_user_score)));
		stTextViewStatus = (TextView) findViewById(R.id.textViewStStatus);
		stProgressBarStatus = (ProgressBar) findViewById(R.id.progressBarStStatus);
		stTextViewStatus.setText("Fetching..");
		stProgressBarStatus.setVisibility(View.VISIBLE);
		ParseQuery<ParseObject> oneOnOneQueryA = ParseQuery.getQuery(getString(R.string.parse_class_1on1_game));
		oneOnOneQueryA.whereEqualTo("userA", currentUser);
		ParseQuery<ParseObject> oneOnOneQueryB = ParseQuery.getQuery(getString(R.string.parse_class_1on1_game));
		oneOnOneQueryB.whereEqualTo("userB", currentUser);
		List<ParseQuery<ParseObject>> queries = Arrays.asList(oneOnOneQueryA, oneOnOneQueryB);
		ParseQuery<ParseObject> mainQuery = ParseQuery.or(queries);
		mainQuery.orderByDescending("updatedAt");
		mainQuery.setLimit(5);
		mainQuery.findInBackground(new FindCallback<ParseObject>() {

			@Override
			public void done(List<ParseObject> results, ParseException e) {
				if (e != null) {
					Log.e(MemoryGame.LOGGING_KEY, "error in fetching", e);
					stProgressBarStatus.setVisibility(View.INVISIBLE);
					stTextViewStatus.setText(e.getMessage());
					return;
				}
				stProgressBarStatus.setVisibility(View.INVISIBLE);
				stTextViewStatus.setText("");
				if (results.size() == 0) {
					stTextViewStatus.setText("You don't seem to have played any 1 on 1's");
				} else {
					StandingsActivity.this.results.addAll(results);
					adapter.notifyDataSetChanged();
				}
			}
		});

	}
}
