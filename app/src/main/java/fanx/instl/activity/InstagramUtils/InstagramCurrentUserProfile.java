package fanx.instl.activity.InstagramUtils;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;


/**
 * Created by SShrestha on 26/09/2015.
 */
public class InstagramCurrentUserProfile extends AsyncTask <Void, Void, JSONObject>{
    private final String API_URL = "https://api.instagram.com/v1";
    private final String SHARED = "Instagram_Preferences";

    TextView textView_full_name;
    TextView textview_username;
    TextView textview_bio;
    TextView textView_website;
    TextView textView_mediaCounts;
    TextView textView_followsCounts;
    TextView textView_followed_byCounts;
    ImageView imageView_profile_picture;
    Context context_userProfileActivity;

    public InstagramCurrentUserProfile(Context context_userProfileActivity,
                                       TextView textView_full_name,
                                       TextView textview_username,
                                       TextView textview_bio,
                                       TextView textView_website,
                                       TextView textView_mediaCounts,
                                       TextView textView_followsCounts,
                                       TextView textView_followed_byCounts,
                                       ImageView imageView_profile_picture)
    {
        this.textView_full_name = textView_full_name;
        this.textview_username = textview_username;
        this.textview_bio = textview_bio;
        this.textView_website = textView_website;
        this.textView_mediaCounts = textView_mediaCounts;
        this.textView_followsCounts = textView_followsCounts;
        this.textView_followed_byCounts = textView_followed_byCounts;
        this.imageView_profile_picture = imageView_profile_picture;
        this.context_userProfileActivity = context_userProfileActivity;

    }
    // Second constructor for drawer view by FanX

    public InstagramCurrentUserProfile(Context context_userProfileActivity,
                                       TextView textView_full_name,
                                       ImageView imageView_profile_picture) {

        this.textView_full_name = textView_full_name;
        this.imageView_profile_picture = imageView_profile_picture;
        this.context_userProfileActivity = context_userProfileActivity;
    }


    protected JSONObject doInBackground(Void... params) {


                try {

                    String profileURL = API_URL + "/users/" + getUserId(context_userProfileActivity) + "/?access_token=" + getAccessToken(context_userProfileActivity);
                    URL url = new URL(profileURL);
                    HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("GET");
                    urlConnection.setDoInput(true);
                    int responseCode = urlConnection.getResponseCode();
                    urlConnection.connect();
                    String response = AppData.streamToString(urlConnection.getInputStream());

                    JSONObject jsonObj = (JSONObject) new JSONTokener(response).nextValue();
                    JSONObject result = jsonObj.getJSONObject("data");
                    urlConnection.disconnect();

                    return result;

                } catch (Exception e) {
                    e.getStackTrace();
                }

        return null;
    }

    protected void onPostExecute(JSONObject result){
        try {
            textView_full_name.setText(result.getString("full_name"));
            textview_username.setText(result.getString("username"));
            textview_bio.setText(result.getString("bio"));
            textView_website.setText(result.getString("website"));
            final String  profile_picture = result.getString("profile_picture");

            JSONObject counts = result.getJSONObject("counts");
            textView_mediaCounts.setText(counts.getString("media"));
            textView_mediaCounts.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
            textView_mediaCounts.setTextColor(Color.parseColor("#0000ff"));
            textView_mediaCounts.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Intent i = new Intent(context_userProfileActivity, DisplayUserPhotoActivity.class);
                    //context_userProfileActivity.startActivity(i);
                }
            });

            textView_followsCounts.setText(counts.getString("follows"));
            textView_followed_byCounts.setText(counts.getString("followed_by"));


            //Use default picture or sone ijax images to represent loading
            ImageLoadTask imageLoadTask = new ImageLoadTask(profile_picture, imageView_profile_picture);
            imageLoadTask.execute();


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getAccessToken(Context c)
    {
        SharedPreferences s = c.getSharedPreferences(SHARED, Context.MODE_PRIVATE);
        return s.getString("access_token",null);
    }

    public boolean hasAccessToken(Context c)
    {
        if (this.getAccessToken(c) != null )
            return true;
        else
            return false;
    }

    public String getUserId(Context c){
        SharedPreferences s = c.getSharedPreferences(SHARED, Context.MODE_PRIVATE);
        return s.getString("id",null);
    }

}
