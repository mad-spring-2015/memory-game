package edu.uncc.mad.memorygame.oneonone;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.CountDownTimer;
import android.util.Log;
import android.widget.Toast;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;

import edu.uncc.mad.memorygame.MemoryGame;
import edu.uncc.mad.memorygame.R;
import edu.uncc.mad.memorygame.SelectPlayerActivity;

public class WaitForOpponentAcceptTask extends CountDownTimer {
	private ParseObject gameInstance;
	private ProgressDialog progress;
	private SelectPlayerActivity activity;
	private String opponent;
	private String objectId;

	public WaitForOpponentAcceptTask(ParseObject gameInstance, ProgressDialog progress, SelectPlayerActivity activity,
			String opponent, String objectId) {
		super(30000, 3000);
		this.gameInstance = gameInstance;
		this.progress = progress;
		this.activity = activity;
		this.opponent = opponent;
		this.objectId = objectId;
	}

	@Override
	public void onTick(long millisUntilFinished) {
		gameInstance.fetchInBackground(new GetCallback<ParseObject>() {

			@Override
			public void done(ParseObject object, ParseException e) {
				if (e != null) {
					Log.e(MemoryGame.LOGGING_KEY, "Couldn't fetch gameInstance", e);
					return;
				}
				String opGameStatus = object.getString(activity.getString(R.string.parse_field_1on1_game_statusB));
				if (opGameStatus != null && opGameStatus.equals(OneOnOneGameStatus.PLAYING.toString())) {
					progress.dismiss();
					WaitForOpponentAcceptTask.this.cancel();
					startOneOnOne(opponent, objectId);
				}
			}
		});
	}

	@Override
	public void onFinish() {
		Toast.makeText(activity, "Looks like your opponent isn't available!!", Toast.LENGTH_LONG).show();
		progress.dismiss();
	}

	/**
	 * 
	 * @param opponent
	 * @param objectId
	 *            We'll need this at the opponent side to maintain shared state
	 */
	private void startOneOnOne(String opponent, String objectId) {
		Intent intent = new Intent(activity, OneOnOneActivity.class);
		intent.putExtra("opponent", opponent);
		intent.putExtra("isMeInitiator", true);
		intent.putExtra("gameInstanceId", objectId);
		activity.startActivity(intent);
	}
}