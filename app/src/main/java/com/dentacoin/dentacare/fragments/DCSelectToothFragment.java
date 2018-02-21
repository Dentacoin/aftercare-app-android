package com.dentacoin.dentacare.fragments;

import android.os.Bundle;
import android.support.v7.widget.AppCompatImageView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.dentacoin.dentacare.R;
import com.dentacoin.dentacare.activities.DCEmergencyActivity;
import com.dentacoin.dentacare.widgets.DCButton;
import com.dentacoin.dentacare.widgets.DCTooth;

/**
 * Created by Atanas Chervarov on 9/3/17.
 */

public class DCSelectToothFragment extends DCFragment implements View.OnClickListener, DCTooth.IDCToothListener {

    private RelativeLayout rlEmergencyTeeth;
    private DCButton btnEmergencyNext;
    private DCTooth[] teeth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance) {
        View view = inflater.inflate(R.layout.fragment_select_tooth, container, false);

        rlEmergencyTeeth = view.findViewById(R.id.rl_emergency_teeth);
        btnEmergencyNext = view.findViewById(R.id.btn_emergency_next);
        btnEmergencyNext.setOnClickListener(this);
        btnEmergencyNext.setEnabled(false);

        teeth = new DCTooth[32];
        for (int i = 0; i < 32; i++) {
            teeth[i] = view.findViewById(getResources().getIdentifier("iv_t" + (i + 1), "id", getActivity().getPackageName()));
            teeth[i].setListener(DCSelectToothFragment.this);
        }

        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_emergency_next:
                if (btnEmergencyNext.isEnabled()) {
                    ((DCEmergencyActivity)getActivity()).takeTeethScreenshot(rlEmergencyTeeth);
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onToothSelected(AppCompatImageView view) {
        updateNextButton();
    }

    @Override
    public void onToothDeselected(AppCompatImageView view) {
        updateNextButton();
    }

    private void updateNextButton() {
        btnEmergencyNext.setEnabled(false);
        for (DCTooth tooth : teeth) {
            if (tooth.isVisible()) {
                btnEmergencyNext.setEnabled(true);
                return;
            }
        }
    }
}
