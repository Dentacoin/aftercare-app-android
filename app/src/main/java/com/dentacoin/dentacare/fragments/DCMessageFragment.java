package com.dentacoin.dentacare.fragments;

import android.content.Context;
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
import com.dentacoin.dentacare.utils.AudibleMessage;
import com.dentacoin.dentacare.utils.DCConstants;
import com.dentacoin.dentacare.utils.DCSharedPreferences;
import com.dentacoin.dentacare.utils.Routine;
import com.dentacoin.dentacare.utils.Voice;
import com.dentacoin.dentacare.widgets.DCButton;
import com.dentacoin.dentacare.widgets.DCSoundManager;
import com.dentacoin.dentacare.widgets.DCTextView;
import com.google.gson.JsonSyntaxException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Atanas Chervarov on 10/20/17.
 */

public class DCMessageFragment extends DCDialogFragment implements View.OnClickListener {

    public interface IDCMessageFragmentListener {
        void onStartRoutine(Routine routine);
    }

    public static final String TAG = DCLoginFragment.class.getSimpleName();

    public static final String KEY_DAY = "KEY_DAY";
    public static final String KEY_VOICES = "KEY_VOICES";
    public static final String KEY_MESSAGE = "KEY_MESSAGE";

    private DCTextView tvMessageDay;
    private DCTextView tvMessageOf;
    private DCTextView tvMessage;
    private ImageView ivMessageTooth;
    private DCButton btnMessageActivity;
    private IDCMessageFragmentListener listener;

    public void setListener(IDCMessageFragmentListener listener) {
        this.listener = listener;
    }

    public static DCMessageFragment create(Context context, AudibleMessage message) {
        int day = DCSharedPreferences.loadInt(DCSharedPreferences.DCSharedKey.DAYS_COUNTER, 1);
        return DCMessageFragment.create(day, message.getMessage(context), message.getVoices());
    }

    public static DCMessageFragment create(int day, String message, Voice[] voices) {
        DCMessageFragment messageFragment = new DCMessageFragment();
        Bundle arguments = new Bundle();
        arguments.putInt(KEY_DAY, day);
        arguments.putString(KEY_MESSAGE, message);
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

        int day = 1;
        String message = "";
        ArrayList<Voice> voices = null;

        Bundle arguments = getArguments();
        if (arguments != null) {
            day = arguments.getInt(KEY_DAY);
            message = arguments.getString(KEY_MESSAGE);

            Serializable serializable = arguments.getSerializable(KEY_VOICES);
            if (serializable instanceof ArrayList) {
                voices = (ArrayList<Voice>)serializable;
            }
        }

        tvMessageDay = (DCTextView) view.findViewById(R.id.tv_message_day);
        tvMessageDay.setText(getString(R.string.message_day, day));

        tvMessageOf = (DCTextView) view.findViewById(R.id.tv_message_of);
        tvMessageOf.setText(getString(R.string.message_of, DCConstants.DAYS_OF_USE));

        if (day >= DCConstants.DAYS_OF_USE) {
            tvMessageOf.setText(R.string.message_congratulations);
        }

        setCancelable(false);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                setCancelable(true);
            }
        }, 1500);

        tvMessage = (DCTextView) view.findViewById(R.id.tv_message);
        tvMessage.setText(message);

        ivMessageTooth = (ImageView) view.findViewById(R.id.iv_message_tooth);

        btnMessageActivity = (DCButton) view.findViewById(R.id.btn_message_activity);
        btnMessageActivity.setOnClickListener(this);


        Routine.Type routineType = Routine.getAppropriateRoutineTypeForNow();

        if (routineType != null) {
            switch (routineType) {
                case MORNING:
                    btnMessageActivity.setText(getString(R.string.btn_message_morning_routine));
                    break;
                case EVENING:
                    btnMessageActivity.setText(getString(R.string.btn_message_evening_routine));
                    break;
            }

            btnMessageActivity.setTag(routineType);
        }

        AlphaAnimation alphaAnimation = new AlphaAnimation(0f, 1f);
        alphaAnimation.setDuration(1000);
        ivMessageTooth.startAnimation(alphaAnimation);

        AlphaAnimation dayAlphaAnimation = new AlphaAnimation(0f, 1f);
        dayAlphaAnimation.setDuration(2000);
        tvMessageDay.startAnimation(dayAlphaAnimation);

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

                if (listener != null && view.getTag() instanceof Routine.Type) {
                    Routine.Type type = (Routine.Type) view.getTag();
                    Routine routine = new Routine(type);
                    listener.onStartRoutine(routine);
                }

                dismissAllowingStateLoss();
                break;
        }
    }

    public static boolean shouldShowMessage() {
        boolean enoughTimePassed = true;

        String json = DCSharedPreferences.loadString(DCSharedPreferences.DCSharedKey.LAST_MESSAGE_TIME);
        if (json != null) {
            try {
                Date lastDate = DCApiManager.gson.fromJson(json, Date.class);
                Date now = Calendar.getInstance().getTime();
                if (now.getTime() - lastDate.getTime() < 3600000) {
                    enoughTimePassed = false;
                }
            } catch (JsonSyntaxException e) {
                e.printStackTrace();
            }
        }

        if (enoughTimePassed) {
            Calendar calendar = Calendar.getInstance();
            int hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);
            if (hourOfDay >= 2 && hourOfDay < 11) {
                return true;
            } else if (hourOfDay >= 17 && hourOfDay < 24) {
                return true;
            }
            return true;
        }

        return false;
    }

    public static AudibleMessage getProperGreetingMessage() {
        Routine.Type routineType = Routine.getAppropriateRoutineTypeForNow();

        if (routineType != null) {
            switch (routineType) {
                case MORNING:
                    return AudibleMessage.MORNING_GREETING;
                case EVENING:
                    return AudibleMessage.EVENING_GREETING;
            }
        }

        return null;
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
