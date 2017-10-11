package com.dentacoin.dentacare.adapters.viewholders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.dentacoin.dentacare.R;
import com.dentacoin.dentacare.model.DCGoal;
import com.dentacoin.dentacare.widgets.DCTextView;

/**
 * Created by Atanas Chervarov on 10/4/17.
 */

public class DCGoalItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public interface IDCGoalItemListener {
        void onGoalItemClicked(DCGoal item);
    }

    private final ImageView ivGoalItem;
    private final DCTextView tvGoalItemTitle;
    private final LinearLayout llGoalItem;
    private IDCGoalItemListener listener;

    public DCGoalItemViewHolder(View view) {
        super(view);
        ivGoalItem = (ImageView) view.findViewById(R.id.iv_goal_item);
        tvGoalItemTitle = (DCTextView) view.findViewById(R.id.tv_goal_item_title);
        llGoalItem = (LinearLayout) view.findViewById(R.id.ll_goal_item);
        llGoalItem.setOnClickListener(this);
    }

    public void setup(DCGoal item, IDCGoalItemListener listener) {
        if (item != null) {
            ivGoalItem.setImageURI(item.getImageUri());
            tvGoalItemTitle.setText(item.getTitle());
            llGoalItem.setTag(item);
        }
        this.listener = listener;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_goal_item:
                if (listener != null) {
                    if (view.getTag() instanceof DCGoal) {
                        listener.onGoalItemClicked((DCGoal) view.getTag());
                    }
                }
                break;
        }
    }
}
