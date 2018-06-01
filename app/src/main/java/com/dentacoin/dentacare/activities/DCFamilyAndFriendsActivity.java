package com.dentacoin.dentacare.activities;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BaseTransientBottomBar;
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
import com.dentacoin.dentacare.fragments.DCLoadingFragment;
import com.dentacoin.dentacare.model.DCError;
import com.dentacoin.dentacare.model.DCFriend;
import com.dentacoin.dentacare.model.DCInvitationToken;
import com.dentacoin.dentacare.network.DCApiManager;
import com.dentacoin.dentacare.network.DCResponseListener;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;

import java.util.ArrayList;
import java.util.Arrays;

import de.mateware.snacky.Snacky;

import static com.dentacoin.dentacare.utils.DCConstants.DENTACARE_WEBSITE;
import static com.dentacoin.dentacare.utils.DCConstants.FIREBASE_SHARE_LINK;
import static com.dentacoin.dentacare.utils.DCConstants.IOS_BUNDLE_ID;

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
        final DCLoadingFragment loadingFragment = showLoading();
        DCApiManager.getInstance().getInvitationToken(new DCResponseListener<DCInvitationToken>() {
            @Override
            public void onFailure(DCError error) {
                onError(error);
                loadingFragment.dismissAllowingStateLoss();
            }

            @Override
            public void onResponse(DCInvitationToken object) {
                String link = FIREBASE_SHARE_LINK + "?link=" + DENTACARE_WEBSITE + "invites/" + object.getToken() + "&apn=" + getApplicationContext().getPackageName() + "&ibi=" + IOS_BUNDLE_ID + "&dfl=" + DENTACARE_WEBSITE;
                FirebaseDynamicLinks.getInstance().createDynamicLink()
                        .setLongLink(Uri.parse(link))
                        .buildShortDynamicLink()
                        .addOnCompleteListener(task -> {
                            loadingFragment.dismissAllowingStateLoss();
                            if (task.getResult() != null && task.getResult().getShortLink() != null) {
                                Intent sendIntent = new Intent();
                                sendIntent.setAction(Intent.ACTION_SEND);
                                sendIntent.putExtra(Intent.EXTRA_TEXT, task.getResult().getShortLink().toString());
                                sendIntent.setType("text/plain");
                                startActivity(Intent.createChooser(sendIntent, getString(R.string.invites_txt_send_via)));
                            } else {
                                onError(new DCError(getString(R.string.error_txt_something_went_wrong)));
                            }
                        });
            }
        });
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
    public void onAccept(final DCFriend friend) {
        DCApiManager.getInstance().acceptInvitation(friend.getId(), new DCResponseListener<Void>() {
            @Override
            public void onFailure(DCError error) {
                onError(error);
            }

            @Override
            public void onResponse(Void object) {
                onRefresh();
                Snacky.builder().setActivty(DCFamilyAndFriendsActivity.this)
                        .success()
                        .setText(friend.getFullName().isEmpty() ? getString(R.string.family_txt_added_to_list, "User") : getString(R.string.family_txt_added_to_list, friend.getFullName()))
                        .setDuration(BaseTransientBottomBar.LENGTH_SHORT)
                        .show();
            }
        });
    }

    @Override
    public void onDecline(DCFriend friend) {
        DCApiManager.getInstance().declineInvitation(friend.getId(), new DCResponseListener<Void>() {
            @Override
            public void onFailure(DCError error) {
                onError(error);
            }

            @Override
            public void onResponse(Void object) {
                onRefresh();
                Snacky.builder().setActivty(DCFamilyAndFriendsActivity.this)
                        .success()
                        .setText(R.string.family_txt_request_declined)
                        .setDuration(BaseTransientBottomBar.LENGTH_SHORT)
                        .show();
            }
        });
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
