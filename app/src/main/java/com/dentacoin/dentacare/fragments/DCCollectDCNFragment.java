package com.dentacoin.dentacare.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dentacoin.dentacare.R;
import com.dentacoin.dentacare.activities.DCCollectActivity;
import com.dentacoin.dentacare.model.DCRecord;
import com.dentacoin.dentacare.model.DCDashboard;
import com.dentacoin.dentacare.model.DCError;
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
import com.dentacoin.dentacare.widgets.DCEditText;
import com.dentacoin.dentacare.widgets.DCTextView;

import de.mateware.snacky.Snacky;

/**
 * Created by Atanas Chervarov on 9/29/17.
 */

public class DCCollectDCNFragment extends DCFragment implements View.OnClickListener, IDCDashboardObserver {

    public static final String TAG = DCCollectDCNFragment.class.getSimpleName();
    private DCButton btnCollect;
    private DCTextView tvCollectCollected;
    private DCTextView tvCollectCurrentBalance;
    private DCEditText etCollectAmount;
    private DCTextView tvCollectInfo;
    private String wallet;
    private DCDashboard dashboard;
    private int collected = 0;
    DCLoadingFragment loadingFragment;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance) {
        View view = inflater.inflate(R.layout.fragment_collect_dcn, container, false);
        wallet = DCSharedPreferences.loadString(DCSharedPreferences.DCSharedKey.DEFAULT_WALLET);
        btnCollect = view.findViewById(R.id.btn_collect);
        btnCollect.setOnClickListener(this);
        tvCollectCollected = view.findViewById(R.id.tv_collect_collected);
        tvCollectCurrentBalance = view.findViewById(R.id.tv_collect_current_balance);
        etCollectAmount = view.findViewById(R.id.et_collect_amount);
        tvCollectInfo = view.findViewById(R.id.tv_collect_info);
        tvCollectInfo.setOnClickListener(this);
        loadingFragment = showLoading();
        btnCollect.setEnabled(false);
        DCDashboardDataProvider.getInstance().updateDashboard(true);
        getTransactions();
        setupView();
        return view;
    }

    private void getTransactions() {
        DCApiManager.getInstance().getTransactions(new DCResponseListener<DCTransaction[]>() {
            @Override
            public void onFailure(DCError error) {
                collected = 0;
                setupView();
            }

            @Override
            public void onResponse(DCTransaction[] object) {
                collected = 0;
                if (object != null && object.length > 0) {
                    for (int i = 0; i < object.length; i++) {
                        DCTransaction transaction = object[i];
                        if (transaction.getStatus() == DCTransaction.STATUS.APPROVED || transaction.getStatus() == DCTransaction.STATUS.PENDING) {
                            collected += transaction.getAmount();
                        }
                    }
                }
                setupView();
            }
        });
    }

    private void setupView() {
        tvCollectCollected.setText(getString(R.string.txt_dcn, collected));
        tvCollectCurrentBalance.setText(getString(R.string.txt_dcn, 0));

        if (dashboard != null) {
            tvCollectCurrentBalance.setText(getString(R.string.txt_dcn, dashboard.getTotalDCN()));
            etCollectAmount.setText(Integer.toString(dashboard.getTotalDCN()));
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_collect:
                if (dashboard != null && !etCollectAmount.getText().toString().isEmpty() && etCollectAmount.getText().toString().length() < 8) {
                    try {
                        final int amount = Integer.parseInt(etCollectAmount.getText().toString());
                        if (amount > 0 && amount <= dashboard.getTotalDCN()) {
                            final DCTransaction transaction = new DCTransaction();
                            transaction.setAmount(amount);
                            transaction.setWallet(wallet);

                            DCApiManager.getInstance().postTransaction(transaction, new DCResponseListener<Void>() {
                                @Override
                                public void onFailure(DCError error) {
                                    if (error != null) {
                                        String message = error.getMessage(getActivity());
                                        if ("user_cannot_withdraw".equals(message) && getActivity() != null) {
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
                                    }
                                    onError(error);
                                }

                                @Override
                                public void onResponse(Void object) {
                                    if (getActivity() != null && isAdded()) {
                                        ((DCCollectActivity) getActivity()).showSuccessScreen(amount);
                                    }
                                }
                            });
                        } else {
                            Snacky.builder().setActivty(getActivity()).error().setText(R.string.collect_error_not_enough).show();
                        }
                    } catch (NumberFormatException e) {
                    }
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
}