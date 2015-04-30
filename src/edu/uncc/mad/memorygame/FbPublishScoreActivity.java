package edu.uncc.mad.memorygame;

import java.util.Arrays;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.share.ShareApi;
import com.facebook.share.Sharer;
import com.facebook.share.Sharer.Result;
import com.facebook.share.model.ShareLinkContent;
import com.parse.ParseUser;

public class FbPublishScoreActivity extends Activity {

	private CallbackManager callbackManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_fb_publish_score);
		callbackManager = CallbackManager.Factory.create();
		if (AccessToken.getCurrentAccessToken() == null) {

		} else {
			if (!AccessToken.getCurrentAccessToken().getPermissions().contains("publish_actions")) {
				LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {

					@Override
					public void onSuccess(LoginResult result) {
						Log.d(MemoryGame.LOGGING_KEY, "login success"
								+ AccessToken.getCurrentAccessToken().getPermissions().toString());
						shareHighScore();
					}

					@Override
					public void onError(FacebookException error) {
						Log.e(MemoryGame.LOGGING_KEY, "ee", error);

					}

					@Override
					public void onCancel() {
						Log.d(MemoryGame.LOGGING_KEY, "cancelled");

					}
				});
				LoginManager.getInstance().logInWithPublishPermissions(this, Arrays.asList("publish_actions"));

			} else {
				shareHighScore();
			}
		}
	}

	private void shareHighScore() {

		ParseUser user = ParseUser.getCurrentUser();
		ShareLinkContent linkContent = new ShareLinkContent.Builder()
				.setContentTitle("Hello Memory Game")
				.setContentDescription(
						"Check out my highscore of " + user.getInt(getString(R.string.parse_field_user_score))
								+ ", Level " + user.getInt(getString(R.string.parse_field_user_level))
								+ " on Memory Game. Can you beat me??")
				.setContentUrl(Uri.parse("http://mad-spring-2015.github.io/memory-game/")).build();
		ShareApi.share(linkContent, new FacebookCallback<Sharer.Result>() {

			@Override
			public void onSuccess(Result result) {
				Log.d(MemoryGame.LOGGING_KEY, "Success");
			}

			@Override
			public void onError(FacebookException error) {
				Log.e(MemoryGame.LOGGING_KEY, "", error);

			}

			@Override
			public void onCancel() { 
				Log.d(MemoryGame.LOGGING_KEY, "Cancelled");
			}
		});

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		callbackManager.onActivityResult(requestCode, resultCode, data);
	}
	
}
