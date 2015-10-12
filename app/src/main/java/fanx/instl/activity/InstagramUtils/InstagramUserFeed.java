package fanx.instl.activity.InstagramUtils;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import fanx.instl.R;
import fanx.instl.activity.UserFeedCommentsActivity;


/**
 * Created by MubashirMunawar on 10/7/2015.
 */

public class InstagramUserFeed extends AsyncTask<String,Void,ArrayList<InstagramUserFeedMedia>> {

    private final String API_URL = "https://api.instagram.com/v1";
    private final String SHARED = "Instagram_Preferences";
    ListView listView;
    Context context;
    Button sortByLoc;
    Button sortByDate;
    String url;

    public ProgressDialog pDialog;
    public InstagramUserFeed(Context context, final int count, ListView listView, Button sortByLoc, Button sortByDate){
        this.context = context;
        this.url = API_URL + "/users/self/feed?access_token=" +AppData.getAccessToken(context);
        this.listView = listView;
        this.sortByLoc = sortByLoc;
        this.sortByDate = sortByDate;

        pDialog = new ProgressDialog(context);
        pDialog.setMessage("Loading...");
        pDialog.setCancelable(true);
        pDialog.show();
    }

    @Override
    protected ArrayList<InstagramUserFeedMedia> doInBackground(String... p){
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
                ArrayList<InstagramUserFeedMedia> ud = new ArrayList<InstagramUserFeedMedia>();
                Log.e("b4for","");
                for (int i = 0; i < ar.length(); i++) {

                    InstagramUserFeedMedia _ud = new InstagramUserFeedMedia();
                    JSONObject obj = ar.getJSONObject(i).getJSONObject("images").getJSONObject("standard_resolution");
                    _ud.standard_resolution = obj.getString("url");

                    obj = ar.getJSONObject(i).getJSONObject("user");
                    _ud.username = obj.getString("username");
                    _ud.profile_pic = obj.getString("profile_picture");

                    obj = ar.getJSONObject(i).getJSONObject("caption");
                    _ud.caption = obj.getString("text");

                    obj = ar.getJSONObject(i).getJSONObject("likes");
                    _ud.likes_counts = obj.getString("count");

                    obj = ar.getJSONObject(i).getJSONObject("comments");
                    _ud.comment_counts = obj.getString("count");

                    obj = ar.getJSONObject(i);//.getJSONObject("location");

                    if(obj.isNull("location")) {
                        Log.e("aaaa", "t");
                        _ud.location = "N/A";
                    }
                    else {
                        obj = ar.getJSONObject(i).getJSONObject("location");
                        _ud.location = obj.getString("name");
                    }
                    obj = ar.getJSONObject(i);
                    _ud.media_id = obj.getString("id");
                    _ud.timestamp = obj.getString("created_time");

                   // String dd  = convertDate(obj.getString("created_time"), "dd/MM/yyyy hh:mm:ss");

                    ud.add(_ud);


                    String x = _ud.timestamp; // created_time tag value goes here.
                    long foo = Long.parseLong(x)*1000;
                    Date date = new Date(foo);
                    DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                    String www = formatter.format(date);
                    _ud.timestamp = www;
                    Log.e("", "");
                    Log.e("", "------------------------------------------------");
                    Log.e("Username:" , _ud.username);
                    Log.e("Photo:" , _ud.standard_resolution);
                    Log.e("Caption:" , _ud.caption);
                    Log.e("timestamp:", _ud.timestamp);
                    Log.e("timestamp:", www.toString());
                    Log.e("","------------------------------------------------");


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
    protected void onPostExecute(ArrayList<InstagramUserFeedMedia> result)
    {
        if (null != pDialog && pDialog.isShowing()) {
            pDialog.dismiss();
        }


        Log.e("InstagramUserFeed","onPostExecute" );
        if (result != null){
            listView.setAdapter(new SearchResultAdapter(context, result));
        }
    }

    public class SearchResultAdapter extends BaseAdapter{
        int count;
        private LayoutInflater layoutInflater;
        private ArrayList<InstagramUserFeedMedia> instagramUsers = new ArrayList<InstagramUserFeedMedia>();
        //Typeface type;
        Context context;

        public SearchResultAdapter(Context context, ArrayList<InstagramUserFeedMedia> instagramUsers){
            layoutInflater = LayoutInflater.from(context);
            this.instagramUsers = instagramUsers;
            this.count = instagramUsers.size();
            this.context = context;
            //this.type = Typeface.createFromAsset(context.getAssets(), "Styles");
        }

        /** Sort shopping list by name ascending */
        public String test(){
            return "OK";
        }
        public void sortByLocationDsc() {
            Comparator<InstagramUserFeedMedia> comparator = new Comparator<InstagramUserFeedMedia>() {
                @Override
                public int compare(InstagramUserFeedMedia object1, InstagramUserFeedMedia object2) {
                    return object2.getLocation().compareToIgnoreCase(object1.getLocation());
                }
            };
            Collections.sort(instagramUsers, comparator);
            notifyDataSetChanged();
        }
        public void sortByLocationAsc() {
            Comparator<InstagramUserFeedMedia> comparator = new Comparator<InstagramUserFeedMedia>() {

                @Override
                public int compare(InstagramUserFeedMedia object1, InstagramUserFeedMedia object2) {
                    return object1.getLocation().compareToIgnoreCase(object2.getLocation());
                }
            };
            Collections.sort(instagramUsers, comparator);

            notifyDataSetChanged();
        }
        public void sortByDateAsc() {
            Comparator<InstagramUserFeedMedia> comparator = new Comparator<InstagramUserFeedMedia>() {

                @Override
                public int compare(InstagramUserFeedMedia object1, InstagramUserFeedMedia object2) {
                    return object1.getTimestamp().compareToIgnoreCase(object2.getTimestamp());
                }
            };
            Collections.sort(instagramUsers, comparator);
            notifyDataSetChanged();
        }
        public void sortByDateDsc() {
            Comparator<InstagramUserFeedMedia> comparator = new Comparator<InstagramUserFeedMedia>() {

                @Override
                public int compare(InstagramUserFeedMedia object1, InstagramUserFeedMedia object2) {
                    return object2.getTimestamp().compareToIgnoreCase(object1.getTimestamp());
                }
            };
            Collections.sort(instagramUsers, comparator);
            notifyDataSetChanged();
        }


        @Override
        public int getCount()
        {
            return count;
        }

        public View getView(int position, View convertView, ViewGroup parent)
        {
            final ViewHolder holder;

            if(convertView == null)
            {
                convertView = layoutInflater.inflate(R.layout.search_result_userfeed_media, null);
                holder = new ViewHolder();
                holder.textView_username = (TextView) convertView.findViewById(R.id.feed_username);
                holder.imageView_profile_picture = (ImageView) convertView.findViewById(R.id.feed_profile_pic);
                holder.imageView_media = (ImageView) convertView.findViewById(R.id.standard_resolution_pic);
                holder.textView_caption = (TextView) convertView.findViewById(R.id.feed_caption);
                holder.textView_feed_likes = (TextView) convertView.findViewById(R.id.feed_likes);
                holder.textView_comment_counts = (TextView) convertView.findViewById(R.id.feed_comments);
                holder.textView_media_id = (TextView) convertView.findViewById(R.id.feed_media_id);
                holder.textView_location = (TextView) convertView.findViewById(R.id.feed_location);
                holder.textView_timestamp = (TextView) convertView.findViewById(R.id.feed_timestamp);

                sortByLoc.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String btnText = sortByLoc.getText().toString();

                        if(btnText.contains("DSC"))
                        {
                            sortByLocationAsc();
                            sortByLoc.setText("BY LOCATION - ASC");
                        }else{
                            sortByLocationDsc();
                            sortByLoc.setText("BY LOCATION - DSC");
                        }

                        Log.e("Sorted:","OK");
                    }
                });

                sortByDate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sortByDateAsc();
                        final Button bButton = (Button) v.findViewById(R.id.sort_by_date);

                        String btnText = bButton.getText().toString();
                        Log.e("btnText", btnText);

                        if(btnText.contains("DSC")) {
                            sortByDateAsc();
                            bButton.setText("BY DATE - ASC");
                            Log.e("Sorted:", "ASC");
                        }else{
                            Log.e("Sorted:", "DSC");
                            sortByDateDsc();
                            bButton.setText("BY DATE - DSC");
                        }

                    }
                });


                TextView tvComments = (TextView) convertView.findViewById(R.id.feed_comments);
                tvComments.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.e("Item Selected:", holder.textView_media_id.getText().toString());
                        Intent i = new Intent(context.getApplicationContext(), UserFeedCommentsActivity.class);
                        i.putExtra("media_id", holder.textView_media_id.getText().toString());
                        i.putExtra("comments", "true");
                        context.startActivity(i);
                    }
                });


                ImageView imgLikes = (ImageView) convertView.findViewById(R.id.feed_likes_pic);
                imgLikes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(context.getApplicationContext(),"You Liked This!!!",Toast.LENGTH_SHORT).show();
                    }
                });

                TextView tvLikes = (TextView) convertView.findViewById(R.id.feed_likes);
                tvLikes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.e("Item Selected:", holder.textView_media_id.getText().toString());
                        Intent i = new Intent(context.getApplicationContext(), UserFeedCommentsActivity.class);
                        i.putExtra("media_id", holder.textView_media_id.getText().toString());
                        i.putExtra("comments", "false");
                        context.startActivity(i);
                    }
                });



                convertView.setTag(holder);
            }
            else
            {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.textView_username.setText(instagramUsers.get(position).username);
            ImageLoadTask i = new ImageLoadTask(instagramUsers.get(position).profile_pic, holder.imageView_profile_picture);
            ImageLoadTask j = new ImageLoadTask(instagramUsers.get(position).standard_resolution, holder.imageView_media);
            holder.textView_caption.setText(instagramUsers.get(position).caption);
            holder.textView_feed_likes.setText(instagramUsers.get(position).likes_counts);
            holder.textView_comment_counts.setText(instagramUsers.get(position).comment_counts);
            holder.textView_media_id.setText(instagramUsers.get(position).media_id);
            holder.textView_location.setText(instagramUsers.get(position).location);
            holder.textView_timestamp.setText(instagramUsers.get(position).timestamp);


            i.execute();
            j.execute();
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
        public ImageView imageView_media;
        public TextView textView_caption;
        public TextView textView_feed_likes;
        public TextView textView_comment_counts;
        public TextView textView_media_id;
        public TextView textView_location;
        public TextView textView_timestamp;

    }
}
