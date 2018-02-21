package com.dentacoin.dentacare.fragments;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;

import com.anthonycr.grant.PermissionsManager;
import com.anthonycr.grant.PermissionsResultAction;
import com.dentacoin.dentacare.R;
import com.dentacoin.dentacare.activities.DCCollectActivity;
import com.dentacoin.dentacare.activities.DCQRScannerActivity;
import com.dentacoin.dentacare.utils.DCConstants;
import com.dentacoin.dentacare.utils.DCSharedPreferences;
import com.dentacoin.dentacare.utils.Tutorial;
import com.dentacoin.dentacare.widgets.DCButton;
import com.dentacoin.dentacare.widgets.DCEditText;
import com.takusemba.spotlight.SimpleTarget;
import com.takusemba.spotlight.Spotlight;

import java.util.regex.Matcher;

import de.mateware.snacky.Snacky;

/**
 * Created by Atanas Chervarov on 9/10/17.
 */

public class DCCollectWalletFragment extends DCFragment implements View.OnClickListener {

    private static final int REQUEST_CODE_CAMERA_QR_SCAN = 1001;

    private DCButton btnCollectSend;
    private ImageView ivCollectQrScan;
    private DCEditText etCollectWallet;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance) {
        View view = inflater.inflate(R.layout.fragment_collect_wallet, container, false);
        btnCollectSend = view.findViewById(R.id.collect_btn_send);
        btnCollectSend.setOnClickListener(this);
        btnCollectSend.setEnabled(false);

        ivCollectQrScan = view.findViewById(R.id.iv_collect_qr_scan);
        ivCollectQrScan.setOnClickListener(this);
        etCollectWallet = view.findViewById(R.id.et_collect_wallet);

        etCollectWallet.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 40) {
                    Matcher addressMatcher = DCConstants.ADDRESS_PATTERN.matcher(s);
                    if (addressMatcher.matches()) {
                        btnCollectSend.setEnabled(true);
                        return;
                    }
                }
                btnCollectSend.setEnabled(false);
            }
        });

        etCollectWallet.setText(DCSharedPreferences.loadString(DCSharedPreferences.DCSharedKey.DEFAULT_WALLET));

        showTutorial();
        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.collect_btn_send:
                if (!etCollectWallet.getText().toString().isEmpty()) {
                    Matcher addressMatcher = DCConstants.ADDRESS_PATTERN.matcher(etCollectWallet.getText().toString());
                    if (addressMatcher.matches()) {
                        DCSharedPreferences.saveString(DCSharedPreferences.DCSharedKey.DEFAULT_WALLET, etCollectWallet.getText().toString());
                        ((DCCollectActivity) getActivity()).showCollectDCN();
                    }
                }
                break;
            case R.id.iv_collect_qr_scan:
                scanQRCode();
                break;
        }
    }

    private void scanQRCode() {
        PermissionsManager.getInstance().requestPermissionsIfNecessaryForResult(getActivity(),
                new String[]{Manifest.permission.CAMERA},
                new PermissionsResultAction() {
                    @Override
                    public void onGranted() {
                        final Intent qrCodeScanIntent = new Intent(getActivity(), DCQRScannerActivity.class);
                        startActivityForResult(qrCodeScanIntent, REQUEST_CODE_CAMERA_QR_SCAN);
                    }

                    @Override
                    public void onDenied(String permission) {
                        if (!ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), permission)) {
                            Snacky.builder()
                                    .setActivty(getActivity())
                                    .warning()
                                    .setDuration(Snacky.LENGTH_LONG)
                                    .setText(R.string.collect_txt_permission_camera)
                                    .setAction(R.string.collect_txt_permission_camera_settings, new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Intent intent = new Intent();
                                            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                            Uri uri = Uri.parse("package:" + getActivity().getPackageName());
                                            intent.setData(uri);
                                            startActivity(intent);
                                        }
                                    }).show();
                        } else {
                            Snacky.builder().setActivty(getActivity()).warning().setText(R.string.collect_txt_permission_camera).show();
                        }
                    }
                });
    }

    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_CAMERA_QR_SCAN) {
            if (data != null) {
                String wallet = data.getStringExtra(DCQRScannerActivity.KEY_SCANNED_WALLET);
                if (wallet != null) {
                    etCollectWallet.setText(wallet);
                    Snacky.builder().setActivty(getActivity()).success().setText(R.string.collect_txt_wallet_copied).setDuration(Snacky.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void showTutorial() {
        if (!DCSharedPreferences.getShownTutorials().contains(Tutorial.QR_CODE.name())) {
            ivCollectQrScan.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    ivCollectQrScan.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    int[] location = new int[2];
                    ivCollectQrScan.getLocationInWindow(location);
                    float oneX = location[0] + ivCollectQrScan.getWidth() / 2f;
                    float oneY = location[1] + ivCollectQrScan.getHeight() / 2f;

                    SimpleTarget qrTarget = new SimpleTarget.Builder(getActivity())
                            .setPoint(oneX, oneY)
                            .setRadius(80f) // radius of the Target
                            .setTitle("Add your wallet") // title
                            .setDescription(getString(R.string.tutorial_txt_qr_code)) // description
                            .build();
                    Spotlight.with(getActivity())
                            .setOverlayColor(getResources().getColor(R.color.blackTransparent80)) // background overlay color
                            .setDuration(500L) // duration of Spotlight emerging and disappearing in ms
                            .setAnimation(new DecelerateInterpolator(2f)) // animation of Spotlight
                            .setTargets(qrTarget) // set targets. see below for more info
                            .start(); // start Spotlight

                    DCSharedPreferences.setShownTutorial(Tutorial.QR_CODE, true);
                }
            });
        }
    }
}
