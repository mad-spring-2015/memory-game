/**
 * Ajay Vijayakumaran Nair
 * Ayang
 * Nachiket Doke
 */
package edu.uncc.mad.memorygame;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

public class LoginActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		// getActionBar().setTitle("Login");
		if (ParseUser.getCurrentUser() != null) {
			Intent intent = new Intent(LoginActivity.this, MemoryGame.class);
			startActivity(intent);
			finish();
		}

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
}
