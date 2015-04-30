package edu.uncc.mad.memorygame;

import java.util.Arrays;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

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
	private ProgressDialog progressDialog;
	private TextView statusView;
	private ImageView imgStatus ;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_fb_publish_score);
		callbackManager = CallbackManager.Factory.create();
		statusView = (TextView) findViewById(R.id.textViewFbPostStatus);
		imgStatus = (ImageView) findViewById(R.id.imageViewStatus);
		if (AccessToken.getCurrentAccessToken() == null) {
			statusView.setText("You have to be logged in as facebook user to user this feature.");
			return;
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

		progressDialog = new ProgressDialog(this);
		progressDialog.setTitle("Loading");
		progressDialog.setMessage("Posting your highscore");
		progressDialog.show();
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
				statusView.setText("Your score is posted on your fb wall.");
				imgStatus.setImageDrawable(getResources().getDrawable(R.drawable.ic_done_grey600_48dp));
				imgStatus.setVisibility(View.VISIBLE);
				progressDialog.dismiss();
				
			}

			@Override
			public void onError(FacebookException error) {
				Log.e(MemoryGame.LOGGING_KEY, "", error);
				statusView.setText(error.getMessage());
				imgStatus.setImageDrawable(getResources().getDrawable(R.drawable.ic_report_problem_grey600_48dp));
				imgStatus.setVisibility(View.VISIBLE);
				progressDialog.dismiss();
			}

			@Override
			public void onCancel() {
				Log.d(MemoryGame.LOGGING_KEY, "Cancelled");
				progressDialog.dismiss();
			}
		});

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		callbackManager.onActivityResult(requestCode, resultCode, data);
	}

}
