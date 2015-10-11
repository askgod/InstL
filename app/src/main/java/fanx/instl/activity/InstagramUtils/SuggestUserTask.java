package fanx.instl.activity.InstagramUtils;

/**
 * Created by SShrestha on 10/10/15.
 */

import android.content.Context;
import android.os.AsyncTask;

import android.util.Log;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HttpsURLConnection;

import fanx.instl.activity.SearchUserActivity;

/**
 * Created by SShrestha on 7/10/2015.
 */
public class SuggestUserTask extends AsyncTask <Void, Void, ArrayList<SuggestUserTask.InstaUser>>{
    private final int TIMEOUT_SEC = 5;
    private Context appContext;
    private ListView listView;
    private int count;

    public SuggestUserTask(Context appContext, ListView listView, int count){
        this.appContext = appContext;
        this.listView  = listView;
        this.count = count;
    }
    @Override
    protected ArrayList<InstaUser> doInBackground(Void... param)
    {
        try {
            //Get all the users the authenticated user is following
            ArrayList<String> mFollows = getFollows("self");

            final ArrayList<String> mmFollows = new  ArrayList<String>();

            final ArrayList<InstaUser> mInstaUsers = new ArrayList<InstaUser>();

            ExecutorService es = Executors.newCachedThreadPool();

            //Get all the users being followed by  "the users the authenticated user is following" - (suggestion)
            for (String instaUser: mFollows) {
                final String userId = instaUser;

                es.execute(new Runnable() {
                    @Override
                    public void run() {
                        mmFollows.addAll(getFollows(userId));
                    }
                });

            }

            es.shutdown();
            boolean finshed;
            finshed = es.awaitTermination(TIMEOUT_SEC, TimeUnit.SECONDS);

            if(finshed) {

                Log.e("SuggestedUserTask", "mmFollows"+mmFollows.size());

                //Remove users the authenticated user is currently following
                for (String uId:mFollows) {
                    if (mmFollows.contains(uId))
                        mmFollows.remove(uId);
                }

                //Get the count of media the above users have posted and the count of followers for the users for ordering
                ExecutorService es2 = Executors.newCachedThreadPool();

                for (String _userId : mmFollows) {
                    final String userId = _userId;

                    es2.execute(new Runnable() {
                        @Override
                        public void run() {
                            InstaUser instaUser = getInstaUserData(userId);
                            try {
                                if (instaUser.toString() != null)
                                    mInstaUsers.add(getInstaUserData(userId));
                            } catch (Exception e) {
                                //Log.e("InstaUser", "Missing Values ... ("+userId+")");
                            }
                        }
                    });

                }

                es2.shutdown();

                finshed = es2.awaitTermination(TIMEOUT_SEC*mFollows.size(), TimeUnit.SECONDS);

                if (finshed) {
                    Log.e("SuggestedUserTask", "mInstaUsers"+mInstaUsers.size());
                    Collections.sort(mInstaUsers, new CustomComparator());
                    return mInstaUsers;
                }

            }

            return mInstaUsers;

        } catch (Exception e) {
            Log.d("SuggestUserTask", e.getMessage());
            return null;
        }
    }
    @Override
    protected void onPostExecute(ArrayList<InstaUser> result)
    {
        if (result != null){

            int i = 0;

            ArrayList<InstagramUser> r = new ArrayList<InstagramUser>();

            for (InstaUser iu:result) {
                try{
                    r.add(iu);
                }
                catch (Exception e)
                {}

                if (i++>count)
                    break;
            }

            listView.setAdapter(new InstagramUserSearchTask.SearchResultAdapter(appContext, r));
            listView.setVisibility(ListView.VISIBLE);
            Log.w("Info", "Exit gracefully!");
        }
        /*String txt = "";
        for (InstagramUser iu:r) {
                txt= txt+iu.toString()+"\n----------------\n";

        }
        Log.w("Info","Result Count: "+Integer.toString(result.size())+"\n"+txt);*/

    }


    public ArrayList<String> getFollows(String userId)
    {
        try {
            String urlString = AppData.API_URL + "/users/"+userId+"/follows?access_token="+AppData.getAccessToken(appContext);
            URL url = new URL(urlString);
            HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setDoInput(true);
            int responseCode = urlConnection.getResponseCode();
            if(responseCode == 200)
            {
                ArrayList<String> follows=  new  ArrayList<String>();
                String response = AppData.streamToString(urlConnection.getInputStream());
                JSONObject jsonObj = (JSONObject) new JSONTokener(response).nextValue();
                JSONArray data = jsonObj.getJSONArray("data");


                for (int i = 0; i < data.length(); i++) {
                    JSONObject follow = data.getJSONObject(i);

                    follows.add(follow.getString("id"));
                }

                return follows;
            }
        } catch (Exception e) {
            Log.d("SuggestUserTask", e.getMessage());
        }

        return null;
    }

    public InstaUser getInstaUserData(String userId)
    {
        try {
            String urlString = AppData.API_URL + "/users/"+userId+"/?access_token="+AppData.getAccessToken(appContext);
            //Log.e("URL", urlString);
            URL url = new URL(urlString);
            HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setDoInput(true);
            int responseCode = urlConnection.getResponseCode();
            if(responseCode == 200)
            {
                String response = AppData.streamToString(urlConnection.getInputStream());
                JSONObject jsonObj = (JSONObject) new JSONTokener(response).nextValue();
                JSONObject data = jsonObj.getJSONObject("data");
                JSONObject counts = data.getJSONObject("counts");
                String full_name =  data.getString("full_name");
                int separator = 0;
                try {
                    separator = full_name.indexOf(" ");
                } catch (Exception e) {
                    e.printStackTrace();
                }

                InstaUser i = new InstaUser(userId,
                        data.getString("username"),
                        counts.getString("follows"),
                        counts.getString("followed_by"),
                        counts.getString("media"),
                        data.getString("profile_picture"),
                        separator>0?full_name.substring(0, separator):full_name,
                        separator>0?full_name.substring(separator+1, full_name.length()-separator):""
                );


                return i;
            }
        } catch (Exception e) {
            Log.d("SuggestUserTask", e.getMessage());
        }

        return null;
    }

    public class InstaUser extends InstagramUser
    {
        //inherited
        //public String id;
        //public String username;
        //public String profile_picture;
        //public String first_name;
        //public String last_name;

        public String follows;
        public String followed_by;
        public String media;

        public InstaUser(String id,
                         String username,
                         String follows,
                         String followed_by,
                         String media,
                         String profile_picture,
                         String first_name,
                         String last_name)
        {
            this.id = id;
            this.username = username;
            if (follows == null)
                this.follows = "0";
            else
                this.follows = media;

            if (followed_by == null)
                this.followed_by = "0";
            else
                this.followed_by = followed_by;

            if (media == null)
                this.media = "0";
            else
                this.media = media;

            this.profile_picture = profile_picture;
            this.first_name = first_name;
            this.last_name = last_name;

        }


        public String toString(){
            return "USERID: "+id
                    +"\nUsername: "+username
                    +"\nFirstname: "+ first_name
                    +"\nLastname: "+last_name
                    +"\nFollows: "+follows
                    +"\nFollowedBy: "+ followed_by
                    +"\nMedia: "+media
                    +"\nProfile Pic: "+profile_picture;
        }

    }

    public class CustomComparator implements Comparator<InstaUser> {
        @Override
        public int compare(InstaUser f1, InstaUser f2) {
            try{
                Integer v1 = Integer.parseInt(f1.followed_by);
                Integer v2 = Integer.parseInt(f2.followed_by);
                return v2-v1  ;
            }catch (Exception e){
                System.out.println("Error: 000"+e.getMessage());
                return 0;
            }
        }
    }

}

