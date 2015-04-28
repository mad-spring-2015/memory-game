package edu.uncc.mad.memorygame.oneonone;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.content.Context;
import android.util.Log;
import android.widget.TextView;

import com.parse.ParseException;
import com.parse.ParseObject;

import edu.uncc.mad.memorygame.MemoryGame;
import edu.uncc.mad.memorygame.R;

/**
 * Streamline all gameInstance related access
 * 
 * @author Ajay
 *
 */
public class GameInstanceHandler {
	private static ParseObject gameInstance;
	private static Context context;
	private static ExecutorService executor = Executors.newFixedThreadPool(1);

	private GameInstanceHandler() {

	}

	static {
		GameInstanceHandler.gameInstance = OneOnOneGame.getCurrentGame().getGameInstance();
		GameInstanceHandler.context = OneOnOneGame.getCurrentGame().getActivity();
	}

	private static void checkState() {
		if (gameInstance == null) {
			throw new RuntimeException("GameInstance not initialized. Call init() first");
		}
	}

	/**
	 * Update current player's status. This will appropriately decide which
	 * field to update
	 * 
	 * @param status
	 */
	public static void updateMyGameStatus(final OneOnOneGameStatus status) {
		checkState();
		final String myStatus = OneOnOneGame.getCurrentGame().getMeStatus();
		executor.execute(new Runnable() {

			@Override
			public void run() {
				gameInstance.put(myStatus, status.toString());
				try {
					gameInstance.save();
				} catch (ParseException e) {
					Log.e(MemoryGame.LOGGING_KEY, "Error saving gameInstance", e);
				}
			}
		});
	}

	/**
	 * Updates the Opponent score on screen. Also takes the responsibility of displaying winner
	 */
	public static void updateOpponentScoreOnScreen() {
		checkState();
		executor.execute(new Runnable() {

			@Override
			public void run() {
				try {
					gameInstance.fetch();
					OneOnOneGame.getCurrentGame().getActivity().runOnUiThread(new Runnable() {

						@Override
						public void run() {
							int score = gameInstance.getInt(OneOnOneGame.getCurrentGame().getOpScore());
							String sScore = score + "";
							OneOnOneGame.getCurrentGame().getOpScoreView().setText(sScore);
							
							String opponentStatus = gameInstance.getString(OneOnOneGame.getCurrentGame().getOpStatus());
							TextView msgTv = (TextView) OneOnOneGame.getCurrentGame().getActivity().findViewById(R.id.textViewMsg);
							if(opponentStatus != null && opponentStatus.equals(OneOnOneGameStatus.EXITED.toString())){
								msgTv.setText("Your opponent seem to have fled the scene!");
							}else if(opponentStatus != null && opponentStatus.equals(OneOnOneGameStatus.DONE.toString())){
								String myStatus = gameInstance.getString(OneOnOneGame.getCurrentGame().getMeStatus());
								if(myStatus.equals(OneOnOneGameStatus.DONE.toString())){
									if(gameInstance.getInt(OneOnOneGame.getCurrentGame().getMeScore()) > score){
										msgTv.setText("You win !!");
									} else if(gameInstance.getInt(OneOnOneGame.getCurrentGame().getMeScore()) < score){
										msgTv.setText("Defeated !!");
									} else{
										msgTv.setText("Its a tie !!");
									}
								}
							}
						}
					});
				} catch (ParseException e) {
					Log.e(MemoryGame.LOGGING_KEY, "Error fetching gameInstance", e);
				}
			}
		});
	}

	/**
	 * Save the current player's score on parse
	 * 
	 * @param score
	 */
	public static void saveScore(final int score) {
		checkState();
		gameInstance.put(OneOnOneGame.getCurrentGame().getMeScore(), score);
		try {
			gameInstance.save();
		} catch (ParseException e) {
			Log.e(MemoryGame.LOGGING_KEY, "problem syncing score", e);
		}
	}

	/**
	 * Updates the status of current player and displays an appropriate message
	 * @param status The status to update
	 */
	public static void updateMyGameStatusAndShowMsg(final OneOnOneGameStatus status) {
		checkState();
		final String myStatus = OneOnOneGame.getCurrentGame().getMeStatus();
		executor.execute(new Runnable() {

			@Override
			public void run() {
				gameInstance.put(myStatus, status.toString());
				try {
					gameInstance.save();
					OneOnOneGame.getCurrentGame().getActivity().runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							showOpPlayingMsg();
						}
					});
				
				} catch (ParseException e) {
					Log.e(MemoryGame.LOGGING_KEY, "Error saving gameInstance", e);
				}
			}
		});
	}

	private static void showOpPlayingMsg() {
		String opponentStatus = gameInstance.getString(OneOnOneGame.getCurrentGame().getOpStatus());
		if(opponentStatus != null && opponentStatus.equals(OneOnOneGameStatus.PLAYING.toString())){
			TextView msgTv = (TextView) OneOnOneGame.getCurrentGame().getActivity().findViewById(R.id.textViewMsg);
			msgTv.setText("Your opponent is still in the hunt");
		}
	}
}
