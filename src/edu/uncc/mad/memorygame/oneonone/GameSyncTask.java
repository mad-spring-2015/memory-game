package edu.uncc.mad.memorygame.oneonone;

import java.util.TimerTask;

import android.util.Log;

import com.parse.ParseException;
import com.parse.ParseObject;

import edu.uncc.mad.memorygame.MemoryGame;

/**
 * This Task will keep the game instance parse object in sync. After each sync,
 * the opponents, score will reflect on screen.
 * 
 * @author Ajay
 *
 */
public class GameSyncTask extends TimerTask {

	private ParseObject gameInstance;

	
	@Override
	public void run() {
		gameInstance = OneOnOneGame.getCurrentGame().getGameInstance();
		try {
			gameInstance.fetch();
			OneOnOneGame.getCurrentGame().getActivity().runOnUiThread(new Runnable() {

				@Override
				public void run() {
					String score = gameInstance.getInt(OneOnOneGame.getCurrentGame().getOpScore()) + "";
					updateScoreInScreen(score);
				}
			});
		} catch (ParseException e) {
			Log.w(MemoryGame.LOGGING_KEY, "GameSyncTask is facing problems to sync gameInstance", e);
		}
	}

	/**
	 * To update the opponent's score on screen
	 */
	private void updateScoreInScreen(String score) {
		OneOnOneGame.getCurrentGame().getOpScoreView().setText(score);
	}
}
