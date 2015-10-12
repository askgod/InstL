package fanx.instl.activity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.Toast;

import fanx.instl.R;
import fanx.instl.activity.InstagramUtils.InstagramUserFeedCommentsWrapper;
import fanx.instl.activity.InstagramUtils.InstagramUserFeedLikesWrapper;

public class UserFeedCommentsActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_a);

        Bundle extras = getIntent().getExtras();
        String media_id ="";
        String comments ="";

        if (extras != null) {
            media_id = extras.getString("media_id");
            comments = extras.getString("comments");
            Log.e("Comments:",comments);

            if(comments.equals("true")){
                final ListView feedListView = (ListView) findViewById(R.id.mylistview);
                feedListView.destroyDrawingCache();
                feedListView.invalidateViews();
                InstagramUserFeedCommentsWrapper userFeed = new InstagramUserFeedCommentsWrapper(UserFeedCommentsActivity.this, 10, feedListView, media_id);
                userFeed.execute();

                FrameLayout fl = (FrameLayout) findViewById(R.id.frame_comments);
                fl.setVisibility(View.VISIBLE);


                Button button = (Button) findViewById(R.id.send_comments);
                button.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {

                        Toast.makeText(getApplicationContext(), "Comments Saved!",Toast.LENGTH_SHORT).show();
                    }
                });
            }else{
                final ListView feedListView = (ListView) findViewById(R.id.mylistview);
                feedListView.destroyDrawingCache();
                feedListView.invalidateViews();
                InstagramUserFeedLikesWrapper userFeed = new InstagramUserFeedLikesWrapper(UserFeedCommentsActivity.this, 10, feedListView, media_id);
                userFeed.execute();

                FrameLayout fl = (FrameLayout) findViewById(R.id.frame_comments);
                fl.setVisibility(View.INVISIBLE);
            }

        }


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
        if (id == R.id.action_settings) {
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
        Log.e("UserFeedComments", "onRestart");
    }

    @Override
    protected void onPause() {
        super.onPause();

        Log.e("UserFeedComments", "onPause");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e("UserFeedComments", "onResume");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.e("UserFeedComments", "onStop");
    }
}
