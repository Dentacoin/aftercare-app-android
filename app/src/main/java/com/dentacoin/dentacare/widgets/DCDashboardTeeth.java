package com.dentacoin.dentacare.widgets;

import android.content.Context;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import com.dentacoin.dentacare.R;

/**
 * Created by Atanas Chervarov on 10/17/17.
 */

public class DCDashboardTeeth extends RelativeLayout {

    public enum Quadrant {
        UL(8, 16),
        WL(16, 24),
        WR(24, 32),
        UR(0, 8);

        final int from, to;
        Quadrant(int from, int to) {
            this.from = from;
            this.to = to;
        }

        public int getFrom() { return from; }
        public int getTo() { return to; }
    }

    private DCTooth[] teeth;

    public DCDashboardTeeth(Context context, AttributeSet attr, int style) {
        super(context, attr, style);
        init(attr);
    }

    public DCDashboardTeeth(Context context, AttributeSet attr) {
        super(context, attr);
        init(attr);
    }

    public DCDashboardTeeth(Context context) {
        super(context);
        init(null);
    }

    private void init(AttributeSet attributeSet) {
        inflate(getContext(), R.layout.view_dashboard_teeth, this);

        teeth = new DCTooth[32];
        for (int i = 0; i < 32; i++) {
            teeth[i] = (DCTooth) findViewById(getResources().getIdentifier("iv_t" + (i + 1), "id", getContext().getPackageName()));
        }

        hideAll();
    }

    public void hideAll() {
        for (int i = 0; i < 32; i ++) {
            teeth[i].setVisible(false);
        }
    }

    public void fadeIn(Quadrant qr, int tint) {
        long duration = 200;
        for (int from = qr.getFrom(); from < qr.getTo(); from++) {
            teeth[from].setColorFilter(tint, PorterDuff.Mode.MULTIPLY);
            teeth[from].setVisible(true, duration);
            duration += 100;
        }
    }

    public void fadeOut(Quadrant qr) {
        long duration = 200;
        for (int from = qr.getFrom(); from < qr.getTo(); from++) {
            teeth[from].setVisible(false, duration);
            duration += 100;
        }
    }
}