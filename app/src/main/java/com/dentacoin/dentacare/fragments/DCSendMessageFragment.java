package com.dentacoin.dentacare.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.dentacoin.dentacare.R;
import com.dentacoin.dentacare.activities.DCEmergencyActivity;
import com.dentacoin.dentacare.widgets.DCButton;
import com.dentacoin.dentacare.widgets.DCTextInputEditText;

import de.mateware.snacky.Snacky;

/**
 * Created by Atanas Chervarov on 9/3/17.
 */

public class DCSendMessageFragment extends DCFragment implements View.OnClickListener {

    private EditText etEmergencyMessage;
    private DCButton btnEmergencySend;
    private DCTextInputEditText tietEmergencyPhone;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance) {
        View view = inflater.inflate(R.layout.fragment_send_message, container, false);
        etEmergencyMessage = view.findViewById(R.id.et_emergency_message);
        tietEmergencyPhone = view.findViewById(R.id.tiet_emergency_phone);
        btnEmergencySend = view.findViewById(R.id.btn_emergency_send);
        btnEmergencySend.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_emergency_send:
                if (etEmergencyMessage.getText().length() < 10) {
                    Snacky.builder().setActivty(getActivity()).error().setText(R.string.error_txt_message_too_short).show();
                } else {
                    ((DCEmergencyActivity) getActivity()).sendEmail(etEmergencyMessage.getText().toString(), tietEmergencyPhone.getText().toString());
                }
                break;
        }
    }
}
