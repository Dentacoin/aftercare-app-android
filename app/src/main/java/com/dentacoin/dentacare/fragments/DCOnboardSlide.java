package com.dentacoin.dentacare.fragments;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.dentacoin.dentacare.R;
import com.dentacoin.dentacare.widgets.DCTextView;

/**
 * Created by Atanas Chervarov on 29.01.18.
 */

public class DCOnboardSlide extends DCFragment {

    private static final String KEY_TITLE = "KEY_TITLE";
    private static final String KEY_MESSAGE = "KEY_MESSAGE";
    private static final String KEY_DRAWABLE = "KEY_DRAWABLE";

    private DCTextView tvSubTitle;
    private DCTextView tvTitle;
    private ImageView ivImage;

    public static DCOnboardSlide create(String title, String message, int drawableResource) {
        DCOnboardSlide slide = new DCOnboardSlide();
        Bundle bundle = new Bundle();
        bundle.putString(KEY_TITLE, title);
        bundle.putString(KEY_MESSAGE, message);
        bundle.putInt(KEY_DRAWABLE, drawableResource);
        slide.setArguments(bundle);
        return slide;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance) {
        View view = inflater.inflate(R.layout.fragment_onboard_slide, container, false);
        tvSubTitle = view.findViewById(R.id.tv_sub_title);
        ivImage = view.findViewById(R.id.iv_image);
        tvTitle = view.findViewById(R.id.tv_title);

        if (getArguments() != null) {
            tvTitle.setText(getArguments().getString(KEY_TITLE));
            tvSubTitle.setText(getArguments().getString(KEY_MESSAGE));
            Drawable drawable = getResources().getDrawable(getArguments().getInt(KEY_DRAWABLE));
            ivImage.setImageDrawable(drawable);
        }

        return view;
    }
}
