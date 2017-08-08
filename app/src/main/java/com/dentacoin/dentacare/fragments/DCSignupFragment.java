package com.dentacoin.dentacare.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.anthonycr.grant.PermissionsManager;
import com.anthonycr.grant.PermissionsResultAction;
import com.dentacoin.dentacare.R;
import com.dentacoin.dentacare.activities.DCAuthenticationActivity;
import com.dentacoin.dentacare.model.DCAvatar;
import com.dentacoin.dentacare.model.DCError;
import com.dentacoin.dentacare.network.DCApiManager;
import com.dentacoin.dentacare.network.DCResponseListener;
import com.dentacoin.dentacare.utils.DCConstants;
import com.dentacoin.dentacare.widgets.DCButton;
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
public class DCSignupFragment extends DCFragment implements View.OnClickListener {

    public static final String TAG = DCSignupFragment.class.getSimpleName();

    private ImageView ivSignupBack;
    private SimpleDraweeView sdvSignupProfileImage;

    private DCButton btnSignupFacebook;
    private DCButton btnSignupTwitter;
    private DCButton btnSignupGoogle;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance) {
        View view = inflater.inflate(R.layout.fragment_signup, container, false);

        ivSignupBack = (ImageView) view.findViewById(R.id.iv_signup_back);
        ivSignupBack.setOnClickListener(this);
        sdvSignupProfileImage = (SimpleDraweeView) view.findViewById(R.id.sdv_signup_profile_image);
        sdvSignupProfileImage.setOnClickListener(this);

        btnSignupFacebook = (DCButton) view.findViewById(R.id.btn_signup_facebook);
        btnSignupFacebook.setOnClickListener(this);

        btnSignupTwitter = (DCButton) view.findViewById(R.id.btn_signup_twitter);
        btnSignupTwitter.setOnClickListener(this);

        btnSignupGoogle = (DCButton) view.findViewById(R.id.btn_signup_google);
        btnSignupGoogle.setOnClickListener(this);

        EasyImage.configuration(getActivity())
                .setImagesFolderName("Dentacare")
                .saveInRootPicturesDirectory();

        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_signup_back:
                onBackPressed();
                break;
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
        }
    }


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

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == Activity.RESULT_OK) {
                Uri resultUri = result.getUri();
                if (resultUri != null) {
                    File file = new File(resultUri.getPath());
                    DCApiManager.getInstance().uploadAvatar(file, new DCResponseListener<DCAvatar>() {
                        @Override
                        public void onFailure(DCError error) {
                            if (error != null) {
                                //TODO: Display error description
                                Snacky.builder().setActivty(getActivity()).error().setText("Failed to upload avatar").show();
                            }
                        }

                        @Override
                        public void onResponse(DCAvatar object) {
                            if (object != null) {

                            }
                        }
                    });
                }
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Snacky.builder().setActivty(getActivity()).error().setText(R.string.error_txt_error_picking_image).show();
            }
        }

        EasyImage.handleActivityResult(requestCode, resultCode, data, getActivity(), new DefaultCallback() {
            @Override
            public void onImagePickerError(Exception e, EasyImage.ImageSource source, int type) {
                Snacky.builder().setActivty(getActivity()).error().setText(R.string.error_txt_error_picking_image).show();
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
                    Snacky.builder().setActivty(getActivity()).error().setText(R.string.error_txt_error_picking_image).show();
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
