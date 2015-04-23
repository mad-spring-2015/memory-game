package edu.uncc.mad.memorygame.parse;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.parse.ParseAnalytics;
import com.parse.ParsePushBroadcastReceiver;

import edu.uncc.mad.memorygame.MemoryGame;
import edu.uncc.mad.memorygame.OneOnOneActivity;

public class OneOnOneBroadcastReceiver extends ParsePushBroadcastReceiver {

	@Override
	protected void onPushOpen(Context context, Intent intent) {

		ParseAnalytics.trackAppOpenedInBackground(intent);
		String data = intent.getStringExtra("com.parse.Data");
		Intent oneOnOneIntent = new Intent(context, OneOnOneActivity.class);
		try {
			JSONObject dataObject = new JSONObject(data);
			String opponent = dataObject.getString("username");
			oneOnOneIntent.putExtra("opponent", opponent);
			oneOnOneIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(oneOnOneIntent);
		} catch (JSONException e) {
			Log.e(MemoryGame.LOGGING_KEY, "Error in parsing push payload", e);
		}

	}

	@Override
	protected void onPushDismiss(Context context, Intent intent) {
		// TODO Send cancel game msg
		super.onPushDismiss(context, intent);
	}

}
