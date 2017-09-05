package com.dentacoin.dentacare.widgets;

import android.content.Context;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

import com.dentacoin.dentacare.R;

/**
 * Created by Atanas Chervarov on 8/31/17.
 */

public class DCTooth extends ImageView implements View.OnClickListener {

    public interface IDCToothListener {
        void onToothSelected(ImageView view);
        void onToothDeselected(ImageView view);
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
        setColorFilter(getContext().getResources().getColor(R.color.pinkishRed), PorterDuff.Mode.MULTIPLY);
        setVisible(visible);
        setClickable(true);
        setOnClickListener(this);
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
        setAlpha(visible ? 1.0f : 0f);
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
