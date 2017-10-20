package com.dentacoin.dentacare.widgets;

import android.content.Context;
import android.support.design.widget.TextInputEditText;
import android.util.AttributeSet;

import com.dentacoin.dentacare.utils.DCFonts;

/**
 * Created by Atanas Chervarov on 8/14/17.
 */

public class DCTextInputEditText extends TextInputEditText {
    public DCTextInputEditText(Context context, AttributeSet attr, int style) {
        super(context, attr, style);
        init();
    }

    public DCTextInputEditText(Context context, AttributeSet attr) {
        super(context, attr);
        init();
    }

    public DCTextInputEditText(Context context) {
        super(context);
        init();
    }

    private void init() {
        setTypeface(DCFonts.getFont(getContext(), DCFonts.FONT_LATO_LIGHT));
    }
}
