package com.dentacoin.dentacare.activities;

import android.Manifest;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v13.app.ActivityCompat;
import android.view.View;

import com.anthonycr.grant.PermissionsManager;
import com.anthonycr.grant.PermissionsResultAction;
import com.dentacoin.dentacare.R;
import com.dentacoin.dentacare.fragments.DCSelectToothFragment;
import com.dentacoin.dentacare.fragments.DCSendMessageFragment;
import com.dentacoin.dentacare.fragments.DCSentFeedbackFragment;
import com.dentacoin.dentacare.fragments.IDCFragmentInterface;
import com.dentacoin.dentacare.network.DCSession;
import com.dentacoin.dentacare.utils.DCConstants;
import com.dentacoin.dentacare.utils.DCUtils;

import de.mateware.snacky.Snacky;

/**
 * Created by Atanas Chervarov on 8/29/17.
 */

public class DCEmergencyActivity extends DCToolbarActivity implements IDCFragmentInterface {

    private Uri teethUri;
    private static final int REQUEST_CODE_SEND_EMERGENCY_EMAIL = 1001;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addContentView(R.layout.activity_emergency);
        setActionBarTitle(R.string.emergency_hdl_emergency);
        getFragmentManager().beginTransaction().add(R.id.fragment_container, new DCSelectToothFragment()).commit();
    }

    public void takeTeethScreenshot(final View teeth) {
        PermissionsManager.getInstance().requestPermissionsIfNecessaryForResult(this,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                new PermissionsResultAction() {
                    @Override
                    public void onGranted() {
                        Bitmap bitmap = DCUtils.loadBitmapFromView(teeth);
                        String path = MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, "dentacare-teeth", null);
                        if (path != null) {
                            teethUri = Uri.parse(path);
                        }

                        final FragmentTransaction transaction = getFragmentManager().beginTransaction();
                        transaction.setCustomAnimations(R.animator.slide_in_right, R.animator.slide_out_left, R.animator.slide_in_left, R.animator.slide_out_right);

                        transaction.add(R.id.fragment_container, new DCSendMessageFragment());
                        transaction.addToBackStack(null);
                        transaction.commit();
                    }

                    @Override
                    public void onDenied(String permission) {
                        if (!ActivityCompat.shouldShowRequestPermissionRationale(DCEmergencyActivity.this, permission)) {
                            Snacky.builder()
                                    .setActivty(DCEmergencyActivity.this)
                                    .warning()
                                    .setDuration(Snacky.LENGTH_LONG)
                                    .setText(R.string.emergency_txt_permission_teeth)
                                    .setAction(R.string.emergency_txt_settings, new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Intent intent = new Intent();
                                            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                            Uri uri = Uri.parse("package:" + getPackageName());
                                            intent.setData(uri);
                                            startActivity(intent);
                                        }
                                    })
                                    .show();
                        } else {
                            Snacky.builder().setActivty(DCEmergencyActivity.this).warning().setText(R.string.emergency_txt_permission_teeth).show();
                        }
                    }
                });
    }

    public void sendEmail(String message, String phoneNumber) {
        final Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:" + DCConstants.EMERGENCY_EMAIL));
        emailIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        if (DCSession.getInstance().getUser() != null && DCSession.getInstance().getUser().getFullName() != null) {
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.email_subject, DCSession.getInstance().getUser().getFullName()));
        }

        if (teethUri != null) {
            emailIntent.putExtra(Intent.EXTRA_STREAM, teethUri);
        }

        String content = message;
        if (phoneNumber != null && phoneNumber.length() > 0) {
            content+= "\n\n" + "Contact: " + phoneNumber;
        }

        emailIntent.putExtra(Intent.EXTRA_TEXT, content);

        Intent intentChooser = Intent.createChooser(emailIntent, getString(R.string.email_send_via));

        if (emailIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intentChooser, REQUEST_CODE_SEND_EMERGENCY_EMAIL);
        }
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SEND_EMERGENCY_EMAIL) {
            toolbar.setVisibility(View.GONE);
            getFragmentManager().beginTransaction().add(R.id.container, new DCSentFeedbackFragment()).commit();
        }
    }

    @Override
    public void onFragmentRemoved() {
        toolbar.setVisibility(View.VISIBLE);
        finish();
    }
}
