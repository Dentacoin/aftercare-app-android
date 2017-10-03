package com.dentacoin.dentacare.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.dentacoin.dentacare.R;
import com.dentacoin.dentacare.adapters.viewholders.DCOralHealthItemViewHolder;
import com.dentacoin.dentacare.model.DCOralHealthItem;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Atanas Chervarov on 10/1/17.
 */

public class DCOralHealthAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<DCOralHealthItem> items;
    private DCOralHealthItemViewHolder.IDCOralHealthItemListener listener;

    public DCOralHealthAdapter(DCOralHealthItemViewHolder.IDCOralHealthItemListener listener) {
        items = new ArrayList<>();
        this.listener = listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new DCOralHealthItemViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.view_holder_oral_health_item, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        DCOralHealthItemViewHolder viewHolder = (DCOralHealthItemViewHolder) holder;
        viewHolder.setup(items.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void setItems(DCOralHealthItem[] items) {
        this.items.clear();
        if (items != null) {
            this.items.addAll(Arrays.asList(items));
        }
        notifyDataSetChanged();
    }
}
