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
 * Created by SShrestha on 28/09/2015.
 */
public class InstagramUserSearchTask extends AsyncTask<String,Void,ArrayList<InstagramUser>> {
    private final String API_URL = "https://api.instagram.com/v1";
    ListView listView;
    Context context;
    String url;
    public InstagramUserSearchTask(Context context, final int count, ListView listView){
        this.context = context;
        this.url = API_URL + "/users/search?q=search_text&count=" + String.valueOf(count) + "&access_token=" +AppData.getAccessToken(context);
        this.listView = listView;
    }
    @Override
    protected ArrayList<InstagramUser> doInBackground(String... p){
        try {
            Log.w("Info", this.url.replace("search_text", p[0]));
            URL url = new URL(this.url.replace("search_text", p[0]));
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setDoInput(true);
            int responseCode = urlConnection.getResponseCode();
            urlConnection.connect();
            String response = AppData.streamToString(urlConnection.getInputStream());
            if (!response.equalsIgnoreCase("!")) {

                JSONObject jsonObj = (JSONObject) new JSONTokener(response).nextValue();
                JSONArray ar = jsonObj.getJSONArray("data");
                ArrayList<InstagramUser> ud = new ArrayList<InstagramUser>();
                for (int i = 0; i < ar.length(); i++) {
                    JSONObject obj = ar.getJSONObject(i);
                    InstagramUser _ud = new InstagramUser();
                    _ud.first_name = obj.getString("full_name");
                    _ud.last_name = "";
                    _ud.id = obj.getString("id");
                    _ud.username = obj.getString("username");
                    _ud.profile_picture = obj.getString("profile_picture");
                    ud.add(_ud);

                }
                return ud;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.e("InstagramUserSearchTask", "doInBackground");
        return null;
    }

    @Override
    protected void onPostExecute(ArrayList<InstagramUser> result)
    {
        Log.e("InstagramUserSearchTask","onPostExecute" );
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
                convertView = layoutInflater.inflate(R.layout.search_user_result_layout, null);
                holder = new ViewHolder();
                holder.textView_fullname = (TextView) convertView.findViewById(R.id.textView_fullname);
                holder.textView_username = (TextView) convertView.findViewById(R.id.textView_username);
                holder.textView_userid = (TextView) convertView.findViewById(R.id.textView_userid);
                holder.imageView_profile_picture = (ImageView) convertView.findViewById(R.id.profile_picture);
                convertView.setTag(holder);
            }
            else
            {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.textView_fullname.setText(instagramUsers.get(position).first_name+" "+instagramUsers.get(position).last_name);
            holder.textView_username.setText(instagramUsers.get(position).username);
            holder.textView_userid.setText(instagramUsers.get(position).id);
            ImageLoadTask i = new ImageLoadTask(instagramUsers.get(position).profile_picture, holder.imageView_profile_picture);
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
        public TextView textView_fullname;
        public TextView textView_username;
        public TextView textView_userid;
        public ImageView imageView_profile_picture;
    }



}

