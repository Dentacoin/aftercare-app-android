package com.dentacoin.dentacare.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.text.SpannableString;
import android.text.Spanned;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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
import com.dentacoin.dentacare.utils.DCTutorialManager;
import com.dentacoin.dentacare.utils.DCUtils;
import com.dentacoin.dentacare.utils.IDCTutorial;
import com.dentacoin.dentacare.widgets.DCTextView;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.login.LoginManager;
import com.github.florent37.viewtooltip.ViewTooltip;

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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        nvNavigation = (NavigationView) findViewById(R.id.nv_navigation);

        tvDrawerHeaderFullname = (DCTextView) nvNavigation.getHeaderView(0).findViewById(R.id.tv_drawer_header_fullname);
        tvDrawerHeaderEmail = (DCTextView) nvNavigation.getHeaderView(0).findViewById(R.id.tv_drawer_header_email);
        sdvDrawerHeaderAvatar = (SimpleDraweeView) nvNavigation.getHeaderView(0).findViewById(R.id.sdv_drawer_header_avatar);
        sdvDrawerHeaderAvatar.setOnClickListener(this);
        rlDrawerHeader = (RelativeLayout) nvNavigation.getHeaderView(0).findViewById(R.id.rl_drawer_header);
        llDrawerHeaderUserInfo = (LinearLayout) nvNavigation.getHeaderView(0).findViewById(R.id.ll_drawer_header_userInfo);
        llDrawerHeaderUserInfo.setOnClickListener(this);

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
                loadUserData();

                DCTutorialManager.getInstance().showTutorial(DCDrawerActivity.this, sdvDrawerHeaderAvatar, DCTutorialManager.TUTORIAL.EDIT_PROFILE, ViewTooltip.ALIGN.CENTER, ViewTooltip.Position.RIGHT);
                DCTutorialManager.getInstance().showTutorial(DCDrawerActivity.this, nvNavigation.getTouchables().get(3), DCTutorialManager.TUTORIAL.COLLECT_DCN, ViewTooltip.ALIGN.CENTER, ViewTooltip.Position.TOP);
                DCTutorialManager.getInstance().showTutorial(DCDrawerActivity.this, nvNavigation.getTouchables().get(4), DCTutorialManager.TUTORIAL.GOALS, ViewTooltip.ALIGN.CENTER, ViewTooltip.Position.BOTTOM);
                DCTutorialManager.getInstance().showTutorial(DCDrawerActivity.this, nvNavigation.getTouchables().get(7), DCTutorialManager.TUTORIAL.EMERGENCY_MENU, ViewTooltip.ALIGN.CENTER, ViewTooltip.Position.BOTTOM);
            }

            @Override
            public void onDrawerStateChanged(int newState) {
                super.onDrawerStateChanged(newState);
                if (newState != DrawerLayout.STATE_IDLE && drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    DCTutorialManager.getInstance().hideTutorial(DCTutorialManager.TUTORIAL.EDIT_PROFILE);
                    DCTutorialManager.getInstance().hideTutorial(DCTutorialManager.TUTORIAL.COLLECT_DCN);
                    DCTutorialManager.getInstance().hideTutorial(DCTutorialManager.TUTORIAL.GOALS);
                    DCTutorialManager.getInstance().hideTutorial(DCTutorialManager.TUTORIAL.EMERGENCY_MENU);
                }
                else if (newState != DrawerLayout.STATE_IDLE && !drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    hideTutorials();
                }
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
        loadUserData();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_drawer_header_userInfo:
            case R.id.sdv_drawer_header_avatar:
                drawerLayout.closeDrawer(GravityCompat.START);
                final Intent profileIntent = new Intent(this, DCProfileActivity.class);
                startActivity(profileIntent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                break;
        }
    }

    /**
     * Retrieves the user data for the navigation header
     */
    private void loadUserData() {
        if (DCSession.getInstance().getUser() == null) {
            DCApiManager.getInstance().getUser(new DCResponseListener<DCUser>() {
                @Override
                public void onFailure(DCError error) {
                }

                @Override
                public void onResponse(DCUser object) {
                    updateNavigationHeader(object);
                }
            });
        } else {
            updateNavigationHeader(DCSession.getInstance().getUser());
        }
    }

    private void updateNavigationHeader(DCUser user) {
        if (user != null) {
            sdvDrawerHeaderAvatar.setImageURI(user.getAvatarUrl(DCDrawerActivity.this));
            tvDrawerHeaderFullname.setText(user.getFullName());
            tvDrawerHeaderEmail.setText(user.getEmail());
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
            case R.id.drawer_nav_statistics:
                final Intent statisticsIntent = new Intent(this, DCStatisticsActivity.class);
                startActivity(statisticsIntent);
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
                                        DCSession.getInstance().clear();
                                        LoginManager.getInstance().logOut();
                                        DCTutorialManager.getInstance().clear();
                                        DCLocalNotificationsManager.getInstance().scheduleNotifications(DCDrawerActivity.this, true);
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
    public void showTutorials() {
    }

    @Override
    public void hideTutorials() {
    }
}
