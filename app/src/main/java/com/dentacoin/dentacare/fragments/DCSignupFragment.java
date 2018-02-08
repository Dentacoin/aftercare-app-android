package com.dentacoin.dentacare.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.anthonycr.grant.PermissionsManager;
import com.anthonycr.grant.PermissionsResultAction;
import com.dentacoin.dentacare.R;
import com.dentacoin.dentacare.activities.DCAuthenticationActivity;
import com.dentacoin.dentacare.model.DCError;
import com.dentacoin.dentacare.model.DCUser;
import com.dentacoin.dentacare.network.DCApiManager;
import com.dentacoin.dentacare.network.DCResponseListener;
import com.dentacoin.dentacare.network.response.DCCaptchaResponse;
import com.dentacoin.dentacare.utils.DCConstants;
import com.dentacoin.dentacare.utils.DCUtils;
import com.dentacoin.dentacare.widgets.DCButton;
import com.dentacoin.dentacare.widgets.DCTextInputEditText;
import com.dentacoin.dentacare.widgets.DCTextInputLayout;
import com.facebook.drawee.view.SimpleDraweeView;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.util.List;

import de.mateware.snacky.Snacky;
import pl.aprilapps.easyphotopicker.DefaultCallback;
import pl.aprilapps.easyphotopicker.EasyImage;


/**
 * Created by Atanas Chervarov on 7/29/17.
 */
public class DCSignupFragment extends DCFragment implements View.OnClickListener, View.OnFocusChangeListener {

    public static final String TAG = DCSignupFragment.class.getSimpleName();

    private SimpleDraweeView sdvSignupProfileImage;

    private DCButton btnSignupFacebook;
    private DCButton btnSignupTwitter;
    private DCButton btnSignupGoogle;
    private DCButton btnSignupCreate;

    private DCTextInputLayout tilSignupFirstname;
    private DCTextInputEditText tietSignupFirstName;
    private DCTextInputLayout tilSignupLastname;
    private DCTextInputEditText tietSignupLastname;
    private DCTextInputLayout tilSignupEmail;
    private DCTextInputEditText tietSignupEmail;
    private DCTextInputLayout tilSignupPassword;
    private DCTextInputEditText tietSignupPassword;
    private DCTextInputEditText tietSignupCaptcha;
    private ProgressBar pbCaptcha;
    private ImageView ivCaptcha;
    private Uri avatarUri;
    private int captchaId;
    private final Handler handler = new Handler();
    private final int captchaInterval = 150000;
    private CountDownTimer countDownTimer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance) {
        View view = inflater.inflate(R.layout.fragment_signup, container, false);

        sdvSignupProfileImage = view.findViewById(R.id.sdv_signup_profile_image);
        sdvSignupProfileImage.setOnClickListener(this);

        btnSignupFacebook = view.findViewById(R.id.btn_signup_facebook);
        btnSignupFacebook.setOnClickListener(this);

        btnSignupTwitter = view.findViewById(R.id.btn_signup_twitter);
        btnSignupTwitter.setOnClickListener(this);

        btnSignupGoogle = view.findViewById(R.id.btn_signup_google);
        btnSignupGoogle.setOnClickListener(this);

        btnSignupCreate = view.findViewById(R.id.btn_signup_create);
        btnSignupCreate.setOnClickListener(this);

        tilSignupFirstname = view.findViewById(R.id.til_signup_first_name);
        tietSignupFirstName = view.findViewById(R.id.tiet_signup_first_name);
        tietSignupFirstName.setOnFocusChangeListener(this);

        tilSignupLastname = view.findViewById(R.id.til_signup_last_name);
        tietSignupLastname = view.findViewById(R.id.tiet_signup_last_name);
        tietSignupLastname.setOnFocusChangeListener(this);

        tilSignupEmail = view.findViewById(R.id.til_signup_email);
        tietSignupEmail = view.findViewById(R.id.tiet_signup_email);
        tietSignupEmail.setOnFocusChangeListener(this);

        tilSignupPassword = view.findViewById(R.id.til_signup_password);
        tietSignupPassword = view.findViewById(R.id.tiet_signup_password);
        tietSignupPassword.setOnFocusChangeListener(this);
        tietSignupCaptcha = view.findViewById(R.id.tiet_signup_captcha);
        ivCaptcha = view.findViewById(R.id.iv_captcha);
        pbCaptcha = view.findViewById(R.id.pb_captcha);
        pbCaptcha.setMax(100);

        tietSignupPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s != null && s.length() >= 8) {
                    tilSignupPassword.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        EasyImage.configuration(getActivity())
                .setImagesFolderName("Dentacare")
                .setAllowMultiplePickInGallery(false);

        return view;
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (!hasFocus) {
            switch (v.getId()) {
                case R.id.tiet_signup_first_name:
                    if (TextUtils.isEmpty(tietSignupFirstName.getText().toString())) {
                        tilSignupFirstname.setErrorEnabled(true);
                        tilSignupFirstname.setError(getString(R.string.error_txt_first_name_required));
                    }
                    else if (tietSignupFirstName.getText().toString().length() < 2) {
                        tilSignupFirstname.setErrorEnabled(true);
                        tilSignupFirstname.setError(getString(R.string.error_txt_first_name_too_short));
                    }
                    else if (tietSignupFirstName.getText().toString().length() > 40) {
                        tilSignupFirstname.setErrorEnabled(true);
                        tilSignupFirstname.setError(getString(R.string.error_txt_first_name_too_long));
                    }
                    else if (!DCUtils.isValidName(tietSignupFirstName.getText().toString())) {
                        tilSignupFirstname.setErrorEnabled(true);
                        tilSignupFirstname.setError(getString(R.string.error_txt_name_not_valid));
                    }
                    else {
                        tilSignupFirstname.setErrorEnabled(false);
                    }
                    break;
                case R.id.tiet_signup_last_name:
                    if (tietSignupLastname.getText().toString().length() > 40) {
                        tilSignupLastname.setErrorEnabled(true);
                        tilSignupLastname.setError(getString(R.string.error_txt_last_name_too_long));
                    }
                    else if (tietSignupLastname.getText().toString().length() > 0 && !DCUtils.isValidName(tietSignupLastname.getText().toString())) {
                        tilSignupLastname.setErrorEnabled(true);
                        tilSignupLastname.setError(getString(R.string.error_txt_name_not_valid));
                    }
                    else {
                        tilSignupLastname.setErrorEnabled(false);
                    }
                    break;
                case R.id.tiet_signup_email:
                    if (TextUtils.isEmpty(tietSignupEmail.getText().toString())) {
                        tilSignupEmail.setErrorEnabled(true);
                        tilSignupEmail.setError(getString(R.string.error_txt_email_required));
                    }
                    else if (!DCUtils.isValidEmail(tietSignupEmail.getText().toString())) {
                        tilSignupEmail.setErrorEnabled(true);
                        tilSignupEmail.setError(getString(R.string.error_txt_email_not_valid));
                    }
                    else {
                        tilSignupEmail.setErrorEnabled(false);
                    }
                    break;
                case R.id.tiet_signup_password:
                    if (tietSignupPassword.getText().toString().length() < 8) {
                        tilSignupPassword.setErrorEnabled(true);
                        tilSignupPassword.setError(getString(R.string.error_txt_password_short));
                    }
                    else {
                        tilSignupPassword.setErrorEnabled(false);
                    }
                    break;
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.sdv_signup_profile_image:
                pickAvatar();
                break;
            case R.id.btn_signup_facebook:
                ((DCAuthenticationActivity)getActivity()).onFacebookLogin();
                break;
            case R.id.btn_signup_twitter:
                ((DCAuthenticationActivity)getActivity()).onTwitterLogin();
                break;
            case R.id.btn_signup_google:
                ((DCAuthenticationActivity)getActivity()).onGoogleLogin();
                break;
            case R.id.btn_signup_create:
                signup();
                break;

        }
    }

    private void signup() {
        if (validate()) {
            DCUser user = new DCUser();
            user.setAvatar_64(DCUtils.base64Bitmap(avatarUri));
            user.setFirstname(tietSignupFirstName.getText().toString());
            user.setLastname(tietSignupLastname.getText().toString());
            user.setEmail(tietSignupEmail.getText().toString());
            user.setPassword(tietSignupPassword.getText().toString());
            user.setCaptchaId(captchaId);
            user.setCaptchaCode(tietSignupCaptcha.getText().toString());
            ((DCAuthenticationActivity) getActivity()).signupUser(user);
        }
    }

    /**
     * Checks if the fields are valid, shows error for the first invalid field
     * @return
     */
    private boolean validate() {
        if (TextUtils.isEmpty(tietSignupFirstName.getText().toString())) {
            onError(new DCError(R.string.error_txt_first_name_required));
            return false;
        }
        else if (tietSignupFirstName.getText().toString().length() < 2) {
            onError(new DCError(R.string.error_txt_first_name_too_short));
            return false;
        }
        else if (tietSignupFirstName.getText().toString().length() > 40) {
            onError(new DCError(R.string.error_txt_first_name_too_long));
            return false;
        }
        else if (!DCUtils.isValidName(tietSignupFirstName.getText().toString())) {
            onError(new DCError(R.string.error_txt_name_not_valid));
            return false;
        }
        else if (tietSignupLastname.getText().toString().length() > 40) {
            onError(new DCError(R.string.error_txt_last_name_too_long));
            return false;
        }
        else if (tietSignupLastname.getText().toString().length() > 0 && !DCUtils.isValidName(tietSignupLastname.getText().toString())) {
            onError(new DCError(R.string.error_txt_name_not_valid));
            return false;
        }
        else if (TextUtils.isEmpty(tietSignupEmail.getText().toString())) {
            onError(new DCError(R.string.error_txt_email_required));
            return false;
        }
        else if (!DCUtils.isValidEmail(tietSignupEmail.getText().toString())) {
            onError(new DCError(R.string.error_txt_email_not_valid));
            return false;
        }
        else if (tietSignupPassword.getText().toString().length() < 8) {
            onError(new DCError(R.string.error_txt_password_short));
            return false;
        }

        return true;
    }

    /**
     * Pick avatar image, request permissions first
     */
    private void pickAvatar() {
        PermissionsManager.getInstance().requestPermissionsIfNecessaryForResult(getActivity(),
                new String[]{Manifest.permission.CAMERA},
                new PermissionsResultAction() {
                    @Override
                    public void onGranted() {
                        EasyImage.openChooserWithGallery(DCSignupFragment.this, "", 0);
                    }

                    @Override
                    public void onDenied(String permission) {
                        if (!ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), permission)) {
                            Snacky.builder()
                                    .setActivty(getActivity())
                                    .warning()
                                    .setDuration(Snacky.LENGTH_LONG)
                                    .setText(R.string.signup_txt_permission_avatar)
                                    .setAction(R.string.signup_txt_settings, new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent();
                                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                    if (getActivity() != null) {
                                        Uri uri = Uri.parse("package:" + getActivity().getPackageName());
                                        intent.setData(uri);
                                        getActivity().startActivity(intent);
                                    }
                                }
                            }).show();
                        } else {
                            Snacky.builder().setActivty(getActivity()).warning().setText(R.string.signup_txt_permission_avatar).show();
                        }
                    }
                });
    }

    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //Crop the selected avatar image
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == Activity.RESULT_OK) {
                avatarUri = result.getUri();
                sdvSignupProfileImage.setImageURI(avatarUri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                onError(new DCError(R.string.error_txt_error_picking_image));
            }
        }

        EasyImage.handleActivityResult(requestCode, resultCode, data, getActivity(), new DefaultCallback() {
            @Override
            public void onImagePickerError(Exception e, EasyImage.ImageSource source, int type) {
                onError(new DCError(R.string.error_txt_error_picking_image));
            }

            @Override
            public void onImagesPicked(@NonNull List<File> imageFiles, EasyImage.ImageSource source, int type) {
                if (imageFiles.size() > 0) {
                    CropImage.activity(Uri.fromFile(imageFiles.get(0)))
                            .setGuidelines(CropImageView.Guidelines.ON)
                            .setAspectRatio(1, 1)
                            .setRequestedSize(DCConstants.AVATAR_DEFAULT_SIZE_WIDTH, DCConstants.AVATAR_DEFAULT_SIZE_HEIGHT, CropImageView.RequestSizeOptions.RESIZE_INSIDE)
                            .start(getActivity(), DCSignupFragment.this);
                } else {
                    onError(new DCError(R.string.error_txt_error_picking_image));
                }
            }

            @Override
            public void onCanceled(EasyImage.ImageSource source, int type) {
                if (source == EasyImage.ImageSource.CAMERA) {
                    File photoFile = EasyImage.lastlyTakenButCanceledPhoto(getActivity());
                    if (photoFile != null)
                        photoFile.delete();
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        startUpdatingCaptcha();
    }

    @Override
    public void onPause() {
        stopUpdatingCaptcha();
        super.onPause();
    }


    private void startUpdatingCaptcha() {
        captchaRunnable.run();
    }

    private void stopUpdatingCaptcha() {
        handler.removeCallbacks(captchaRunnable);
    }

    private final Runnable captchaRunnable = new Runnable() {
        @Override
        public void run() {
            updateCaptcha();
            handler.postDelayed(captchaRunnable, captchaInterval);
        }
    };


    private void updateCaptcha() {
        DCApiManager.getInstance().getCaptcha(new DCResponseListener<DCCaptchaResponse>() {
            @Override
            public void onFailure(DCError error) {
                //ignoring errors for now
            }
            @Override
            public void onResponse(DCCaptchaResponse object) {
                if (object !=  null && isAdded()) {
                    DCSignupFragment.this.captchaId = object.getId();
                    Bitmap captchaBitmap = DCUtils.bitmapFromBase64(object.getImage());
                    if (captchaBitmap != null) {
                        DCSignupFragment.this.ivCaptcha.setImageBitmap(captchaBitmap);
                        DCSignupFragment.this.tietSignupCaptcha.setText(null);

                        if (countDownTimer != null) {
                            countDownTimer.cancel();
                            countDownTimer = null;
                        }

                        pbCaptcha.setProgress(100);
                        pbCaptcha.getProgressDrawable().setColorFilter(Color.GREEN, PorterDuff.Mode.SRC_IN);
                        countDownTimer = new CountDownTimer(captchaInterval, 100) {
                            @Override
                            public void onTick(long milsUntilFinished) {
                                if (isAdded()) {
                                    float progress = ((float) milsUntilFinished / (float)captchaInterval) * 100.0f;
                                    pbCaptcha.setProgress(Math.round(progress));
                                    if (progress > 70) {
                                        pbCaptcha.getProgressDrawable().setColorFilter(Color.GREEN, PorterDuff.Mode.SRC_IN);
                                    } else if (progress > 40) {
                                        pbCaptcha.getProgressDrawable().setColorFilter(Color.YELLOW, PorterDuff.Mode.SRC_IN);
                                    } else {
                                        pbCaptcha.getProgressDrawable().setColorFilter(Color.RED, PorterDuff.Mode.SRC_IN);
                                    }
                                }
                            }

                            @Override
                            public void onFinish() {
                            }
                        };
                        countDownTimer.start();
                    }
                }
            }
        });
    }
}
