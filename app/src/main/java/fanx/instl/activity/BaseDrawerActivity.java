package fanx.instl.activity;

import android.content.Intent;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import butterknife.Bind;
import butterknife.OnClick;
import fanx.instl.R;
import fanx.instl.activity.InstagramUtils.InstagramCurrentUserProfile;
import fanx.instl.utils.CircleTransformation;


public class BaseDrawerActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener{

    @Bind(R.id.drawerLayout)
    DrawerLayout drawerLayout;

    @Bind(R.id.ivMenuUserProfilePhoto)
    ImageView ivMenuUserProfilePhoto;

    private int avatarSize;
    private String profilePhoto;

    ViewGroup viewGroup;
    // NEW
    @Bind(R.id.label_fullname)
    TextView full_name;


    @Override
    public void setContentView(int layoutResID) {
        super.setContentViewWithoutInject(R.layout.activity_drawer);
        viewGroup = (ViewGroup) findViewById(R.id.flContentRoot);
        LayoutInflater.from(this).inflate(layoutResID, viewGroup, true);
        injectViews();

        InstagramCurrentUserProfile instagramUser = new InstagramCurrentUserProfile(this,
                full_name);
        instagramUser.execute();

        setupHeader();

        NavigationView navigationView = (NavigationView) findViewById(R.id.vNavigation);
        navigationView.setNavigationItemSelectedListener(this);

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
    // List item
    @OnClick(R.id.vGlobalMenuHeader)
    public void onGlobalMenuHeaderClick(final View v) {
        drawerLayout.closeDrawer(Gravity.LEFT);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                int[] startingLocation = new int[2];
                v.getLocationOnScreen(startingLocation);
                startingLocation[0] += v.getWidth() / 2;
                UserProfileActivity.startUserProfileFromLocation(startingLocation, BaseDrawerActivity.this);
                overridePendingTransition(0, 0);
            }
        }, 200);

    }

    private Toast toast;
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawerLayout);

        if (id == R.id.menu_feed) {
            // Handle the camera action
            Intent intent = new Intent(getBaseContext(), UserFeedActivity.class);
            drawer.closeDrawer(GravityCompat.START);
            startActivity(intent);
            finish();
            return true;
        } else if (id == R.id.menu_gallery) {
            Intent intent = new Intent(getBaseContext(), GalleryActivity.class);
            drawer.closeDrawer(GravityCompat.START);
            startActivity(intent);
            finish();
            return true;
        } else if (id == R.id.menu_discover) {
            Intent intent = new Intent(getBaseContext(), SearchUserActivity.class);
            drawer.closeDrawer(GravityCompat.START);
            startActivity(intent);
            finish();
            return true;
        } else if (id == R.id.menu_activity_feed) {
            Intent intent = new Intent(getBaseContext(), ActivityFeedAcitivity.class);
            drawer.closeDrawer(GravityCompat.START);
            startActivity(intent);
            finish();
            return true;
        } else if (id == R.id.menu_photos_nearby) {

            toast = Toast.makeText(this, "Coming soon", Toast.LENGTH_SHORT);
            toast.show();
            drawer.closeDrawer(GravityCompat.START);
            return true;
        } else if (id == R.id.menu_photo_you_liked) {

            toast = Toast.makeText(this, "Coming soon", Toast.LENGTH_SHORT);
            toast.show();
            drawer.closeDrawer(GravityCompat.START);
            return true;
        }
        return true;
    }

}
