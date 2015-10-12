package fanx.instl.activity.InstagramUtils;

/**
 * Created by SShrestha on 10/10/15.
 */

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.net.URL;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

import fanx.instl.R;


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
                String x = "Username:  "+likeUserInfo.getString("username")
                        +"\nFull Name: "+likeUserInfo.getString("full_name")
                        +"\nID:        "+likeUserInfo.getString("id")
                        +"\nProfile Picture:"+likeUserInfo.getString("profile_picture");

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
        final CustomAdapter adapter = new CustomAdapter(context, result);
        listView.setAdapter(adapter);
    }

    public class CustomAdapter extends BaseAdapter {
        ArrayList<String> usersInfo;
        Context context;
        private LayoutInflater inflater = null;
        public CustomAdapter(Context context, ArrayList<String> usersInfo) {
            // TODO Auto-generated constructor stub

            this.context=context;
            this.usersInfo = usersInfo;
            //inflater = ( LayoutInflater )context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            inflater = LayoutInflater.from(context);

        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return usersInfo.size();
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }


        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub

            Holder holder;


            if(convertView == null)
            {
                convertView = inflater.inflate(R.layout.individual_user_info, null);
                holder = new Holder();
                holder.tv = (TextView) convertView.findViewById(R.id.userInfoText);
                holder.img = (ImageView) convertView.findViewById(R.id.profileImageView);
                convertView.setTag(holder);
            }
            else
            {
                holder = (Holder) convertView.getTag();
            }

            final String[] x = usersInfo.get(position).split("\nProfile Picture:");
            holder.tv.setText(x[0]);
            ImageLoadTask i = new ImageLoadTask(x[1], holder.img);
            i.execute();



            return convertView;
        }

        public class Holder
        {
            TextView tv;
            ImageView img;
        }

    }

}
