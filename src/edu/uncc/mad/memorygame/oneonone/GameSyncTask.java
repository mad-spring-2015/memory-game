package edu.uncc.mad.memorygame.oneonone;

import java.util.TimerTask;

/**
 * This Task will keep the game instance parse object in sync. After each sync,
 * the opponents, score will reflect on screen.
 * 
 * @author Ajay
 *
 */
public class GameSyncTask extends TimerTask {

	@Override
	public void run() {
		GameInstanceHandler.updateOpponentScoreOnScreen();
	}
}
