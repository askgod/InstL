package fanx.instl.activity.InstagramUtils;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONObject;
import org.json.JSONTokener;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;


/**
 * Created by SShrestha on 23/09/15.
 */
public class InstagramClass extends AsyncTask <Void, Void, Boolean>{
    private List<String> cookies;
    private HttpsURLConnection httpsConnection;
    private Activity currentActivity;
    private String username, password;
    private InstagramSession instagramSession;
    private Intent nextIntent;

    private static final String CLIENT_ID = "7184a42939664823b7a637401a05616d";
    private static final String CLIENT_SECRET = "01754ea077fe4334b979cfd9e6a9864c";
    private static final String REDIRECT_URI = "http://www.vexnepal.org";

    private static final String FORCE_CLASSIC_LOGIN_URL = "https://instagram.com/accounts/login/?force_classic_login=&next=/oauth/authorize/%3Fclient_id%3D"+ CLIENT_ID
            +"%26redirect_uri%3D"+REDIRECT_URI+"%26response_type%3Dcode%26display%3Dtouch%26scope%3Dlikes%2Bcomments%2Brelationships";

    private static final String TOKEN_URL = "https://api.instagram.com/oauth/access_token";
    private static final String API_URL = "https://api.instagram.com/v1";

    final String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:40.0) Gecko/20100101 Firefox/40.0";
    final String ACCEPT = "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8";
    final String ACCEPT_LANGUAGE = "en-GB,en-US;q=0.8,en;q=0.6";
    final String CONTENT_TYPE = "application/x-www-form-urlencoded";

    public InstagramClass(Activity currentActivity, String username, String password, Intent nextIntent)
    {
        this.currentActivity = currentActivity;
        this.nextIntent =  nextIntent;;
        this.username = username;
        this.password = password;
    }
    @Override
    protected Boolean doInBackground(Void... param){

        try {
            // make sure cookies is turn on
            CookieHandler.setDefault(new CookieManager());

            // 1. Send a "GET" request, so that you can extract the form's data.
            String page = this.GetPageContent(FORCE_CLASSIC_LOGIN_URL);
            String postParams = this.getFormParams(page, username, password);

            // 2. Construct above post's content and then send a POST request for authentication & code
            String code = this.getCode(FORCE_CLASSIC_LOGIN_URL, postParams);

            // 3. Request Access Token.
            if (code != null) {
                this.getAccessToken(code);
            }
            else {
                currentActivity.runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(currentActivity, "Invalid Username/Password", Toast.LENGTH_SHORT).show();
                    }
                });
                return false;

            }
        }catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override//Not Working not sure why
    protected void onPostExecute(Boolean result) {
        if (result)
        {
            //Context c = currentActivity.getApplicationContext();

            if (result) {
                Log.e("InstagramClass", "Starting Main Activity");
                Intent loginIntent = new Intent(nextIntent);
                currentActivity.startActivity(loginIntent);
            }
            else {
                Log.e("InstagramClass", "Restarting Login Intent");
                currentActivity.startActivity(currentActivity.getIntent());
            }

        }
    }

    private String getCode(String url, String postParams) throws Exception {

        URL obj = new URL(url);
        // First set the default cookie manager.
        CookieHandler.setDefault(new CookieManager(null, CookiePolicy.ACCEPT_ALL));
        httpsConnection = (HttpsURLConnection) obj.openConnection();

        httpsConnection.setInstanceFollowRedirects(true);  //handle redirect manually.

        // Acts like a browser
        httpsConnection.setUseCaches(false);
        httpsConnection.setRequestMethod("POST");
        httpsConnection.setRequestProperty("Host", "instagram.com");
        httpsConnection.setRequestProperty("User-Agent", USER_AGENT);
        httpsConnection.setRequestProperty("Accept", ACCEPT);
        httpsConnection.setRequestProperty("Accept-Language", ACCEPT_LANGUAGE);

        for (String cookie : this.cookies) {
            httpsConnection.addRequestProperty("Cookie", cookie.split(";", 1)[0]);
        }

        httpsConnection.setRequestProperty("Connection", "keep-alive");
        httpsConnection.setRequestProperty("Referer", url);
        httpsConnection.setRequestProperty("Content-Type", CONTENT_TYPE);
        httpsConnection.setRequestProperty("Content-Length", Integer.toString(postParams.length()));

        httpsConnection.setDoOutput(true);
        httpsConnection.setDoInput(true);


        // Prepare data to send across with post request
        DataOutputStream wr = new DataOutputStream(httpsConnection.getOutputStream());
        wr.writeBytes(postParams);
        wr.flush();
        wr.close();

        System.out.println("Sending post request...");

        // Send post request
        httpsConnection.connect();

        int responseCode = httpsConnection.getResponseCode();

        if(responseCode == 200) {

            InputStream is = httpsConnection.getInputStream();

            String redirectURL = (httpsConnection.getURL()).toString();

            System.out.println(redirectURL);

            if (redirectURL.contains("http://www.vexnepal.org?code=")){
                return redirectURL.replace("http://www.vexnepal.org?code=", "");
            }
            else{
                String actionParam = getFormParams(AppData.streamToString(is));
                URL authorizeURL = new URL(redirectURL);

                HttpsURLConnection httpsConnectionAuthorize = (HttpsURLConnection) authorizeURL.openConnection();

                httpsConnectionAuthorize.setInstanceFollowRedirects(true);  //handle redirect manually.

                // Acts like a browser
                httpsConnectionAuthorize.setUseCaches(false);
                httpsConnectionAuthorize.setRequestMethod("POST");
                httpsConnectionAuthorize.setRequestProperty("Host", "instagram.com");
                httpsConnectionAuthorize.setRequestProperty("User-Agent", USER_AGENT);
                httpsConnectionAuthorize.setRequestProperty("Accept", ACCEPT);
                httpsConnectionAuthorize.setRequestProperty("Accept-Language", ACCEPT_LANGUAGE);

                for (String cookie : this.cookies) {
                    httpsConnectionAuthorize.addRequestProperty("Cookie", cookie.split(";", 1)[0]);
                }

                httpsConnectionAuthorize.setRequestProperty("Connection", "keep-alive");
                httpsConnectionAuthorize.setRequestProperty("Referer", redirectURL);
                httpsConnectionAuthorize.setRequestProperty("Content-Type", CONTENT_TYPE);



                httpsConnectionAuthorize.setRequestProperty("Content-Length", Integer.toString(actionParam.length()));

                httpsConnectionAuthorize.setDoOutput(true);
                httpsConnectionAuthorize.setDoInput(true);
                wr = new DataOutputStream(httpsConnectionAuthorize.getOutputStream());
                wr.writeBytes(actionParam);
                wr.flush();
                wr.close();

                System.out.println("Authorizing request...");

                httpsConnectionAuthorize.connect();
                is = httpsConnectionAuthorize.getInputStream();
                redirectURL = (httpsConnectionAuthorize.getURL()).toString();

                System.out.println(redirectURL);
                if (redirectURL.contains("http://www.vexnepal.org?code="))
                    return redirectURL.replace("http://www.vexnepal.org?code=", "");


            }

            is.close();

        }

        return null;

    }

    private String GetPageContent(String url) throws Exception {

        URL obj = new URL(url);
        httpsConnection = (HttpsURLConnection) obj.openConnection();

        // default is GET
        httpsConnection.setRequestMethod("GET");

        httpsConnection.setUseCaches(false);

        // act like a browser
        httpsConnection.setRequestProperty("User-Agent", USER_AGENT);
        httpsConnection.setRequestProperty("Accept", ACCEPT);
        httpsConnection.setRequestProperty("Accept-Language", ACCEPT_LANGUAGE);

        if (cookies != null) {
            for (String cookie : this.cookies) {
                httpsConnection.addRequestProperty("Cookie", cookie.split(";", 1)[0]);
            }
        }


        System.out.println("\nSending 'GET' request to URL : " + url);
        int responseCode = httpsConnection.getResponseCode();
        System.out.println("Response Code : " + responseCode);

        BufferedReader in = new BufferedReader(new InputStreamReader(httpsConnection.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        // Get the response cookies
        setCookies(httpsConnection.getHeaderFields().get("Set-Cookie"));
        return response.toString();
    }

    public String getFormParams(String html, String username, String password)
            throws UnsupportedEncodingException {

        System.out.println("Extracting form's data...");

        Document doc = Jsoup.parse(html);

        // Google form id
        Element loginform = doc.getElementById("login-form");
        Elements inputElements = loginform.getElementsByTag("input");
        List<String> paramList = new ArrayList<String>();
        for (Element inputElement : inputElements) {
            String key = inputElement.attr("name");
            String value = inputElement.attr("value");

            if (key.equals("username"))
                value = username;
            else if (key.equals("password"))
                value = password;

            if(!key.equals(""))
                paramList.add(key + "=" + URLEncoder.encode(value, "UTF-8"));
        }
        // build parameters list
        StringBuilder result = new StringBuilder();
        for (String param : paramList) {
            if (result.length() == 0) {
                result.append(param);
            } else {
                result.append("&" + param);
            }
        }

        return result.toString();

    }

    public String getFormParams(String html) throws UnsupportedEncodingException
    {
        System.out.println("Extracting form's data...");

        Document doc = Jsoup.parse(html);

        // Google form id
        Elements actionform = doc.getElementsByClass("form-actions");
        Elements inputElements = actionform.get(0).getElementsByTag("input");
        List<String> paramList = new ArrayList<String>();
        for (Element inputElement : inputElements) {
            String key = inputElement.attr("name");
            String value = inputElement.attr("value");

            if (key.equals("allow")) {
                value = "Authorize";
                System.out.println("Value Changed");
            }

            if(!key.equals(""))
                paramList.add(key + "=" + URLEncoder.encode(value, "UTF-8"));
        }
        // build parameters list
        StringBuilder result = new StringBuilder();
        for (String param : paramList) {
            if (result.length() == 0) {
                result.append(param);
            } else {
                result.append("&" + param);
            }
        }

        return result.toString();
    }

    private void getAccessToken(final String code) throws  Exception{
        String mAccessToken;

        Log.i("AccessToken", "Getting access token");

        URL url = new URL(TOKEN_URL);

        Log.i("AccessToken", "Opening Token URL " + url.toString());
        HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
        urlConnection.setRequestMethod("POST");
        urlConnection.setDoInput(true);
        urlConnection.setDoOutput(true);

        OutputStreamWriter writer = new OutputStreamWriter(urlConnection.getOutputStream());
        writer.write("client_id=" + CLIENT_ID +
                "&client_secret=" + CLIENT_SECRET +
                "&grant_type=authorization_code" +
                "&redirect_uri=" + REDIRECT_URI +
                "&code=" + code);
        writer.flush();
        String response = streamToString(urlConnection.getInputStream());
        Log.i("AccessToken", "response " + response);
        JSONObject jsonObj = (JSONObject) new JSONTokener(response).nextValue();

        mAccessToken = jsonObj.getString("access_token");
        Log.i("AccessToken", "Got access token: " + mAccessToken);

        String id = jsonObj.getJSONObject("user").getString("id");
        String user = jsonObj.getJSONObject("user").getString("username");
        String name = jsonObj.getJSONObject("user").getString("full_name");
        String profile_picture = jsonObj.getJSONObject("user").getString("profile_picture");
        instagramSession = new InstagramSession(currentActivity);
        instagramSession.storeAccessToken(mAccessToken, id, user, name, profile_picture);
    }

    private String streamToString(InputStream is) throws Exception
    {String str = "";
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
            }
            finally {
                is.close();
            }

            str = sb.toString();
        }

        return str;
    }
    public List<String> getCookies() {
        return cookies;
    }

    public void setCookies(List<String> cookies) {
        this.cookies = cookies;
    }
}
