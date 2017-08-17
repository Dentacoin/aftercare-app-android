package com.dentacoin.dentacare.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.text.SpannableString;
import android.text.Spanned;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.dentacoin.dentacare.R;
import com.dentacoin.dentacare.network.DCSession;
import com.dentacoin.dentacare.utils.DCCustomTypefaceSpan;
import com.dentacoin.dentacare.utils.DCFonts;

/**
 * Created by Atanas Chervarov on 8/11/17.
 * Basic Drawer Activity
 */

public class DCDrawerActivity extends DCToolbarActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private NavigationView nvNavigation;
    private ActionBarDrawerToggle toggle;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        nvNavigation = (NavigationView) findViewById(R.id.nv_navigation);

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
                //TODO: add to locos
                final AlertDialog dialog = new AlertDialog.Builder(this)
                        .setTitle("Sign out?")
                        .setMessage("Are you sure you want to logout?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                DCSession.getInstance().clear();
                                onLogout();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
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
        return true;
    }
}
