package com.dentacoin.dentacare.fragments;

import android.content.res.Resources;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;

import com.dentacoin.dentacare.R;
import com.dentacoin.dentacare.widgets.DCTextView;
import com.facebook.drawee.view.SimpleDraweeView;

/**
 * Created by Atanas Chervarov on 8/8/17.
 */

public class DCWelcomeFragment extends DCFragment {

    private SimpleDraweeView sdvWelcomeAvatar;
    private DCTextView tvWelcome;
    private DCTextView tvWelcomeName;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance) {
        View view = inflater.inflate(R.layout.fragment_welcome, container, false);
        sdvWelcomeAvatar = (SimpleDraweeView) view.findViewById(R.id.sdv_welcome_avatar);
        tvWelcome = (DCTextView) view.findViewById(R.id.tv_welcome);
        tvWelcomeName = (DCTextView) view.findViewById(R.id.tv_welcome_name);

        sdvWelcomeAvatar.setImageURI("http://lorempixel.com/512/512/people/");

        Resources r = getResources();
        float translationPx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100, r.getDisplayMetrics());

        AlphaAnimation avatarAnimation = new AlphaAnimation(0, 1);
        avatarAnimation.setDuration(2000);
        sdvWelcomeAvatar.startAnimation(avatarAnimation);

        TranslateAnimation welcomeAnimationTranslate = new TranslateAnimation(0, 0, translationPx, 0);
        welcomeAnimationTranslate.setDuration(1000);
        AlphaAnimation welcomeAnimationAlpha = new AlphaAnimation(0, 1);
        welcomeAnimationAlpha.setDuration(1000);

        AnimationSet welcomeAnimation = new AnimationSet(true);
        welcomeAnimation.addAnimation(welcomeAnimationAlpha);
        welcomeAnimation.addAnimation(welcomeAnimationTranslate);
        tvWelcome.startAnimation(welcomeAnimation);

        AlphaAnimation welcomeAnimationName = new AlphaAnimation(0, 1);
        welcomeAnimationName.setDuration(2500);
        tvWelcomeName.startAnimation(welcomeAnimationName);

        return view;
    }
}
