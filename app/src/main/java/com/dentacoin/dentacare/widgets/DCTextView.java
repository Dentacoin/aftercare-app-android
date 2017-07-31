package com.dentacoin.dentacare.widgets;

import android.content.Context;
import android.util.AttributeSet;

import android.support.v7.widget.AppCompatTextView;

import com.dentacoin.dentacare.utils.DCFonts;

/**
 * Created by Atanas Chervarov on 7/30/17.
 */

public class DCTextView extends AppCompatTextView {
    public DCTextView(Context context, AttributeSet attr, int style) {
        super(context, attr, style);
        init();
    }

    public DCTextView(Context context, AttributeSet attr) {
        super(context, attr);
        init();
    }

    public DCTextView(Context context) {
        super(context);
        init();
    }

    private void init() {
        setTypeface(DCFonts.getFont(getContext(), DCFonts.FONT_LATO_LIGHT));
    }
}
