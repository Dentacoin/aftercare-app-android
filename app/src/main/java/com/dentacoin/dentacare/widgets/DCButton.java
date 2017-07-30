package com.dentacoin.dentacare.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.support.v7.widget.AppCompatButton;

/**
 * Created by Atanas Chervarov on 7/29/17.
 */

public class DCButton extends AppCompatButton {
    public DCButton(Context context, AttributeSet attr, int style) {
        super(context, attr, style);
        init();
    }

    public DCButton(Context context, AttributeSet attr) {
        super(context, attr);
        init();
    }

    public DCButton(Context context) {
        super(context);
        init();
    }

    private void init() {
    }
}
