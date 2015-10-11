package fanx.instl.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;

import butterknife.Bind;
import butterknife.OnClick;
import fanx.instl.R;

public class ImageDetailActivity extends AppCompatActivity {
    ImageView gallery_imageView;
    private String imageURI = null;
    private String imageName = null;

    @Bind(R.id.fab_edit)
    FloatingActionButton fab_edit;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_detail2);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent previousIntent = getIntent();
        imageURI = previousIntent.getStringExtra("picPath");
        Log.v("pic path", imageURI);
        imageName = imageURI.substring(26,43);
        setTitle(imageName);

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
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_share:
                share();
                return true;
            case R.id.action_delete:
                deleteImage(imageURI);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private Toast toast;
    private void deleteImage(String imageURI) {
        File file = new File(imageURI);
        boolean deleted = file.delete();
        String deleteStatus = " Deleted!";
        Log.i("info", "File: " + imageURI + String.valueOf(deleted));
        if (deleted != true ){
            deleteStatus = " Delete fail!";
        }
        toast = Toast.makeText(this, "File: " + imageURI + deleteStatus,
                Toast.LENGTH_SHORT);
        toast.show();
        finish();
    }

    public void share() {
        Log.i("info", String.valueOf(imageURI));
        Uri imagePath = Uri.parse(imageURI);
        PublishActivity.openWithPhotoUri(this, imagePath);
        finish();
    }

    @OnClick (R.id.fab)
    public void edit_Image (){

    }

}

