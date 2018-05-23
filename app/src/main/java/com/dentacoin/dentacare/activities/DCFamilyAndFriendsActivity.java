package com.dentacoin.dentacare.activities;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.dentacoin.dentacare.R;
import com.dentacoin.dentacare.adapters.DCFriendsAdapter;
import com.dentacoin.dentacare.adapters.viewholders.DCFriendViewHolder;
import com.dentacoin.dentacare.model.DCError;
import com.dentacoin.dentacare.model.DCFriend;
import com.dentacoin.dentacare.network.DCApiManager;
import com.dentacoin.dentacare.network.DCResponseListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

/**
 * Created by Atanas Chervarov on 7.05.18.
 */
public class DCFamilyAndFriendsActivity extends DCToolbarActivity implements SwipeRefreshLayout.OnRefreshListener, DCFriendViewHolder.IDCFriendListener {

    private SwipeRefreshLayout srlFriends;
    private FloatingActionButton fabAdd;
    private RecyclerView rvFamilyAndFriends;
    private DCFriendsAdapter adapter;
    private LinearLayout llNoFriends;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addContentView(R.layout.activity_family_and_friends);
        setActionBarTitle(R.string.family_hdl_title);

        adapter = new DCFriendsAdapter(this);

        srlFriends = findViewById(R.id.srl_friends);
        srlFriends.setOnRefreshListener(this);

        rvFamilyAndFriends = findViewById(R.id.rvFamilyAndFriends);
        rvFamilyAndFriends.setAdapter(adapter);
        rvFamilyAndFriends.setLayoutManager(new LinearLayoutManager(this));
        llNoFriends = findViewById(R.id.ll_no_friends);
        llNoFriends.setVisibility(View.VISIBLE);


        fabAdd = findViewById(R.id.fab_add);
        fabAdd.setOnClickListener(v -> new FabButtonDialog(DCFamilyAndFriendsActivity.this).show());
        loadFriends();
    }

    private void loadFriends() {
        DCApiManager.getInstance().getFriends(new DCResponseListener<DCFriend[]>() {
            @Override
            public void onFailure(DCError error) {
                onError(error);
                srlFriends.setRefreshing(false);
            }

            @Override
            public void onResponse(DCFriend[] object) {
                ArrayList<DCFriend> friends = new ArrayList<>();
                if (object != null) {
                    friends.addAll(Arrays.asList(object));
                }
                adapter.setData(friends);
                llNoFriends.setVisibility(friends.size() == 0 ? View.VISIBLE : View.GONE);
                srlFriends.setRefreshing(false);
            }
        });
    }

    @Override
    public void onRefresh() {
        loadFriends();
    }

    private void createChildAccount() {
        Intent intent = new Intent(this, DCCreateChildAccount.class);
        startActivity(intent);
    }

    private void inviteFriends() {
    }

    @Override
    public void onResume() {
        super.onResume();
        onRefresh();
    }
    
    @Override
    public void onFriendClicked(DCFriend friend) {
        startActivity(DCFriendDetailsActivity.createIntent(this, friend));
    }

    @Override
    public void onAccept(DCFriend friend) {
        //TODO:
    }

    @Override
    public void onDecline(DCFriend friend) {
        //TODO:
    }


    private class FabButtonDialog extends Dialog {
        FabButtonDialog(Context context) {
            super(context);
            init();
        }

        private void init() {
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            final View view = ViewGroup.inflate(getContext(), R.layout.dialog_family_buttons, null);
            setContentView(view);
            final Window window = getWindow();
            if (window != null) {
                window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
                window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL, WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);
            }

            findViewById(R.id.fab_create_child_account).setOnClickListener(v -> {
                createChildAccount();
                dismiss();
            });

            findViewById(R.id.fab_invite_friends).setOnClickListener(v -> {
                inviteFriends();
                dismiss();
            });
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            dismiss();
            return false;
        }
    }
}
