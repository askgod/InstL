package fanx.instl.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.ThumbnailUtils;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.NavigationView;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

import fanx.instl.R;
import fanx.instl.ui.RevealBackgroundView;

public class GalleryActivity extends BaseDrawerActivity implements RevealBackgroundView.OnStateChangeListener{
    private GridView gallery_grid;
    private FileAdapter fileAdapter;
    public static final String ARG_REVEAL_START_LOCATION = "reveal_start_location";

    public static void startGalleryFromLocation(int[] startingLocation, Activity startingActivity) {
        Intent intent = new Intent(startingActivity, GalleryActivity.class);
        intent.putExtra(ARG_REVEAL_START_LOCATION, startingLocation);
        startingActivity.startActivity(intent);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);
        NavigationView navigationView = (NavigationView) findViewById(R.id.vNavigation);
        navigationView.setNavigationItemSelectedListener(this);

        setupViews();
        setProgressBarIndeterminateVisibility(true);
        new AsyncLoadedImage().execute();

    }

    /*
     * 初始化文件浏览View
     */
    private void setupViews() {
        gallery_grid = (GridView) findViewById(R.id.gallery_gridview);
        gallery_grid.setOnItemClickListener(new fileListener());
        fileAdapter = new FileAdapter(getApplicationContext());
        gallery_grid.setAdapter(fileAdapter);
    }

    /*
     * 释放Bitmap内存
     */
    protected void onDestroy() {
        super.onDestroy();
        final GridView grid = gallery_grid;
        final int count = grid.getChildCount();
        ImageView v = null;
        for (int i = 0; i < count; i++) {
            v = (ImageView) grid.getChildAt(i);
            ((BitmapDrawable) v.getDrawable()).setCallback(null);
        }
    }

    /*
     * 刷新Adapter
     */
    private void addImage(LoadedImage... value) {
        for (LoadedImage image : value) {
            fileAdapter.addPhoto(image);
            fileAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onStateChange(int state) {

    }

    /*
     * 点击监听
     */
    class fileListener implements OnItemClickListener {
        public void onItemClick(AdapterView<?> paramAdapterView,
                                View paramView, int paramInt, long paramLong) {
            LoadedImage loadedImage = (LoadedImage) gallery_grid.getItemAtPosition(paramInt);

            showMsg(""+loadedImage.getPicPath());

            Intent intentPicView = new Intent(getBaseContext(), ImageDetailActivity.class);
            intentPicView.putExtra("picPath", loadedImage.getPicPath());
            startActivity(intentPicView);
        }
    }

    /*
     * 异步加载缩略图到LoadedImage然后调用addImage方法更新Adapter
     */
    class AsyncLoadedImage extends AsyncTask<Object, LoadedImage, Object> {
        @Override
        protected Object doInBackground(Object... params) {
            String path = Environment.getExternalStorageDirectory().getPath() + "/InstL";
            File file = new File(path);
            if (!file.exists()) {
                file.mkdirs();
            } else {
                File[] files = file.listFiles();
                String[] paths = new String[files.length];
                Bitmap bitmap;
                Bitmap newBitmap;
                for (int i = 0; i < files.length; i++) {
                    paths[i] = files[i].getPath();
                    try {
                        BitmapFactory.Options options = new BitmapFactory.Options();
                        options.inSampleSize = 2;
                        bitmap = BitmapFactory.decodeFile(paths[i], options);
                        newBitmap = ThumbnailUtils.extractThumbnail(bitmap,
                                330, 330);
                        if (bitmap != null) {
                            bitmap.recycle();
                        }
                        if (newBitmap != null) {
                            publishProgress(new LoadedImage(newBitmap,paths[i] ));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            return null;
        }

        //实时更新UI  onProgressUpdate()方法的参数对应于doInBackground中publishProgress方法的参数，同时也对应于
        //doInBackground的第二个参数
        @Override
        public void onProgressUpdate(LoadedImage... value) {
            addImage(value);
        }

        //更新UI结束后的处理
        @Override
        protected void onPostExecute(Object result) {
            setProgressBarIndeterminateVisibility(false);
        }
    }

    /*
     * Adapter
     */
    class FileAdapter extends BaseAdapter {

        private Context mContext;
        private ArrayList<LoadedImage> photos = new ArrayList<LoadedImage>();

        public FileAdapter(Context context) {
            mContext = context;
        }

        public void addPhoto(LoadedImage photo) {
            photos.add(photo);
        }

        public int getCount() {
            return photos.size();
        }

        public Object getItem(int position) {
            return photos.get(position);
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            final ImageView imageView;
            if (convertView == null) {
                imageView = new ImageView(mContext);
            } else {
                imageView = (ImageView) convertView;
            }
            imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            imageView.setPadding(0, 1, 1, 1);
            imageView.setImageBitmap(photos.get(position).getBitmap());

            return imageView;
        }
    }

    /*
     * 这是个保存bitmap的类，加入Adapter的ArrayList中，随着addImage更新Adapter
     */
    private static class LoadedImage {
        String picPath;
        Bitmap mBitmap;

        LoadedImage(Bitmap bitmap,String picPath ) {
            this.mBitmap = bitmap;
            this.picPath = picPath;
        }

        public Bitmap getBitmap() {
            return mBitmap;
        }

        public String getPicPath() {
            return picPath;
        }


    }

    /*
     * 消息提示
     */
    private Toast toast;

    public void showMsg(String arg) {
        if (toast == null) {
            toast = Toast.makeText(this, arg, Toast.LENGTH_SHORT);
        } else {
            toast.cancel();
            toast.setText(arg);
        }
        toast.show();
    }

    public void onPause() {
        super.onPause();
    }

    public void onResume() {
        super.onResume();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            Intent intent = new Intent(getBaseContext(), UserFeedActivity.class);
            startActivity(intent);
            finish();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

}