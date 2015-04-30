package edu.uncc.mad.memorygame;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.parse.ParseUser;

public class LeaderboardListAdapter extends ArrayAdapter<ParseUser> {

	private Context context;
	private int resource;
	private List<ParseUser> users;
	
	public LeaderboardListAdapter(Context context, int resource, List<ParseUser> objects) {
		super(context, resource, objects);
		this.context = context;
		this.resource= resource;
		this.users = objects;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if(convertView == null ){
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(resource, parent, false);
		}
		ParseUser user = users.get(position);
		((TextView)convertView.findViewById(R.id.textViewLbUserFnameLname)).setText(user.getString("firstName") + " " + user.getString("lastName"));
		((TextView)convertView.findViewById(R.id.textViewLbScore)).setText(user.getInt("score") + "");
		((TextView)convertView.findViewById(R.id.textViewLbLevel)).setText("Level : " + user.getInt("level"));
		return convertView;
	}
	

}
