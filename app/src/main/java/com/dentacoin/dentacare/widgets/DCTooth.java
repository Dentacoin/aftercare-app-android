package com.dentacoin.dentacare.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.view.View;
import android.support.v7.widget.AppCompatImageView;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;

import com.dentacoin.dentacare.R;

/**
 * Created by Atanas Chervarov on 8/31/17.
 */

public class DCTooth extends AppCompatImageView implements View.OnClickListener {

    public interface IDCToothListener {
        void onToothSelected(AppCompatImageView view);
        void onToothDeselected(AppCompatImageView view);
    }

    private boolean visible = false;
    private IDCToothListener listener;

    public  DCTooth(Context context, AttributeSet attr, int style) {
        super(context, attr, style);
        init(attr);
    }

    public DCTooth(Context context, AttributeSet attr) {
        super(context, attr);
        init(attr);
    }

    public DCTooth(Context context) {
        super(context);
        init(null);
    }

    public void setListener(IDCToothListener listener) {
        this.listener = listener;
    }

    private void init(AttributeSet attr) {
        int color = getContext().getResources().getColor(R.color.pinkishRed);
        boolean isClickable = false;

        if (attr != null) {
            TypedArray typedArray = getContext().getTheme().obtainStyledAttributes(attr, R.styleable.DCTooth, 0, 0);

            try {
                color = typedArray.getColor(R.styleable.DCTooth_tintColor, color);
                isClickable = typedArray.getBoolean(R.styleable.DCTooth_isClickable, false);
            } finally {
                typedArray.recycle();
            }
        }

        setColorFilter(color, PorterDuff.Mode.MULTIPLY);
        setVisible(visible);

        if (isClickable) {
            setClickable(true);
            setOnClickListener(this);
        }
    }

    public void setVisible(boolean visible) {
        setVisible(visible, 0);
    }

    public void setVisible(final boolean visible, long duration) {
        this.visible = visible;

        if (duration > 0) {
            setAlpha(1f);
            AlphaAnimation alphaAnimation = new AlphaAnimation(visible ? 0f : 0.9f, visible? 0.9f : 0f);
            alphaAnimation.setDuration(duration);
            alphaAnimation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    setAlpha(visible ? 0.9f : 0f);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }
            });
            this.clearAnimation();
            this.startAnimation(alphaAnimation);
        } else {
            setAlpha(visible ? 0.9f : 0f);
        }
    }

    public boolean isVisible() {
        return visible;
    }

    @Override
    public void onClick(View view) {
        setVisible(!visible);
        if (listener != null) {
            if (visible) {
                listener.onToothSelected(this);
            } else {
                listener.onToothDeselected(this);
            }
        }
    }
}
