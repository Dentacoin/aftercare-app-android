package com.dentacoin.dentacare.activities;

import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.dentacoin.dentacare.R;
import com.dentacoin.dentacare.fragments.DCCollectDCNFragment;
import com.dentacoin.dentacare.fragments.DCCollectSuccessFragment;
import com.dentacoin.dentacare.fragments.DCCollectWalletFragment;
import com.dentacoin.dentacare.fragments.IDCFragmentInterface;

/**
 * Created by Atanas Chervarov on 9/10/17.
 */

public class DCCollectActivity extends DCToolbarActivity implements IDCFragmentInterface {

    public static final String KEY_DCN_AMOUNT = "KEY_DCN_AMOUNT";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addContentView(R.layout.activity_collect);
        setActionBarTitle(R.string.collect_hdl_collect);
        getFragmentManager().beginTransaction().add(R.id.fragment_container, new DCCollectWalletFragment()).commit();
    }

    public void showCollectDCN() {
        final FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.animator.slide_in_right, R.animator.slide_out_left, R.animator.slide_in_left, R.animator.slide_out_right);
        transaction.replace(R.id.fragment_container, new DCCollectDCNFragment());
        transaction.addToBackStack(DCCollectDCNFragment.TAG);
        transaction.commit();
    }

    public void showSuccessScreen(int amount) {
        DCCollectSuccessFragment fragment = new DCCollectSuccessFragment();
        Bundle arguments = new Bundle();
        arguments.putInt(KEY_DCN_AMOUNT, amount);
        fragment.setArguments(arguments);
        toolbar.setVisibility(View.GONE);
        getFragmentManager().beginTransaction().add(R.id.container, fragment).commit();
    }

    @Override
    public void onFragmentRemoved() {
        toolbar.setVisibility(View.VISIBLE);
        finish();
    }
}
