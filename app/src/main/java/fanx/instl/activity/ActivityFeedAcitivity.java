package fanx.instl.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;

import fanx.instl.R;

public class ActivityFeedAcitivity extends BaseDrawerActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activity_feed_acitivity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /*ListView activityFeedListView = (ListView)findViewById(R.id.activityFeedListView);

        GridView g = (GridView) findViewById(R.id.gridView);

        InstagramRetrieveUserMediaTask i = new InstagramRetrieveUserMediaTask(getApplicationContext(), g);
        i.execute();

        DisplayMediaLikeUserTask d = new DisplayMediaLikeUserTask(getApplicationContext(), activityFeedListView);
        d.execute("1092484232200772082_2219605693");

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            Intent intent = new Intent(getBaseContext(), MainActivity.class);
            startActivity(intent);
            finish();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }
}
