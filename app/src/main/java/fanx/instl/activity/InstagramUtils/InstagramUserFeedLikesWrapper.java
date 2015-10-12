package fanx.instl.activity.InstagramUtils;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import fanx.instl.R;


/**
 * Created by MubashirMunawar on 10/7/2015.
 */

public class InstagramUserFeedLikesWrapper extends AsyncTask<String,Void,ArrayList<InstagramUser>> {

    private final String API_URL = "https://api.instagram.com/v1";
    private final String SHARED = "Instagram_Preferences";
    ListView listView;
    Context context;
    String url;

    public InstagramUserFeedLikesWrapper(Context context, final int count, ListView listView, String media_id){
        this.context = context;
        this.url = API_URL + "/media/" + media_id + "/likes?access_token=" +AppData.getAccessToken(context);
        this.listView = listView;
    }

    @Override
    protected ArrayList<InstagramUser> doInBackground(String... p){
        try {

            Log.e("ur: ", url);
            URL url = new URL(this.url);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setDoInput(true);

            int responseCode = urlConnection.getResponseCode();
            urlConnection.connect();

            String response = AppData.streamToString(urlConnection.getInputStream());
            Log.e("fffff: ", "");
            if (!response.equalsIgnoreCase("!")) {

                JSONObject jsonObj = (JSONObject) new JSONTokener(response).nextValue();
                JSONArray ar = jsonObj.getJSONArray("data");
                ArrayList<InstagramUser> ud = new ArrayList<InstagramUser>();
                Log.e("b4for","");
                for (int i = 0; i < ar.length(); i++) {

                    InstagramUser _ud = new InstagramUser();
                    JSONObject obj = ar.getJSONObject(i);

                    _ud.username = obj.getString("username");
                    _ud.profile_picture = obj.getString("profile_picture");
                    _ud.full_name = obj.getString("full_name");

                    ud.add(_ud);

                }
                return ud;
            }else{


            }


        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.e("InstagramUserFeedLikes", "doInBackground");
        return null;
    }

    @Override
    protected void onPostExecute(ArrayList<InstagramUser> result)
    {
        Log.e("InstagramUserFeeds","onPostExecute" );
        if (result != null){
            listView.setAdapter(new SearchResultAdapter(context, result));
        }
    }

    private class SearchResultAdapter extends BaseAdapter{
        int count;
        private LayoutInflater layoutInflater;
        private ArrayList<InstagramUser> instagramUsers = new ArrayList<InstagramUser>();
        //Typeface type;
        Context context;

        public SearchResultAdapter(Context context, ArrayList<InstagramUser> instagramUsers){
            layoutInflater = LayoutInflater.from(context);
            this.instagramUsers = instagramUsers;
            this.count = instagramUsers.size();
            this.context = context;
            //this.type = Typeface.createFromAsset(context.getAssets(), "Styles");
        }

        @Override
        public int getCount()
        {
            return count;
        }

        public View getView(int position, View convertView, ViewGroup parent)
        {
            ViewHolder holder;

            if(convertView == null)
            {
                convertView = layoutInflater.inflate(R.layout.search_result_userfeed_comments, null);
                holder = new ViewHolder();
                holder.textView_username = (TextView) convertView.findViewById(R.id.feed_username);
                holder.imageView_profile_picture = (ImageView) convertView.findViewById(R.id.feed_profile_pic);
                holder.textView_full_name = (TextView) convertView.findViewById(R.id.feed_comments);

                convertView.setTag(holder);
            }
            else
            {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.textView_username.setText(instagramUsers.get(position).username);
            ImageLoadTask i = new ImageLoadTask(instagramUsers.get(position).profile_picture, holder.imageView_profile_picture);
            holder.textView_full_name.setText(instagramUsers.get(position).full_name);

            //holder.textView_likes.setText("200");

            i.execute();
            return convertView;

        }

        @Override
        public Object getItem(int position)
        {
            return instagramUsers.get(position);
        }

        @Override
        public long getItemId(int position)
        {
            return position;
        }

    }

    private class ViewHolder{
        public TextView textView_username;
        public ImageView imageView_profile_picture;
        public TextView textView_full_name;
    }
}
