package com.dentacoin.dentacare.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dentacoin.dentacare.R;
import com.dentacoin.dentacare.widgets.DCButton;

import de.mateware.snacky.Snacky;

/**
 * Created by Atanas Chervarov on 9/10/17.
 */

public class DCCollectWalletFragment extends DCFragment implements View.OnClickListener {

    private DCButton btnCollectSend;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance) {
        View view = inflater.inflate(R.layout.fragment_collect_wallet, container, false);
        btnCollectSend = (DCButton) view.findViewById(R.id.collect_btn_send);
        btnCollectSend.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.collect_btn_send:
                Snacky.builder().setActivty(getActivity()).warning().setText(R.string.error_not_implemented).show();
                break;
        }
    }
}
