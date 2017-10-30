package com.dentacoin.dentacare.fragments;

import android.net.Uri;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.dentacoin.dentacare.R;
import com.dentacoin.dentacare.model.DCGoal;
import com.dentacoin.dentacare.utils.DCConstants;
import com.dentacoin.dentacare.widgets.DCTextView;
import com.facebook.share.model.ShareHashtag;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareButton;

import java.util.Locale;

/**
 * Created by Atanas Chervarov on 10/9/17.
 */

public class DCGoalDialogFragment extends DCDialogFragment {
    public static final String TAG = DCLoginFragment.class.getSimpleName();
    public static final String KEY_GOAL = "KEY_GOAL";

    private RelativeLayout rlGoalBadge;
    private ImageView ivGoalDottedBackground;
    private ImageView ivGoalFlag;
    private RelativeLayout rlGoalBackground;
    private ImageView ivGoalBackground;
    private ImageView ivGoalIconTooth;
    private DCTextView tvGoalNumber;
    private ImageView ivGoalStar;
    private DCTextView tvGoalTitle;
    private DCTextView tvGoalDescription;
    private ShareButton fbGoalShare;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_goal, container);

        rlGoalBadge = (RelativeLayout) view.findViewById(R.id.rl_goal_badge);
        ivGoalDottedBackground = (ImageView) view.findViewById(R.id.iv_goal_dotted_background);
        ivGoalFlag = (ImageView) view.findViewById(R.id.iv_goal_flag);
        rlGoalBackground = (RelativeLayout) view.findViewById(R.id.rl_goal_background);
        ivGoalBackground = (ImageView) view.findViewById(R.id.iv_goal_background);
        ivGoalIconTooth = (ImageView) view.findViewById(R.id.iv_goal_icon_tooth);
        tvGoalNumber = (DCTextView) view.findViewById(R.id.tv_goal_number);
        ivGoalStar = (ImageView) view.findViewById(R.id.iv_goal_star);

        tvGoalTitle = (DCTextView) view.findViewById(R.id.tv_goal_title);
        tvGoalDescription = (DCTextView) view.findViewById(R.id.tv_goal_description);
        fbGoalShare = (ShareButton) view.findViewById(R.id.fb_goal_share);
        fbGoalShare.setVisibility(View.GONE);

        ivGoalFlag.setImageDrawable(getResources().getDrawable(R.drawable.badge_bg_unachieved_flag));
        ivGoalBackground.setImageDrawable(getResources().getDrawable(R.drawable.badge_bg_unachieved));
        ivGoalIconTooth.setVisibility(View.VISIBLE);
        tvGoalNumber.setText("");
        ivGoalStar.setImageDrawable(getResources().getDrawable(R.drawable.badge_star_yellow));
        ivGoalStar.setVisibility(View.GONE);

        Bundle arguments = getArguments();
        DCGoal goal = null;

        if (arguments != null && arguments.containsKey(KEY_GOAL)) {
            if (arguments.getSerializable(KEY_GOAL) instanceof DCGoal) {
                goal = (DCGoal) arguments.getSerializable(KEY_GOAL);
            }
        }


        if (goal != null) {
            tvGoalTitle.setText(goal.getTitle());
            tvGoalDescription.setText(goal.getDescription());
            if (goal.getAmount() != null) {
                tvGoalNumber.setText(String.format(Locale.ENGLISH, "%d", goal.getAmount()));
                ivGoalIconTooth.setVisibility(View.GONE);
            }

            if (goal.isCompleted()) {
                ivGoalStar.setVisibility(View.VISIBLE);

                switch (goal.getType()) {
                    case WEEK:
                        ivGoalFlag.setImageDrawable(getResources().getDrawable(R.drawable.badge_bg_week_flag));
                        ivGoalBackground.setImageDrawable(getResources().getDrawable(R.drawable.badge_bg_week));
                        break;
                    case MONTH:
                        ivGoalFlag.setImageDrawable(getResources().getDrawable(R.drawable.badge_bg_month_flag));
                        ivGoalBackground.setImageDrawable(getResources().getDrawable(R.drawable.badge_bg_month));
                        break;
                    case YEAR:
                        ivGoalFlag.setImageDrawable(getResources().getDrawable(R.drawable.badge_bg_year_flag));
                        ivGoalBackground.setImageDrawable(getResources().getDrawable(R.drawable.badge_bg_year));
                        ivGoalStar.setImageDrawable(getResources().getDrawable(R.drawable.badge_star_orange));
                        break;
                    default:
                        ivGoalFlag.setImageDrawable(getResources().getDrawable(R.drawable.badge_bg_default_flag));
                        ivGoalBackground.setImageDrawable(getResources().getDrawable(R.drawable.badge_bg_default));
                        break;
                }

                ShareLinkContent shareLinkContent = new ShareLinkContent.Builder()
                        .setContentUrl(Uri.parse(DCConstants.DENTACARE_GOOGLE_PLAY))
                        .setShareHashtag(new ShareHashtag.Builder()
                                .setHashtag("#dentacoin")
                                .build())
                        .setQuote(getString(R.string.fb_share_goal_message, Integer.toString(goal.getReward()), goal.getTitle()))
                        .build();

                fbGoalShare.setShareContent(shareLinkContent);
                fbGoalShare.setVisibility(View.VISIBLE);

                showAnimation();
            }
        } else {
            dismiss();
        }

        return view;
    }

    private void showAnimation() {
        AnimationSet dottedBackgroundAnimation = new AnimationSet(true);

        AlphaAnimation dottedBackgroundAlphaAnimation = new AlphaAnimation(0f, 1f);
        dottedBackgroundAlphaAnimation.setDuration(1000);

        RotateAnimation dottedBackgroundRotateAnimation = new RotateAnimation(0f, 60f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        dottedBackgroundRotateAnimation.setDuration(800);

        dottedBackgroundAnimation.addAnimation(dottedBackgroundAlphaAnimation);
        dottedBackgroundAnimation.addAnimation(dottedBackgroundRotateAnimation);

        ivGoalDottedBackground.startAnimation(dottedBackgroundAnimation);

        Animation backgroundAnimation = new ScaleAnimation(0f, 1f, 0f, 1f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        backgroundAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
        backgroundAnimation.setDuration(200);
        backgroundAnimation.setStartOffset(100);

        ivGoalBackground.startAnimation(backgroundAnimation);


        AnimationSet flagAnimationSet = new AnimationSet(true);
        flagAnimationSet.setStartOffset(300);

        float flagTranslationPx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 30, getResources().getDisplayMetrics());
        Animation flagTranslateAnimation = new TranslateAnimation(0f, 0f, -flagTranslationPx, 0f);
        flagTranslateAnimation.setDuration(1000);

        AlphaAnimation flagAlphaAnimation = new AlphaAnimation(0f, 1f);
        flagAlphaAnimation.setDuration(500);

        flagAnimationSet.addAnimation(flagTranslateAnimation);
        flagAnimationSet.addAnimation(flagAlphaAnimation);

        ivGoalFlag.startAnimation(flagAnimationSet);

        AnimationSet numberAnimationSet = new AnimationSet(true);

        AlphaAnimation numberAlphaAnimation = new AlphaAnimation(0f, 1f);
        numberAlphaAnimation.setDuration(1000);

        float numberTranslationPx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20, getResources().getDisplayMetrics());
        Animation numberTranslateAnimation = new TranslateAnimation(0f, 0f, numberTranslationPx, 0f);
        numberTranslateAnimation.setDuration(1000);

        numberAnimationSet.addAnimation(numberAlphaAnimation);
        numberAnimationSet.addAnimation(numberTranslateAnimation);

        tvGoalNumber.startAnimation(numberAnimationSet);

        AnimationSet iconAnimationSet = new AnimationSet(true);

        AlphaAnimation iconAlphaAnimation = new AlphaAnimation(0f, 1f);
        iconAlphaAnimation.setDuration(1000);

        Animation iconTranslateAnimation = new TranslateAnimation(0f, 0f, numberTranslationPx, 0f);
        iconTranslateAnimation.setDuration(1000);

        iconAnimationSet.addAnimation(iconAlphaAnimation);
        iconAnimationSet.addAnimation(iconTranslateAnimation);

        ivGoalIconTooth.startAnimation(iconAnimationSet);

        AnimationSet starAnimationSet = new AnimationSet(true);
        starAnimationSet.setStartOffset(500);

        AlphaAnimation starAlphaAnimation = new AlphaAnimation(0f, 1f);
        starAlphaAnimation.setDuration(100);

        ScaleAnimation starScaleAnimation = new ScaleAnimation(2f, 1f, 2f, 1f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        starScaleAnimation.setDuration(200);

        RotateAnimation rotateStarAnimation = new RotateAnimation(0f, 120f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotateStarAnimation.setDuration(200);

        starAnimationSet.addAnimation(starAlphaAnimation);
        starAnimationSet.addAnimation(starScaleAnimation);
        starAnimationSet.addAnimation(rotateStarAnimation);

        ivGoalStar.startAnimation(starAnimationSet);
    }
}
