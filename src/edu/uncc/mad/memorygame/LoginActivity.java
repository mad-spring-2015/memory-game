/**
 * Ajay Vijayakumaran Nair
 * Ayang
 * Nachiket Doke
 */
package edu.uncc.mad.memorygame;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.parse.FindCallback;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

public class LoginActivity extends Activity {

	private CallbackManager callbackManager;
	private TextView loginStatusView ;
	private ProgressBar loginProgress;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		callbackManager = CallbackManager.Factory.create();
		LoginButton loginButton = (LoginButton) findViewById(R.id.login_button);
		loginStatusView = (TextView) findViewById(R.id.textViewLoginStatus);
		loginProgress = (ProgressBar) findViewById(R.id.progressBarLoginStatus);
		loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
			private ProfileTracker mProfileTracker;

			@Override
			public void onSuccess(LoginResult result) {
				loginStatusView.setText("Initializing..");
				loginProgress.setVisibility(View.VISIBLE);
				mProfileTracker = new ProfileTracker() {
					@Override
					protected void onCurrentProfileChanged(Profile profile, Profile profile2) {
						Log.d(MemoryGame.LOGGING_KEY, profile2.getFirstName());
						mProfileTracker.stopTracking();
						Profile.fetchProfileForCurrentAccessToken();
						ParseQuery<ParseUser> userQuery = ParseUser.getQuery();
						userQuery.whereEqualTo("username", Profile.getCurrentProfile().getId());
						userQuery.findInBackground(new FindCallback<ParseUser>() {

							@Override
							public void done(List<ParseUser> objects, ParseException e) {
								if (e != null) {
									Log.e(MemoryGame.LOGGING_KEY, "error in fetching user", e);
								}
								if (objects.size() == 0) {
									// need to add this user to parse
									final ParseUser user = new ParseUser();
									user.setUsername(Profile.getCurrentProfile().getId());
									user.setPassword(Profile.getCurrentProfile().getId());
									user.put("firstName", Profile.getCurrentProfile().getFirstName());
									user.put("lastName", Profile.getCurrentProfile().getLastName());
									user.put("score", 0);
									user.put("level", 1);
									user.signUpInBackground(new SignUpCallback() {

										@Override
										public void done(ParseException e) {
											if (e != null) {
												Log.e(MemoryGame.LOGGING_KEY, "Problem creating Parse", e);
												return;
											}
											postLogin(ParseUser.getCurrentUser());
										}
									});
								} else {
									logUserIn(objects.get(0).getUsername(), objects.get(0).getUsername());
								}
							}
						});
					}
				};
				mProfileTracker.startTracking();

			}

			@Override
			public void onError(FacebookException error) {
				Log.d(MemoryGame.LOGGING_KEY, "Error", error);
			}

			@Override
			public void onCancel() {
				Log.d(MemoryGame.LOGGING_KEY, "Cancelled");

			}
		});
		// AccessToken.getCurrentAccessToken().
		// getActionBar().setTitle("Login");
		if (ParseUser.getCurrentUser() != null) {
			Intent intent = new Intent(LoginActivity.this, MemoryGame.class);
			startActivity(intent);
			finish();
		}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		callbackManager.onActivityResult(requestCode, resultCode, data);
	}

	public void signUpClicked(View view) {
		Intent intent = new Intent(LoginActivity.this, SignUp.class);
		startActivity(intent);
		finish();
	}

	public void loginClicked(View view) {
		String email = ((EditText) findViewById(R.id.editTextEmail)).getText().toString();
		String pass = ((EditText) findViewById(R.id.editTextPassword)).getText().toString();
		if (email == null || email.isEmpty() || pass == null || pass.isEmpty()) {
			Toast.makeText(this, "Mandatory login fields cant be empty", Toast.LENGTH_LONG).show();
			return;
		}
		// Email val
		String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
		if (!email.matches(emailPattern) || !email.contains("@") || !email.contains(".")) {
			Toast.makeText(this, "Email ID not valid", Toast.LENGTH_LONG).show();
			return;
		}
		logUserIn(email, pass);

	}

	private void logUserIn(String email, String pass) {
		ParseUser.logInInBackground(email, pass, new LogInCallback() {
			public void done(ParseUser user, ParseException e) {
				if (user != null) {
					Log.d(MemoryGame.LOGGING_KEY, "User logged in");
					// Associate user with this parse installation
					postLogin(user);
				} else {
					Log.d(MemoryGame.LOGGING_KEY, "User couldnt b logged in", e);
					if (e.getCode() == ParseException.OBJECT_NOT_FOUND) {
						Toast.makeText(LoginActivity.this, "Sign in failure :" + e.getMessage(), Toast.LENGTH_LONG)
								.show();
					}
				}
			}
		});
	}

	private void postLogin(ParseUser user) {
		ParseInstallation installation = ParseInstallation.getCurrentInstallation();
		installation.put("user", user);
		installation.put("username", user.getUsername());
		installation.saveInBackground(new SaveCallback() {

			@Override
			public void done(ParseException e) {
				if (e != null) {
					Log.e(MemoryGame.LOGGING_KEY, "couldn't save installation", e);
				}

			}
		});

		Intent intent = new Intent(LoginActivity.this, MemoryGame.class);
		startActivity(intent);
		finish();
	}
}
