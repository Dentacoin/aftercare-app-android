package com.dentacoin.dentacare.adapters;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.support.v13.app.FragmentStatePagerAdapter;

import com.dentacoin.dentacare.R;
import com.dentacoin.dentacare.fragments.DCFriendGoalsFragment;
import com.dentacoin.dentacare.fragments.DCFriendInfoFragment;
import com.dentacoin.dentacare.fragments.DCFriendStatisticsFragment;
import com.dentacoin.dentacare.model.DCFriend;

/**
 * Created by Atanas Chervarov on 21.05.18.
 */
public class DCFriendDetailPagerAdapter extends FragmentStatePagerAdapter {

    private static final int COUNT = 3;
    private final String infoTitle;
    private final String statisticsTitle;
    private final String goalsTitle;
    private DCFriend friend;

    public DCFriendDetailPagerAdapter(Context context, DCFriend friend, FragmentManager fm) {
        super(fm);
        infoTitle = context.getString(R.string.friend_tab_info);
        statisticsTitle = context.getString(R.string.friend_tab_statistics);
        goalsTitle = context.getString(R.string.friend_tab_goals);
        this.friend = friend;
    }

    public void setFriend(DCFriend friend) {
        this.friend = friend;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return COUNT;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return DCFriendInfoFragment.create(friend);
            case 1:
                return DCFriendStatisticsFragment.create(friend);
            case 2:
                return DCFriendGoalsFragment.create(friend);
        }
        return null;
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return infoTitle;
            case 1:
                return statisticsTitle;
            case 2:
                return goalsTitle;
        }
        return null;
    }
}