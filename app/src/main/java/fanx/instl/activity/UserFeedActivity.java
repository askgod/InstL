package fanx.instl.activity;

import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import butterknife.Bind;
import butterknife.OnClick;
import fanx.instl.R;
import fanx.instl.activity.InstagramUtils.InstagramUserFeed;


public class UserFeedActivity extends BaseDrawerActivity {
    public static final String ACTION_SHOW_LOADING_ITEM = "action_show_loading_item";
    public static final String ARG_REVEAL_START_LOCATION = "reveal_start_location";

    @Bind(R.id.fab)
    FloatingActionButton fabCreate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_a);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Button sortByLoc = (Button) findViewById(R.id.sort_by_loc);
        Button sortByDate = (Button) findViewById(R.id.sort_by_date);

        final ListView feedListView = (ListView) findViewById(R.id.mylistview);
        InstagramUserFeed userFeed = new InstagramUserFeed(UserFeedActivity.this, 10, feedListView, sortByLoc, sortByDate);
        userFeed.execute();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_a, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_sort_by_location) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    protected void onStart() {
        super.onStart();  // Always call the superclass method first
        // The activity is either being restarted or started for the first time
        Log.e("UserFeedActivity", "onStart");
    }

    @Override
    protected void onRestart() {
        super.onRestart();  // Always call the superclass method first
        // Activity being restarted from stopped state
        Log.e("UserFeedActivity", "onRestart");
    }

    @Override
    protected void onPause() {
        super.onPause();

        Log.e("UserFeedActivity", "onPause");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e("UserFeedActivity", "onResume");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.e("UserFeedActivity", "onStop");
    }
    // Floating action bar Listener
    @OnClick(R.id.fab)
    public void onTakePhotoClick() {
        int[] startingLocation = new int[2];
        fabCreate.getLocationOnScreen(startingLocation);
        startingLocation[0] += fabCreate.getWidth() / 2;
        TakePhotoActivity.startCameraFromLocation(startingLocation, this);
        overridePendingTransition(0, 0);
    }
    // Back Twice to exit
    boolean doubleBackToExitPressedOnce = false;
    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }
}
