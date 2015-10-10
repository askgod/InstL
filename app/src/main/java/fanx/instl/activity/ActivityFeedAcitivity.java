package fanx.instl.activity;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ListView;

import java.util.ArrayList;

import fanx.instl.R;
import fanx.instl.activity.InstagramUtils.DisplayMediaLikeUserTask;
import fanx.instl.activity.InstagramUtils.InstagramRetrieveUserMediaTask;

public class ActivityFeedAcitivity extends AppCompatActivity {

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

}
