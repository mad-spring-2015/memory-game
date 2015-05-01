package edu.uncc.mad.memorygame;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

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
		return convertView;
	}

	private void setOpponentName(ParseObject result, final View convertView) {
		ParseUser opponent;
		String currentUserPrefix;
		if (result.getParseUser("userA").getUsername().equals(currentUser.getUsername())) {
			opponent = result.getParseUser("userB");
			currentUserPrefix = "A";
		}else{
			opponent = result.getParseUser("userA");
			currentUserPrefix = "B";
		}
		((TextView) convertView.findViewById(R.id.textViewStOpponent)).setText(opponent.getString("firstName"));
		((TextView) convertView.findViewById(R.id.textViewStResult)).setText(getResult(result, currentUserPrefix));
	}

	private String getResult(ParseObject result, String currentUserPrefix) {
		String resultS = "";
		String opponentPrefix = currentUserPrefix.equals("A") ? "B":"A";
		String currentUserScoreString = "score" + currentUserPrefix;
		String opponentScoreString = "score" + opponentPrefix;
		if (result.getInt(currentUserScoreString) > result.getInt(opponentScoreString)) {
			resultS = "won";
		} else if (result.getInt(currentUserScoreString) < result.getInt(opponentScoreString)) {
			resultS = "lost";
		} else {
			resultS = "tie";
		}
		return resultS;
	}

}
