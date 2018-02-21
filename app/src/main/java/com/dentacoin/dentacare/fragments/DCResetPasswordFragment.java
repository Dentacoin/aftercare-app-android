package com.dentacoin.dentacare.fragments;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.LinearLayout;

import com.dentacoin.dentacare.R;
import com.dentacoin.dentacare.model.DCError;
import com.dentacoin.dentacare.model.DCResetPassword;
import com.dentacoin.dentacare.network.DCApiManager;
import com.dentacoin.dentacare.network.DCResponseListener;
import com.dentacoin.dentacare.utils.DCUtils;
import com.dentacoin.dentacare.widgets.DCButton;
import com.dentacoin.dentacare.widgets.DCTextInputEditText;
import com.dentacoin.dentacare.widgets.DCTextInputLayout;
import com.dentacoin.dentacare.widgets.DCTextView;

/**
 * Created by Atanas Chervarov on 10/10/17.
 */

public class DCResetPasswordFragment extends DCFragment implements View.OnClickListener {

    public static final String TAG = DCResetPasswordFragment.class.getSimpleName();

    private DCTextInputLayout tilResetEmail;
    private DCTextInputEditText tietResetEmail;
    private DCButton btnResetSend;
    private DCTextView tvResetSuccess;
    private LinearLayout llResetPassword;
    private DCButton btnResetOk;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance) {
        View view = inflater.inflate(R.layout.fragment_reset_password, container, false);
        tilResetEmail = view.findViewById(R.id.til_reset_email);
        tietResetEmail = view.findViewById(R.id.tiet_reset_email);
        btnResetSend = view.findViewById(R.id.btn_reset_send);
        btnResetSend.setOnClickListener(this);
        tvResetSuccess = view.findViewById(R.id.tv_reset_success);
        tvResetSuccess.setVisibility(View.GONE);
        llResetPassword = view.findViewById(R.id.ll_reset_password);
        llResetPassword.setVisibility(View.VISIBLE);
        btnResetOk = view.findViewById(R.id.btn_reset_ok);
        btnResetOk.setOnClickListener(this);
        btnResetOk.setVisibility(View.GONE);
        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_reset_send:
                sendEmail();
                break;
            case R.id.btn_reset_ok:
                getActivity().getFragmentManager().beginTransaction().remove(this).commit();
                break;
        }
    }

    private boolean validate() {
        if (TextUtils.isEmpty(tietResetEmail.getText().toString())) {
            onError(new DCError(R.string.error_txt_email_required));
            return false;
        }
        else if (!DCUtils.isValidEmail(tietResetEmail.getText().toString())) {
            onError(new DCError(R.string.error_txt_email_not_valid));
            return false;
        }
        return true;
    }

    private void sendEmail() {
        if (!validate())
            return;

        final DCResetPassword resetPassword = new DCResetPassword();
        resetPassword.setEmail(tietResetEmail.getText().toString());

        DCApiManager.getInstance().resetPassword(resetPassword, new DCResponseListener<Void>() {
            @Override
            public void onFailure(DCError error) {
                onError(error);
            }

            @Override
            public void onResponse(Void object) {
                showSuccess();
            }
        });
    }

    private void showSuccess() {
        btnResetSend.setVisibility(View.GONE);
        btnResetOk.setVisibility(View.VISIBLE);

        AlphaAnimation alphaAnimationDisappear = new AlphaAnimation(1.0f, 0f);
        alphaAnimationDisappear.setDuration(1000);
        alphaAnimationDisappear.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (llResetPassword != null) {
                    llResetPassword.setVisibility(View.GONE);
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

        llResetPassword.startAnimation(alphaAnimationDisappear);


        AlphaAnimation alphaAnimationAppear = new AlphaAnimation(0f, 1.0f);
        alphaAnimationAppear.setDuration(1200);
        alphaAnimationAppear.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                if (tvResetSuccess != null) {
                    tvResetSuccess.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onAnimationEnd(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        tvResetSuccess.startAnimation(alphaAnimationAppear);
    }
}
