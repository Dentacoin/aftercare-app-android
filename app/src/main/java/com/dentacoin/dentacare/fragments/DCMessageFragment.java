package com.dentacoin.dentacare.fragments;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;

import com.dentacoin.dentacare.R;
import com.dentacoin.dentacare.network.DCApiManager;
import com.dentacoin.dentacare.utils.DCSharedPreferences;
import com.dentacoin.dentacare.utils.Voice;
import com.dentacoin.dentacare.widgets.DCButton;
import com.dentacoin.dentacare.widgets.DCSoundManager;
import com.dentacoin.dentacare.widgets.DCTextView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Atanas Chervarov on 10/20/17.
 */

public class DCMessageFragment extends DCDialogFragment implements View.OnClickListener {

    public interface IDCMessageFragment {
        void onButtonClicked();
    }

    public static final String TAG = DCLoginFragment.class.getSimpleName();

    public static final String KEY_TITLE = "KEY_TITLE";
    public static final String KEY_SUBTITLE = "KEY_SUBTITLE";
    public static final String KEY_VOICES = "KEY_VOICES";
    public static final String KEY_MESSAGE = "KEY_MESSAGE";
    public static final String KEY_BUTTON_TITLE = "KEY_BUTTON_TITLE";

    private DCTextView tvMessageTitle;
    private DCTextView tvMessageSubtitle;
    private DCTextView tvMessage;
    private ImageView ivMessageTooth;
    private DCButton btnMessageActivity;
    private IDCMessageFragment listener;

    public static DCMessageFragment create(String title, String subTitle, String message, String buttonTitle, Voice[] voices, IDCMessageFragment listener) {
        DCMessageFragment messageFragment = new DCMessageFragment();
        messageFragment.listener = listener;
        Bundle arguments = new Bundle();
        arguments.putString(KEY_TITLE, title);
        arguments.putString(KEY_SUBTITLE, subTitle);
        arguments.putString(KEY_MESSAGE, message);
        arguments.putString(KEY_BUTTON_TITLE, buttonTitle);

        if (voices != null) {
            ArrayList<Voice> voicesArray = new ArrayList<>(Arrays.asList(voices));
            arguments.putSerializable(KEY_VOICES, voicesArray);
        }
        messageFragment.setArguments(arguments);
        return messageFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_message, container);

        tvMessageTitle = view.findViewById(R.id.tv_message_title);
        tvMessageSubtitle = view.findViewById(R.id.tv_message_subtitle);
        tvMessage = view.findViewById(R.id.tv_message);
        ivMessageTooth = view.findViewById(R.id.iv_message_tooth);
        btnMessageActivity = view.findViewById(R.id.btn_message_activity);
        btnMessageActivity.setOnClickListener(this);

        tvMessageTitle.setVisibility(View.GONE);
        tvMessageSubtitle.setVisibility(View.GONE);
        tvMessage.setVisibility(View.GONE);

        ArrayList<Voice> voices = null;

        Bundle arguments = getArguments();

        if (arguments != null) {
            if (arguments.getString(KEY_TITLE) != null) {
                tvMessageTitle.setText(arguments.getString(KEY_TITLE));
                tvMessageTitle.setVisibility(View.VISIBLE);
            }

            if (arguments.getString(KEY_SUBTITLE) != null) {
                tvMessageSubtitle.setText(arguments.getString(KEY_SUBTITLE));
                tvMessageSubtitle.setVisibility(View.VISIBLE);
            }

            if (arguments.getString(KEY_MESSAGE) != null) {
                tvMessage.setText(arguments.getString(KEY_MESSAGE));
                tvMessage.setVisibility(View.VISIBLE);
            }

            if (arguments.getString(KEY_BUTTON_TITLE) != null) {
                btnMessageActivity.setText(arguments.getString(KEY_BUTTON_TITLE));
            }

            Serializable serializable = arguments.getSerializable(KEY_VOICES);
            if (serializable instanceof ArrayList) {
                voices =  (ArrayList)serializable;
            }
        }

        setCancelable(false);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                setCancelable(true);
            }
        }, 1500);

        AlphaAnimation alphaAnimation = new AlphaAnimation(0f, 1f);
        alphaAnimation.setDuration(1000);
        ivMessageTooth.startAnimation(alphaAnimation);

        AlphaAnimation dayAlphaAnimation = new AlphaAnimation(0f, 1f);
        dayAlphaAnimation.setDuration(2000);
        tvMessageTitle.startAnimation(dayAlphaAnimation);

        AlphaAnimation alphaAnimationMessage = new AlphaAnimation(0f, 1f);
        alphaAnimationMessage.setDuration(1000);
        tvMessage.startAnimation(alphaAnimationMessage);

        playVoices(voices);

        Date now = Calendar.getInstance().getTime();
        DCSharedPreferences.saveString(DCSharedPreferences.DCSharedKey.LAST_MESSAGE_TIME, DCApiManager.gson.toJson(now));
        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_message_activity:
                if (listener != null)
                    listener.onButtonClicked();

                dismissAllowingStateLoss();
                break;
        }
    }

    private void playVoices(ArrayList<Voice> voices) {
        if (voices != null && voices.size() > 0) {
            DCSoundManager.getInstance().playVoice(getActivity(), voices.get(0));
        }
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
        DCSoundManager.getInstance().cancelSounds();
    }
}
