package fanx.instl.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import fanx.instag.R;
import fanx.instag.activities.util.InstagramClass;


public class LoginActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        final Button logInButton =  (Button)findViewById(R.id.LoginButton);
        final TextView username = (TextView) findViewById(R.id.editText_Username);
        final TextView password = (TextView) findViewById(R.id.editText_Password);
        logInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    //Log.e("HTML: ", submittingForm());
                    Intent nextIntent = new Intent(LoginActivity.this, MainActivity.class);
                    String param_username =  username.getText().toString();
                    String param_password = password.getText().toString();
                    InstagramClass i = new InstagramClass(LoginActivity.this, param_username, param_password, nextIntent);
                    i.execute();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();  // Always call the superclass method first
        // The activity is either being restarted or started for the first time
        Log.e("LoginActivity", "onStart");
    }

    @Override
    protected void onRestart() {
        super.onRestart();  // Always call the superclass method first
        // Activity being restarted from stopped state
        Log.e("LoginActivity", "onRestart");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.e("LoginActivity", "onPause");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e("LoginActivity", "onResume");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.e("LoginActivity", "onStop");
    }
}