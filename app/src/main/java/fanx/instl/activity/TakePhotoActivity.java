package fanx.instl.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.commonsware.cwac.camera.CameraHost;
import com.commonsware.cwac.camera.CameraHostProvider;
import com.commonsware.cwac.camera.CameraView;
import com.commonsware.cwac.camera.PictureTransaction;
import com.commonsware.cwac.camera.SimpleCameraHost;

import java.io.File;

import butterknife.Bind;
import butterknife.OnClick;
import fanx.instl.R;
import fanx.instl.ui.RevealBackgroundView;
import fanx.instl.utils.Utils;

public class TakePhotoActivity extends BaseActivity
        implements RevealBackgroundView.OnStateChangeListener , CameraHostProvider {

    public static final String ARG_REVEAL_START_LOCATION = "reveal_start_location";

    private static final Interpolator ACCELERATE_INTERPOLATOR = new AccelerateInterpolator();
    private static final Interpolator DECELERATE_INTERPOLATOR = new DecelerateInterpolator();
    private static final int STATE_TAKE_PHOTO = 0;
    private static final int STATE_SETUP_PHOTO = 1;

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
    @Bind(R.id.cameraView)
    CameraView cameraView;
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

    //Drawable myDrawable = ivTakenPhoto.getDrawable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_photo);
        updateStatusBarColor();
        updateState(STATE_TAKE_PHOTO);
        setupRevealBackground(savedInstanceState);
        setupPhotoFilters();

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
        // Grid line & flash light
        ImageButton gridButton = (ImageButton) findViewById(R.id.camera_grid);
        //ImageButton flashLightButton = (ImageButton) findViewById(R.id.camera_flashLight);
        final ImageView gridLine = (ImageView) findViewById(R.id.camera_grid_line);
        gridLine.setVisibility(View.INVISIBLE);

        gridButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (gridLineOn == false) {
                    Toast.makeText(getApplicationContext(), "Grid line Enabled",
                            Toast.LENGTH_SHORT).show();
                    gridLine.setVisibility(View.VISIBLE);
                    gridLineOn = true;
                } else if (gridLineOn == true) {
                    Toast.makeText(getApplicationContext(), "Grid line Disabled",
                            Toast.LENGTH_SHORT).show();
                    gridLine.setVisibility(View.INVISIBLE);
                    gridLineOn = false;
                }
            }
        });

        // Filter

    }

    public static void startCameraFromLocation(int[] startingLocation, Activity startingActivity) {
        Intent intent = new Intent(startingActivity, TakePhotoActivity.class);
        intent.putExtra(ARG_REVEAL_START_LOCATION, startingLocation);
        startingActivity.startActivity(intent);
    }



    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void updateStatusBarColor() {
        if (Utils.isAndroid5()) {
            getWindow().setStatusBarColor(0xff111111);
        }
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

    private void setupPhotoFilters() {
        //PhotoFiltersAdapter photoFiltersAdapter = new PhotoFiltersAdapter(this);
        //rvFilters.setHasFixedSize(true);
        //rvFilters.setAdapter(photoFiltersAdapter);
        //camera_filter1.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
    }

    @Override
    protected void onResume() {
        super.onResume();
        cameraView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        cameraView.onPause();
    }

    // Inject bind views functions
    @OnClick(R.id.btnTakePhoto)
    public void onTakePhotoClick() {
        btnTakePhoto.setEnabled(false);
        cameraView.takePicture(true, true);
        animateShutter();
    }

    @OnClick(R.id.btnAccept)
    public void onAcceptClick() {
        PublishActivity.openWithPhotoUri(this, Uri.fromFile(photoPath));
    }

    @OnClick(R.id.camera_flashLight)
    public void onSwitchFlash(){
        if (isLightOn) {
            cameraView.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
            Log.i("info", "torch is turn off!");
        } else {
            cameraView.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
            Log.i("info", "torch is turn on!");
        }
        isLightOn = !isLightOn;
    }

    @OnClick(R.id.camera_cancel)
    public void camera_cancel (){
        Log.i("info", "Camera activity cancelled!");
        finish();
    }

    // Animations
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

    private void startIntroAnimation() {
        vUpperPanel.animate().translationY(0).setDuration(400).setInterpolator(DECELERATE_INTERPOLATOR);
        vLowerPanel.animate().translationY(0).setDuration(400).setInterpolator(DECELERATE_INTERPOLATOR).start();
    }

    @Override
    public CameraHost getCameraHost() {
        return new MyCameraHost(this);
    }

    class MyCameraHost extends SimpleCameraHost {

        private Camera.Size previewSize;

        public MyCameraHost(Context ctxt) {
            super(ctxt);
        }

        @Override
        public boolean useFullBleedPreview() {
            return true;
        }

        @Override
        public Camera.Size getPictureSize(PictureTransaction xact, Camera.Parameters parameters) {
            return previewSize;
        }

        @Override
        public Camera.Parameters adjustPreviewParameters(Camera.Parameters parameters) {
            Camera.Parameters parameters1 = super.adjustPreviewParameters(parameters);
            previewSize = parameters1.getPreviewSize();
            parameters.setFlashMode(Camera.Parameters.FLASH_MODE_ON);

            return parameters1;
        }


        @Override
        public void saveImage(PictureTransaction xact, final Bitmap bitmap) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    showTakenPicture(bitmap);
                }
            });
        }

        @Override
        public void saveImage(PictureTransaction xact, byte[] image) {
            super.saveImage(xact, image);
            photoPath = getPhotoPath();
        }
    }

    private void showTakenPicture(Bitmap bitmap) {
        vUpperPanel.showNext();
        vLowerPanel.showNext();
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
    // Filters
    @OnClick (R.id.camera_filter1)
    public void add_filter1(){
        Log.i("info", "Filters change red");
        ivTakenPhoto.setColorFilter(Color.RED, PorterDuff.Mode.LIGHTEN);
    }

    @OnClick (R.id.camera_filter2)
    public void add_filter2(){
        Log.i("info", "Filters change red");
        ivTakenPhoto.setColorFilter(Color.BLUE, PorterDuff.Mode.LIGHTEN);
    }

    @OnClick (R.id.camera_filter3)
    public void add_filter3(){
        Log.i("info", "Filters change red");
        ivTakenPhoto.setColorFilter(Color.YELLOW, PorterDuff.Mode.LIGHTEN);
    }

    @OnClick (R.id.camera_remove_filter)
    public void remove_filter(){
        Log.i("info", "Filters change red");
        ivTakenPhoto.setColorFilter(Color.TRANSPARENT, PorterDuff.Mode.LIGHTEN);
    }


    /*
    private int getImageResource(ImageView iv) {
        return (Integer) iv.getTag();
    }


    @OnClick (R.id.camera_filter4)
    public void cool_filter(){
        Log.i("info", "Filters change cool");
        Drawable[] layers = new Drawable[2];
        layers[0] = myDrawable;
        layers[1] = getResources().getDrawable(R.drawable.filter1);
        LayerDrawable layerDrawable = new LayerDrawable(layers);
        ivTakenPhoto.setImageDrawable(layerDrawable);
    }*/
}
