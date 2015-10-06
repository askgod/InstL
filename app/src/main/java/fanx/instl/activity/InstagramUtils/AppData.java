package fanx.instl.activity.InstagramUtils;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by SShrestha on 7/09/2015.
 *
 */
public class AppData {
    private final String API_URL = "https://api.instagram.com/v1";
    private final String SHARED = "Instagram_Preferences";

    public String response;

    public UserData[] searchUser(Context context, final String searchText, final int resultCount) {
        final String mAccessToken = getAccessToken(context);
        response = null;
        try {
            new Thread() {
                public void run() {
                    try {
                        String searchURL = API_URL + "/users/search?q=" + searchText + "&count=" + String.valueOf(resultCount) + "&access_token=" + mAccessToken;

                        URL url = new URL(searchURL);
                        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                        urlConnection.setRequestMethod("GET");
                        urlConnection.setDoInput(true);
                        int responseCode = urlConnection.getResponseCode();
                        urlConnection.connect();
                        response = streamToString(urlConnection.getInputStream());

                    } catch (Exception e) {
                        System.out.print(e.fillInStackTrace());
                    }
                }
            }.start();
            //Commented by: Sandip Shrestha
            //I have spanned a new thread which will execute asynchronously, therefore waiting for the response from new thread
            while (response == null);

            if (!response.equalsIgnoreCase("!")) {
                JSONObject jsonObj = (JSONObject) new JSONTokener(response).nextValue();
                JSONArray ar = jsonObj.getJSONArray("data");
                UserData ud[] = new UserData[ar.length()];
                for (int i = 0; i < ar.length(); i++) {
                    JSONObject obj = ar.getJSONObject(i);
                    UserData _ud = new UserData();
                    _ud.first_name = obj.getString("first_name");
                    _ud.last_name = obj.getString("last_name");
                    _ud.id = obj.getString("id");
                    _ud.username = obj.getString("username");
                    _ud.profile_picture = obj.getString("profile_picture");
                    ud[i] = _ud;

                }
                return ud;
            }
            else {
                return null;
            }

        } catch (Exception e) {
            System.out.print(e.fillInStackTrace());
            return null;
        }
    }


    public static String streamToString(InputStream is) throws IOException {
        String str = "";
        if (is != null) {
            StringBuilder sb = new StringBuilder();
            String line;

            try {
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(is));

                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }

                reader.close();
            } finally {
                is.close();
            }

            str = sb.toString();
        }

        return str;
    }

    public static void createInstagramIntent(String type, String mediaPath, Context context){

        // Create the new Intent using the 'Send' action.
        Intent share = new Intent(Intent.ACTION_SEND);

        // Set the MIME type
        share.setType(type);

        // Create the URI from the media
        File media = new File(mediaPath);
        Uri uri = Uri.fromFile(media);

        // Add the URI to the Intent.
        share.putExtra(Intent.EXTRA_STREAM, uri);

        // Broadcast the Intent.
        context.startActivity(Intent.createChooser(share, "Share to"));
    }

    public class UserData
    {
        public String id;
        public String username;
        public String first_name;
        public String last_name;
        public String profile_picture;
        UserData(){}
    }


    public static String getAccessToken(Context c)
    {
        SharedPreferences s = c.getSharedPreferences("Instagram_Preferences", Context.MODE_PRIVATE);
        return s.getString("access_token",null);
    }

    public boolean hasAccessToken(Context c)
    {
        if (this.getAccessToken(c) != null )
            return true;
        else
            return false;
    }


    public static String getUserId(Context c){
        SharedPreferences s = c.getSharedPreferences("Instagram_Preferences", Context.MODE_PRIVATE);
        return s.getString("id",null);
    }



}

