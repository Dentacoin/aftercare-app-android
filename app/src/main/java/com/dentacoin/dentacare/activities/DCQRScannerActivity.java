package com.dentacoin.dentacare.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import com.dentacoin.dentacare.R;
import com.dentacoin.dentacare.utils.DCConstants;
import com.dentacoin.dentacare.utils.DCUtils;
import com.google.zxing.Result;

import java.util.regex.Matcher;
import me.dm7.barcodescanner.zxing.ZXingScannerView;

/**
 * Created by Atanas Chervarov on 9/25/17.
 */

public class DCQRScannerActivity extends Activity implements ZXingScannerView.ResultHandler {
    private ZXingScannerView mScannerView;

    public static final String KEY_SCANNED_WALLET = "KEY_SCANNED_WALLET";

    private String wallet = null;

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        mScannerView = new ZXingScannerView(this);   // Programmatically initialize the scanner view
        setContentView(mScannerView);                // Set the scanner view as the content view
    }

    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this); // Register ourselves as a handler for scan results.
        mScannerView.startCamera();          // Start camera on resume
        mScannerView.setAutoFocus(true);
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();           // Stop camera on pause
    }

    @Override
    public void handleResult(Result rawResult) {
        String adr = rawResult.getText();
        wallet = null;

        if (adr != null && !adr.isEmpty()) {

            Matcher addressMatcher = DCConstants.ADDRESS_PATTERN.matcher(adr);
            Matcher ibanMatcherShort = DCConstants.IBAN_SHORT_PATTERN.matcher(adr);
            Matcher ibanMatcherLong = DCConstants.IBAN_LONG_PATTERN.matcher(adr);
            String convertedAddress = null;

            if (addressMatcher.find()) {
                wallet = addressMatcher.group();
            }
            else if (ibanMatcherLong.find()) {
                convertedAddress = DCUtils.ibanToAdress(ibanMatcherLong.group());
            }
            else if (ibanMatcherShort.find()) {
                convertedAddress =  DCUtils.ibanToAdress(ibanMatcherShort.group());
            }

            if (convertedAddress != null) {
                addressMatcher = DCConstants.ADDRESS_PATTERN.matcher(convertedAddress);

                if (addressMatcher.find()) {
                    wallet = addressMatcher.group();
                }
            }
        }

        if (wallet != null) {
            final AlertDialog dialog = new AlertDialog.Builder(this)
                    .setTitle(R.string.collect_txt_found_wallet)
                    .setMessage(wallet)
                    .setPositiveButton(R.string.txt_yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            mScannerView.stopCamera();
                            Intent intent = new Intent();
                            intent.putExtra(KEY_SCANNED_WALLET, wallet);
                            setResult(Activity.RESULT_OK, intent);
                            finish();
                        }
                    })
                    .setNegativeButton(R.string.txt_try_again, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            mScannerView.resumeCameraPreview(DCQRScannerActivity.this);
                        }
                    })
                    .create();
            dialog.show();
        } else {
            mScannerView.resumeCameraPreview(DCQRScannerActivity.this);
        }
    }
}
