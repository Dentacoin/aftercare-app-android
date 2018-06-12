package com.dentacoin.dentacare.adapters.viewholders;

import android.view.View;

import com.dentacoin.dentacare.R;
import com.dentacoin.dentacare.widgets.DCTextView;

import org.zakariya.stickyheaders.SectioningAdapter;

/**
 * Created by Atanas Chervarov on 14.05.18.
 */
public class DCSectionViewHolder extends SectioningAdapter.HeaderViewHolder {

    private final DCTextView tvHeaderTitle;

    public DCSectionViewHolder(View view) {
        super(view);
        tvHeaderTitle = view.findViewById(R.id.tv_header_title);
    }

    public void setTitle(int titleResource) {
        tvHeaderTitle.setText(titleResource);
    }
}
