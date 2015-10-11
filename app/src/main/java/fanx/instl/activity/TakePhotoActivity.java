package fanx.instl.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ViewSwitcher;

import java.io.File;
import java.io.FileOutputStream;

import butterknife.Bind;
import butterknife.OnClick;
import fanx.instl.R;
import fanx.instl.ui.RevealBackgroundView;
import fanx.instl.utils.CameraPreview;
import fanx.instl.utils.Utils;

public class TakePhotoActivity extends BaseActivity
        implements RevealBackgroundView.OnStateChangeListener {

    public static final String ARG_REVEAL_START_LOCATION = "reveal_start_location";

    private static final Interpolator ACCELERATE_INTERPOLATOR = new AccelerateInterpolator();
    private static final Interpolator DECELERATE_INTERPOLATOR = new DecelerateInterpolator();
    private static final int STATE_TAKE_PHOTO = 0;
    private static final int STATE_SETUP_PHOTO = 1;
    protected static final String TAG = "main";

    static String lastFilePath = null;

    @Bind(R.id.vRevealBackground)
    RevealBackgroundView vRevealBackground;

    @Bind(R.id.vPhotoRoot)
    View vTakePhotoRoot;

    @Bind(R.id.vShutter)
    View vShutter;

    @Bind(R.id.ivTakenPhoto)
    ImageView ivTakenPhoto;

    @Bind(R.id.vUpperPanel)
    ViewSwitcher vUpperPanel;

    @Bind(R.id.vLowerPanel)
    ViewSwitcher vLowerPanel;

    //@Bind(R.id.cameraView)
    //View cameraView;
    //@Bind(R.id.rvFilters)
    //RecyclerView rvFilters;
    private boolean pendingIntro;
    private int currentState;

    @Bind(R.id.btnTakePhoto)
    Button btnTakePhoto;

    private File photoPath;

    // Grid Line & Flash Light
    private boolean isLightOn = false;
    private boolean gridLineOn = false;

    @Bind(R.id.camera_flashLight)
    ImageButton camera_flashLight;

    @Bind(R.id.camera_grid)
    ImageButton camera_grid;

    @Bind(R.id.camera_cancel)
    ImageButton camera_cancel;

    @Bind(R.id.camera_filter1)
    ImageButton camera_filter1;

    @Bind(R.id.camera_filter2)
    ImageButton camera_filter2;

    @Bind(R.id.camera_filter3)
    ImageButton camera_filter3;


    @Bind(R.id.camera_filter4)
    ImageButton camera_filter4;

    @Bind(R.id.camera_remove_filter)
    ImageButton camera_remove_filter;

    @Bind(R.id.btn_file_chooser)
    ImageButton btn_file_chooser;

    @Bind(R.id.camera_grid_line)
    ImageView camera_grid_line;

    //@Bind(R.id.camera_bright)
    //ImageButton camera_bright;

    private Camera mCamera;
    private CameraPreview mPreview;

    private static String fullPath;

    Bitmap bitmap;

    private int pick_result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_photo);
        updateStatusBarColor();
        updateState(STATE_TAKE_PHOTO);
        setupRevealBackground(savedInstanceState);
        camera_grid_line.setVisibility(View.INVISIBLE);
        //

        vUpperPanel.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                vUpperPanel.getViewTreeObserver().removeOnPreDrawListener(this);
                pendingIntro = true;
                vUpperPanel.setTranslationY(-vUpperPanel.getHeight());
                vLowerPanel.setTranslationY(vLowerPanel.getHeight());
                return true;
            }
        });
        // Camera
        mCamera = getCameraInstance();
        mCamera.setDisplayOrientation(90);
        Camera.Parameters params = mCamera.getParameters();
        params.setRotation(90);
        mCamera.setParameters(params);
        mCamera.startPreview();
        // Create a preview class
        mPreview = new CameraPreview(this, mCamera);
        FrameLayout preview = (FrameLayout) findViewById(R.id.cameraView);
        preview.addView(mPreview);

        fullPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/InstL/";


    }

    //Open Camera //
    public Camera getCameraInstance() {
        Camera c = null;

        try {
            c = Camera.open();
            updateState(STATE_TAKE_PHOTO);
        } catch (Exception e) {
            Log.d(TAG, "Fail to open camera");
        }
        return c;
    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void updateStatusBarColor() {
        if (Utils.isAndroid5()) {
            getWindow().setStatusBarColor(0xff111111);
        }
    }

    @Override
    protected void onResume() {
        if (pick_result == RESULT_OK){
            mCamera.release();
        }
        super.onResume();
    }

    @Override
    protected void onDestroy() {

        // Release Camera
        if(mCamera!=null){
            mCamera.release();
            mCamera=null;
        }
        super.onDestroy();
    }

    @Override
    protected void onStop(){
        super.onStop();

        if(mCamera!=null){
            mCamera.release();
        }
    }
    // Inject bind views functions
    @OnClick(R.id.btnTakePhoto)
    public void onTakePhotoClick() {
        btnTakePhoto.setEnabled(false);
        mCamera.autoFocus(new Camera.AutoFocusCallback() {
            @Override
            public void onAutoFocus(boolean success, Camera camera) {
                // Capture from camera
                mCamera.takePicture(null, null, mPicture);
                vUpperPanel.showNext();
                vLowerPanel.showNext();
                updateState(STATE_SETUP_PHOTO);
            }
        });

        animateShutter();

    }


    @OnClick(R.id.btnAccept)
    public void onAcceptClick() {
        saveImage();
        Log.i("info", String.valueOf(photoPath));
        PublishActivity.openWithPhotoUri(this, Uri.fromFile(photoPath));
        finish();
    }

    @OnClick(R.id.camera_grid)
    public void onSwitchGridLine() {
        if (gridLineOn == false) {
            Log.i("info", "Gridline is turn off!");
            camera_grid_line.setVisibility(View.VISIBLE);
            gridLineOn = true;
        } else if (gridLineOn == true) {
            Log.i("info", "Gridline is turn off!");
            camera_grid_line.setVisibility(View.INVISIBLE);
            gridLineOn = false;
        }
    }

    @OnClick(R.id.camera_flashLight)
    public void onSwitchFlash(){
        Camera.Parameters cameraParameter = mCamera.getParameters();
        if (isLightOn) {
            Log.i("info", "torch is turn off!");
            cameraParameter.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
            mCamera.setParameters(cameraParameter);
            mCamera.startPreview();
            isLightOn = false;
        } else {
            Log.i("info", "torch is turn on!");
            cameraParameter.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
            mCamera.setParameters(cameraParameter);
            mCamera.startPreview();
            isLightOn = true;
        }
    }

    @OnClick(R.id.camera_cancel)
    public void camera_cancel (){
        Log.i("info", "Camera activity cancelled!");
        finish();
    }
    // Callback from camera
    private Camera.PictureCallback mPicture = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            /*BitmapFactory.Options opts = new BitmapFactory.Options();
            Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
            ivTakenPhoto.setImageBitmap(bitmap);
            // Save jpg to sd
            String root = Environment.getExternalStorageDirectory().toString();
            Log.d(TAG, root);
            File pictureFile = new File(root + "/InstL/1st" + System.currentTimeMillis()
                    + ".jpg");
            try {
                FileOutputStream fos = new FileOutputStream(pictureFile);
                fos.write(data);
                fos.close();
            } catch (Exception e) {
                Log.d(TAG, "Fail to save picture");
            }*/
            Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
            ivTakenPhoto.setImageBitmap(bitmap);
            //saveImage();
            //showTakenPicture(bitmap);

        }
    };

    private void saveImage(){
        String filename = String.valueOf(System.currentTimeMillis());
        bitmap = loadBitmapFromView(ivTakenPhoto);
;
        File file = new File(fullPath + filename + ".jpg");
        File dir = new File(fullPath);
        try {
            if (!dir.exists()) {
                dir.mkdirs();
            }
            if (file.exists()) {
                file.delete();
            }

            FileOutputStream out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 80, out);
            // End
            out.flush();
            out.close();

        }
        catch (Exception e) {
            Log.e("saveToExternalStorage()", e.getMessage());
        }
        photoPath = dir;
        lastFilePath = String.valueOf(file);
        Log.v(TAG, lastFilePath);
    }

    @Override
    public void onStateChange(int state) {
        if (RevealBackgroundView.STATE_FINISHED == state) {
            vTakePhotoRoot.setVisibility(View.VISIBLE);
            if (pendingIntro) {
                startIntroAnimation();
            }
        } else {
            vTakePhotoRoot.setVisibility(View.INVISIBLE);
        }
    }


    private void showTakenPicture(Bitmap bitmap) {
        //vUpperPanel.showNext();
        //vLowerPanel.showNext();
        ivTakenPhoto.setImageBitmap(bitmap);
        updateState(STATE_SETUP_PHOTO);
    }

    @Override
    public void onBackPressed() {
        if (currentState == STATE_SETUP_PHOTO) {
            btnTakePhoto.setEnabled(true);
            vUpperPanel.showNext();
            vLowerPanel.showNext();
            updateState(STATE_TAKE_PHOTO);
        } else {
            super.onBackPressed();
        }
    }

    private void updateState(int state) {
        currentState = state;
        if (currentState == STATE_TAKE_PHOTO) {
            vUpperPanel.setInAnimation(this, R.anim.slide_in_from_right);
            vLowerPanel.setInAnimation(this, R.anim.slide_in_from_right);
            vUpperPanel.setOutAnimation(this, R.anim.slide_out_to_left);
            vLowerPanel.setOutAnimation(this, R.anim.slide_out_to_left);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    ivTakenPhoto.setVisibility(View.GONE);
                }
            }, 400);
        } else if (currentState == STATE_SETUP_PHOTO) {
            vUpperPanel.setInAnimation(this, R.anim.slide_in_from_left);
            vLowerPanel.setInAnimation(this, R.anim.slide_in_from_left);
            vUpperPanel.setOutAnimation(this, R.anim.slide_out_to_right);
            vLowerPanel.setOutAnimation(this, R.anim.slide_out_to_right);
            ivTakenPhoto.setVisibility(View.VISIBLE);
        }
    }

    private Bitmap loadBitmapFromView(View v) {
        final int w = v.getWidth();
        final int h = v.getHeight();
        final Bitmap b = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        final Canvas c = new Canvas(b);
        //v.layout(0, 0, w, h);
        v.layout(v.getLeft(), v.getTop(), v.getRight(), v.getBottom());
        v.draw(c);
        return b;
    }

    // CLick listener
    @OnClick (R.id.btn_file_chooser)
    public void choose_file(){
        Log.i("info", "Choose file");
        mCamera.stopPreview();
        mCamera.release();
        int[] startingLocation = new int[2];
        btn_file_chooser.getLocationOnScreen(startingLocation);
        startingLocation[0] += btn_file_chooser.getWidth() / 2;
        GalleryActivity.startGalleryFromLocation(startingLocation, this);
        overridePendingTransition(0, 0);
    }


    @OnClick (R.id.camera_filter1)
    public void add_filter1(){
        Log.i("info", "Filters change red");
        ivTakenPhoto.setColorFilter(Color.RED, PorterDuff.Mode.LIGHTEN);
    }

    @OnClick (R.id.camera_filter2)
    public void add_filter2(){
        Log.i("info", "Filters change BLUE");
        ivTakenPhoto.setColorFilter(Color.BLUE, PorterDuff.Mode.LIGHTEN);
    }

    @OnClick (R.id.camera_filter3)
    public void add_filter3(){
        Log.i("info", "Filters change YELLOW");
        ivTakenPhoto.setColorFilter(Color.YELLOW, PorterDuff.Mode.LIGHTEN);
    }

    @OnClick (R.id.camera_remove_filter)
    public void remove_filter(){
        Log.i("info", "Filters change back");
        ivTakenPhoto.setColorFilter(Color.TRANSPARENT, PorterDuff.Mode.LIGHTEN);
    }

    /*
    private int getImageResource(ImageView iv) {
        return (Integer) iv.getTag();
    }

*/
    @OnClick (R.id.camera_filter4)
    public void cool_filter(){
        Log.i("info", "Filters change cool");
        Drawable[] layers = new Drawable[2];
        //layers[0] = myDrawable;
        //layers[1] = getResources().getDrawable(R.drawable.filter1);
       // LayerDrawable layerDrawable = new LayerDrawable(layers);
        //ivTakenPhoto.setImageDrawable(layerDrawable);
        // TODO
    }

   /* @OnClick (R.id.camera_bright)
    public void changeBright(){
        vLowerPanel.showNext();
    }*/

    // Animations

    private void startIntroAnimation() {
        vUpperPanel.animate().translationY(0)
                .setDuration(400)
                .setInterpolator(DECELERATE_INTERPOLATOR);
        vLowerPanel.animate()
                .translationY(0)
                .setDuration(400)
                .setInterpolator(DECELERATE_INTERPOLATOR).start();
    }

    private void animateShutter() {
        vShutter.setVisibility(View.VISIBLE);
        vShutter.setAlpha(0.f);

        ObjectAnimator alphaInAnim = ObjectAnimator.ofFloat(vShutter, "alpha", 0f, 0.8f);
        alphaInAnim.setDuration(100);
        alphaInAnim.setStartDelay(100);
        alphaInAnim.setInterpolator(ACCELERATE_INTERPOLATOR);

        ObjectAnimator alphaOutAnim = ObjectAnimator.ofFloat(vShutter, "alpha", 0.8f, 0f);
        alphaOutAnim.setDuration(200);
        alphaOutAnim.setInterpolator(DECELERATE_INTERPOLATOR);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playSequentially(alphaInAnim, alphaOutAnim);
        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                vShutter.setVisibility(View.GONE);
            }
        });
        animatorSet.start();
    }

    private void setupRevealBackground(Bundle savedInstanceState) {
        vRevealBackground.setFillPaintColor(0xFF16181a);
        vRevealBackground.setOnStateChangeListener(this);
        if (savedInstanceState == null) {
            final int[] startingLocation = getIntent().getIntArrayExtra(ARG_REVEAL_START_LOCATION);
            vRevealBackground.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    vRevealBackground.getViewTreeObserver().removeOnPreDrawListener(this);
                    vRevealBackground.startFromLocation(startingLocation);
                    return true;
                }
            });
        } else {
            vRevealBackground.setToFinishedFrame();
        }
    }


    public static void startCameraFromLocation(int[] startingLocation, Activity startingActivity) {
        Intent intent = new Intent(startingActivity, TakePhotoActivity.class);
        intent.putExtra(ARG_REVEAL_START_LOCATION, startingLocation);
        startingActivity.startActivity(intent);
    }

    // FILE CHOOSER
    /*
    ------------------- For further develop ---------------
    //
    private static final int FILE_SELECT_CODE = 0;

    private void showFileChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        try {
            startActivityForResult(
                    Intent.createChooser(intent, "Select a File to Upload"),
                    FILE_SELECT_CODE);
        } catch (android.content.ActivityNotFoundException ex) {
            // Potentially direct the user to the Market with a Dialog
            Toast.makeText(this, "Please install a File Manager.",
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        String path = null;
        pick_result = resultCode;
        switch (requestCode) {
            case FILE_SELECT_CODE:
                if (resultCode == RESULT_OK) {
                    // Get the Uri of the selected file
                    Uri uri = data.getData();
                    Log.d(TAG, "File Uri: " + uri.toString());
                    // Get the path
                    path = uri.toString();
                    try {
                        path = Utils.getPath(this, uri);
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                    }
                    Log.d(TAG, "File Path: " + path);
                    // Get the file instance
                    // File file = new File(path);
                    // Initiate the upload
                }
                break;

        }
        if (path != null) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 1;
            bitmap = BitmapFactory.decodeFile(path, options);
            Log.d(TAG, "File Uri: " + path);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }*/

}
