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
import android.widget.ImageView;

import com.anthonycr.grant.PermissionsManager;
import com.anthonycr.grant.PermissionsResultAction;
import com.dentacoin.dentacare.R;
import com.dentacoin.dentacare.activities.DCQRScannerActivity;
import com.dentacoin.dentacare.utils.DCConstants;
import com.dentacoin.dentacare.utils.DCSharedPreferences;
import com.dentacoin.dentacare.widgets.DCButton;
import com.dentacoin.dentacare.widgets.DCTextInputEditText;

import java.util.regex.Matcher;

import de.mateware.snacky.Snacky;

/**
 * Created by Atanas Chervarov on 9/10/17.
 */

public class DCCollectWalletFragment extends DCFragment implements View.OnClickListener {

    private static final int REQUEST_CODE_CAMERA_QR_SCAN = 1001;

    private DCButton btnCollectSend;
    private ImageView ivCollectQrScan;

    private DCTextInputEditText tietCollectWallet;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance) {
        View view = inflater.inflate(R.layout.fragment_collect_wallet, container, false);
        btnCollectSend = (DCButton) view.findViewById(R.id.collect_btn_send);
        btnCollectSend.setOnClickListener(this);
        btnCollectSend.setEnabled(false);

        ivCollectQrScan = (ImageView) view.findViewById(R.id.iv_collect_qr_scan);
        ivCollectQrScan.setOnClickListener(this);
        tietCollectWallet = (DCTextInputEditText) view.findViewById(R.id.tiet_collect_wallet);

        tietCollectWallet.addTextChangedListener(new TextWatcher() {
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

        tietCollectWallet.setText(DCSharedPreferences.loadString(DCSharedPreferences.DCSharedKey.DEFAULT_WALLET));
        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.collect_btn_send:
                Snacky.builder().setActivty(getActivity()).warning().setText(R.string.error_not_implemented).show();
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
                    tietCollectWallet.setText(wallet);
                    DCSharedPreferences.saveString(DCSharedPreferences.DCSharedKey.DEFAULT_WALLET, wallet);
                    Snacky.builder().setActivty(getActivity()).success().setText(R.string.collect_txt_wallet_copied).setDuration(Snacky.LENGTH_SHORT).show();
                }
            }
        }
    }
}
