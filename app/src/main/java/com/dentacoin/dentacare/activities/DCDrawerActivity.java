package com.dentacoin.dentacare.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
import android.widget.RelativeLayout;

import com.dentacoin.dentacare.R;
import com.dentacoin.dentacare.model.DCError;
import com.dentacoin.dentacare.model.DCUser;
import com.dentacoin.dentacare.network.DCApiManager;
import com.dentacoin.dentacare.network.DCResponseListener;
import com.dentacoin.dentacare.network.DCSession;
import com.dentacoin.dentacare.utils.DCCustomTypefaceSpan;
import com.dentacoin.dentacare.utils.DCFonts;
import com.dentacoin.dentacare.utils.DCUtils;
import com.dentacoin.dentacare.widgets.DCTextView;
import com.facebook.drawee.view.SimpleDraweeView;

/**
 * Created by Atanas Chervarov on 8/11/17.
 * Basic Drawer Activity
 */

public class DCDrawerActivity extends DCToolbarActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private NavigationView nvNavigation;
    private ActionBarDrawerToggle toggle;

    private DCTextView tvDrawerHeaderFullname;
    private DCTextView tvDrawerHeaderEmail;
    private SimpleDraweeView sdvDrawerHeaderAvatar;
    private RelativeLayout rlDrawerHeader;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        nvNavigation = (NavigationView) findViewById(R.id.nv_navigation);

        tvDrawerHeaderFullname = (DCTextView) nvNavigation.getHeaderView(0).findViewById(R.id.tv_drawer_header_fullname);
        tvDrawerHeaderEmail = (DCTextView) nvNavigation.getHeaderView(0).findViewById(R.id.tv_drawer_header_email);
        sdvDrawerHeaderAvatar = (SimpleDraweeView) nvNavigation.getHeaderView(0).findViewById(R.id.sdv_drawer_header_avatar);
        rlDrawerHeader = (RelativeLayout) nvNavigation.getHeaderView(0).findViewById(R.id.rl_drawer_header);

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
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
                loadUserData();
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
                //TODO: go to dashboard
                break;
            case R.id.drawer_nav_goals:
                //TODO: go to goals
                break;
            case R.id.drawer_nav_dentacoin:
                //TODO: go to dentacoins
                break;
            case R.id.drawer_nav_emergency:
                //TODO: go to emergency
                break;
            case R.id.drawer_nav_oral_health:
                //TODO: go to oral health
                break;
            case R.id.drawer_nav_settings:
                //TODO: go to settings
                break;
            case R.id.drawer_nav_about:
                //TODO: go to about
                break;
            case R.id.drawer_nav_signout:
                final AlertDialog dialog = new AlertDialog.Builder(this)
                        .setTitle(R.string.drawer_hdl_signout)
                        .setMessage(R.string.drawer_txt_signout)
                        .setPositiveButton(R.string.txt_yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                DCSession.getInstance().clear();
                                onLogout();
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
}
