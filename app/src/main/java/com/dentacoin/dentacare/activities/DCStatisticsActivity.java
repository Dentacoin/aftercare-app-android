package com.dentacoin.dentacare.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.dentacoin.dentacare.R;
import com.dentacoin.dentacare.model.DCRecord;
import com.dentacoin.dentacare.model.DCDashboard;
import com.dentacoin.dentacare.model.DCError;
import com.dentacoin.dentacare.model.DCRoutine;
import com.dentacoin.dentacare.utils.DCConstants;
import com.dentacoin.dentacare.utils.DCDashboardDataProvider;
import com.dentacoin.dentacare.utils.DCUtils;
import com.dentacoin.dentacare.utils.IDCDashboardObserver;
import com.dentacoin.dentacare.widgets.DCButton;
import com.dentacoin.dentacare.widgets.DCTimerView;

/**
 * Created by Atanas Chervarov on 9/30/17.
 */

public class DCStatisticsActivity extends DCToolbarActivity implements View.OnClickListener, IDCDashboardObserver {

    private DCButton btnStatisticsDaily;
    private DCButton btnStatisticsWeekly;
    private DCButton btnStatisticsMonthly;
    private DCDashboard dashboard;

    private DCTimerView tvStatisticsFlossTimes;
    private DCTimerView tvStatisticsFlossLeft;
    private DCTimerView tvStatisticsFlossAverage;

    private DCTimerView tvStatisticsBrushTimes;
    private DCTimerView tvStatisticsBrushLeft;
    private DCTimerView tvStatisticsBrushAverage;

    private DCTimerView tvStatisticsRinseTimes;
    private DCTimerView tvStatisticsRinseLeft;
    private DCTimerView tvStatisticsRinseAverage;

    private DCConstants.DCStatisticsType selectedType = DCConstants.DCStatisticsType.DAILY;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addContentView(R.layout.activity_statistics);
        setActionBarTitle(R.string.statistics_hdl_statistics);

        btnStatisticsDaily = (DCButton) findViewById(R.id.btn_statistics_daily);
        btnStatisticsWeekly = (DCButton) findViewById(R.id.btn_statistics_weekly);
        btnStatisticsMonthly = (DCButton) findViewById(R.id.btn_statistics_monthly);

        btnStatisticsDaily.setOnClickListener(this);
        btnStatisticsWeekly.setOnClickListener(this);
        btnStatisticsMonthly.setOnClickListener(this);


        tvStatisticsFlossTimes = (DCTimerView) findViewById(R.id.tv_statistics_floss_times);
        tvStatisticsFlossLeft = (DCTimerView) findViewById(R.id.tv_statistics_floss_left);
        tvStatisticsFlossAverage = (DCTimerView) findViewById(R.id.tv_statistics_floss_average);

        tvStatisticsBrushTimes = (DCTimerView) findViewById(R.id.tv_statistics_brush_times);
        tvStatisticsBrushLeft = (DCTimerView) findViewById(R.id.tv_statistics_brush_left);
        tvStatisticsBrushAverage = (DCTimerView) findViewById(R.id.tv_statistics_brush_average);

        tvStatisticsRinseTimes = (DCTimerView) findViewById(R.id.tv_statistics_rinse_times);
        tvStatisticsRinseLeft = (DCTimerView) findViewById(R.id.tv_statistics_rinse_left);
        tvStatisticsRinseAverage = (DCTimerView) findViewById(R.id.tv_statistics_rinse_average);


        DCDashboardDataProvider.getInstance().updateDashboard(false);
        DCDashboardDataProvider.getInstance().updateDashboard(true);

        setupView();
    }

    private void setupView() {
        btnStatisticsDaily.setSelected(false);
        btnStatisticsWeekly.setSelected(false);
        btnStatisticsMonthly.setSelected(false);

        switch (selectedType) {
            case DAILY:
                btnStatisticsDaily.setSelected(true);
                break;
            case WEEKLY:
                btnStatisticsWeekly.setSelected(true);
                break;
            default:
                btnStatisticsMonthly.setSelected(true);
                break;
        }

        if (dashboard != null) {
            tvStatisticsFlossTimes.setTimerDisplay(Integer.toString(dashboard.getFlossed().getPeriod(selectedType).getTimes()));
            tvStatisticsFlossTimes.setSecondaryProgress(dashboard.getFlossed().getPeriod(selectedType).getTimesProgress(selectedType));

            tvStatisticsFlossLeft.setTimerDisplay(DCUtils.secondsToTime(dashboard.getFlossed().getPeriod(selectedType).getLeft()));
            tvStatisticsFlossLeft.setSecondaryProgress(dashboard.getFlossed().getPeriod(selectedType).getLeftProgress(selectedType));

            tvStatisticsFlossAverage.setTimerDisplay(DCUtils.secondsToTime(dashboard.getFlossed().getPeriod(selectedType).getAverage()));
            tvStatisticsFlossAverage.setSecondaryProgress(dashboard.getFlossed().getPeriod(selectedType).getAverageProgress(selectedType));

            tvStatisticsBrushTimes.setTimerDisplay(Integer.toString(dashboard.getBrush().getPeriod(selectedType).getTimes()));
            tvStatisticsBrushTimes.setSecondaryProgress(dashboard.getBrush().getPeriod(selectedType).getTimesProgress(selectedType));

            tvStatisticsBrushLeft.setTimerDisplay(DCUtils.secondsToTime(dashboard.getBrush().getPeriod(selectedType).getLeft()));
            tvStatisticsBrushLeft.setSecondaryProgress(dashboard.getBrush().getPeriod(selectedType).getLeftProgress(selectedType));

            tvStatisticsBrushAverage.setTimerDisplay(DCUtils.secondsToTime(dashboard.getBrush().getPeriod(selectedType).getAverage()));
            tvStatisticsBrushAverage.setSecondaryProgress(dashboard.getBrush().getPeriod(selectedType).getAverageProgress(selectedType));

            tvStatisticsRinseTimes.setTimerDisplay(Integer.toString(dashboard.getRinsed().getPeriod(selectedType).getTimes()));
            tvStatisticsRinseTimes.setSecondaryProgress(dashboard.getRinsed().getPeriod(selectedType).getTimesProgress(selectedType));

            tvStatisticsRinseLeft.setTimerDisplay(DCUtils.secondsToTime(dashboard.getRinsed().getPeriod(selectedType).getLeft()));
            tvStatisticsRinseLeft.setSecondaryProgress(dashboard.getRinsed().getPeriod(selectedType).getLeftProgress(selectedType));

            tvStatisticsRinseAverage.setTimerDisplay(DCUtils.secondsToTime(dashboard.getRinsed().getPeriod(selectedType).getAverage()));
            tvStatisticsRinseAverage.setSecondaryProgress(dashboard.getRinsed().getPeriod(selectedType).getAverageProgress(selectedType));
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_statistics_daily:
                toggleStatistics(DCConstants.DCStatisticsType.DAILY);
                break;
            case R.id.btn_statistics_weekly:
                toggleStatistics(DCConstants.DCStatisticsType.WEEKLY);
                break;
            case R.id.btn_statistics_monthly:
                toggleStatistics(DCConstants.DCStatisticsType.MONTHLY);
                break;
        }
    }

    private void toggleStatistics(DCConstants.DCStatisticsType type) {
        selectedType = type;
        setupView();
    }

    @Override
    public void onResume() {
        super.onResume();
        DCDashboardDataProvider.getInstance().addObserver(this);
    }

    @Override
    public void onPause() {
        DCDashboardDataProvider.getInstance().removeObserver(this);
        super.onPause();
    }

    @Override
    public void onDashboardUpdated(DCDashboard dashboard) {
        this.dashboard = dashboard;
        setupView();
    }

    @Override
    public void onDashboardError(DCError error) {
        onError(error);
    }

    @Override
    public void onSyncNeeded(DCRoutine[] routines) {
    }

    @Override
    public void onSyncSuccess() {
    }
}
