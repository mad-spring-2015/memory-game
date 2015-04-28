package edu.uncc.mad.memorygame;

import java.util.List;

import android.content.Context;
import android.widget.ArrayAdapter;

import com.parse.ParseUser;

public class LeaderboardListAdapter extends ArrayAdapter<ParseUser> {

	
	private Context context;
	private int resource;
	private int textViewResourceId;
	private List<ParseUser> users;
	
	public LeaderboardListAdapter(Context context, int resource, List<ParseUser> objects) {
		super(context, resource, objects);
		this.context = context;
		this.resource= resource;
		this.users = objects;
	}

}
