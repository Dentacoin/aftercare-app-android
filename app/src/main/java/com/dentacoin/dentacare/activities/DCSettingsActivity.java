package com.dentacoin.dentacare.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.dentacoin.dentacare.R;
import com.dentacoin.dentacare.widgets.DCSoundManager;
import com.dentacoin.dentacare.widgets.DCTextView;

/**
 * Created by Atanas Chervarov on 9/19/17.
 */

public class DCSettingsActivity extends DCToolbarActivity {

    private Switch swSettingsSounds;
    private Switch swSettingsVoice;
    private Switch swSettingsMusic;
    private DCTextView tvSettingsVoice;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addContentView(R.layout.activity_settings);
        setActionBarTitle(R.string.settings_hdl_settings);
        tvSettingsVoice = (DCTextView) findViewById(R.id.tv_settings_voice);
        swSettingsSounds = (Switch) findViewById(R.id.sw_settings_sounds);
        swSettingsSounds.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                DCSoundManager.getInstance().setSoundEnabled(isChecked);
            }
        });

        swSettingsSounds.setChecked(DCSoundManager.getInstance().isSoundEnabled());

        swSettingsMusic = (Switch) findViewById(R.id.sw_settings_music);
        swSettingsMusic.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                DCSoundManager.getInstance().setMusicEnabled(isChecked);
            }
        });

        swSettingsMusic.setChecked(DCSoundManager.getInstance().isMusicEnabled());

        swSettingsVoice = (Switch) findViewById(R.id.sw_settings_voice);
        swSettingsVoice.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    tvSettingsVoice.setText(getString(R.string.settings_lbl_voice_female));
                } else {
                    tvSettingsVoice.setText(getString(R.string.settings_lbl_voice_male));
                }

                DCSoundManager.getInstance().setIsFemale(isChecked);
            }
        });

        swSettingsVoice.setChecked(DCSoundManager.getInstance().isVoiceFemale());
    }
}
