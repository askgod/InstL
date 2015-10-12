package fanx.instl.activity.InstagramUtils;

/**
 * Created by SShrestha on 10/10/15.
 */

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.net.URL;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;


public class DisplayMediaLikeUserTask extends AsyncTask<String, Void, ArrayList<String>> {

    Context context;
    ListView listView;

    public DisplayMediaLikeUserTask(Context context, ListView listView)
    {
        this.context = context;
        this.listView = listView;
    }
    @Override
    protected ArrayList<String> doInBackground(String... mediaId)
    {
        String urlString = "https://api.instagram.com/v1/media/"+ mediaId[0] +"/likes?access_token="+AppData.getAccessToken(context);
        Log.w("Info", urlString);

        try {
            URL url = new URL(urlString);
            HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setDoInput(true);
            //int responseCode = urlConnection.getResponseCode();
            urlConnection.connect();
            String response = AppData.streamToString(urlConnection.getInputStream());
            JSONObject jsonObj = (JSONObject) new JSONTokener(response).nextValue();
            JSONArray data = jsonObj.getJSONArray("data");
            ArrayList<String> likeUserInfoArrayList = new  ArrayList<String>();

            for (int i = 0; i < data.length(); i++) {
                JSONObject likeUserInfo = data.getJSONObject(i);
                String x = "Username: "+likeUserInfo.getString("username")
                        +"\nFull Name: "+likeUserInfo.getString("full_name")
                        +"\nProfile Picture: "+likeUserInfo.getString("profile_picture")
                        +"\nID: "+likeUserInfo.getString("id");
                likeUserInfoArrayList.add(x);
            }

            return likeUserInfoArrayList;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(ArrayList<String> result)
    {
        //Log.e("ActivityFeed", result);

        final ArrayAdapter adapter = new ArrayAdapter(context, android.R.layout.simple_list_item_1, result);
        listView.setAdapter(adapter);

    }


}
