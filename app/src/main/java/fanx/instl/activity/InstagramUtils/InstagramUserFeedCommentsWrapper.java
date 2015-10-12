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

public class InstagramUserFeedCommentsWrapper extends AsyncTask<String,Void,ArrayList<InstagramUserFeedComments>> {

    private final String API_URL = "https://api.instagram.com/v1";
    private final String SHARED = "Instagram_Preferences";
    ListView listView;
    Context context;
    String url;

    public InstagramUserFeedCommentsWrapper(Context context, final int count, ListView listView, String media_id){
        this.context = context;
        this.url = API_URL + "/media/" + media_id + "/comments?access_token=" +AppData.getAccessToken(context);
        this.listView = listView;
    }

    @Override
    protected ArrayList<InstagramUserFeedComments> doInBackground(String... p){
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
                ArrayList<InstagramUserFeedComments> ud = new ArrayList<InstagramUserFeedComments>();
                Log.e("b4for","");
                for (int i = 0; i < ar.length(); i++) {

                    InstagramUserFeedComments _ud = new InstagramUserFeedComments();
                    JSONObject obj = ar.getJSONObject(i).getJSONObject("from");

                    _ud.username = obj.getString("username");
                    _ud.profile_pic = obj.getString("profile_picture");

                    obj = ar.getJSONObject(i);
                    _ud.comments = obj.getString("text");

                    ud.add(_ud);


                }
                return ud;
            }else{


            }


        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.e("InstagramUserFeed", "doInBackground");
        return null;
    }

    @Override
    protected void onPostExecute(ArrayList<InstagramUserFeedComments> result)
    {
        Log.e("InstagramUserComments","onPostExecute" );
        if (result != null){
            listView.setAdapter(new SearchResultAdapter(context, result));
        }
    }

    private class SearchResultAdapter extends BaseAdapter{
        int count;
        private LayoutInflater layoutInflater;
        private ArrayList<InstagramUserFeedComments> instagramUsers = new ArrayList<InstagramUserFeedComments>();
        //Typeface type;
        Context context;

        public SearchResultAdapter(Context context, ArrayList<InstagramUserFeedComments> instagramUsers){
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
                holder.textView_comments = (TextView) convertView.findViewById(R.id.feed_comments);

                convertView.setTag(holder);
            }
            else
            {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.textView_username.setText(instagramUsers.get(position).username);
            ImageLoadTask i = new ImageLoadTask(instagramUsers.get(position).profile_pic, holder.imageView_profile_picture);
            holder.textView_comments.setText(instagramUsers.get(position).comments);

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
        public TextView textView_comments;
    }
}
