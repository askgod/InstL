package fanx.instl.activity.InstagramUtils;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by SShrestha on 11/10/2015.
 */
public class InstagramMediaCommentTask extends AsyncTask <String, Void, String>{

    private String mediaId;
    private Context context;

    public InstagramMediaCommentTask( String mediaId,
                                      Context context){
        this.mediaId = mediaId;
        this.context = context;
    }

    protected String doInBackground(String... parameter){

        if (!commentCheck(parameter[0]))
        {
            return "{\"meta\": {\"code\": 000},\"data\": "+parameter[0].replace("\"", "\\\"")+"}";
        }
        try {
            URL url = new URL("https://api.instagram.com/v1/media/"+mediaId+"/comments");

            HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
            urlConnection.setRequestMethod("POST");
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);

            OutputStreamWriter writer = new OutputStreamWriter(urlConnection.getOutputStream());
            writer.write("access_token=" + AppData.getAccessToken(context) +
                    "&text="+parameter[0]);
            writer.flush();

            String response = AppData.streamToString(urlConnection.getInputStream());
            if(response.contains("\"code\": 200"))
                return response;
            else
                return "{\"meta\": {\"code\": 200},\"data\": null}";

        } catch (IOException e) {
            e.printStackTrace();
        }
        return "{\"meta\": {\"code\": 000},\"data\": "+parameter[0].replace("\"", "\\\"")+"}";
    }

    protected void onPostExecute(String result)
    {
        Log.e("Info", result);
    }

    //Validate
    protected boolean commentCheck(String s)
    {
        if (s.length() > 300)
            return false;
        else {
            String[] t = s.split("#", 5);
            if (t.length>5)
            {
                return false;
            }
            else{
                String regularExpression = "((http|ftp|https):\\/\\/)?[\\w\\-_]+(\\.[\\w\\-_]+)+([\\w\\-\\.,@?^=%&amp;:/~\\+#]*[\\w\\-\\@?^=%&amp;/~\\+#])?";
                String[] t2 = s.split(regularExpression, 3);
                Log.w("Info", Integer.toString(t2.length));
                if (t2.length > 2)
                    return false;
                else{
                    String regularExpression2 = "^(?=.*[a-z]).+$";
                    return s.matches(regularExpression);
                }
            }
        }
    }


}
