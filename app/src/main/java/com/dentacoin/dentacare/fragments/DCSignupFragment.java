package com.dentacoin.dentacare.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.anthonycr.grant.PermissionsManager;
import com.anthonycr.grant.PermissionsResultAction;
import com.dentacoin.dentacare.activities.DCActivity;
import com.dentacoin.dentacare.utils.DCUtils;
import com.dentacoin.dentacare.R;
import com.dentacoin.dentacare.activities.DCAuthenticationActivity;
import com.dentacoin.dentacare.model.DCAvatar;
import com.dentacoin.dentacare.model.DCError;
import com.dentacoin.dentacare.model.DCUser;
import com.dentacoin.dentacare.network.DCApiManager;
import com.dentacoin.dentacare.network.DCResponseListener;
import com.dentacoin.dentacare.utils.DCConstants;
import com.dentacoin.dentacare.widgets.DCButton;
import com.dentacoin.dentacare.widgets.DCTextInputEditText;
import com.dentacoin.dentacare.widgets.DCTextInputLayout;
import com.facebook.drawee.view.SimpleDraweeView;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;

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

    private DCAvatar avatar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance) {
        View view = inflater.inflate(R.layout.fragment_signup, container, false);

        sdvSignupProfileImage = (SimpleDraweeView) view.findViewById(R.id.sdv_signup_profile_image);
        sdvSignupProfileImage.setOnClickListener(this);

        btnSignupFacebook = (DCButton) view.findViewById(R.id.btn_signup_facebook);
        btnSignupFacebook.setOnClickListener(this);

        btnSignupTwitter = (DCButton) view.findViewById(R.id.btn_signup_twitter);
        btnSignupTwitter.setOnClickListener(this);

        btnSignupGoogle = (DCButton) view.findViewById(R.id.btn_signup_google);
        btnSignupGoogle.setOnClickListener(this);

        btnSignupCreate = (DCButton) view.findViewById(R.id.btn_signup_create);
        btnSignupCreate.setOnClickListener(this);

        tilSignupFirstname = (DCTextInputLayout) view.findViewById(R.id.til_signup_first_name);
        tietSignupFirstName = (DCTextInputEditText) view.findViewById(R.id.tiet_signup_first_name);
        tietSignupFirstName.setOnFocusChangeListener(this);

        tilSignupLastname = (DCTextInputLayout) view.findViewById(R.id.til_signup_last_name);
        tietSignupLastname = (DCTextInputEditText) view.findViewById(R.id.tiet_signup_last_name);
        tietSignupLastname.setOnFocusChangeListener(this);

        tilSignupEmail = (DCTextInputLayout) view.findViewById(R.id.til_signup_email);
        tietSignupEmail = (DCTextInputEditText) view.findViewById(R.id.tiet_signup_email);
        tietSignupEmail.setOnFocusChangeListener(this);

        tilSignupPassword = (DCTextInputLayout) view.findViewById(R.id.til_signup_password);
        tietSignupPassword = (DCTextInputEditText) view.findViewById(R.id.tiet_signup_password);
        tietSignupPassword.setOnFocusChangeListener(this);

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
                .saveInRootPicturesDirectory();

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
            user.setAvatar(avatar);
            user.setFirstname(tietSignupFirstName.getText().toString());
            user.setLastname(tietSignupLastname.getText().toString());
            user.setEmail(tietSignupEmail.getText().toString());
            user.setPassword(tietSignupPassword.getText().toString());

            ((DCAuthenticationActivity) getActivity()).signupUser(user);
        }
    }

    /**
     * Checks if the fields are valid, shows error for the first invalid field
     * @return
     */
    private boolean validate() {
        if (TextUtils.isEmpty(tietSignupFirstName.getText().toString())) {
            ((DCActivity)getActivity()).onError(new DCError(R.string.error_txt_first_name_required));
            return false;
        }
        else if (tietSignupFirstName.getText().toString().length() < 2) {
            ((DCActivity)getActivity()).onError(new DCError(R.string.error_txt_first_name_too_short));
            return false;
        }
        else if (tietSignupFirstName.getText().toString().length() > 40) {
            ((DCActivity)getActivity()).onError(new DCError(R.string.error_txt_first_name_too_long));
            return false;
        }
        else if (!DCUtils.isValidName(tietSignupFirstName.getText().toString())) {
            ((DCActivity)getActivity()).onError(new DCError(R.string.error_txt_name_not_valid));
            return false;
        }
        else if (tietSignupLastname.getText().toString().length() > 40) {
            ((DCActivity)getActivity()).onError(new DCError(R.string.error_txt_last_name_too_long));
            return false;
        }
        else if (tietSignupLastname.getText().toString().length() > 0 && !DCUtils.isValidName(tietSignupLastname.getText().toString())) {
            ((DCActivity)getActivity()).onError(new DCError(R.string.error_txt_name_not_valid));
            return false;
        }
        else if (TextUtils.isEmpty(tietSignupEmail.getText().toString())) {
            ((DCActivity)getActivity()).onError(new DCError(R.string.error_txt_email_required));
            return false;
        }
        else if (!DCUtils.isValidEmail(tietSignupEmail.getText().toString())) {
            ((DCActivity)getActivity()).onError(new DCError(R.string.error_txt_email_not_valid));
            return false;
        }
        else if (tietSignupPassword.getText().toString().length() < 8) {
            ((DCActivity)getActivity()).onError(new DCError(R.string.error_txt_password_short));
            return false;
        }

        return true;
    }

    /**
     * Pick avatar image, request permissions first
     */
    private void pickAvatar() {
        PermissionsManager.getInstance().requestPermissionsIfNecessaryForResult(getActivity(),
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
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
                                    Uri uri = Uri.parse("package:" + getActivity().getPackageName());
                                    intent.setData(uri);
                                    getActivity().startActivity(intent);
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
                Uri resultUri = result.getUri();
                if (resultUri != null) {
                    //Upload the cropped image
                    File file = new File(resultUri.getPath());
                    DCApiManager.getInstance().uploadAvatar(file, new DCResponseListener<DCAvatar>() {
                        @Override
                        public void onFailure(DCError error) {
                            if (error == null)
                                error = new DCError(R.string.error_txt_failed_upload_avatar);
                            ((DCActivity)getActivity()).onError(error);
                        }

                        @Override
                        public void onResponse(DCAvatar object) {
                            if (object != null) {
                                //set & display the uploaded image
                                avatar = object;
                                sdvSignupProfileImage.setImageURI(object.getAvatarUrl(getActivity()));
                            }
                        }
                    });
                }
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                ((DCActivity)getActivity()).onError(new DCError(R.string.error_txt_error_picking_image));
            }
        }

        EasyImage.handleActivityResult(requestCode, resultCode, data, getActivity(), new DefaultCallback() {
            @Override
            public void onImagePickerError(Exception e, EasyImage.ImageSource source, int type) {
                ((DCActivity)getActivity()).onError(new DCError(R.string.error_txt_error_picking_image));
            }

            @Override
            public void onImagePicked(File imageFile, EasyImage.ImageSource source, int type) {
                if (imageFile != null) {
                    CropImage.activity(Uri.fromFile(imageFile))
                            .setGuidelines(CropImageView.Guidelines.ON)
                            .setAspectRatio(1, 1)
                            .setRequestedSize(DCConstants.AVATAR_DEFAULT_SIZE_WIDTH, DCConstants.AVATAR_DEFAULT_SIZE_HEIGHT, CropImageView.RequestSizeOptions.RESIZE_INSIDE)
                            .start(getActivity(), DCSignupFragment.this);
                } else {
                    ((DCActivity)getActivity()).onError(new DCError(R.string.error_txt_error_picking_image));
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
}
