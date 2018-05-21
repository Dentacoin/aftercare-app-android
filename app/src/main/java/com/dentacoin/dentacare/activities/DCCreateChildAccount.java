package com.dentacoin.dentacare.activities;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.RelativeLayout;

import com.dentacoin.dentacare.R;
import com.dentacoin.dentacare.fragments.DCDialogFragment;
import com.dentacoin.dentacare.fragments.DCLoadingFragment;
import com.dentacoin.dentacare.model.DCChild;
import com.dentacoin.dentacare.model.DCError;
import com.dentacoin.dentacare.network.DCApiManager;
import com.dentacoin.dentacare.network.DCResponseListener;
import com.dentacoin.dentacare.utils.DCUtils;
import com.dentacoin.dentacare.widgets.DCButton;
import com.dentacoin.dentacare.widgets.DCEditText;
import com.dentacoin.dentacare.widgets.DCTextInputEditText;
import com.dentacoin.dentacare.widgets.DCTextInputLayout;
import com.dentacoin.dentacare.widgets.DCTextView;
import com.dentacoin.dentacare.widgets.DCYearPicker;

import java.util.Calendar;

/**
 * Created by Atanas Chervarov on 10.05.18.
 */
public class DCCreateChildAccount extends DCToolbarActivity {

    private RelativeLayout rlChildCreate;
    private RelativeLayout rlChildCreated;

    private DCTextInputLayout tilChildName;
    private DCTextInputEditText tietChildName;
    private DCEditText etChildBirthyear;

    private DCTextView tvChildName;

    private DCButton btnChildCreate;
    private DCButton btnChildUseAccount;
    private DCButton btnChildDone;

    private DCChild child = new DCChild();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addContentView(R.layout.activity_create_child);
        setActionBarTitle(R.string.child_hdl_title);

        rlChildCreate = findViewById(R.id.rl_child_create);
        rlChildCreated = findViewById(R.id.rl_child_created);
        tilChildName = findViewById(R.id.til_child_name);
        tietChildName = findViewById(R.id.tiet_child_name);
        etChildBirthyear = findViewById(R.id.et_child_birthyear);
        tvChildName = findViewById(R.id.tv_child_name);
        btnChildCreate = findViewById(R.id.btn_child_create);
        btnChildUseAccount = findViewById(R.id.btn_child_use_account);
        btnChildDone = findViewById(R.id.btn_child_done);

        rlChildCreate.setVisibility(View.VISIBLE);
        rlChildCreated.setVisibility(View.GONE);
        btnChildCreate.setVisibility(View.VISIBLE);
        btnChildUseAccount.setVisibility(View.GONE);
        btnChildDone.setVisibility(View.GONE);

        btnChildCreate.setOnClickListener(v -> onChildCreate());
        btnChildDone.setOnClickListener(v -> onDone());
        btnChildUseAccount.setOnClickListener(v -> onUseAccount());
        etChildBirthyear.setOnClickListener(v -> onBirthdayClicked());
    }

    private boolean validate() {
        tilChildName.setError(null);
        tilChildName.setErrorEnabled(false);

        if (TextUtils.isEmpty(tietChildName.getText().toString())) {
            tilChildName.setErrorEnabled(true);
            tilChildName.setError(getString(R.string.error_txt_first_name_required));
            return false;
        }
        else if (tietChildName.getText().toString().length() < 2) {
            tilChildName.setErrorEnabled(true);
            tilChildName.setError(getString(R.string.error_txt_first_name_too_short));
            return false;
        }
        else if (!DCUtils.isValidName(tietChildName.getText().toString())) {
            tilChildName.setErrorEnabled(true);
            tilChildName.setError(getString(R.string.error_txt_first_name_too_long));
            return false;
        }

        return true;
    }

    private void onChildCreate() {
        if (validate()) {
            btnChildCreate.setEnabled(false);
            child.setFirstname(tietChildName.getText().toString());
            DCLoadingFragment loadingFragment = showLoading();
            DCApiManager.getInstance().postChild(child, new DCResponseListener<DCChild>() {
                @Override
                public void onFailure(DCError error) {
                    loadingFragment.dismissAllowingStateLoss();
                    btnChildCreate.setEnabled(true);
                    onError(error);
                }

                @Override
                public void onResponse(DCChild object) {
                    loadingFragment.dismissAllowingStateLoss();
                    if (object != null) {
                        initSuccessUI(object);
                    } else {
                        DCError error = new DCError(R.string.error_txt_something_went_wrong);
                        onError(error);
                        btnChildCreate.setEnabled(true);
                    }
                }
            });
        }
    }

    private void onBirthdayClicked() {
        Calendar calendar = Calendar.getInstance();

        calendar.set(Calendar.DAY_OF_YEAR, 1);
        calendar.set(Calendar.MONTH, 1);

        calendar.add(Calendar.YEAR, -3);
        int max = calendar.get(Calendar.YEAR);
        calendar.add(Calendar.YEAR, -10);
        int selected = calendar.get(Calendar.YEAR);

        if (child.getBirthyear() > 0) {
            selected = child.getBirthyear();
        }

        int min = calendar.get(Calendar.YEAR);

        DCYearPicker.create(selected, min, max, new DCYearPicker.IYearPickerListener() {
            @Override
            public void onYearPicked(int year) {
                child.setBirthyear(year);
                etChildBirthyear.setText(Integer.toString(year));
            }

            @Override
            public void onYearCleared() {
                child.setBirthyear(0);
                etChildBirthyear.setText(null);
            }
        }).show(getFragmentManager(), "");
    }

    private void initSuccessUI(DCChild child) {
        tvChildName.setText(child.getFirstname());
        AlphaAnimation fadeOutChildCreateButton = new AlphaAnimation(1, 0);
        fadeOutChildCreateButton.setDuration(300);
        fadeOutChildCreateButton.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                btnChildCreate.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        btnChildCreate.startAnimation(fadeOutChildCreateButton);

        AlphaAnimation fadeOutChildCreate = new AlphaAnimation(1, 0);
        fadeOutChildCreate.setDuration(300);
        fadeOutChildCreate.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                rlChildCreate.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        rlChildCreate.startAnimation(fadeOutChildCreate);


        AlphaAnimation fadeInChildCreated = new AlphaAnimation(0, 1);
        fadeInChildCreated.setDuration(500);
        fadeInChildCreated.setStartOffset(300);
        fadeInChildCreated.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                rlChildCreated.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        rlChildCreated.startAnimation(fadeInChildCreated);

        AlphaAnimation fadeInChildUseAccount = new AlphaAnimation(0, 1);
        fadeInChildUseAccount.setDuration(300);
        fadeInChildUseAccount.setStartOffset(500);
        fadeInChildUseAccount.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                btnChildUseAccount.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        btnChildUseAccount.startAnimation(fadeInChildUseAccount);


        AlphaAnimation fadeInChildDoneButton = new AlphaAnimation(0, 1);
        fadeInChildDoneButton.setDuration(300);
        fadeInChildDoneButton.setStartOffset(600);
        fadeInChildDoneButton.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                btnChildDone.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        btnChildDone.startAnimation(fadeInChildDoneButton);
    }

    private void onDone() {
        finish();
    }

    private void onUseAccount() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.child_hdl_use_account)
                .setMessage(R.string.child_txt_use_account)
                .setNegativeButton(R.string.txt_cancel, (dialog, i) -> {
                    dialog.dismiss();
                })
                .setPositiveButton(R.string.txt_ok, (dialog, i) -> {
                    //TODO: switch accounts
                    finish();
                })
                .create()
                .show();
    }
}
