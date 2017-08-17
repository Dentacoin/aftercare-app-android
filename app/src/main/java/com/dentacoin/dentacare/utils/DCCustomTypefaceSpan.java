package com.dentacoin.dentacare.utils;

import android.graphics.Typeface;
import android.text.TextPaint;
import android.text.style.MetricAffectingSpan;

/**
 * Created by Atanas Chervarov on 8/13/17.
 */

public class DCCustomTypefaceSpan extends MetricAffectingSpan {

    private final Typeface typeface;

    public DCCustomTypefaceSpan(Typeface typeface) {
        super();
        this.typeface = typeface;
    }

    @Override
    public void updateDrawState(TextPaint paint) {
        applyCustomTypeFace(paint);
    }

    @Override
    public void updateMeasureState(TextPaint paint) {
        applyCustomTypeFace(paint);
    }

    private void applyCustomTypeFace(TextPaint paint) {
        paint.setTypeface(typeface);
    }
}
