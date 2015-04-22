/**
 * Ajay Vijayakumaran Nair
 * Ayang
 * Nachiket Doke
 */
package edu.uncc.mad.memorygame;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class SignUp extends Activity {
	EditText edFName, edLName, edEmail, edPwrd, edRpwrd;
	String uFName, uLName, uEmail, UPwrd, URpwrd;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sign_up);
		findViewById(R.id.buttonCancel).setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent i = new Intent(SignUp.this, LoginActivity.class);
				startActivity(i);
				finish();
			}
		});
		findViewById(R.id.buttonSignup).setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				edFName = (EditText) findViewById(R.id.editTextFirstName);
				edLName = (EditText) findViewById(R.id.editTextLastname);
				edEmail = (EditText) findViewById(R.id.editTextEmail);
				edPwrd = (EditText) findViewById(R.id.editTextPassword);
				edRpwrd = (EditText) findViewById(R.id.editTextPasswordConfirm);

				uFName = edFName.getText().toString();
				uLName = edLName.getText().toString();
				uEmail = edEmail.getText().toString();
				UPwrd = edPwrd.getText().toString();
				URpwrd = edRpwrd.getText().toString();

				if (!UPwrd.equals(URpwrd)) {
					Toast.makeText(getBaseContext(), "Passwords don't match!!", Toast.LENGTH_LONG).show();
					return;
				}
				if (uFName.isEmpty() || uEmail.isEmpty() || UPwrd.isEmpty() || URpwrd.isEmpty() || uLName.isEmpty()) {
					Toast.makeText(getBaseContext(), "Fill all the fileds!!", Toast.LENGTH_LONG).show();
					return;
				} else {

					ParseUser user = new ParseUser();
					user.setUsername(uEmail);
					user.put("firstName", uFName);
					user.put("lastName", uLName);
					user.setEmail(uEmail);
					user.setPassword(UPwrd);

					user.signUpInBackground(new SignUpCallback() {

						@SuppressLint("ShowToast")
						@Override
						public void done(ParseException e) {
							if (e == null) {
								Toast.makeText(getBaseContext(), "Account creation successful!!", Toast.LENGTH_LONG)
										.show();
								Intent i = new Intent(SignUp.this, LoginActivity.class);
								startActivity(i);
								finish();
							} else {
								if (e.getCode() == ParseException.USERNAME_TAKEN)
									Toast.makeText(getBaseContext(), "Email already in use!!", Toast.LENGTH_LONG)
											.show();
							}
						}
					});

				}

			}
		});
	}

}
