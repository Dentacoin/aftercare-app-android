package com.dentacoin.dentacare.adapters.viewholders;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.dentacoin.dentacare.R;
import com.dentacoin.dentacare.model.DCGoal;
import com.dentacoin.dentacare.widgets.DCTextView;

import java.util.Locale;

/**
 * Created by Atanas Chervarov on 10/4/17.
 */

public class DCGoalItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public interface IDCGoalItemListener {
        void onGoalItemClicked(DCGoal item);
    }

    private final ImageView ivGoalBackground;
    private final ImageView ivGoalIcon;
    private final DCTextView tvGoalNum;
    private final ImageView ivGoalStar;
    private final DCTextView tvGoalItemTitle;
    private final LinearLayout llGoalItem;
    private IDCGoalItemListener listener;

    public DCGoalItemViewHolder(View view) {
        super(view);

        ivGoalBackground = view.findViewById(R.id.iv_goal_background);
        tvGoalItemTitle = view.findViewById(R.id.tv_goal_item_title);
        llGoalItem = view.findViewById(R.id.ll_goal_item);
        ivGoalIcon = view.findViewById(R.id.iv_goal_icon);
        tvGoalNum = view.findViewById(R.id.tv_goal_num);
        ivGoalStar = view.findViewById(R.id.iv_goal_star);

        llGoalItem.setOnClickListener(this);
    }

    public void setup(Context context, DCGoal item, IDCGoalItemListener listener) {
        ivGoalBackground.setImageDrawable(context.getResources().getDrawable(R.drawable.badge_icon_bg_unachieved));
        ivGoalIcon.setImageDrawable(context.getResources().getDrawable(R.drawable.badge_icon_tooth));
        ivGoalIcon.setVisibility(View.VISIBLE);
        tvGoalNum.setText("");
        ivGoalStar.setImageDrawable(context.getResources().getDrawable(R.drawable.badge_icon_star_yellow));
        ivGoalStar.setVisibility(View.GONE);

        if (item != null) {
            if (item.isCompleted()) {
                switch (item.getType()) {
                    case WEEK:
                        ivGoalBackground.setImageDrawable(context.getResources().getDrawable(R.drawable.badge_icon_bg_week));
                        break;
                    case MONTH:
                        ivGoalBackground.setImageDrawable(context.getResources().getDrawable(R.drawable.badge_icon_bg_month));
                        break;
                    case YEAR:
                        ivGoalBackground.setImageDrawable(context.getResources().getDrawable(R.drawable.badge_icon_bg_year));
                        ivGoalStar.setImageDrawable(context.getResources().getDrawable(R.drawable.badge_icon_star_orange));
                        break;
                    default:
                        ivGoalBackground.setImageDrawable(context.getResources().getDrawable(R.drawable.badge_icon_bg_default));
                        break;
                }

                ivGoalStar.setVisibility(View.VISIBLE);
            }

            if (item.getAmount() != null) {
                tvGoalNum.setText(String.format(Locale.ENGLISH, "%d", item.getAmount()));
                ivGoalIcon.setVisibility(View.GONE);
            }

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
