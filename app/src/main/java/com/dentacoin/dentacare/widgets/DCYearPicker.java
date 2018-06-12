package com.dentacoin.dentacare.widgets;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.NumberPicker;

import com.dentacoin.dentacare.R;
import com.dentacoin.dentacare.fragments.DCDialogFragment;

/**
 * Created by Atanas Chervarov on 11.05.18.
 */
public class DCYearPicker extends DCDialogFragment {

    public interface IYearPickerListener {
        void onYearPicked(int year);
        void onYearCleared();
    }

    private IYearPickerListener listener;

    private static final String KEY_YEAR = "KEY_YEAR";
    private static final String KEY_MAX = "KEY_MAX";
    private static final String KEY_MIN = "KEY_MIN";

    public static DCYearPicker create(int selectedYear, int min, int max, IYearPickerListener listener) {
        DCYearPicker fragment = new DCYearPicker();
        fragment.listener = listener;
        Bundle bundle = new Bundle();
        bundle.putInt(KEY_YEAR, selectedYear);
        bundle.putInt(KEY_MAX, max);
        bundle.putInt(KEY_MIN, min);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        final NumberPicker numberPicker = new NumberPicker(getActivity());
        numberPicker.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);

        if (getArguments() != null) {
            int min = getArguments().getInt(KEY_MIN);
            int max = getArguments().getInt(KEY_MAX);
            int selected = getArguments().getInt(KEY_YEAR);
            numberPicker.setMaxValue(max);
            numberPicker.setMinValue(min);
            numberPicker.setValue(selected);
        }

        numberPicker.setWrapSelectorWheel(false);
        builder.setMessage(R.string.child_hnt_birthyear)
                .setView(numberPicker)
                .setPositiveButton(R.string.txt_ok, (dialog, id) -> {
                    if (listener != null)
                        listener.onYearPicked(numberPicker.getValue());
                })
                .setNegativeButton("Clear", (dialog, id) -> {
                    if (listener != null)
                        listener.onYearCleared();
                });
        builder.setCancelable(true);
        return builder.create();
    }
}
