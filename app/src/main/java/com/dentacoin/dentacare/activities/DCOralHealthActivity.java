package com.dentacoin.dentacare.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;

import com.dentacoin.dentacare.R;
import com.dentacoin.dentacare.adapters.DCOralHealthAdapter;
import com.dentacoin.dentacare.adapters.viewholders.DCOralHealthItemViewHolder;
import com.dentacoin.dentacare.model.DCError;
import com.dentacoin.dentacare.model.DCOralHealthItem;
import com.dentacoin.dentacare.network.DCApiManager;
import com.dentacoin.dentacare.network.DCResponseListener;


/**
 * Created by Atanas Chervarov on 10/1/17.
 */

public class DCOralHealthActivity extends DCToolbarActivity implements SwipeRefreshLayout.OnRefreshListener, DCOralHealthItemViewHolder.IDCOralHealthItemListener {

    private RecyclerView rvOralHealth;
    private LinearLayout llOralHealthNoPosts;
    private DCOralHealthAdapter adapter;
    private SwipeRefreshLayout srlOralHealth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addContentView(R.layout.activity_oral_health);
        setActionBarTitle(R.string.oral_health_hdl_oral_health);

        rvOralHealth = (RecyclerView) findViewById(R.id.rv_oral_health);
        llOralHealthNoPosts = (LinearLayout) findViewById(R.id.ll_oral_health_no_posts);
        srlOralHealth = (SwipeRefreshLayout) findViewById(R.id.srl_oral_health);
        srlOralHealth.setOnRefreshListener(this);

        adapter = new DCOralHealthAdapter(this);
        rvOralHealth.setAdapter(adapter);
        rvOralHealth.setLayoutManager(new LinearLayoutManager(this));

        loadOralHealth();
    }

    @Override
    public void onRefresh() {
        loadOralHealth();
    }

    private void loadOralHealth() {
        DCApiManager.getInstance().getOralHealth(new DCResponseListener<DCOralHealthItem[]>() {
            @Override
            public void onFailure(DCError error) {
                onError(error);
                srlOralHealth.setRefreshing(false);
            }

            @Override
            public void onResponse(DCOralHealthItem[] object) {
                adapter.setItems(object);
                if (adapter.getItemCount() > 0) {
                    llOralHealthNoPosts.setVisibility(View.GONE);
                } else {
                    llOralHealthNoPosts.setVisibility(View.VISIBLE);
                }
                srlOralHealth.setRefreshing(false);
            }
        });
    }

    @Override
    public void onOralHealthItemClicked(DCOralHealthItem item) {
        if (item != null) {
            if (item.getUrl() != null) {
                Intent webIntent = new Intent(this, DCWebActivity.class);
                webIntent.putExtra(DCWebActivity.KEY_WEB_ADDRESS, item.getUrl());
                webIntent.putExtra(DCWebActivity.KEY_TITLE, item.getTitle());
                startActivity(webIntent);
            }
        }
    }
}
