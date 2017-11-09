package com.dentacoin.dentacare.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationManagerCompat;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;

import com.dentacoin.dentacare.R;
import com.dentacoin.dentacare.utils.DCLocalNotificationsManager;
import com.dentacoin.dentacare.utils.DCSharedPreferences;
import com.dentacoin.dentacare.widgets.DCSoundManager;
import com.dentacoin.dentacare.widgets.DCTextView;

/**
 * Created by Atanas Chervarov on 9/19/17.
 */

public class DCSettingsActivity extends DCToolbarActivity implements CompoundButton.OnCheckedChangeListener {

    private static final int REQUEST_CODE_NOTIFICATIONS = 1001;

    private Switch swSettingsSounds;
    private Switch swSettingsVoice;
    private Switch swSettingsMusic;

    private Switch swSettingsDailyBrushing;
    private Switch swSettingsChangeBrush;
    private Switch swSettingsVisitDentist;
    private Switch swSettingsReminderToVisit;
    private Switch swSettingsHealthyHabits;
    private Switch swSettingsEnableNotifications;
    private LinearLayout llSettingsEnableNotifications;
    private LinearLayout llSettingsNotifications;

    private DCTextView tvSettingsVoice;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addContentView(R.layout.activity_settings);
        setActionBarTitle(R.string.settings_hdl_settings);

        tvSettingsVoice = (DCTextView) findViewById(R.id.tv_settings_voice);
        swSettingsSounds = (Switch) findViewById(R.id.sw_settings_sounds);
        swSettingsMusic = (Switch) findViewById(R.id.sw_settings_music);
        swSettingsVoice = (Switch) findViewById(R.id.sw_settings_voice);
        swSettingsDailyBrushing = (Switch) findViewById(R.id.sw_settings_daily_brushing);
        swSettingsChangeBrush = (Switch) findViewById(R.id.sw_settings_change_brush);
        swSettingsVisitDentist = (Switch) findViewById(R.id.sw_settings_visit_dentist);
        swSettingsReminderToVisit = (Switch) findViewById(R.id.sw_settings_reminder_to_visit);
        swSettingsHealthyHabits = (Switch) findViewById(R.id.sw_settings_healthy_habits);
        swSettingsEnableNotifications = (Switch) findViewById(R.id.sw_settings_enable_notifications);
        llSettingsEnableNotifications = (LinearLayout) findViewById(R.id.ll_settings_enable_notifications);
        llSettingsNotifications = (LinearLayout) findViewById(R.id.ll_settings_notifications);

        setupUI();

        swSettingsSounds.setOnCheckedChangeListener(this);
        swSettingsMusic.setOnCheckedChangeListener(this);
        swSettingsVoice.setOnCheckedChangeListener(this);
        swSettingsDailyBrushing.setOnCheckedChangeListener(this);
        swSettingsChangeBrush.setOnCheckedChangeListener(this);
        swSettingsVisitDentist.setOnCheckedChangeListener(this);
        swSettingsReminderToVisit.setOnCheckedChangeListener(this);
        swSettingsHealthyHabits.setOnCheckedChangeListener(this);
        swSettingsEnableNotifications.setOnCheckedChangeListener(this);
    }

    private void setupUI() {
        if (NotificationManagerCompat.from(this).areNotificationsEnabled()) {
            llSettingsEnableNotifications.setVisibility(View.GONE);
            llSettingsNotifications.setVisibility(View.VISIBLE);
        } else {
            llSettingsEnableNotifications.setVisibility(View.VISIBLE);
            llSettingsNotifications.setVisibility(View.GONE);
        }

        swSettingsSounds.setChecked(DCSoundManager.getInstance().isSoundEnabled());
        swSettingsMusic.setChecked(DCSoundManager.getInstance().isMusicEnabled());
        swSettingsVoice.setChecked(DCSoundManager.getInstance().isVoiceFemale());
        swSettingsDailyBrushing.setChecked(!DCSharedPreferences.getBoolean(DCSharedPreferences.DCSharedKey.DAILY_BRUSHING, false));
        swSettingsChangeBrush.setChecked(!DCSharedPreferences.getBoolean(DCSharedPreferences.DCSharedKey.CHANGE_BRUSH, false));
        swSettingsVisitDentist.setChecked(!DCSharedPreferences.getBoolean(DCSharedPreferences.DCSharedKey.VISIT_DENTIST, false));
        swSettingsReminderToVisit.setChecked(!DCSharedPreferences.getBoolean(DCSharedPreferences.DCSharedKey.REMINDER_TO_VISIT, false));
        swSettingsHealthyHabits.setChecked(!DCSharedPreferences.getBoolean(DCSharedPreferences.DCSharedKey.HEALTHY_HABIT, false));
        swSettingsEnableNotifications.setChecked(false);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.sw_settings_sounds:
                DCSoundManager.getInstance().setSoundEnabled(isChecked);
                break;
            case R.id.sw_settings_music:
                DCSoundManager.getInstance().setMusicEnabled(isChecked);
                break;
            case R.id.sw_settings_voice:
                if (isChecked) {
                    tvSettingsVoice.setText(getString(R.string.settings_lbl_voice_female));
                } else {
                    tvSettingsVoice.setText(getString(R.string.settings_lbl_voice_male));
                }
                DCSoundManager.getInstance().setIsFemale(isChecked);
                break;
            case R.id.sw_settings_daily_brushing:
                DCSharedPreferences.saveBoolean(DCSharedPreferences.DCSharedKey.DAILY_BRUSHING, !isChecked);
                DCLocalNotificationsManager.getInstance().scheduleNotifications(this, false);
                break;
            case R.id.sw_settings_change_brush:
                DCSharedPreferences.saveBoolean(DCSharedPreferences.DCSharedKey.CHANGE_BRUSH, !isChecked);
                DCLocalNotificationsManager.getInstance().scheduleNotifications(this, false);
                break;
            case R.id.sw_settings_visit_dentist:
                DCSharedPreferences.saveBoolean(DCSharedPreferences.DCSharedKey.VISIT_DENTIST, !isChecked);
                DCLocalNotificationsManager.getInstance().scheduleNotifications(this, false);
                break;
            case R.id.sw_settings_reminder_to_visit:
                DCSharedPreferences.saveBoolean(DCSharedPreferences.DCSharedKey.REMINDER_TO_VISIT, !isChecked);
                DCLocalNotificationsManager.getInstance().scheduleNotifications(this, false);
                break;
            case R.id.sw_settings_healthy_habits:
                DCSharedPreferences.saveBoolean(DCSharedPreferences.DCSharedKey.HEALTHY_HABIT, !isChecked);
                DCLocalNotificationsManager.getInstance().scheduleNotifications(this, false);
                break;
            case R.id.sw_settings_enable_notifications:
                if (isChecked) {
                    Intent intent = new Intent();
                    if (android.os.Build.VERSION.SDK_INT > Build.VERSION_CODES.N_MR1) {
                        intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
                        intent.putExtra("android.provider.extra.APP_PACKAGE", getPackageName());
                    } else {
                        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        intent.setData(Uri.parse("package:" + getPackageName()));
                    }
                    startActivityForResult(intent, REQUEST_CODE_NOTIFICATIONS);
                }
                break;

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_NOTIFICATIONS) {
            setupUI();
        }
    }
}
