package com.dentacoin.dentacare.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dentacoin.dentacare.R;
import com.dentacoin.dentacare.model.DCDashboard;
import com.dentacoin.dentacare.model.DCError;
import com.dentacoin.dentacare.model.DCFriend;
import com.dentacoin.dentacare.network.DCApiManager;
import com.dentacoin.dentacare.network.DCResponseListener;
import com.dentacoin.dentacare.utils.DCConstants;
import com.dentacoin.dentacare.utils.DCUtils;
import com.dentacoin.dentacare.widgets.DCButton;
import com.dentacoin.dentacare.widgets.DCTimerView;

/**
 * Created by Atanas Chervarov on 22.05.18.
 */
public class DCFriendStatisticsFragment extends DCFragment implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {

    private static final String KEY_FRIEND = "KEY_FRIEND";

    private SwipeRefreshLayout srlStatistics;

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

    private DCFriend friend;

    public static Fragment create(DCFriend friend) {
        DCFriendStatisticsFragment fragment = new DCFriendStatisticsFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(KEY_FRIEND, friend);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance) {
        View view = inflater.inflate(R.layout.fragment_friend_statistics, container, false);
        if (getArguments() != null && getArguments().getSerializable(KEY_FRIEND) instanceof DCFriend) {
            friend = (DCFriend) getArguments().getSerializable(KEY_FRIEND);
        }

        srlStatistics = view.findViewById(R.id.srl_statistics);
        srlStatistics.setOnRefreshListener(this);

        btnStatisticsDaily = view.findViewById(R.id.btn_statistics_daily);
        btnStatisticsWeekly = view.findViewById(R.id.btn_statistics_weekly);
        btnStatisticsMonthly = view.findViewById(R.id.btn_statistics_monthly);

        btnStatisticsDaily.setOnClickListener(this);
        btnStatisticsWeekly.setOnClickListener(this);
        btnStatisticsMonthly.setOnClickListener(this);

        tvStatisticsFlossTimes = view.findViewById(R.id.tv_statistics_floss_times);
        tvStatisticsFlossLeft = view.findViewById(R.id.tv_statistics_floss_left);
        tvStatisticsFlossAverage = view.findViewById(R.id.tv_statistics_floss_average);

        tvStatisticsBrushTimes = view.findViewById(R.id.tv_statistics_brush_times);
        tvStatisticsBrushLeft = view.findViewById(R.id.tv_statistics_brush_left);
        tvStatisticsBrushAverage = view.findViewById(R.id.tv_statistics_brush_average);

        tvStatisticsRinseTimes = view.findViewById(R.id.tv_statistics_rinse_times);
        tvStatisticsRinseLeft = view.findViewById(R.id.tv_statistics_rinse_left);
        tvStatisticsRinseAverage = view.findViewById(R.id.tv_statistics_rinse_average);

        setupView();
        loadStatistics();
        return view;
    }

    @Override
    public void onRefresh() {
        loadStatistics();
    }

    private void loadStatistics() {
        DCApiManager.getInstance().getFriendStatistics(friend.getId(), new DCResponseListener<DCDashboard>() {
            @Override
            public void onFailure(DCError error) {
                srlStatistics.setRefreshing(false);
                onError(error);
            }

            @Override
            public void onResponse(DCDashboard object) {
                srlStatistics.setRefreshing(false);
                DCFriendStatisticsFragment.this.dashboard = object;
                setupView();
            }
        });
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
                selectedType = DCConstants.DCStatisticsType.DAILY;
                break;
            case R.id.btn_statistics_weekly:
                selectedType = DCConstants.DCStatisticsType.WEEKLY;
                break;
            case R.id.btn_statistics_monthly:
                selectedType = DCConstants.DCStatisticsType.MONTHLY;
                break;
        }
        setupView();
    }
}
