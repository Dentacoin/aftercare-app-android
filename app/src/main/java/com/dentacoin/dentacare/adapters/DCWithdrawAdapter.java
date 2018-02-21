package com.dentacoin.dentacare.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.dentacoin.dentacare.R;
import com.dentacoin.dentacare.adapters.viewholders.DCGoalItemViewHolder;
import com.dentacoin.dentacare.adapters.viewholders.DCWithdrawItemViewHolder;
import com.dentacoin.dentacare.model.DCTransaction;

import java.util.ArrayList;

/**
 * Created by Atanas Chervarov on 16.02.18.
 */

public class DCWithdrawAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private ArrayList<DCTransaction> items;

    public DCWithdrawAdapter(Context context) {
        this.context = context;
        items = new ArrayList<>();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new DCWithdrawItemViewHolder(context, LayoutInflater.from(parent.getContext()).inflate(R.layout.view_holder_withdraw_item, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        DCWithdrawItemViewHolder viewHolder = (DCWithdrawItemViewHolder) holder;
        viewHolder.setup(items.get(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void setItems(ArrayList<DCTransaction> items) {
        this.items.clear();
        if (items != null) {
            this.items = items;
        }
        notifyDataSetChanged();
    }
}
