package com.dentacoin.dentacare.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dentacoin.dentacare.R;
import com.dentacoin.dentacare.activities.DCCollectActivity;
import com.dentacoin.dentacare.activities.DCProfileActivity;
import com.dentacoin.dentacare.activities.DCWithdrawsActivity;
import com.dentacoin.dentacare.model.DCDashboard;
import com.dentacoin.dentacare.model.DCError;
import com.dentacoin.dentacare.model.DCGasPrice;
import com.dentacoin.dentacare.model.DCJourney;
import com.dentacoin.dentacare.model.DCRoutine;
import com.dentacoin.dentacare.model.DCTransaction;
import com.dentacoin.dentacare.network.DCApiManager;
import com.dentacoin.dentacare.network.DCResponseListener;
import com.dentacoin.dentacare.utils.DCConstants;
import com.dentacoin.dentacare.utils.DCDashboardDataProvider;
import com.dentacoin.dentacare.utils.DCSharedPreferences;
import com.dentacoin.dentacare.utils.DCUtils;
import com.dentacoin.dentacare.utils.IDCDashboardObserver;
import com.dentacoin.dentacare.widgets.DCButton;
import com.dentacoin.dentacare.widgets.DCTextView;

import de.mateware.snacky.Snacky;

/**
 * Created by Atanas Chervarov on 9/29/17.
 */

public class DCCollectDCNFragment extends DCFragment implements View.OnClickListener, IDCDashboardObserver {

    public static final String TAG = DCCollectDCNFragment.class.getSimpleName();
    private DCButton btnCollect;
    private DCTextView tvCollectTotal;
    private DCTextView tvCollectAvailable;
    private DCTextView tvCollectInfo;
    private String wallet;
    private DCDashboard dashboard;
    DCLoadingFragment loadingFragment;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance) {
        View view = inflater.inflate(R.layout.fragment_collect_dcn, container, false);
        wallet = DCSharedPreferences.loadString(DCSharedPreferences.DCSharedKey.DEFAULT_WALLET);
        btnCollect = view.findViewById(R.id.btn_collect);
        btnCollect.setOnClickListener(this);
        tvCollectTotal = view.findViewById(R.id.tv_collect_total);
        tvCollectAvailable = view.findViewById(R.id.tv_collect_available);
        tvCollectInfo = view.findViewById(R.id.tv_collect_info);
        tvCollectInfo.setOnClickListener(this);
        loadingFragment = showLoading();
        btnCollect.setEnabled(false);
        DCDashboardDataProvider.getInstance().updateDashboard(true);
        setupView();
        checkGasPrice();
        return view;
    }

    private void setupView() {
        tvCollectTotal.setText(getString(R.string.txt_dcn, 0));
        tvCollectAvailable.setText(getString(R.string.txt_dcn, 0));
        btnCollect.setEnabled(false);

        if (dashboard != null) {
            tvCollectTotal.setText(getString(R.string.txt_dcn, dashboard.getTotalDCN()));
            tvCollectAvailable.setText(getString(R.string.txt_dcn, dashboard.getEarnedDCN()));
            btnCollect.setEnabled(dashboard.getEarnedDCN() > 0);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_collect:
                if (dashboard != null) {
                    final DCTransaction transaction = new DCTransaction();
                    transaction.setAmount(dashboard.getEarnedDCN());
                    transaction.setWallet(wallet);
                    DCApiManager.getInstance().postTransaction(transaction, new DCResponseListener<Void>() {
                        @Override
                        public void onFailure(DCError error) {
                            if (error != null && getActivity() != null) {
                                if (error.isType("user_cannot_withdraw")) {
                                    Snacky.builder()
                                            .setActivty(getActivity())
                                            .error()
                                            .setText(R.string.collect_error_not_enough_use)
                                            .setDuration(Snacky.LENGTH_INDEFINITE)
                                            .setAction(R.string.txt_ok, new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {
                                                }
                                            })
                                            .show();
                                    return;
                                }
                                else if (error.isType("user_not_confirmed")) {
                                    Snacky.builder()
                                            .setActivty(getActivity())
                                            .warning()
                                            .setText(R.string.collect_error_not_confirmed)
                                            .setDuration(Snacky.LENGTH_INDEFINITE)
                                            .setAction(R.string.collect_error_btn_verify, new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {
                                                    final Intent profileIntent = new Intent(getActivity(), DCProfileActivity.class);
                                                    startActivity(profileIntent);
                                                    getActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                                                }
                                            })
                                            .show();
                                }
                                else {
                                    onError(error);
                                }
                            }
                        }

                        @Override
                        public void onResponse(Void object) {
                            if (getActivity() != null && isAdded()) {
                                ((DCCollectActivity) getActivity()).showSuccessScreen(dashboard.getEarnedDCN());
                            }
                        }
                    });
                }
                break;
            case R.id.tv_collect_info:
                DCUtils.openURL(getActivity(), DCConstants.DENTACOIN_WEBSITE);
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        DCDashboardDataProvider.getInstance().addObserver(this);
    }

    @Override
    public void onPause() {
        DCDashboardDataProvider.getInstance().removeObserver(this);
        super.onPause();
    }

    @Override
    public void onDashboardUpdated(DCDashboard dashboard) {
        loadingFragment.dismissAllowingStateLoss();
        btnCollect.setEnabled(true);
        this.dashboard = dashboard;
        setupView();
    }

    @Override
    public void onDashboardError(DCError error) {
        loadingFragment.dismissAllowingStateLoss();
        btnCollect.setEnabled(true);
        Snacky.builder().setActivty(getActivity()).error().setAction(R.string.txt_try_again, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DCDashboardDataProvider.getInstance().updateDashboard(true);
            }
        }).setText(R.string.collect_error_retrieving_data).setDuration(Snacky.LENGTH_INDEFINITE).show();
    }

    @Override
    public void onSyncNeeded(DCRoutine[] routines) {
    }

    @Override
    public void onSyncSuccess() {
    }

    @Override
    public void onJourneyUpdated(DCJourney journey) {
    }

    @Override
    public void onJourneyError(DCError error) {
    }

    private void checkGasPrice() {
        DCApiManager.getInstance().getGasPrice(new DCResponseListener<DCGasPrice>() {
            @Override
            public void onFailure(DCError error) {
                //Don't handle that
            }

            @Override
            public void onResponse(DCGasPrice object) {
                if (object != null && object.isOverTreshold()) {
                    Snacky.builder()
                            .setActivty(getActivity())
                            .warning()
                            .setText(R.string.warning_txt_slow_transfer)
                            .setDuration(Snacky.LENGTH_INDEFINITE)
                            .setAction(R.string.txt_ok, new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                }
                            })
                            .show();
                }
            }
        });
    }
}