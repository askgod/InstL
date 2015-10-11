package fanx.instl.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TableLayout;

import butterknife.Bind;
import butterknife.OnClick;
import fanx.instl.R;

public class ImageDetailActivity extends AppCompatActivity {
    ImageView gallery_imageView;
    private String imageURI;

    @Bind(R.id.fab_edit)
    FloatingActionButton fab_edit;

    @Bind(R.id.action_delete)
    FloatingActionButton action_delete;

    @Bind(R.id.action_share)
    FloatingActionButton action_share;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_detail2);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent previousIntent = getIntent();
        imageURI = previousIntent.getStringExtra("picPath");
        Log.v("pic path", imageURI);
        gallery_imageView = (ImageView)findViewById(R.id.gallery_imageView);
        Bitmap bitmap = BitmapFactory.decodeFile(imageURI);

        gallery_imageView.setImageBitmap(bitmap);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_imagedetail, menu);
        super.onCreateOptionsMenu(menu);

        return true;
    }

    @OnClick (R.id.action_share)
    public void action_share() {
        Log.i("info", String.valueOf(imageURI));
        Uri imagePath = Uri.parse(imageURI);
        PublishActivity.openWithPhotoUri(this, imagePath);
        finish();
    }


}

