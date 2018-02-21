package com.dentacoin.dentacare.widgets;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.dentacoin.dentacare.R;
import com.dentacoin.dentacare.activities.DCActivity;
import com.dentacoin.dentacare.fragments.DCDialogFragment;
import com.dentacoin.dentacare.model.DCError;
import com.dentacoin.dentacare.network.DCApiManager;
import com.dentacoin.dentacare.network.DCResponseListener;
import com.dentacoin.dentacare.network.request.DCCaptcha;
import com.dentacoin.dentacare.network.response.DCCaptchaResponse;
import com.dentacoin.dentacare.utils.DCUtils;

/**
 * Created by Atanas Chervarov on 17.02.18.
 */

public class DCUserDeleteDialog extends DCDialogFragment {

    public static final String TAG = DCDialogFragment.class.getSimpleName();

    public interface IDCUserDeleteDialog {
        void onUserDelete(DCCaptcha captcha);
    }

    private ImageView ivCaptcha;
    private DCTextInputEditText tietCaptcha;
    private DCCaptcha captcha;
    private IDCUserDeleteDialog listener;


    public static DCUserDeleteDialog create(IDCUserDeleteDialog listener) {
        final DCUserDeleteDialog dialog = new DCUserDeleteDialog();
        dialog.setListener(listener);
        return dialog;
    }

    public void setListener(IDCUserDeleteDialog listener) {
        this.listener = listener;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View view = inflater.inflate(R.layout.view_delete_user, null);

        ivCaptcha = view.findViewById(R.id.iv_captcha);
        tietCaptcha = view.findViewById(R.id.tiet_captcha);
        tietCaptcha.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (captcha == null)
                    captcha = new DCCaptcha();

                captcha.setCaptchaCode(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        builder.setTitle(R.string.profile_hdl_delete_profile)
                .setMessage(R.string.profile_txt_delete_profile)
                .setPositiveButton(R.string.txt_yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (listener != null)
                            listener.onUserDelete(captcha);
                    }
                })
                .setNegativeButton(R.string.txt_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dismissAllowingStateLoss();
                    }
                });

        builder.setView(view);
        builder.setCancelable(true);
        getCaptcha();
        return builder.create();
    }

    private void getCaptcha() {
        DCApiManager.getInstance().getCaptcha(new DCResponseListener<DCCaptchaResponse>() {
            @Override
            public void onFailure(DCError error) {
                if (getActivity() instanceof DCActivity) {
                    ((DCActivity) getActivity()).onError(error);
                }
                dismissAllowingStateLoss();
            }

            @Override
            public void onResponse(DCCaptchaResponse object) {
                if (object != null && ivCaptcha != null && tietCaptcha != null) {
                    if (captcha == null)
                        captcha = new DCCaptcha();

                    captcha.setCaptchaId(object.getId());
                    Bitmap captchaBitmap = DCUtils.bitmapFromBase64(object.getImage());
                    if (captchaBitmap != null) {
                        ivCaptcha.setImageBitmap(captchaBitmap);
                        tietCaptcha.setText(null);
                    }
                }
            }
        });
    }
}