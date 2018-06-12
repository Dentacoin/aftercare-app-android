package com.dentacoin.dentacare.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dentacoin.dentacare.R;
import com.dentacoin.dentacare.adapters.viewholders.DCFriendViewHolder;
import com.dentacoin.dentacare.adapters.viewholders.DCSectionViewHolder;
import com.dentacoin.dentacare.model.DCFriend;
import com.dentacoin.dentacare.model.DCSection;

import org.zakariya.stickyheaders.SectioningAdapter;

import java.util.ArrayList;

/**
 * Created by Atanas Chervarov on 11.05.18.
 */
public class DCFriendsAdapter extends DCSectioningAdapter {

    private ArrayList<DCSection<DCFriend>> data;
    private DCFriendViewHolder.IDCFriendListener listener;

    public DCFriendsAdapter(DCFriendViewHolder.IDCFriendListener listener) {
        super();
        data = new ArrayList<>();
        this.listener = listener;
    }

    public void setData(ArrayList<DCFriend> data) {
        this.data.clear();

        if (data != null) {
            DCSection<DCFriend> family = new DCSection<>();
            family.setTitle(R.string.family_hdl_family);
            family.setData(new ArrayList<>());

            DCSection<DCFriend> friends = new DCSection<>();
            friends.setTitle(R.string.family_hdl_friends);
            friends.setData(new ArrayList<>());

            for (DCFriend friend : data) {
                if (friend.isChild() || friend.isFamily()) {
                    family.getData().add(friend);
                } else if (friend.isFriend()) {
                    friends.getData().add(friend);
                }
            }

            if (family.getData().size() > 0)
                this.data.add(family);

            if (friends.getData().size() > 0)
                this.data.add(friends);
        }

        notifyAllSectionsDataSetChanged();
    }

    public ArrayList<DCSection<DCFriend>> getData() {
        return data;
    }

    @Override
    public int getNumberOfSections() {
        return data.size();
    }

    @Override
    public int getNumberOfItemsInSection(int sectionIndex) {
        if (data.size() > sectionIndex) {
            if (data.get(sectionIndex).getData() != null) {
                return data.get(sectionIndex).getData().size();
            }
        }
        return 0;
    }

    @Override
    public boolean doesSectionHaveHeader(int sectionIndex) {
        return true;
    }

    @Override
    public boolean doesSectionHaveFooter(int sectionIndex) {
        return false;
    }

    @Override
    public ItemViewHolder onCreateItemViewHolder(ViewGroup parent, int itemUserType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.view_holder_friend_item, parent, false);
        return new DCFriendViewHolder(view, listener);
    }

    @Override
    public HeaderViewHolder onCreateHeaderViewHolder(ViewGroup parent, int itemUserType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_holder_header_item, parent, false);
        return new DCSectionViewHolder(view);
    }

    @Override
    public void onBindItemViewHolder(SectioningAdapter.ItemViewHolder viewHolder, int sectionIndex, int itemIndex, int itemUserType) {
        ((DCFriendViewHolder) viewHolder).setupView(data.get(sectionIndex).getData().get(itemIndex));
    }

    @Override
    public void onBindHeaderViewHolder(SectioningAdapter.HeaderViewHolder viewHolder, int sectionIndex, int itemUserType) {
        ((DCSectionViewHolder) viewHolder).setTitle(data.get(sectionIndex).getTitle());
    }
}
