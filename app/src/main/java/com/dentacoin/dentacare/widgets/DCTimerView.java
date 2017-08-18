package com.dentacoin.dentacare.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.dentacoin.dentacare.R;
import com.dentacoin.dentacare.utils.DCFonts;

/**
 * Created by Atanas Chervarov on 8/18/17.
 */

public class DCTimerView extends RelativeLayout {

    public enum DCTimerType {
        BIG_PROGRESS,
        SMALL_PROGRESS,
        SMALL_INFO
    }

    private ProgressBar pbTimerProgress;
    private DCTextView tvTimerDisplay;
    private DCTextView tvTimerTitle;

    public DCTimerView(Context context, AttributeSet attr, int style) {
        super(context, attr, style);
        init(attr);
    }

    public DCTimerView(Context context, AttributeSet attr) {
        super(context, attr);
        init(attr);
    }

    public DCTimerView(Context context) {
        super(context);
        init(null);
    }

    private void init(AttributeSet attributeSet) {
        inflate(getContext(), R.layout.view_timer, this);

        pbTimerProgress = (ProgressBar) findViewById(R.id.pb_timer_progress);
        tvTimerDisplay = (DCTextView) findViewById(R.id.tv_timer_display);
        tvTimerTitle = (DCTextView) findViewById(R.id.tv_timer_title);
        tvTimerTitle.setVisibility(GONE);

        if (attributeSet != null) {
            TypedArray typedArray = getContext().getTheme().obtainStyledAttributes(attributeSet, R.styleable.DCTimerView, 0, 0);
            try {
                switch (typedArray.getInt(R.styleable.DCTimerView_type, -1)) {
                    case 0:
                        setupTimer(DCTimerType.BIG_PROGRESS);
                        break;
                    case 1:
                        setupTimer(DCTimerType.SMALL_PROGRESS);
                        break;
                    case 2:
                        setupTimer(DCTimerType.SMALL_INFO);
                        break;
                    default:
                        setupTimer(DCTimerType.BIG_PROGRESS);
                        break;
                }

                tvTimerDisplay.setText(typedArray.getString(R.styleable.DCTimerView_text));
                tvTimerTitle.setText(typedArray.getString(R.styleable.DCTimerView_title));

            } finally {
                typedArray.recycle();
            }
        } else {
            setupTimer(DCTimerType.BIG_PROGRESS);
        }
    }

    public void setProgress(int progress) {
        pbTimerProgress.setProgress(progress);
    }

    public void setTitle(String title) {
        tvTimerTitle.setText(title);
    }

    public void setTimerDisplay(String timerDisplay) {
        tvTimerDisplay.setText(timerDisplay);
    }

    private void setupTimer(DCTimerType type) {
        switch (type) {
            case BIG_PROGRESS:
                pbTimerProgress.setLayoutParams(new RelativeLayout.LayoutParams((int) getResources().getDimension(R.dimen.timer_size_big), (int) getResources().getDimension(R.dimen.timer_size_big)));
                pbTimerProgress.setProgressDrawable(getResources().getDrawable(R.drawable.circular_big_progressbar_background));
                tvTimerDisplay.setTextColor(getResources().getColor(R.color.ceruleanBlue));
                tvTimerDisplay.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.timer_text_size_big));
                tvTimerDisplay.setTypeface(DCFonts.getFont(getContext(), DCFonts.FONT_LATO_LIGHT));
                break;
            case SMALL_PROGRESS:
                pbTimerProgress.setLayoutParams(new RelativeLayout.LayoutParams((int) getResources().getDimension(R.dimen.timer_size_small_progress), (int) getResources().getDimension(R.dimen.timer_size_small_progress)));
                pbTimerProgress.setProgressDrawable(getResources().getDrawable(R.drawable.circular_small_progressbar_progress_background));
                tvTimerTitle.setVisibility(VISIBLE);
                tvTimerTitle.setTextColor(getResources().getColor(R.color.white));
                tvTimerDisplay.setTextColor(getResources().getColor(R.color.white));
                tvTimerDisplay.setTypeface(DCFonts.getFont(getContext(), DCFonts.FONT_LATO_LIGHT));
                tvTimerDisplay.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.timer_text_size_small_progress));
                break;
            case SMALL_INFO:
                pbTimerProgress.setLayoutParams(new RelativeLayout.LayoutParams((int) getResources().getDimension(R.dimen.timer_size_small_info), (int) getResources().getDimension(R.dimen.timer_size_small_info)));
                pbTimerProgress.setProgressDrawable(getResources().getDrawable(R.drawable.circular_small_progressbar_info_background));
                tvTimerTitle.setVisibility(VISIBLE);
                tvTimerTitle.setTextColor(getResources().getColor(R.color.ceruleanBlue));
                tvTimerDisplay.setTextColor(getResources().getColor(R.color.ceruleanBlue));
                tvTimerDisplay.setTypeface(DCFonts.getFont(getContext(), DCFonts.FONT_LATO_REGULAR));
                tvTimerDisplay.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.timer_text_size_small_info));
                pbTimerProgress.setSecondaryProgress(1000);
                break;
            default:
                break;
        }
    }
}
