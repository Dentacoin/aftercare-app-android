package com.dentacoin.dentacare.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.dentacoin.dentacare.R;
import com.dentacoin.dentacare.adapters.viewholders.DCGoalItemViewHolder;
import com.dentacoin.dentacare.model.DCGoal;

import java.util.ArrayList;

/**
 * Created by Atanas Chervarov on 10/4/17.
 */

public class DCGoalsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<DCGoal> items;
    private DCGoalItemViewHolder.IDCGoalItemListener listener;
    private Context context;

    public DCGoalsAdapter(Context context, DCGoalItemViewHolder.IDCGoalItemListener listener) {
        this.context = context;
        items = new ArrayList<>();
        this.listener = listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new DCGoalItemViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.view_holder_goal_item, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        DCGoalItemViewHolder viewHolder = (DCGoalItemViewHolder) holder;
        viewHolder.setup(context, items.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void setItems(ArrayList<DCGoal> items) {
        this.items.clear();
        if (items != null) {
            this.items = items;
        }
        notifyDataSetChanged();
    }
}
