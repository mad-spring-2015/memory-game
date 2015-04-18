package edu.uncc.mad.memorygame;

import java.util.List;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class CircleAdapter extends BaseAdapter {
	private List<Circle> circles;

	public CircleAdapter(List<Circle> circles) {
		super();
		this.circles = circles;
	}

	@Override
	public int getCount() {
		return circles.size();
	}

	@Override
	public Object getItem(int position) {
		return circles.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			return circles.get(position);
		}
		return convertView;
	}

}
