package fanx.instl.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;


public class AppStart extends AppCompatActivity {
    private final int SPLASH_DISPLAY_LENGTH = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        // Splash screen for SPLASH_DISPLAY_LENGTH milliseconds
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                InstagramSession i = new InstagramSession(AppStart.this);
                try {
                    if (i.hasAccessToken()) {
                        //Hi Xuan not sure why this part is not working
                        //Create an Intent that will start the Main Activity.
                        Toast.makeText(AppStart.this, "Redirecting to MainActivity ...", Toast.LENGTH_LONG);
                        Log.e("AppStart", "Main Activity");
                        Intent mainIntent = new Intent(AppStart.this, MainActivity.class);
                        startActivity(mainIntent);

                        //This is example call for for Search User
                        //mAppData.searchUser(AppStart.this, "sandip", 3);
                    } else {
                        //i.getAuthenticated(AppStart.this, listener);
                        Toast.makeText(AppStart.this, "Please login using instagram account.", Toast.LENGTH_LONG);
                        Log.e("AppStart", "Login Request");
                        Intent loginIntent = new Intent(AppStart.this, LoginActivity.class);
                        startActivity(loginIntent);

                    }
                } catch (NullPointerException ne) {

                }

            }
        }, SPLASH_DISPLAY_LENGTH);
    }

}
