package com.dentacoin.dentacare.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;

import com.dentacoin.dentacare.R;
import com.dentacoin.dentacare.adapters.DCFriendDetailPagerAdapter;
import com.dentacoin.dentacare.model.DCFriend;
import com.dentacoin.dentacare.widgets.DCVIewPager;

/**
 * Created by Atanas Chervarov on 21.05.18.
 */
public class DCFriendDetailsActivity extends DCToolbarActivity {

    private static final String KEY_FRIEND = "KEY_FRIEND";

    public static Intent createIntent(Context context, DCFriend friend) {
        Intent intent = new Intent(context, DCFriendDetailsActivity.class);
        intent.putExtra(KEY_FRIEND, friend);
        return intent;
    }

    private DCFriend friend;
    private TabLayout tlFriend;
    private DCVIewPager vpFriend;
    private DCFriendDetailPagerAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getIntent().getSerializableExtra(KEY_FRIEND) instanceof DCFriend) {
            friend = ((DCFriend) getIntent().getSerializableExtra(KEY_FRIEND));
        }

        if (friend == null)
            finish();

        addContentView(R.layout.activity_friend_details);

        adapter = new DCFriendDetailPagerAdapter(this, friend, getFragmentManager());
        tlFriend = findViewById(R.id.tl_friend);
        vpFriend = findViewById(R.id.vp_friend);

        vpFriend.setAdapter(adapter);
        tlFriend.setupWithViewPager(vpFriend);
        vpFriend.setCurrentItem(0);

        setTitle(friend.getFullName());
    }

}
