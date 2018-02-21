package com.dentacoin.dentacare.activities;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.view.View;

import com.dentacoin.dentacare.R;
import com.dentacoin.dentacare.fragments.DCSelectToothFragment;
import com.dentacoin.dentacare.fragments.DCSendMessageFragment;
import com.dentacoin.dentacare.fragments.DCSentFeedbackFragment;
import com.dentacoin.dentacare.fragments.IDCFragmentInterface;
import com.dentacoin.dentacare.network.DCSession;
import com.dentacoin.dentacare.utils.DCConstants;
import com.dentacoin.dentacare.utils.DCUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

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

    private Bitmap bitmap;
    public void takeTeethScreenshot(final View teeth) {

        bitmap = DCUtils.loadBitmapFromView(teeth);

        File cachePath = new File(getExternalCacheDir(), "images");
        cachePath.mkdirs();

        try {
            FileOutputStream stream = new FileOutputStream(cachePath + "/dentacare-teeth.png"); // overwrites this image every time
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            stream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        File newFile = new File(cachePath, "dentacare-teeth.png");
        teethUri = FileProvider.getUriForFile(this, getPackageName(), newFile);

        final FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.animator.slide_in_right, R.animator.slide_out_left, R.animator.slide_in_left, R.animator.slide_out_right);

        transaction.add(R.id.fragment_container, new DCSendMessageFragment());
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public void sendEmail(String message, String phoneNumber) {
        final Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setType("image/jpeg");
        emailIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        emailIntent.putExtra(Intent.EXTRA_EMAIL, DCConstants.EMERGENCY_EMAIL);
        if (DCSession.getInstance().getUser() != null && DCSession.getInstance().getUser().getFullName() != null) {
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.email_subject, DCSession.getInstance().getUser().getFullName()));
        }
        emailIntent.putExtra(Intent.EXTRA_STREAM, teethUri);
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
