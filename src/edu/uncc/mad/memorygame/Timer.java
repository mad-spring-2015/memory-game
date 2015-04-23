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

	public Timer(Activity activity, long millisInFuture) {
		super(millisInFuture, 1000);
		this.millisInFuture = millisInFuture;
		this.activity = activity;
		this.tv = (TextView) activity.findViewById(R.id.textViewTimeRem);
	}

	public void startTimer() {
		this.start();
		tv.setText(TIME_REMAINING_PREFIX_TEXT + (millisUntilFinished / 1000));
		tv.setVisibility(View.VISIBLE);
	}

	public void endTimer() {
		this.cancel();
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

	public long pause() {
		this.cancel();
		return millisUntilFinished;
	}

	@Override
	public void onFinish() {
		Toast.makeText(activity, "Times up", Toast.LENGTH_LONG).show();
		((Button) activity.findViewById(R.id.buttonPlay))
				.setVisibility(View.GONE);
		Button btn = (Button) activity.findViewById(R.id.buttonRestart);
		btn.setText(activity.getString(R.string.btn_txt_retry));
		btn.setVisibility(View.VISIBLE);
	}
}
