package fanx.instl.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import butterknife.Bind;
import butterknife.OnClick;
import fanx.instl.R;
import fanx.instl.activity.InstagramUtils.AppData;
import fanx.instl.activity.InstagramUtils.InstagramCurrentUserProfile;
import fanx.instl.activity.InstagramUtils.InstagramRetrieveUserMediaTask;
import fanx.instl.activity.adapter.UserProfileAdapter;
import fanx.instl.ui.RevealBackgroundView;
import fanx.instl.utils.CircleTransformation;

public class UserProfileActivity extends BaseDrawerActivity implements RevealBackgroundView.OnStateChangeListener {
    public static final String ARG_REVEAL_START_LOCATION = "reveal_start_location";

    private static final int USER_OPTIONS_ANIMATION_DELAY = 300;
    private static final Interpolator INTERPOLATOR = new DecelerateInterpolator();

    @Bind(R.id.vRevealBackground)
    RevealBackgroundView vRevealBackground;
    // Grid Recycle View
    //@Bind(R.id.rvUserProfile)
    //RecyclerView rvUserProfile;

    @Bind(R.id.tlUserProfileTabs)
    TabLayout tlUserProfileTabs;

    @Bind(R.id.ivUserProfilePhoto)
    ImageView ivUserProfilePhoto;
    @Bind(R.id.vUserDetails)
    View vUserDetails;
    @Bind(R.id.btnFollow)
    Button btnFollow;
    @Bind(R.id.vUserStats)
    View vUserStats;
    @Bind(R.id.vUserProfileRoot)
    View vUserProfileRoot;
    // NEW
    @Bind(R.id.profile_fullname)
    TextView profile_fullname;
    @Bind(R.id.profile_username)
    TextView profile_username;
    @Bind(R.id.profile_bio)
    TextView profile_bio;
    @Bind(R.id.profile_website)
    TextView profile_website;
    @Bind(R.id.profile_media_count)
    TextView profile_media_count;
    @Bind(R.id.profile_followsCounts)
    TextView profile_followsCounts;
    @Bind(R.id.profile_followed_byCounts)
    TextView profile_followed_byCounts;


    private int avatarSize;
    private String profilePhoto;
    private UserProfileAdapter userPhotosAdapter;

    public static void startUserProfileFromLocation(int[] startingLocation, Activity startingActivity) {
        Intent intent = new Intent(startingActivity, UserProfileActivity.class);
        intent.putExtra(ARG_REVEAL_START_LOCATION, startingLocation);
        startingActivity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        // NEW
        InstagramCurrentUserProfile instagramUser = new InstagramCurrentUserProfile(this,
                profile_fullname,
                profile_username,
                profile_bio,
                profile_website,
                profile_media_count,
                profile_followsCounts,
                profile_followed_byCounts,
                ivUserProfilePhoto);
        instagramUser.execute();

         InstagramRetrieveUserMediaTask instagramRetrieveUserMediaTask =
                 new InstagramRetrieveUserMediaTask(this,(GridView) findViewById(R.id.gridView2));
        instagramRetrieveUserMediaTask.execute();
        //
        setupTabs();
        //setupUserProfileGrid();
        setupRevealBackground(savedInstanceState);
        //
        setRoundImage();


        //
    }

    private void setupTabs() {
        tlUserProfileTabs.addTab(tlUserProfileTabs.newTab().setIcon(R.drawable.ic_grid_on_white));
        tlUserProfileTabs.addTab(tlUserProfileTabs.newTab().setIcon(R.drawable.ic_list_white));
        tlUserProfileTabs.addTab(tlUserProfileTabs.newTab().setIcon(R.drawable.ic_place_white));
        tlUserProfileTabs.addTab(tlUserProfileTabs.newTab().setIcon(R.drawable.ic_label_white));
    }


    private void setupRevealBackground(Bundle savedInstanceState) {
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
            userPhotosAdapter.setLockedAnimations(true);
        }
    }

    @Override
    public void onStateChange(int state) {
        if (RevealBackgroundView.STATE_FINISHED == state) {
            //rvUserProfile.setVisibility(View.VISIBLE);
            tlUserProfileTabs.setVisibility(View.VISIBLE);
            vUserProfileRoot.setVisibility(View.VISIBLE);
            userPhotosAdapter = new UserProfileAdapter(this);
            //rvUserProfile.setAdapter(userPhotosAdapter);
            animateUserProfileOptions();
            animateUserProfileHeader();
        } else {
            tlUserProfileTabs.setVisibility(View.INVISIBLE);
            //rvUserProfile.setVisibility(View.INVISIBLE);
            vUserProfileRoot.setVisibility(View.INVISIBLE);
        }
    }

    private void animateUserProfileOptions() {
        tlUserProfileTabs.setTranslationY(-tlUserProfileTabs.getHeight());
        tlUserProfileTabs.animate().translationY(0).setDuration(300).setStartDelay(USER_OPTIONS_ANIMATION_DELAY).setInterpolator(INTERPOLATOR);
    }

    private void animateUserProfileHeader() {
           vUserProfileRoot.setTranslationY(-vUserProfileRoot.getHeight());
           ivUserProfilePhoto.setTranslationY(-ivUserProfilePhoto.getHeight());
           vUserDetails.setTranslationY(-vUserDetails.getHeight());
           vUserStats.setAlpha(0);

           vUserProfileRoot.animate().translationY(0).setDuration(300).setInterpolator(INTERPOLATOR);
           ivUserProfilePhoto.animate().translationY(0).setDuration(300).setStartDelay(100).setInterpolator(INTERPOLATOR);
           vUserDetails.animate().translationY(0).setDuration(300).setStartDelay(200).setInterpolator(INTERPOLATOR);
           vUserStats.animate().alpha(1).setDuration(200).setStartDelay(400).setInterpolator(INTERPOLATOR).start();
    }
// NEW

    @OnClick(R.id.btnFollow)
    public void btnFollow(View view) {

        String profileURL = "https://api.instagram.com/v1" + "/users/"
                + AppData.getUserId(getApplicationContext())
                + "/?access_token="
                + AppData.getAccessToken(getApplicationContext());

        Log.v("test", profileURL);

    }
    public void setRoundImage () {
        this.avatarSize = getResources().getDimensionPixelSize(R.dimen.user_profile_avatar_size);
        this.profilePhoto = InstagramCurrentUserProfile.profile_image_url;

        Picasso.with(this)
                .load(profilePhoto)
                .placeholder(R.drawable.img_circle_placeholder)
                .resize(avatarSize, avatarSize)
                .centerCrop()
                .transform(new CircleTransformation())
                .into(ivUserProfilePhoto);
    }

}
