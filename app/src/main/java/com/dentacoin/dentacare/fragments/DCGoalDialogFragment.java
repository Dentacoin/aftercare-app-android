package com.dentacoin.dentacare.fragments;

import android.app.DialogFragment;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.dentacoin.dentacare.R;
import com.dentacoin.dentacare.model.DCGoal;
import com.dentacoin.dentacare.utils.DCConstants;
import com.dentacoin.dentacare.widgets.DCTextView;
import com.facebook.share.model.ShareHashtag;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareButton;

/**
 * Created by Atanas Chervarov on 10/9/17.
 */

public class DCGoalDialogFragment extends DialogFragment {
    public static final String TAG = DCLoginFragment.class.getSimpleName();
    public static final String KEY_REACHED_GOAL = "KEY_REACHED_GOAL";

    private ImageView ivGoalIcon;
    private DCTextView tvGoalTitle;
    private DCTextView tvGoalDescription;
    private ShareButton fbGoalShare;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_goal, container, false);
        ivGoalIcon = (ImageView) view.findViewById(R.id.iv_goal_icon);
        tvGoalTitle = (DCTextView) view.findViewById(R.id.tv_goal_title);
        tvGoalDescription = (DCTextView) view.findViewById(R.id.tv_goal_description);
        fbGoalShare = (ShareButton) view.findViewById(R.id.fb_goal_share);
        fbGoalShare.setVisibility(View.GONE);

        Bundle arguments = getArguments();
        DCGoal goal = null;

        if (arguments != null && arguments.containsKey(KEY_REACHED_GOAL)) {
            if (arguments.getSerializable(KEY_REACHED_GOAL) instanceof DCGoal) {
                goal = (DCGoal) arguments.getSerializable(KEY_REACHED_GOAL);
            }
        }

        if (goal != null) {
            ivGoalIcon.setImageURI(goal.getImageUri());
            tvGoalTitle.setText(goal.getTitle());
            tvGoalDescription.setText(goal.getDescription());

            ShareLinkContent shareLinkContent = new ShareLinkContent.Builder()
                    .setContentUrl(Uri.parse(DCConstants.DENTACARE_WEBSITE))
                    .setShareHashtag(new ShareHashtag.Builder()
                            .setHashtag("#dentacoin")
                            .build())
                    .setQuote(getString(R.string.fb_share_goal_message, Integer.toString(goal.getReward()), goal.getTitle()))
                    .build();

            if (goal.isCompleted()) {
                fbGoalShare.setShareContent(shareLinkContent);
                fbGoalShare.setVisibility(View.VISIBLE);
            }
        } else {
            dismiss();
        }

        return view;
    }
}
