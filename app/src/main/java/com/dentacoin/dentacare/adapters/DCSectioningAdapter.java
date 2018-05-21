package com.dentacoin.dentacare.adapters;

import android.view.View;
import android.view.ViewGroup;

import org.zakariya.stickyheaders.SectioningAdapter;

/**
 * Created by Atanas Chervarov on 13.05.18.
 */
public class DCSectioningAdapter extends SectioningAdapter {
    @Override
    public GhostHeaderViewHolder onCreateGhostHeaderViewHolder(ViewGroup parent) {
        View ghostView = new View(parent.getContext());
        ghostView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        return new GhostHeaderViewHolder(ghostView);
    }
}
