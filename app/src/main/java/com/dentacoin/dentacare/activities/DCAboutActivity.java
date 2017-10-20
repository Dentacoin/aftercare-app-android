package com.dentacoin.dentacare.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.dentacoin.dentacare.R;
import com.dentacoin.dentacare.utils.DCConstants;
import com.dentacoin.dentacare.utils.DCUtils;
import com.dentacoin.dentacare.widgets.DCTextView;

/**
 * Created by Atanas Chervarov on 9/5/17.
 */

public class DCAboutActivity extends DCToolbarActivity implements View.OnClickListener {

    private DCTextView tvAboutWebsite;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addContentView(R.layout.activity_about);
        setActionBarTitle(R.string.about_hdl_about);

        tvAboutWebsite = (DCTextView) findViewById(R.id.tv_about_website);
        tvAboutWebsite.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_about_website:
                DCUtils.openURL(this, DCConstants.DENTACARE_WEBSITE);
                break;
        }
    }
}
