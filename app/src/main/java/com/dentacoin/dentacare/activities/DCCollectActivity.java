package com.dentacoin.dentacare.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.dentacoin.dentacare.R;
import com.dentacoin.dentacare.fragments.DCCollectWalletFragment;

/**
 * Created by Atanas Chervarov on 9/10/17.
 */

public class DCCollectActivity extends DCToolbarActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addContentView(R.layout.activity_collect);
        setActionBarTitle(R.string.collect_hdl_collect);
        getFragmentManager().beginTransaction().add(R.id.fragment_container, new DCCollectWalletFragment()).commit();
    }
}
