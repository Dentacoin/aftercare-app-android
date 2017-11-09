package com.dentacoin.dentacare.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;

import com.dentacoin.dentacare.R;
import com.dentacoin.dentacare.widgets.DCTextView;

/**
 * Created by Atanas Chervarov on 9/3/17.
 */

public class DCSentFeedbackFragment extends DCFragment {

    private ImageView ivFeedbackTooth;
    private DCTextView tvFeedbackThankYou;
    private DCTextView tvFeedbackMessage;

    private IDCFragmentInterface listener;
    private final Handler handler = new Handler();
    private Runnable runnable;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance) {
        View view = inflater.inflate(R.layout.fragment_sent_feedback, container, false);

        ivFeedbackTooth = (ImageView) view.findViewById(R.id.iv_feedback_tooth);
        tvFeedbackThankYou = (DCTextView) view.findViewById(R.id.tv_feedback_thank_you);
        tvFeedbackMessage = (DCTextView) view.findViewById(R.id.tv_feedback_message);


        Resources r = getResources();
        float translationPx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100, r.getDisplayMetrics());

        AlphaAnimation logoAnimation = new AlphaAnimation(0, 1);
        logoAnimation.setDuration(2000);
        ivFeedbackTooth.startAnimation(logoAnimation);

        TranslateAnimation thankYouTranslate = new TranslateAnimation(0, 0, translationPx, 0);
        thankYouTranslate.setDuration(1000);
        AlphaAnimation thankYouAlpha = new AlphaAnimation(0, 1);
        thankYouAlpha.setDuration(1000);

        AnimationSet thankYouSet = new AnimationSet(true);
        thankYouSet.addAnimation(thankYouTranslate);
        thankYouSet.addAnimation(thankYouAlpha);
        tvFeedbackThankYou.startAnimation(thankYouSet);

        AlphaAnimation messageAnimation = new AlphaAnimation(0, 1);
        messageAnimation.setDuration(3500);
        tvFeedbackMessage.startAnimation(messageAnimation);

        runnable = new Runnable() {
            @Override
            public void run() {
                if (getActivity() != null) {
                    if (listener != null)
                        listener.onFragmentRemoved();

                    getActivity().getFragmentManager().beginTransaction().remove(DCSentFeedbackFragment.this).commitAllowingStateLoss();
                }
            }
        };

        handler.postDelayed(runnable, 5000);

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof IDCFragmentInterface) {
            listener = (IDCFragmentInterface) context;
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof IDCFragmentInterface) {
            listener = (IDCFragmentInterface) activity;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (runnable != null) {
            handler.removeCallbacks(runnable);
        }
    }
}
