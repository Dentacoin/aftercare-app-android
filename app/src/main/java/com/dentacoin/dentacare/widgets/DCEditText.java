package com.dentacoin.dentacare.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.support.v7.widget.AppCompatEditText;

import com.dentacoin.dentacare.utils.DCFonts;

/**
 * Created by Atanas Chervarov on 7/30/17.
 */

public class DCEditText extends AppCompatEditText {
    public DCEditText(Context context, AttributeSet attr, int style) {
        super(context, attr, style);
        init();
    }

    public DCEditText(Context context, AttributeSet attr) {
        super(context, attr);
        init();
    }

    public DCEditText(Context context) {
        super(context);
        init();
    }

    private void init() {
        setTypeface(DCFonts.getFont(getContext(), DCFonts.FONT_LATO_LIGHT));
    }
}
