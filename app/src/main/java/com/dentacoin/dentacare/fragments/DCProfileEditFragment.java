package com.dentacoin.dentacare.fragments;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.anthonycr.grant.PermissionsManager;
import com.anthonycr.grant.PermissionsResultAction;
import com.dentacoin.dentacare.R;
import com.dentacoin.dentacare.activities.DCActivity;
import com.dentacoin.dentacare.model.DCError;
import com.dentacoin.dentacare.model.DCUser;
import com.dentacoin.dentacare.network.DCApiManager;
import com.dentacoin.dentacare.network.DCResponseListener;
import com.dentacoin.dentacare.network.DCSession;
import com.dentacoin.dentacare.utils.DCConstants;
import com.dentacoin.dentacare.utils.DCUtils;
import com.dentacoin.dentacare.utils.IDatePickerListener;
import com.dentacoin.dentacare.widgets.DCButton;
import com.dentacoin.dentacare.widgets.DCEditText;
import com.dentacoin.dentacare.widgets.DCTextInputEditText;
import com.dentacoin.dentacare.widgets.DCTextInputLayout;
import com.dentacoin.dentacare.widgets.DCUserDeleteDialog;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.mukesh.countrypicker.Country;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.util.Date;
import java.util.List;

import de.mateware.snacky.Snacky;
import pl.aprilapps.easyphotopicker.DefaultCallback;
import pl.aprilapps.easyphotopicker.EasyImage;

import static android.app.Activity.RESULT_OK;

/**
 * Created by Atanas Chervarov on 9/5/17.
 */

public class DCProfileEditFragment extends DCFragment implements View.OnClickListener, View.OnFocusChangeListener {

    public static final String TAG = DCProfileEditFragment.class.getSimpleName();

    private SimpleDraweeView sdvProfileAvatar;
    private DCTextInputLayout tilProfileFirstname;
    private DCTextInputEditText tietProfileFirstname;
    private DCTextInputLayout tilProfileLastname;
    private DCTextInputEditText tietProfileLastname;
    private DCTextInputLayout tilProfileEmail;
    private DCTextInputEditText tietProfileEmail;
    private DCTextInputLayout tilProfilePassword;
    private DCTextInputEditText tietProfilePassword;
    private DCEditText etProfileBirthday;
    private DCEditText etProfileLocation;
    private DCTextInputLayout tilProfileZipcode;
    private DCTextInputEditText tietProfileZipcode;
    private DCButton btnProfileMale;
    private DCButton btnProfileFemale;
    private DCButton btnDelete;

    private DCButton btnProfileUpdate;
    private DCUser user;
    private Uri avatarUri;
    private ImageView ivProfileClose;

    private int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1002;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance) {
        View view = inflater.inflate(R.layout.fragment_profile_edit, container, false);

        sdvProfileAvatar = view.findViewById(R.id.sdv_profile_avatar);
        sdvProfileAvatar.setOnClickListener(this);

        tilProfileFirstname = view.findViewById(R.id.til_profile_firstname);
        tietProfileFirstname = view.findViewById(R.id.tiet_profile_firstname);
        tietProfileFirstname.setOnFocusChangeListener(this);

        tilProfileLastname = view.findViewById(R.id.til_profile_lastname);
        tietProfileLastname = view.findViewById(R.id.tiet_profile_lastname);
        tietProfileLastname.setOnFocusChangeListener(this);

        tilProfileEmail = view.findViewById(R.id.til_profile_email);
        tietProfileEmail = view.findViewById(R.id.tiet_profile_email);

        tilProfilePassword = view.findViewById(R.id.til_profile_password);
        tietProfilePassword = view.findViewById(R.id.tiet_profile_password);
        tietProfilePassword.setOnFocusChangeListener(this);

        etProfileBirthday = view.findViewById(R.id.et_profile_birthday);
        etProfileBirthday.setOnClickListener(this);

        etProfileLocation = view.findViewById(R.id.et_profile_location);
        etProfileLocation.setOnClickListener(this);

        tilProfileZipcode = view.findViewById(R.id.til_profile_zipcode);
        tietProfileZipcode = view.findViewById(R.id.tiet_profile_zipcode);

        btnProfileMale = view.findViewById(R.id.btn_profile_male);
        btnProfileFemale = view.findViewById(R.id.btn_profile_female);
        btnProfileMale.setOnClickListener(this);
        btnProfileFemale.setOnClickListener(this);

        btnProfileUpdate = view.findViewById(R.id.btn_profile_update);
        btnProfileUpdate.setOnClickListener(this);

        ivProfileClose = view.findViewById(R.id.iv_profile_close);
        ivProfileClose.setOnClickListener(this);
        ivProfileClose.setVisibility(View.GONE);

        btnDelete = view.findViewById(R.id.btn_profile_delete);
        btnDelete.setOnClickListener(this);

        loadUser();
        return view;
    }

    private void loadUser() {
        if (DCSession.getInstance().getUser() != null) {
            setUser(DCSession.getInstance().getUser());
        } else {
            DCApiManager.getInstance().getUser(new DCResponseListener<DCUser>() {
                @Override
                public void onFailure(DCError error) {
                    onError(error);
                }

                @Override
                public void onResponse(DCUser object) {
                    setUser(DCSession.getInstance().getUser());
                }
            });
        }
    }

    private void setUser(DCUser user) {
        if (user == null || !isAdded())
            return;

        this.user = user;
        sdvProfileAvatar.setImageURI(user.getAvatarUrl(getActivity()));

        if (avatarUri != null || user.getAvatar() != null) {
            ivProfileClose.setVisibility(View.VISIBLE);
        } else {
            ivProfileClose.setVisibility(View.GONE);
        }

        tietProfileFirstname.setText(user.getFirstname());
        tietProfileLastname.setText(user.getLastname());
        tietProfileEmail.setText(user.getEmail());
        if (user.getBirthday() != null) {
            etProfileBirthday.setText(DCConstants.DATE_FORMAT_BIRTHDAY.format(user.getBirthday()));
        }
        etProfileLocation.setText(user.getLocation());

        btnProfileMale.setSelected(false);
        btnProfileFemale.setSelected(false);

        if (user.getGender() != null) {
            if (DCConstants.GENDER_MALE.equals(user.getGender())) {
                btnProfileMale.setSelected(true);
                btnProfileFemale.setSelected(false);
            }
            else if (DCConstants.GENDER_FEMALE.equals(user.getGender())) {
                btnProfileMale.setSelected(false);
                btnProfileFemale.setSelected(true);
            }
        }

        tietProfileZipcode.setText(null);

        if (user.getPostalCode() != null)
            tietProfileZipcode.setText(user.getPostalCode().toString());
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_profile_update:
                patchUser();
                break;
            case R.id.sdv_profile_avatar:
                pickAvatar();
                break;
            case R.id.et_profile_birthday:
                DCUtils.pickBirthday(getActivity(), user.getBirthday(), new IDatePickerListener() {
                    @Override
                    public void onPickedDate(Date date) {
                        user.setBirthday(date);
                        setUser(user);
                    }
                });
                break;
            case R.id.et_profile_location:
                pickLocation();
                break;
            case R.id.btn_profile_male:
                user.setGender(DCConstants.GENDER_MALE);
                setUser(user);
                break;
            case R.id.btn_profile_female:
                user.setGender(DCConstants.GENDER_FEMALE);
                setUser(user);
                break;
            case R.id.iv_profile_close:
                cancelAvatar();
                break;
            case R.id.btn_profile_delete:
                deleteProfile();
                break;
        }
    }

    private void deleteProfile() {
      DCUserDeleteDialog.create(captcha -> {
          DCApiManager.getInstance().deleteUser(captcha, new DCResponseListener<Void>() {
              @Override
              public void onFailure(DCError error) {
                  onError(error);
              }

              @Override
              public void onResponse(Void object) {
                  ((DCActivity)getActivity()).onLogout();
              }
          });
      }).show(getFragmentManager(), DCUserDeleteDialog.TAG);
    }

    private void cancelAvatar() {
        final AlertDialog dialog = new AlertDialog.Builder(getActivity())
                .setTitle(R.string.profile_hdl_remove_avatar)
                .setPositiveButton(R.string.txt_yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        avatarUri = null;
                        user.setAvatar_64(null);
                        user.setAvatar(null);
                        setUser(user);
                    }
                })
                .setNegativeButton(R.string.txt_no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();
        dialog.show();
    }

    private void patchUser() {
        if (!validate())
            return;

        user.setAvatar_64(DCUtils.base64Bitmap(avatarUri));
        user.setFirstname(tietProfileFirstname.getText().toString());
        user.setLastname(tietProfileLastname.getText().toString());
        user.setEmail(null);

        if (tietProfilePassword.getText().toString().length() > 0) {
            user.setPassword(tietProfilePassword.getText().toString());
        }

        if (tietProfileZipcode.getText().toString().length() > 0) {
            try {
                Integer zip = Integer.parseInt(tietProfileZipcode.getText().toString());
                user.setPostalCode(zip);
            } catch (NumberFormatException e) { }
        }

        if (user != null) {
            final DCLoadingFragment loadingFragment = showLoading();
            DCApiManager.getInstance().patchUser(user, new DCResponseListener<DCUser>() {
                @Override
                public void onFailure(DCError error) {
                    onError(error);
                    if (loadingFragment != null)
                        loadingFragment.dismissAllowingStateLoss();
                }

                @Override
                public void onResponse(DCUser object) {
                    if (object != null) {
                        setUser(DCSession.getInstance().getUser());
                        getActivity().getFragmentManager().popBackStack();
                    }
                    if (loadingFragment != null)
                        loadingFragment.dismissAllowingStateLoss();
                }
            });
        }
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (!hasFocus) {
            switch (v.getId()) {
                case R.id.tiet_profile_firstname:
                    if (TextUtils.isEmpty(tietProfileFirstname.getText().toString())) {
                        tilProfileFirstname.setErrorEnabled(true);
                        tilProfileFirstname.setError(getString(R.string.error_txt_first_name_required));
                    }
                    else if (tietProfileFirstname.getText().toString().length() < 2) {
                        tilProfileFirstname.setErrorEnabled(true);
                        tilProfileFirstname.setError(getString(R.string.error_txt_first_name_too_short));
                    }
                    else if (tietProfileFirstname.getText().toString().length() > 40) {
                        tilProfileFirstname.setErrorEnabled(true);
                        tilProfileFirstname.setError(getString(R.string.error_txt_first_name_too_long));
                    }
                    else if (!DCUtils.isValidName(tietProfileFirstname.getText().toString())) {
                        tilProfileFirstname.setErrorEnabled(true);
                        tilProfileFirstname.setError(getString(R.string.error_txt_name_not_valid));
                    }
                    else {
                        tilProfileFirstname.setErrorEnabled(false);
                    }
                    break;
                case R.id.tiet_profile_lastname:
                    if (tietProfileLastname.getText().toString().length() > 40) {
                        tilProfileLastname.setErrorEnabled(true);
                        tilProfileLastname.setError(getString(R.string.error_txt_last_name_too_long));
                    }
                    else if (tietProfileLastname.getText().toString().length() > 0 && !DCUtils.isValidName(tietProfileLastname.getText().toString())) {
                        tilProfileLastname.setErrorEnabled(true);
                        tilProfileLastname.setError(getString(R.string.error_txt_name_not_valid));
                    }
                    else {
                        tilProfileLastname.setErrorEnabled(false);
                    }
                    break;
                case R.id.tiet_profile_password:
                    if (tietProfilePassword.getText().toString().length() > 0 && tietProfilePassword.getText().toString().length() < 8) {
                        tilProfilePassword.setErrorEnabled(true);
                        tilProfilePassword.setError(getString(R.string.error_txt_password_short));
                    }
                    else {
                        tilProfilePassword.setErrorEnabled(false);
                    }
                    break;
            }
        }
    }

    private boolean validate() {
        if (TextUtils.isEmpty(tietProfileFirstname.getText().toString())) {
            onError(new DCError(R.string.error_txt_first_name_required));
            return false;
        }
        else if (tietProfileFirstname.getText().toString().length() < 2) {
            onError(new DCError(R.string.error_txt_first_name_too_short));
            return false;
        }
        else if (tietProfileFirstname.getText().toString().length() > 40) {
            onError(new DCError(R.string.error_txt_first_name_too_long));
            return false;
        }
        else if (!DCUtils.isValidName(tietProfileFirstname.getText().toString())) {
            onError(new DCError(R.string.error_txt_name_not_valid));
            return false;
        }
        else if (tietProfileLastname.getText().toString().length() > 40) {
            onError(new DCError(R.string.error_txt_last_name_too_long));
            return false;
        }
        else if (tietProfileLastname.getText().toString().length() > 0 && !DCUtils.isValidName(tietProfileLastname.getText().toString())) {
            onError(new DCError(R.string.error_txt_name_not_valid));
            return false;
        }
        else if (tietProfilePassword.getText().toString().length() > 0 && tietProfilePassword.getText().toString().length() < 8) {
            onError(new DCError(R.string.error_txt_password_short));
            return false;
        }

        return true;
    }

    private void pickLocation() {
        try {
            AutocompleteFilter typeFilter = new AutocompleteFilter.Builder()
                    .setTypeFilter(AutocompleteFilter.TYPE_FILTER_CITIES)
                    .build();

            Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                    .setFilter(typeFilter)
                    .build(getActivity());

            startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
        } catch (GooglePlayServicesRepairableException e) {
            Snacky.builder().setActivty(getActivity()).warning().setText(e.getMessage()).show();
        } catch (GooglePlayServicesNotAvailableException e) {
            Snacky.builder().setActivty(getActivity()).warning().setText(e.getMessage()).show();
        }
    }

    private void pickAvatar() {
        PermissionsManager.getInstance().requestPermissionsIfNecessaryForResult(getActivity(),
                new String[]{ Manifest.permission.CAMERA },
                new PermissionsResultAction() {
                    @Override
                    public void onGranted() {
                        EasyImage.openChooserWithGallery(DCProfileEditFragment.this, "", 0);
                    }

                    @Override
                    public void onDenied(String permission) {
                        if (!ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), permission)) {
                            Snacky.builder()
                                    .setActivty(getActivity())
                                    .warning()
                                    .setDuration(Snacky.LENGTH_LONG)
                                    .setText(R.string.signup_txt_permission_avatar)
                                    .setAction(R.string.txt_settings, new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Intent intent = new Intent();
                                            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                            Uri uri = Uri.parse("package:" + getActivity().getPackageName());
                                            intent.setData(uri);
                                            getActivity().startActivity(intent);
                                        }
                                    })
                                    .show();
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
            if (resultCode == RESULT_OK) {
                avatarUri = result.getUri();
                sdvProfileAvatar.setImageURI(avatarUri);
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
                            .start(getActivity(), DCProfileEditFragment.this);
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

        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(getActivity(), data);

                if (place.getAddress() != null && place.getAddress().length() > 0) {

                    String address = place.getAddress().toString();
                    String[] cc = address.split(", ");
                    if (cc.length == 2) {

                        String ci = cc[0];
                        String co = cc[1];

                        Country country = Country.getCountryByName(co);
                        if (country == null)
                            country = Country.getCountryByISO(co);

                        if (country != null) {
                            user.setCity(ci);
                            user.setCountry(country.getCode());
                            setUser(user);
                        }
                    }
                }
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(getActivity(), data);
                Snacky.builder().setActivty(getActivity()).warning().setText(status.getStatusMessage()).show();
            }
        }
    }
}
