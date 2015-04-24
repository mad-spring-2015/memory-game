package edu.uncc.mad.memorygame;

import android.app.Activity;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class Timer extends CountDownTimer {

	private static final String TIME_REMAINING_PREFIX_TEXT = "Time Remaining: ";
	private long millisUntilFinished;
	private long millisInFuture;
	private Activity activity;
	private TextView tv;
	private boolean isRunning ;
	public Timer(Activity activity, long millisInFuture) {
		super(millisInFuture, 1000);
		this.millisInFuture = millisInFuture;
		this.activity = activity;
		this.tv = (TextView) activity.findViewById(R.id.textViewTimeRem);
		isRunning = false;
	}

	public void startTimer() {
		this.start();
		isRunning = true;
		tv.setText(TIME_REMAINING_PREFIX_TEXT + (millisUntilFinished / 1000));
		tv.setVisibility(View.VISIBLE);
	}

	public void endTimer() {
		this.cancel();
		isRunning = false;
	}

	public boolean isRunning(){
		return isRunning;
	}
	/**
	 * 
	 * @return how long a player took to complete the level
	 */
	public long getTime() {
		return millisInFuture - millisUntilFinished;
	}

	@Override
	public void onTick(long millisUntilFinished) {
		this.millisUntilFinished = millisUntilFinished;
		tv.setText(TIME_REMAINING_PREFIX_TEXT + (millisUntilFinished / 1000));
	}

	/**
	 * Cancel the timer and return remaining time
	 * @return
	 */
	public long pause() {
		this.cancel();
		isRunning = false;
		return millisUntilFinished;
	}

	@Override
	public void onFinish() {
		isRunning = false;
		Toast.makeText(activity, "Times up", Toast.LENGTH_LONG).show();
		((Button) activity.findViewById(R.id.buttonPlay))
				.setVisibility(View.GONE);
		Button btn = (Button) activity.findViewById(R.id.buttonRestart);
		btn.setText(activity.getString(R.string.btn_txt_retry));
		btn.setVisibility(View.VISIBLE);
	}
}
