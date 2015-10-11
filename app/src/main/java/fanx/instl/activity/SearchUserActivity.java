package fanx.instl.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.SearchView;

import fanx.instl.R;
import fanx.instl.activity.InstagramUtils.InstagramUserSearchTask;

public class SearchUserActivity extends AppCompatActivity {

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
                    searchResultListView.setVisibility(SearchView.VISIBLE);
                } else {
                    searchResultListView.setVisibility(SearchView.INVISIBLE);
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
    }
}
