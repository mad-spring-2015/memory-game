package edu.uncc.mad.memorygame;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

public class MemoryGame extends Activity {

	public static final String LOGGING_KEY = "app";
	private Game game;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_memory_game);
		if (ParseUser.getCurrentUser() == null) {
			logUserIn("avijaya2@uncc.edu", "123");
		} else {
			game = new Game(this);
			game.init();
		}
	}

	private void logUserIn(String email, String pass) {
		ParseUser.logInInBackground(email, pass, new LogInCallback() {
			public void done(ParseUser user, ParseException e) {
				if (user != null) {
					Log.d(MemoryGame.LOGGING_KEY, "User logged in");
					game = new Game(MemoryGame.this);
					game.init();
				} else {
					Log.d(MemoryGame.LOGGING_KEY, "User couldnt b logged in");
					if (e.getCode() == ParseException.OBJECT_NOT_FOUND) {
						Toast.makeText(MemoryGame.this, "Dint find given user in server", Toast.LENGTH_LONG).show();
					}
				}
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.memory_game, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.logout) {
			ParseUser.logOut();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	public void playBtnClicked(View view){
		game.begin();
	}
	public void restartBtnClicked(View view){
		if(((Button)view).getText().equals(getString(R.string.btn_txt_nxt_level))){
			recreate();
		}else{
			recreate();
		}
	}
}
