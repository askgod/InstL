package fanx.instl.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;

import fanx.instl.BuildConfig;
import fanx.instl.ui.ImageGridFragment;
import fanx.instl.utils.Utils;

public class GalleryActivity extends FragmentActivity {

    private static final String TAG = "GalleryAcitivty";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (BuildConfig.DEBUG) {
            Utils.enableStrictMode();
        }
        

        if (getSupportFragmentManager().findFragmentByTag(TAG) == null) {
            final FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.add(android.R.id.content, new ImageGridFragment(), TAG);
            ft.commit();
        }
    }


}
