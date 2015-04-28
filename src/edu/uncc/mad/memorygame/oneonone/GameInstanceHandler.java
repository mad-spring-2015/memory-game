package edu.uncc.mad.memorygame.oneonone;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.content.Context;
import android.util.Log;

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
	private static void checkState(){
		if(gameInstance == null){
			throw new RuntimeException("GameInstance not initialized. Call init() first");
		}
	}

	public static void saveOpponentGameStatus(final OneOnOneGameStatus status) {
		checkState();
		executor.execute(new Runnable() {

			@Override
			public void run() {
				gameInstance.put(context.getString(R.string.parse_field_1on1_game_statusB), status.toString());
				try {
					gameInstance.save();
				} catch (ParseException e) {
					Log.e(MemoryGame.LOGGING_KEY, "Error saving gameInstance", e);
				}
			}
		});
	}
	public static void saveMyGameStatus(final OneOnOneGameStatus status) {
		checkState();
		executor.execute(new Runnable() {

			@Override
			public void run() {
				gameInstance.put(context.getString(R.string.parse_field_1on1_game_statusA), status.toString());
				try {
					gameInstance.save();
				} catch (ParseException e) {
					Log.e(MemoryGame.LOGGING_KEY, "Error saving gameInstance", e);
				}
			}
		});
	}
	/**
	 * Updates the Opponent score on screen
	 */
	public static void updateOpponentScore(){
		checkState();
		executor.execute(new Runnable() {

			@Override
			public void run() {
				try {
					gameInstance.fetch();
					OneOnOneGame.getCurrentGame().getActivity().runOnUiThread(new Runnable() {

						@Override
						public void run() {
							String score = gameInstance.getInt(OneOnOneGame.getCurrentGame().getOpScore()) + "";
							OneOnOneGame.getCurrentGame().getOpScoreView().setText(score);
						}
					});
				} catch (ParseException e) {
					Log.e(MemoryGame.LOGGING_KEY, "Error fetching gameInstance", e);
				}
			}
		});
	}
}
