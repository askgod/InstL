package fanx.instl.activity;

import android.os.Handler;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.File;

import butterknife.Bind;
import butterknife.OnClick;
import fanx.instl.R;
import fanx.instl.activity.InstagramUtils.AppData;
import fanx.instl.activity.InstagramUtils.InstagramCurrentUserProfile;
import fanx.instl.utils.CircleTransformation;


public class BaseDrawerActivity extends BaseActivity {

    @Bind(R.id.drawerLayout)
    DrawerLayout drawerLayout;

    @Bind(R.id.ivMenuUserProfilePhoto)
    ImageView ivMenuUserProfilePhoto;

    private int avatarSize;
    private String profilePhoto;

    // NEW
    @Bind(R.id.label_fullname)
    TextView full_name;

    @Override
    public void setContentView(int layoutResID) {
        super.setContentViewWithoutInject(R.layout.activity_drawer);
        ViewGroup viewGroup = (ViewGroup) findViewById(R.id.flContentRoot);
        LayoutInflater.from(this).inflate(layoutResID, viewGroup, true);
        injectViews();

        InstagramCurrentUserProfile instagramUser = new InstagramCurrentUserProfile(this,
                full_name);
        instagramUser.execute();

        setupHeader();
    }

    @Override
    protected void setupToolbar() {
        super.setupToolbar();
        if (getToolbar() != null) {
            getToolbar().setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    drawerLayout.openDrawer(Gravity.LEFT);
                }
            });
        }
    }

    @OnClick(R.id.vGlobalMenuHeader)
    public void onGlobalMenuHeaderClick(final View v) {
        drawerLayout.closeDrawer(Gravity.LEFT);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                int[] startingLocation = new int[2];
                v.getLocationOnScreen(startingLocation);
                startingLocation[0] += v.getWidth() / 2;
                // construction
                UserProfileActivity.startUserProfileFromLocation(startingLocation, BaseDrawerActivity.this);
                overridePendingTransition(0, 0);

            }
        }, 200);
    }

    private void setupHeader() {
        this.avatarSize = getResources().getDimensionPixelSize(R.dimen.global_menu_avatar_size);
        this.profilePhoto = InstagramCurrentUserProfile.profile_image_url;
        Picasso.with(this)
                .load(profilePhoto)
                .placeholder(R.drawable.icon_2_n)
                .resize(avatarSize, avatarSize)
                .centerCrop()
                .transform(new CircleTransformation())
                .into(ivMenuUserProfilePhoto);
    }
    // From Instagram Api

}
