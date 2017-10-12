package com.dentacoin.dentacare.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;

import android.support.v7.widget.AppCompatTextView;

import com.dentacoin.dentacare.R;
import com.dentacoin.dentacare.utils.DCFonts;

/**
 * Created by Atanas Chervarov on 7/30/17.
 */

public class DCTextView extends AppCompatTextView {
    public DCTextView(Context context, AttributeSet attr, int style) {
        super(context, attr, style);
        init(attr);
    }

    public DCTextView(Context context, AttributeSet attr) {
        super(context, attr);
        init(attr);
    }

    public DCTextView(Context context) {
        super(context);
        init(null);
    }

    private void init(AttributeSet attrs) {
        if (attrs != null) {
            TypedArray typedArray = getContext().getTheme().obtainStyledAttributes(attrs, R.styleable.DCTextView, 0, 0);
            try {
                switch (typedArray.getInt(R.styleable.DCTextView_fontType, -1)) {
                    case 0:
                        setTypeface(DCFonts.getFont(getContext(), DCFonts.FONT_LATO_REGULAR));
                        break;
                    case 1:
                        setTypeface(DCFonts.getFont(getContext(), DCFonts.FONT_LATO_LIGHT));
                        break;
                    case 2:
                        setTypeface(DCFonts.getFont(getContext(), DCFonts.FONT_LATO_BOLD));
                        break;
                    default:
                        setTypeface(DCFonts.getFont(getContext(), DCFonts.FONT_LATO_REGULAR));
                        break;
                }
            } finally {
                typedArray.recycle();
            }
        } else {
            setTypeface(DCFonts.getFont(getContext(), DCFonts.FONT_LATO_REGULAR));
        }
    }
}
