package edu.uncc.mad.memorygame;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

public class OneOnOneActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_one_on_one);
		Intent intent = getIntent();
		String opponent = intent.getStringExtra("opponent");
		if (opponent == null) {
			Log.e(MemoryGame.LOGGING_KEY, "No opponent");
			return;
		}
		
		((TextView) findViewById(R.id.textViewOpponentTxt)).setText(opponent);
	}
}
