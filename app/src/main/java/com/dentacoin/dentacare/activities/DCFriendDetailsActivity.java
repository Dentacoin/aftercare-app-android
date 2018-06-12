package com.dentacoin.dentacare.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;

import com.dentacoin.dentacare.R;
import com.dentacoin.dentacare.adapters.DCFriendDetailPagerAdapter;
import com.dentacoin.dentacare.model.DCChild;
import com.dentacoin.dentacare.model.DCError;
import com.dentacoin.dentacare.model.DCFriend;
import com.dentacoin.dentacare.network.DCApiManager;
import com.dentacoin.dentacare.network.DCResponseListener;
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

        setActionBarTitle(friend.getFullName());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_EDIT_CHILD && resultCode == RESULT_OK) {
            if (data.getSerializableExtra(KEY_UPDATE_OBJECT) instanceof DCChild) {
                onUpdate();
            }
            else if (data.getSerializableExtra(KEY_DELETE_OBJECT) instanceof DCChild) {
                finish();
            }
        }
    }

    private void setupUI(DCFriend friend) {
        if (friend != null) {
            this.friend = friend;
            setActionBarTitle(friend.getFullName());
            if (adapter != null) {
                adapter.setFriend(friend);
            }
        }
    }

    private void onUpdate() {
        DCApiManager.getInstance().getFriends(new DCResponseListener<DCFriend[]>() {
            @Override
            public void onFailure(DCError error) {
                onError(error);
                finish();
            }

            @Override
            public void onResponse(DCFriend[] friends) {
                if (friends != null) {
                    for (DCFriend friendObject : friends) {
                        if (friendObject.getId() == friend.getId()) {
                            setupUI(friendObject);
                            return;
                        }
                    }
                }
            }
        });
    }
}
