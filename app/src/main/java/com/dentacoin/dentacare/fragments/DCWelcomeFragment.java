package com.dentacoin.dentacare.fragments;

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

import com.dentacoin.dentacare.R;
import com.dentacoin.dentacare.model.DCError;
import com.dentacoin.dentacare.model.DCUser;
import com.dentacoin.dentacare.network.DCApiManager;
import com.dentacoin.dentacare.network.DCResponseListener;
import com.dentacoin.dentacare.utils.DCSharedPreferences;
import com.dentacoin.dentacare.widgets.DCTextView;
import com.facebook.drawee.view.SimpleDraweeView;

/**
 * Created by Atanas Chervarov on 8/8/17.
 */

public class DCWelcomeFragment extends DCFragment {

    public static final String TAG = DCWelcomeFragment.class.getSimpleName();

    private SimpleDraweeView sdvWelcomeAvatar;
    private DCTextView tvWelcome;
    private DCTextView tvWelcomeName;
    private final Handler handler = new Handler();
    private IDCFragmentInterface listener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance) {
        View view = inflater.inflate(R.layout.fragment_welcome, container, false);
        sdvWelcomeAvatar = (SimpleDraweeView) view.findViewById(R.id.sdv_welcome_avatar);
        tvWelcome = (DCTextView) view.findViewById(R.id.tv_welcome);
        tvWelcomeName = (DCTextView) view.findViewById(R.id.tv_welcome_name);

        Resources r = getResources();
        float translationPx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100, r.getDisplayMetrics());

        AlphaAnimation avatarAnimation = new AlphaAnimation(0, 1);
        avatarAnimation.setDuration(2500);
        sdvWelcomeAvatar.startAnimation(avatarAnimation);

        TranslateAnimation welcomeAnimationTranslate = new TranslateAnimation(0, 0, translationPx, 0);
        welcomeAnimationTranslate.setDuration(1500);
        AlphaAnimation welcomeAnimationAlpha = new AlphaAnimation(0, 1);
        welcomeAnimationAlpha.setDuration(1500);

        AnimationSet welcomeAnimation = new AnimationSet(true);
        welcomeAnimation.addAnimation(welcomeAnimationAlpha);
        welcomeAnimation.addAnimation(welcomeAnimationTranslate);
        tvWelcome.startAnimation(welcomeAnimation);

        AlphaAnimation welcomeAnimationName = new AlphaAnimation(0, 1);
        welcomeAnimationName.setDuration(3000);
        tvWelcomeName.startAnimation(welcomeAnimationName);

        loadUser();

        DCSharedPreferences.saveBoolean(DCSharedPreferences.DCSharedKey.WELCOME_SCREEN, true);
        return view;
    }

    /**
     * Load the user object, load up the name & avatar
     * Handle any error or display image & name when successful and remove itself from the current activity
     */
    private void loadUser() {
        DCApiManager.getInstance().getUser(new DCResponseListener<DCUser>() {
            @Override
            public void onFailure(DCError error) {
                onError(error);

                if (listener != null)
                    listener.onFragmentRemoved();

                getActivity().getFragmentManager().beginTransaction().remove(DCWelcomeFragment.this).commit();
            }

            @Override
            public void onResponse(DCUser object) {
                if (object != null) {
                    sdvWelcomeAvatar.setImageURI(object.getAvatarUrl(getActivity()));
                    tvWelcomeName.setText(object.getFullName());
                }

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (getActivity() != null) {
                            if (listener != null)
                                listener.onFragmentRemoved();

                            getActivity().getFragmentManager().beginTransaction().remove(DCWelcomeFragment.this).commit();
                        }
                    }
                }, 4000);
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof IDCFragmentInterface) {
            listener = (IDCFragmentInterface) context;
        }
    }
}
