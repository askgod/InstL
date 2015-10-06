package fanx.instl.InstagramUtils;

/**
 * Created by SShrestha on 25/09/2015.
 */

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import fanx.instag.activities.LoginActivity;

/**
 * Created by SShrestha on 25/09/2015.
 */
public class InstagramSession {

    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editor;

    private final String SHARED = "Instagram_Preferences";
    private final String API_USERNAME = "username";
    private final String API_ID = "id";
    private final String API_NAME = "name";
    private final String API_ACCESS_TOKEN = "access_token";
    private final String PROFILE_PICTURE = "profile_picture";


    public InstagramSession(Context context) {
        sharedPref = context.getSharedPreferences(SHARED, Context.MODE_PRIVATE);
        editor = sharedPref.edit();
    }

    /**
     *
     * @param -accessToken
     * @param -expireToken
     * @param -expiresIn
     * @param -username
     */
    public void storeAccessToken(String accessToken, String id, String username, String name, String profile_picture) {
        editor.putString(API_ID, id);
        editor.putString(API_NAME, name);
        editor.putString(API_ACCESS_TOKEN, accessToken);
        editor.putString(API_USERNAME, username);
        editor.putString(PROFILE_PICTURE, profile_picture);
        editor.commit();
    }

    public void storeAccessToken(String accessToken) {
        editor.putString(API_ACCESS_TOKEN, accessToken);
        editor.commit();
    }

    /**
     * Reset access token and user name
     */
    public void resetAccessToken(Activity a) {
        editor.putString(API_ID, null);
        editor.putString(API_NAME, null);
        editor.putString(API_ACCESS_TOKEN, null);
        editor.putString(API_USERNAME, null);
        editor.putString(PROFILE_PICTURE, null);
        editor.commit();
        Intent intent = new Intent(a, LoginActivity.class);
        a.startActivity(intent);
    }

    /**
     * Get user name
     *
     * @return User name
     */
    public String getUsername() {
        return sharedPref.getString(API_USERNAME, null);
    }

    /**
     *
     * @return
     */
    public String getId() {
        return sharedPref.getString(API_ID, null);
    }

    /**
     *
     * @return
     */
    public String getName() {
        return sharedPref.getString(API_NAME, null);
    }

    /**
     *
     * @return
     */
    public String getProfilePicture() {
        return sharedPref.getString(PROFILE_PICTURE, null);
    }


    /**
     * Get access token
     *
     * @return Access token
     */
    public String getAccessToken() {
        return sharedPref.getString(API_ACCESS_TOKEN, null);
    }

    public boolean hasAccessToken() {
        return (getAccessToken() == null) ? false : true;
    }

}