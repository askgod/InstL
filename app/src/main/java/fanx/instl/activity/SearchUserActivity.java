package fanx.instl.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ListView;
import android.widget.SearchView;

import fanx.instl.R;
import fanx.instl.activity.InstagramUtils.InstagramUserSearchTask;
import fanx.instl.activity.InstagramUtils.SuggestUserTask;

public class SearchUserActivity extends BaseDrawerActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_user);

        final ListView searchResultListView = (ListView) findViewById(R.id.searchResultListView);

        SearchView searchUserView =  (SearchView) findViewById(R.id.searchUserView);
        searchUserView.setQueryHint("Search Text Here");
        searchUserView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

            }
        });

        searchUserView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                if (query.length() > 3) {
                    Log.e("SearchUserActivity", "onQueryTextSubmit");
                    InstagramUserSearchTask instagramUserSearchTask =
                            new InstagramUserSearchTask(SearchUserActivity.this, 25, searchResultListView);
                    instagramUserSearchTask.execute(query.replace(" ", "+"));
                    //searchResultListView.setVisibility(SearchView.VISIBLE);
                } else {
                    //searchResultListView.setVisibility(SearchView.INVISIBLE);
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.length() > 3) {
                    //Log.e("SearchUserActivity", "onQueryTextChange");
                } else {

                }
                return false;
            }
        });

        //By default returns the suggested users
        SuggestUserTask s = new SuggestUserTask(getApplicationContext(), searchResultListView, 15);
        s.execute();
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
