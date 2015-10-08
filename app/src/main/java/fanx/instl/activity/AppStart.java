package fanx.instl.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import fanx.instl.R;
import fanx.instl.activity.InstagramUtils.InstagramSession;

public class AppStart extends AppCompatActivity {
    private final int SPLASH_DISPLAY_LENGTH = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_start);

        // Splash screen for SPLASH_DISPLAY_LENGTH milliseconds
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                InstagramSession i = new InstagramSession(AppStart.this);
                try {/*
                    if (i.hasAccessToken()) {
                        //Hi Xuan not sure why this part is not working
                        //Create an Intent that will start the Main Activity.
                        Toast.makeText(AppStart.this, "Redirecting to MainActivity ...", Toast.LENGTH_LONG);
                        Log.e("AppStart", "Main Activity");
                        Intent mainIntent = new Intent(AppStart.this, MainActivity.class);
                        startActivity(mainIntent);
                        finish();

                        //This is example call for for Search User
                        //mAppData.searchUser(AppStart.this, "sandip", 3);
                    } else {
                        //i.getAuthenticated(AppStart.this, listener);
                        Toast.makeText(AppStart.this, "Please login using instagram account.", Toast.LENGTH_LONG);
                        Log.e("AppStart", "Login Request");
                        Intent loginIntent = new Intent(AppStart.this, LoginActivity.class);
                        startActivity(loginIntent);

                    }*/
                    Intent mainIntent = new Intent(AppStart.this, MainActivity.class);
                    startActivity(mainIntent);
                    finish();
                } catch (NullPointerException ne) {

                }

            }

        }, SPLASH_DISPLAY_LENGTH);

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_app_start, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }



    @Override
    public void onResume() {
        super.onResume();  // Always call the superclass method first
        Log.e("AppStart", "onResume");
    }

    @Override
    public void onPause() {
        super.onPause();  // Always call the superclass method first
        // Release the Camera because we don't need it when paused
        // and other activities might need to use it.
        Log.e("AppStart", "onPause");
        //Toast.makeText(GridActivity.this, "On Pause Called", Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onStop() {
        super.onStop();  // Always call the superclass method first
        // Save the note's current draft, because the activity is stopping
        // and we want to be sure the current note progress isn't lost.
        Log.e("AppStart", "onStop");
    }

    protected void onStart() {
        super.onStart();  // Always call the superclass method first
        // The activity is either being restarted or started for the first time
        Log.e("AppStart", "onStart");
    }

    @Override
    protected void onRestart() {
        super.onRestart();  // Always call the superclass method first
        // Activity being restarted from stopped state
        Log.e("AppStart", "onRestart");
    }
}
