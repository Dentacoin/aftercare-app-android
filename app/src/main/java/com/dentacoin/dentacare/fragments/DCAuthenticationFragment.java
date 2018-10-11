package com.dentacoin.dentacare.fragments;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.LifecycleRegistry;
import android.arch.lifecycle.Observer;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.civic.connect.library.ConnectState;
import com.civic.connect.library.model.VerificationLevels;
import com.civic.connect.library.ui.CivicConnectViewModel;
import com.dentacoin.dentacare.R;
import com.dentacoin.dentacare.activities.DCAuthenticationActivity;
import com.dentacoin.dentacare.model.DCError;
import com.dentacoin.dentacare.network.DCSession;
import com.dentacoin.dentacare.widgets.DCButton;
import com.dentacoin.dentacare.widgets.DCTextView;


/**
 * Created by Atanas Chervarov on 7/29/17.
 */

public class DCAuthenticationFragment extends DCFragment implements LifecycleOwner {

    private DCButton btnCivic;
    private CivicConnectViewModel civicViewModel;
    private final LifecycleRegistry lifecycleRegistry = new LifecycleRegistry(this);

    private Observer<ConnectState> storeObserver = (state) -> {
        if (state != null) {
            switch (state) {
                case ERROR:
                case USER_DATA_AVAILABLE:
                case TOKEN_AVAILABLE:
                    hideLoading();
                    break;
            }
        }
    };

    private Observer<String> errorMessageObserver = (error) -> {
        if (error != null && error.length() > 0) {
            onError(new DCError(getString(R.string.error_txt_civic_login, error)));
        }
    };

    private Observer<String> tokenObserver = (token) -> {
        if (getActivity() instanceof DCAuthenticationActivity) {
            if (token != null && !token.isEmpty()) {
                ((DCAuthenticationActivity) getActivity()).loginCivicUser(token);
            }
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        lifecycleRegistry.markState(Lifecycle.State.CREATED);
        civicViewModel = new CivicConnectViewModel();
        civicViewModel.getConnectState().observe(this, storeObserver);
        civicViewModel.getErrorMessage().observe(this, errorMessageObserver);
        civicViewModel.getToken().observe(this, tokenObserver);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance) {
        View view = inflater.inflate(R.layout.fragment_authentication, container, false);

        btnCivic = view.findViewById(R.id.btn_auth_civic);
        btnCivic.setOnClickListener(v -> onCivicLogin());

        view.findViewById(R.id.btn_auth_facebook).setOnClickListener(v -> ((DCAuthenticationActivity) getActivity()).onFacebookLogin());
        view.findViewById(R.id.btn_auth_google).setOnClickListener(v -> ((DCAuthenticationActivity) getActivity()).onGoogleLogin());
        DCTextView tvAuthEmailLogin = view.findViewById(R.id.tv_auth_email_login);
        tvAuthEmailLogin.setOnClickListener(v -> ((DCAuthenticationActivity) getActivity()).showLoginFragment());
        tvAuthEmailLogin.setPaintFlags(tvAuthEmailLogin.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        final String uuid = DCSession.getInstance().getCivicUUID();
        if (uuid != null) {
            civicViewModel.authorise(uuid, false);
            DCSession.getInstance().setCivicUUID(null);
            showLoading();
        }
        lifecycleRegistry.markState(Lifecycle.State.RESUMED);
    }


    @Override
    public void onStart() {
        super.onStart();
        lifecycleRegistry.markState(Lifecycle.State.STARTED);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        lifecycleRegistry.markState(Lifecycle.State.DESTROYED);
    }

    @NonNull
    @Override
    public Lifecycle getLifecycle() {
        return lifecycleRegistry;
    }

    public void onCivicLogin() {
        civicViewModel.connect(getActivity(), VerificationLevels.CIVIC_BASIC);
        showLoading();
    }
}
