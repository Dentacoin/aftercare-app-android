package com.dentacoin.dentacare.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;

import com.dentacoin.dentacare.R;
import com.dentacoin.dentacare.adapters.DCWithdrawAdapter;
import com.dentacoin.dentacare.model.DCError;
import com.dentacoin.dentacare.model.DCTransaction;
import com.dentacoin.dentacare.network.DCApiManager;
import com.dentacoin.dentacare.network.DCResponseListener;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Atanas Chervarov on 15.02.18.
 */

public class DCWithdrawsActivity extends DCToolbarActivity implements SwipeRefreshLayout.OnRefreshListener {

    private SwipeRefreshLayout srlWithdraws;
    private RecyclerView rvWithdraws;
    private LinearLayout llWithdrawsNoWithdraws;
    private DCWithdrawAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addContentView(R.layout.activity_withdraws);
        setActionBarTitle(R.string.withdraws_hdl_title);
        srlWithdraws = findViewById(R.id.srl_transactions);
        srlWithdraws.setOnRefreshListener(this);
        rvWithdraws = findViewById(R.id.rv_transactions);
        llWithdrawsNoWithdraws = findViewById(R.id.ll_transactions_no_transactions);

        adapter = new DCWithdrawAdapter(this);
        rvWithdraws.setAdapter(adapter);
        rvWithdraws.setLayoutManager(new LinearLayoutManager(this));
        loadTransactions();
    }

    @Override
    public void onRefresh() {
        loadTransactions();
    }

    private void loadTransactions() {
        DCApiManager.getInstance().getTransactions(new DCResponseListener<DCTransaction[]>() {
            @Override
            public void onFailure(DCError error) {
                onError(error);
                srlWithdraws.setRefreshing(false);
            }

            @Override
            public void onResponse(DCTransaction[] object) {
                srlWithdraws.setRefreshing(false);
                if (object != null) {
                    adapter.setItems(new ArrayList<>(Arrays.asList(object)));
                    if (object.length > 0) {
                        llWithdrawsNoWithdraws.setVisibility(View.GONE);
                    } else {
                        llWithdrawsNoWithdraws.setVisibility(View.VISIBLE);
                    }
                } else {
                    adapter.setItems(null);
                    llWithdrawsNoWithdraws.setVisibility(View.VISIBLE);
                }
            }
        });
    }
}
