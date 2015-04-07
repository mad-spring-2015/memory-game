/**
 * Ajay Vijayakumaran Nair
 * Ayang
 * Nachiket Doke
 */
package edu.uncc.mad.memorygame;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseACL;

public class ParseApplication extends Application {

  @Override
  public void onCreate() {
    super.onCreate();

    // Initialize Crash Reporting.
    //ParseCrashReporting.enable(this);

    // Enable Local Datastore.
    //Parse.enableLocalDatastore(this);

    // Add your initialization code here
    Parse.initialize(this, "7Ug2aUSdbvxl7gvsAOhhfRKUS3o4ZxL8CTnoC1ZF", "raKOKjyOmbqBUbrpHO8amywgVgcy7Z3dmRQn5SPB");


    //ParseUser.enableAutomaticUser();
    ParseACL defaultACL = new ParseACL();
    // Optionally enable public read access.
    // defaultACL.setPublicReadAccess(true);
    //ParseUser.getCurrentUser().saveInBackground();
    ParseACL.setDefaultACL(defaultACL, true);
  }
}
