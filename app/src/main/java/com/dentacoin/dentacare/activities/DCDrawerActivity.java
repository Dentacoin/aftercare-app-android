package com.dentacoin.dentacare.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.text.SpannableString;
import android.text.Spanned;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.dentacoin.dentacare.R;
import com.dentacoin.dentacare.model.DCError;
import com.dentacoin.dentacare.model.DCUser;
import com.dentacoin.dentacare.network.DCApiManager;
import com.dentacoin.dentacare.network.DCResponseListener;
import com.dentacoin.dentacare.network.DCSession;
import com.dentacoin.dentacare.utils.DCCustomTypefaceSpan;
import com.dentacoin.dentacare.utils.DCFonts;
import com.dentacoin.dentacare.utils.DCLocalNotificationsManager;
import com.dentacoin.dentacare.utils.DCSharedPreferences;
import com.dentacoin.dentacare.utils.DCTutorialManager;
import com.dentacoin.dentacare.utils.DCUtils;
import com.dentacoin.dentacare.utils.IDCTutorial;
import com.dentacoin.dentacare.utils.Tutorial;
import com.dentacoin.dentacare.widgets.DCTextView;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.takusemba.spotlight.OnSpotlightEndedListener;
import com.takusemba.spotlight.SimpleTarget;
import com.takusemba.spotlight.Spotlight;
import com.twitter.sdk.android.core.TwitterCore;

/**
 * Created by Atanas Chervarov on 8/11/17.
 * Basic Drawer Activity
 */

public class DCDrawerActivity extends DCToolbarActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener, IDCTutorial {

    private DrawerLayout drawerLayout;
    private NavigationView nvNavigation;
    private ActionBarDrawerToggle toggle;

    private DCTextView tvDrawerHeaderFullname;
    private DCTextView tvDrawerHeaderEmail;
    private SimpleDraweeView sdvDrawerHeaderAvatar;
    private RelativeLayout rlDrawerHeader;
    private LinearLayout llDrawerHeaderUserInfo;
    private ImageView ivVerified;
    
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        drawerLayout = findViewById(R.id.drawer_layout);
        nvNavigation = findViewById(R.id.nv_navigation);

        tvDrawerHeaderFullname = nvNavigation.getHeaderView(0).findViewById(R.id.tv_drawer_header_fullname);
        tvDrawerHeaderEmail = nvNavigation.getHeaderView(0).findViewById(R.id.tv_drawer_header_email);
        sdvDrawerHeaderAvatar = nvNavigation.getHeaderView(0).findViewById(R.id.sdv_drawer_header_avatar);
        ivVerified = nvNavigation.getHeaderView(0).findViewById(R.id.iv_verified);
        ivVerified.setVisibility(View.GONE);

        sdvDrawerHeaderAvatar.setOnClickListener(this);
        rlDrawerHeader = nvNavigation.getHeaderView(0).findViewById(R.id.rl_drawer_header);
        llDrawerHeaderUserInfo = nvNavigation.getHeaderView(0).findViewById(R.id.ll_drawer_header_userInfo);
        llDrawerHeaderUserInfo.setOnClickListener(this);

        for (int i = 0; i < nvNavigation.getMenu().size(); ++i) {
            final MenuItem item = nvNavigation.getMenu().getItem(i);
            tintMenuItemIcon(R.color.charcoalGrey, item);
        }

        rlDrawerHeader.setPadding(
                (int) getResources().getDimension(R.dimen.drawer_header_padding),
                DCUtils.getStatusBarHeight(this) + (int) getResources().getDimension(R.dimen.drawer_header_padding),
                (int) getResources().getDimension(R.dimen.drawer_header_padding),
                (int) getResources().getDimension(R.dimen.drawer_header_padding));

        toggle = new ActionBarDrawerToggle(
                this,
                drawerLayout,
                toolbar,
                R.string.drawer_open,
                R.string.drawer_close
        ) {
            /** Called when a drawer has settled in a completely closed state. */
            @Override
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            /** Called when a drawer has settled in a completely open state. */
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
                loadUserData(true);
                DCTutorialManager.getInstance().showNext();
            }

            @Override
            public void onDrawerStateChanged(int newState) {
                super.onDrawerStateChanged(newState);
            }
        };

        drawerLayout.addDrawerListener(toggle);
        nvNavigation.setNavigationItemSelectedListener(this);

        //Set custom font
        Menu menu = nvNavigation.getMenu();
        for (int i = 0; i < menu.size(); i++) {
            MenuItem item = menu.getItem(i);
            SpannableString spannableString = new SpannableString(item.getTitle());
            spannableString.setSpan(new DCCustomTypefaceSpan(DCFonts.getFont(this, DCFonts.FONT_LATO_REGULAR)), 0, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            item.setTitle(spannableString);
        }

        toggle.syncState();
        loadUserData(true);
        DCLocalNotificationsManager.getInstance().scheduleNotifications(DCDrawerActivity.this, false);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_drawer_header_userInfo:
            case R.id.sdv_drawer_header_avatar: {
                if (!DCSession.getInstance().isChildUser()) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                    final Intent profileIntent = new Intent(this, DCProfileActivity.class);
                    startActivity(profileIntent);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                }
                break;
            }
        }
    }

    /**
     * Retrieves the user data for the navigation header
     * @param force true to force an api request
     */
    private void loadUserData(boolean force) {
        if (DCSession.getInstance().getUser() == null || force) {
            DCApiManager.getInstance().getUser(new DCResponseListener<DCUser>() {
                @Override
                public void onFailure(DCError error) {
                }

                @Override
                public void onResponse(DCUser object) {
                    updateNavigationDrawer(object);
                }
            });
        } else {
            updateNavigationDrawer(DCSession.getInstance().getUser());
        }
    }

    private void updateNavigationDrawer(DCUser user) {
        if (user != null) {
            sdvDrawerHeaderAvatar.setImageURI(user.getAvatarUrl(DCDrawerActivity.this));
            if (user.isChild()) {
                sdvDrawerHeaderAvatar.getHierarchy().setPlaceholderImage(getResources().getDrawable(R.drawable.baseline_face_white_48));
            } else {
                sdvDrawerHeaderAvatar.getHierarchy().setPlaceholderImage(getResources().getDrawable(R.drawable.welcome_avatar_holder));
            }
            tvDrawerHeaderFullname.setText(user.getFullName());
            tvDrawerHeaderEmail.setVisibility(View.GONE);
            if (user.getEmail() != null) {
                tvDrawerHeaderEmail.setText(user.getEmail());
                tvDrawerHeaderEmail.setVisibility(View.VISIBLE);
            }

            ivVerified.setVisibility(user.isConfirmed() ? View.VISIBLE : View.GONE);

            Menu menu = nvNavigation.getMenu();
            if (menu != null) {
                menu.getItem(1).setVisible(!user.isChild());
                menu.getItem(2).setVisible(!user.isChild());
                menu.getItem(5).setVisible(!user.isChild());
            }
        }
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_drawer;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.drawer_nav_home:
                drawerLayout.closeDrawer(GravityCompat.START);
                break;
            case R.id.drawer_nav_goals:
                final Intent goalsIntent = new Intent(this, DCGoalsActivity.class);
                startActivity(goalsIntent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                break;
            case R.id.drawer_nav_dentacoin:
                final Intent collectIntent = new Intent(this, DCCollectActivity.class);
                startActivity(collectIntent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                break;
            case R.id.drawer_nav_withdraws:
                final Intent withdrawsIntent = new Intent(this, DCWithdrawsActivity.class);
                startActivity(withdrawsIntent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                break;
            case R.id.drawer_nav_statistics:
                final Intent statisticsIntent = new Intent(this, DCStatisticsActivity.class);
                startActivity(statisticsIntent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                break;
            case R.id.drawer_nav_friends:
                final Intent familyFriendsIntent = new Intent(this, DCFamilyAndFriendsActivity.class);
                startActivity(familyFriendsIntent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                break;
            case R.id.drawer_nav_emergency:
                final Intent emergencyIntent = new Intent(this, DCEmergencyActivity.class);
                startActivity(emergencyIntent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                break;
            case R.id.drawer_nav_oral_health:
                final Intent oralHealthIntent = new Intent(this, DCOralHealthActivity.class);
                startActivity(oralHealthIntent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                break;
            case R.id.drawer_nav_settings:
                final Intent settingsIntent = new Intent(this, DCSettingsActivity.class);
                startActivity(settingsIntent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                break;
            case R.id.drawer_nav_about:
                final Intent aboutIntent = new Intent(this, DCAboutActivity.class);
                startActivity(aboutIntent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                break;
            case R.id.drawer_nav_signout:
                final AlertDialog dialog = new AlertDialog.Builder(this)
                        .setTitle(R.string.drawer_hdl_signout)
                        .setMessage(R.string.drawer_txt_signout)
                        .setPositiveButton(R.string.txt_yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                DCApiManager.getInstance().logout(new DCResponseListener<Void>() {
                                    @Override
                                    public void onFailure(DCError error) {
                                        if (error != null) {
                                            error.show(DCDrawerActivity.this);
                                        }
                                    }

                                    @Override
                                    public void onResponse(Void object) {
                                        onLogout();
                                    }
                                });
                            }
                        })
                        .setNegativeButton(R.string.txt_no, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .create();
                dialog.show();
                break;
            default:
                break;
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
            return;
        }

        super.onBackPressed();
    }

    @Override
    public void showTutorial(final Tutorial tutorial) {
        if (tutorial != null && drawerLayout.isDrawerOpen(GravityCompat.START)) {
            switch (tutorial) {
                case EDIT_PROFILE:
                    sdvDrawerHeaderAvatar.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                        @Override
                        public void onGlobalLayout() {
                            sdvDrawerHeaderAvatar.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                            int[] location = new int[2];
                            sdvDrawerHeaderAvatar.getLocationInWindow(location);
                            float oneX = location[0] + sdvDrawerHeaderAvatar.getWidth() / 2f;
                            float oneY = location[1] + sdvDrawerHeaderAvatar.getHeight() / 2f;
                            showSpotlightTutorial(tutorial, oneX, oneY);
                        }
                    });
                    break;
                case COLLECT_DCN:
                    final View collectDCN = nvNavigation.getTouchables().get(3);
                    collectDCN.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                        @Override
                        public void onGlobalLayout() {
                            collectDCN.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                            int[] location = new int[2];
                            collectDCN.getLocationInWindow(location);
                            float oneX = location[0] + collectDCN.getWidth() / 3f;
                            float oneY = location[1] + collectDCN.getHeight() / 2f;
                            showSpotlightTutorial(tutorial, oneX, oneY);
                        }
                    });
                    break;
                case WITHDRAWS:
                    final View withdraws = nvNavigation.getTouchables().get(4);
                    withdraws.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                        @Override
                        public void onGlobalLayout() {
                            withdraws.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                            int[] location = new int[2];
                            withdraws.getLocationInWindow(location);
                            float oneX = location[0] + withdraws.getWidth() / 3f;
                            float oneY = location[1] + withdraws.getHeight() / 2f;
                            showSpotlightTutorial(tutorial, oneX, oneY);
                        }
                    });
                    break;
                case GOALS:
                    final View goals = nvNavigation.getTouchables().get(5);
                    goals.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                        @Override
                        public void onGlobalLayout() {
                            goals.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                            int[] location = new int[2];
                            goals.getLocationInWindow(location);
                            float oneX = location[0] + goals.getWidth() / 3f;
                            float oneY = location[1] + goals.getHeight() / 2f;
                            showSpotlightTutorial(tutorial, oneX, oneY);
                        }
                    });
                    break;
                case EMERGENCY_MENU:
                    final View emergency = nvNavigation.getTouchables().get(8);
                    emergency.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                        @Override
                        public void onGlobalLayout() {
                            emergency.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                            int[] location = new int[2];
                            emergency.getLocationInWindow(location);
                            float oneX = location[0] + emergency.getWidth() / 3f;
                            float oneY = location[1] + emergency.getHeight() / 2f;
                            showSpotlightTutorial(tutorial, oneX, oneY);
                        }
                    });
                    break;
            }
        }
    }

    private void showSpotlightTutorial(Tutorial tutorial, float x, float y) {
        SimpleTarget qrTarget = new SimpleTarget.Builder(this)
                .setPoint(x, y)
                .setRadius(140f) // radius of the Target
                .setDescription(getString(tutorial.getResourceId())) // description
                .build();

        Spotlight.with(this)
                .setOverlayColor(getResources().getColor(R.color.blackTransparent80)) // background overlay color
                .setDuration(500L) // duration of Spotlight emerging and disappearing in ms
                .setAnimation(new DecelerateInterpolator(2f)) // animation of Spotlight
                .setTargets(qrTarget) // set targets. see below for more info
                .setOnSpotlightEndedListener(new OnSpotlightEndedListener() { // callback when Spotlight ends
                    @Override
                    public void onEnded() {
                        DCTutorialManager.getInstance().showNext();
                    }
                })
                .start(); // start Spotlight

        DCSharedPreferences.setShownTutorial(tutorial, true);
    }



    @Override
    public void onResume() {
        super.onResume();
        DCTutorialManager.getInstance().subscribe(this);
    }

    @Override
    public void onPause() {
        DCTutorialManager.getInstance().unsubscribe(this);
        super.onPause();
    }

    private static void tintMenuItemIcon(int color, MenuItem item) {
        final Drawable drawable = item.getIcon();
        if (drawable != null) {
            final Drawable wrapped = DrawableCompat.wrap(drawable);
            drawable.mutate();
            DrawableCompat.setTint(wrapped, color);
            item.setIcon(drawable);
        }
    }
}
