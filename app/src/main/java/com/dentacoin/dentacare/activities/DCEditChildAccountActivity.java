package com.dentacoin.dentacare.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.dentacoin.dentacare.R;
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
import com.dentacoin.dentacare.widgets.DCYearPicker;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.Calendar;

/**
 * Created by Atanas Chervarov on 5.06.18.
 */
public class DCEditChildAccountActivity extends DCToolbarActivity {

    private static final String KEY_CHILD = "KEY_CHILD";

    public static final Intent createIntent(Context context, DCChild child) {
        final Intent intent = new Intent(context, DCEditChildAccountActivity.class);
        intent.putExtra(KEY_CHILD, child);
        return intent;
    }

    private DCChild child;

    private SimpleDraweeView sdvChildAvatar;
    private DCTextInputLayout tilChildName;
    private DCTextInputEditText tietChildName;
    private DCTextInputLayout tilChildBirthyear;
    private DCEditText etChildBirthyear;
    private DCButton btnChildUpdate;
    private DCButton btnChildDelete;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addContentView(R.layout.activity_edit_child);

        if (getIntent().getSerializableExtra(KEY_CHILD) instanceof DCChild) {
            child = (DCChild) getIntent().getSerializableExtra(KEY_CHILD);
        } else {
            finish();
        }

        setActionBarTitle(child.getFirstname());

        sdvChildAvatar = findViewById(R.id.sdv_child_avatar);
        tilChildName = findViewById(R.id.til_child_name);
        tietChildName = findViewById(R.id.tiet_child_name);
        tilChildBirthyear = findViewById(R.id.til_child_birthyear);
        etChildBirthyear = findViewById(R.id.et_child_birthyear);
        btnChildUpdate = findViewById(R.id.btn_child_update);
        btnChildDelete = findViewById(R.id.btn_child_delete);
        sdvChildAvatar.getHierarchy().setPlaceholderImage(R.drawable.baseline_face_black_48);
        tietChildName.setText(child.getFirstname());
        etChildBirthyear.setText(Integer.toString(child.getBirthyear()));

        btnChildDelete.setOnClickListener(v -> onDelete());
        btnChildUpdate.setOnClickListener(v -> onUpdate());
        etChildBirthyear.setOnClickListener(v -> onBirthdayClicked());
    }

    private boolean validate() {
        tilChildName.setError(null);
        tilChildName.setErrorEnabled(false);
        tilChildBirthyear.setError(null);
        tilChildBirthyear.setErrorEnabled(false);

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
        else if (child.getBirthyear() <= 0) {
            tilChildBirthyear.setErrorEnabled(true);
            tilChildBirthyear.setError(getString(R.string.txt_error_invalid_birthyear));
            return false;
        }

        return true;
    }

    private void onUpdate() {
        if (validate()) {
            btnChildUpdate.setEnabled(false);
            child.setFirstname(tietChildName.getText().toString());
            DCLoadingFragment loadingFragment = showLoading();

            DCApiManager.getInstance().patchChild(child.getId(), child, new DCResponseListener<DCChild>() {
                @Override
                public void onFailure(DCError error) {
                    loadingFragment.dismissAllowingStateLoss();
                    btnChildUpdate.setEnabled(true);
                    onError(error);
                }

                @Override
                public void onResponse(DCChild object) {
                    loadingFragment.dismissAllowingStateLoss();
                    if (object != null) {
                        Intent intent = new Intent();
                        intent.putExtra(KEY_UPDATE_OBJECT, object);
                        setResult(RESULT_OK, intent);
                        finish();
                    } else {
                        DCError error = new DCError(R.string.error_txt_something_went_wrong);
                        onError(error);
                        btnChildUpdate.setEnabled(true);
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

    private void onDelete() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.child_hdl_delete)
                .setNegativeButton(R.string.txt_cancel, (dialog, i) -> {
                    dialog.dismiss();
                })
                .setPositiveButton(R.string.txt_ok, (dialog, i) -> {
                    DCLoadingFragment loadingFragment = showLoading();
                    DCApiManager.getInstance().deleteChild(child.getId(), new DCResponseListener<Void>() {
                        @Override
                        public void onFailure(DCError error) {
                            onError(error);
                            loadingFragment.dismissAllowingStateLoss();
                        }

                        @Override
                        public void onResponse(Void object) {
                            loadingFragment.dismissAllowingStateLoss();
                            Intent intent = new Intent();
                            intent.putExtra(KEY_DELETE_OBJECT, child);
                            setResult(RESULT_OK, intent);
                            finish();
                        }
                    });
                })
                .create()
                .show();
    }
}
