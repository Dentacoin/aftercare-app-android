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

import static com.dentacoin.dentacare.activities.DCCollectActivity.KEY_DCN_AMOUNT;

/**
 * Created by Atanas Chervarov on 9/29/17.
 */

public class DCCollectSuccessFragment extends DCFragment {

    private ImageView ivCollectDentacoin;
    private DCTextView tvCollectAmount;
    private DCTextView tvCollectMessage;
    private final Handler handler = new Handler();
    private IDCFragmentInterface listener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance) {
        View view = inflater.inflate(R.layout.fragment_collected_dcn, container, false);
        int amount = 0;

        Bundle arguments = getArguments();
        if (arguments != null && arguments.containsKey(KEY_DCN_AMOUNT)) {
            amount = arguments.getInt(KEY_DCN_AMOUNT);
        }

        ivCollectDentacoin = (ImageView) view.findViewById(R.id.iv_collect_dentacoin);
        tvCollectAmount = (DCTextView) view.findViewById(R.id.tv_collect_amount);
        tvCollectMessage = (DCTextView) view.findViewById(R.id.tv_collect_message);
        tvCollectAmount.setText(getString(R.string.txt_dcn, amount));

        Resources r = getResources();
        float translationPx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100, r.getDisplayMetrics());

        AlphaAnimation logoAnimation = new AlphaAnimation(0, 1);
        logoAnimation.setDuration(2000);
        ivCollectDentacoin.startAnimation(logoAnimation);

        TranslateAnimation thankYouTranslate = new TranslateAnimation(0, 0, translationPx, 0);
        thankYouTranslate.setDuration(1000);
        AlphaAnimation thankYouAlpha = new AlphaAnimation(0, 1);
        thankYouAlpha.setDuration(1000);

        AnimationSet animationSet = new AnimationSet(true);
        animationSet.addAnimation(thankYouTranslate);
        animationSet.addAnimation(thankYouAlpha);
        tvCollectAmount.startAnimation(animationSet);

        AlphaAnimation messageAnimation = new AlphaAnimation(0, 1);
        messageAnimation.setDuration(3500);
        tvCollectMessage.startAnimation(messageAnimation);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (getActivity() != null) {
                    getActivity().getFragmentManager().beginTransaction().remove(DCCollectSuccessFragment.this).commit();
                    if (listener != null)
                        listener.onFragmentRemoved();
                }
            }
        }, 5000);

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
}
