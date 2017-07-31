package com.dentacoin.dentacare.widgets;

import android.content.Context;
import android.support.design.widget.TextInputLayout;
import android.util.AttributeSet;

import com.dentacoin.dentacare.utils.DCFonts;

/**
 * Created by Atanas Chervarov on 7/30/17.
 */

public class DCTextInputLayout extends TextInputLayout {
    public DCTextInputLayout(Context context, AttributeSet attr, int style) {
        super(context, attr, style);
        init();
    }

    public DCTextInputLayout(Context context, AttributeSet attr) {
        super(context, attr);
        init();
    }

    public DCTextInputLayout(Context context) {
        super(context);
        init();
    }

    private void init() {
        setTypeface(DCFonts.getFont(getContext(), DCFonts.FONT_LATO_LIGHT));
    }
}
