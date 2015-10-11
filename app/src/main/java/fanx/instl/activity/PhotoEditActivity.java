package fanx.instl.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;

import fanx.instl.R;

public class PhotoEditActivity extends BaseActivity {
    public static final String ARG_REVEAL_START_LOCATION = "reveal_start_location";
    ImageView edit_imageView;
    private String imageURI = null;
    private String imageName = null;
    Bitmap bitmap = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_edit);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        View view = findViewById(R.id.edit_imageView);
        view.setBackgroundColor(0xFF000000);

        Intent previousIntent = getIntent();
        imageURI = previousIntent.getStringExtra("picPath");
        Log.v("pic path", imageURI);
        imageName = imageURI.substring(26,43);
        setTitle(imageName);

        edit_imageView = (ImageView)findViewById(R.id.edit_imageView);
        Bitmap bitmap = BitmapFactory.decodeFile(imageURI);
        edit_imageView.setImageBitmap(bitmap);

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_view_edit_photo, menu);
        super.onCreateOptionsMenu(menu);

        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_crop:
                crop();
                return true;
            case R.id.action_delete:
                deleteImage(imageURI);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void crop() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT, null);
        intent.setType("image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 2);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 200);
        intent.putExtra("outputY", 100);
        intent.putExtra("scale", true);
        intent.putExtra("return-data", false);
        intent.putExtra("output", imageURI);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra("noFaceDetection", false);
        startActivityForResult(intent, 1);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK) {// result is not correct
            return;
        } else {
            switch (requestCode) {
                case 1:
                    if (imageURI != null) {
                        bitmap = BitmapFactory.decodeFile(imageURI);
                        edit_imageView.setImageBitmap(bitmap);
                    }
                    break;
                default:
                    break;
            }
        }
        edit_imageView.setImageBitmap(bitmap);
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



}

