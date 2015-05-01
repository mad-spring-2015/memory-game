package edu.uncc.mad.memorygame;

import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;

public class Standings1on1ListAdapter extends ArrayAdapter<ParseObject> {

	private Context context;
	private int resource;
	private List<ParseObject> results;
	private ParseUser currentUser;

	public Standings1on1ListAdapter(Context context, int resource, List<ParseObject> objects) {
		super(context, resource, objects);
		this.context = context;
		this.resource = resource;
		this.results = objects;
		this.currentUser = ParseUser.getCurrentUser();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(resource, parent, false);
		}
		ParseObject result = results.get(position);
		setOpponentName(results.get(position), convertView);
		((TextView) convertView.findViewById(R.id.textViewStResult)).setText(getResult(result));
		return convertView;
	}

	private void setOpponentName(ParseObject result, final View convertView){
		if (result.getParseObject("userA").equals(currentUser)){
			result.getParseObject("userB").fetchIfNeededInBackground(new GetCallback<ParseUser>() {

				@Override
				public void done(ParseUser user, ParseException e) {
					if(e != null){
						Log.e(MemoryGame.LOGGING_KEY, "error fetching opponent", e);
						return;
					}
					((TextView) convertView.findViewById(R.id.textViewStOpponent)).setText(user.getString("firstName"));
				}
			});
		}else{
			result.getParseObject("userA").fetchIfNeededInBackground(new GetCallback<ParseUser>() {

				@Override
				public void done(ParseUser user, ParseException e) {
					if(e != null){
						Log.e(MemoryGame.LOGGING_KEY, "error fetching opponent", e);
						return;
					}
					((TextView) convertView.findViewById(R.id.textViewStOpponent)).setText(user.getString("firstName"));
				}
			});
		}
	}
	private String getResult(ParseObject result) {
		String resultS = "";
		if (result.getParseObject("userA").equals(currentUser)) {
			if (result.getInt("scoreA") > result.getInt("scoreB")) {
				resultS = "won";
			} else if (result.getInt("scoreA") < result.getInt("scoreB")) {
				resultS = "lost";
			} else {
				resultS = "tie";
			}
		} else {
			if (result.getInt("scoreB") > result.getInt("scoreA")) {
				resultS = "won";
			} else if (result.getInt("scoreB") < result.getInt("scoreA")) {
				resultS = "lost";
			} else {
				resultS = "tie";
			}
		}
		return resultS;
	}

}
