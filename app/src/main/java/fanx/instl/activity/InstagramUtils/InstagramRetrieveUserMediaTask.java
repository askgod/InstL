package fanx.instl.activity.InstagramUtils;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import fanx.instl.R;
import fanx.instl.activity.UserProfileActivity;


/**
 * Created by SShrestha on 30/09/2015.
 */
public class InstagramRetrieveUserMediaTask extends AsyncTask<Void, Void, String> {
    String urlString = "https://api.instagram.com/v1/users/self/media/recent/?access_token=ACCESS-TOKEN";
    GridView gridView;
    Context context;
    public InstagramRetrieveUserMediaTask(Context context, GridView gridView){
        this.urlString = this.urlString.replace("ACCESS-TOKEN",  AppData.getAccessToken(context));
        this.gridView = gridView;
        this.context = context;
    }

    @Override
    protected String doInBackground(Void... param)
    {
        try {
            URL url = new URL(this.urlString);
            HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setDoInput(true);
            int responseCode = urlConnection.getResponseCode();
            urlConnection.connect();
            String response = AppData.streamToString(urlConnection.getInputStream());
            return response;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    protected void onPostExecute(String result)
    {
        Log.e("IRetrieveUserMediaTask", "onPostExecute");
        ArrayList<InstagramUserMedia> instagramUserMediaArrayList = new ArrayList<InstagramUserMedia>();
        if (result != null){
            try {
                JSONObject jsonObj = (JSONObject) new JSONTokener(result).nextValue();
                JSONArray data = jsonObj.getJSONArray("data");
                for (int i = 0; i < data.length(); i++) {
                    JSONObject media = data.getJSONObject(i);

                    if (media.getString("type").equalsIgnoreCase("image"))
                    {
                        InstagramUserMedia instagramUserMedia = new InstagramUserMedia();

                        JSONObject image = media.getJSONObject("images");
                        instagramUserMedia.id = media.getString("id");

                        JSONObject low_resolution = image.getJSONObject("low_resolution");
                        instagramUserMedia.low_resolution = low_resolution.getString("url");

                        JSONObject thumbnail = image.getJSONObject("thumbnail");
                        instagramUserMedia.thumbnail = thumbnail.getString("url");

                        JSONObject standard_resolution = image.getJSONObject("standard_resolution");
                        instagramUserMedia.standard_resolution = standard_resolution.getString("url");

                        JSONObject caption = media.getJSONObject("caption");
                        instagramUserMedia.text = caption.getString("text");

                        instagramUserMediaArrayList.add(instagramUserMedia);
                    }
                }

                gridView.setAdapter(new CustomAdapter(context, instagramUserMediaArrayList));

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        else
        {
            Log.e("Result", "Null");
        }

    }

    public class CustomAdapter extends BaseAdapter {
        ArrayList<InstagramUserMedia> instagramUserMediaArrayList;
        Context context;
        private LayoutInflater inflater = null;
        public CustomAdapter(Context context, ArrayList<InstagramUserMedia> instagramUserMediaArrayList) {
            // TODO Auto-generated constructor stub

            this.context=context;
            this.instagramUserMediaArrayList = instagramUserMediaArrayList;
            //inflater = ( LayoutInflater )context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            inflater = LayoutInflater.from(context);

        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return instagramUserMediaArrayList.size();
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

        public class Holder
        {
            TextView tv;
            ImageView img;
        }
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub

            Holder holder;
            /*
            holder = new Holder();
            View rowView;

            rowView = inflater.inflate(R.layout.user_photo_layout, null);
            holder.tv=(TextView) rowView.findViewById(R.id.textView1);
            holder.img=(ImageView) rowView.findViewById(R.id.imageView1);

            holder.tv.setText(instagramUserMediaArrayList.get(position).text);
            ImageLoadTask i = new ImageLoadTask(instagramUserMediaArrayList.get(position).thumbnail, holder.img);


            rowView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    Toast.makeText(context, "You Clicked " + instagramUserMediaArrayList.get(position).id, Toast.LENGTH_LONG).show();
                }
            });

            return rowView;
            */

            /*
            if(convertView == null)
            {
                convertView = inflater.inflate(R.layout.user_photo_layout, null);
                holder = new Holder();
                holder.tv = (TextView) convertView.findViewById(R.id.textView1);
                holder.img = (ImageView) convertView.findViewById(R.id.imageView1);
                convertView.setTag(holder);
            }
            else
            {
                holder = (Holder) convertView.getTag();
            }

            holder.tv.setText(instagramUserMediaArrayList.get(position).text.substring(0, 8) + "...");

            ImageLoadTask i = new ImageLoadTask(instagramUserMediaArrayList.get(position).thumbnail, holder.img);
            i.execute();
*/
            convertView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    Toast.makeText(context, instagramUserMediaArrayList.get(position).text, Toast.LENGTH_LONG).show();
                }
            });

            return convertView;
        }

    }
}
