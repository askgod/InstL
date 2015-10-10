package fanx.instl.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.ImageView;

import fanx.instl.R;

public class ImageDetailActivity extends AppCompatActivity {
    ImageView gallery_imageView;
    private String imageURI;

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
}

