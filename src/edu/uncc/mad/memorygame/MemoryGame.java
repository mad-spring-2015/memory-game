package edu.uncc.mad.memorygame;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.facebook.AccessToken;
import com.facebook.login.LoginManager;
import com.parse.ParseUser;

public class MemoryGame extends Activity {

	public static final String LOGGING_KEY = "app";
	private Game game;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_memory_game);
		//TODO add margin for single game
		game = new Game(this);
		game.init();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.memory_game, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		switch (id) {
		case R.id.menu_item_logout:
			ParseUser.logOut();
			if(AccessToken.getCurrentAccessToken() != null){
				LoginManager.getInstance().logOut();
			}
			Intent signInIntent = new Intent(this, LoginActivity.class);
			startActivity(signInIntent);
			finish();
			return true;
		case R.id.menu_item_leaderboard:
			Intent leaderboardIntent = new Intent(this, LeaderboardActivity.class);
			startActivity(leaderboardIntent);
			break;
		case R.id.menu_item_announce_on_fb:
			Intent fbIntent = new Intent(this, FbPublishScoreActivity.class);
			startActivity(fbIntent);
			break;
		case R.id.menu_item_one_on_one:
			Intent oneOnOneIntent = new Intent(this, SelectPlayerActivity.class);
			startActivity(oneOnOneIntent);
			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onResume() {
		if (game != null) {
			game.resumeGame();
		}
		super.onResume();
	}

	@Override
	protected void onPause() {
		if (game != null) {
			game.pauseGame();
		}
		super.onPause();
	}

	@Override
	protected void onRestart() {
		game.end();
		super.onRestart();
	}

	public void playBtnClicked(View view) {
		game.begin();
	}

	public void restartBtnClicked(View view) {
		if (((Button) view).getText().equals(getString(R.string.btn_txt_nxt_level))) {
			recreate();
		} else {
			recreate();
		}
	}
}
