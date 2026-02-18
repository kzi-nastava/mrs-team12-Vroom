package com.example.vroom.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vroom.DTOs.ride.requests.DailyRideReportDTO;
import com.example.vroom.DTOs.ride.requests.RideReportDTO;
import com.example.vroom.R;

import com.example.vroom.adapters.DailyStatsAdapter;
import com.example.vroom.data.local.StorageManager;
import com.example.vroom.viewmodels.RideStatisticsViewModel;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.util.ArrayList;
import java.util.List;

public class RideStatisticsFragment extends Fragment {

    private RideStatisticsViewModel viewModel;
    private DailyStatsAdapter adapter;

    private EditText inputFromDate, inputToDate, inputUserId, inputDriverId;
    private LinearLayout adminSection, reportSection;
    private Button btnFetchReport, btnFetchAllUsers, btnFetchAllDrivers;
    private TextView tvTotalRides, tvTotalMoney, tvTotalKm;
    private TextView tvAvgRides, tvAvgMoney, tvAvgKm;
    private RecyclerView recyclerDailyStats;
    private BarChart chartRides, chartMoney, chartDistance;

    private String userRole = "REGISTERED_USER";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_ride_statistics, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(RideStatisticsViewModel.class);

        initViews(view);
        setupRecyclerView();
        setupUserRole();
        observeViewModel();

        btnFetchReport.setOnClickListener(v -> fetchReport());
        btnFetchAllUsers.setOnClickListener(v -> fetchAllUsersReport());
        btnFetchAllDrivers.setOnClickListener(v -> fetchAllDriversReport());
    }

    private void initViews(View view) {
        inputFromDate = view.findViewById(R.id.input_from_date);
        inputToDate = view.findViewById(R.id.input_to_date);
        inputUserId = view.findViewById(R.id.input_user_id);
        inputDriverId = view.findViewById(R.id.input_driver_id);
        adminSection = view.findViewById(R.id.admin_section);
        reportSection = view.findViewById(R.id.report_section);
        btnFetchReport = view.findViewById(R.id.btn_fetch_report);
        btnFetchAllUsers = view.findViewById(R.id.btn_fetch_all_users);
        btnFetchAllDrivers = view.findViewById(R.id.btn_fetch_all_drivers);
        tvTotalRides = view.findViewById(R.id.tv_total_rides);
        tvTotalMoney = view.findViewById(R.id.tv_total_money);
        tvTotalKm = view.findViewById(R.id.tv_total_km);
        tvAvgRides = view.findViewById(R.id.tv_avg_rides);
        tvAvgMoney = view.findViewById(R.id.tv_avg_money);
        tvAvgKm = view.findViewById(R.id.tv_avg_km);
        recyclerDailyStats = view.findViewById(R.id.recycler_daily_stats);
        chartRides = view.findViewById(R.id.chart_rides);
        chartMoney = view.findViewById(R.id.chart_money);
        chartDistance = view.findViewById(R.id.chart_distance);
    }

    private void setupRecyclerView() {
        adapter = new DailyStatsAdapter(new ArrayList<>());
        recyclerDailyStats.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerDailyStats.setAdapter(adapter);
    }

    private void setupUserRole() {
        // TODO: Get actual user role from SharedPreferences or ViewModel
        userRole = StorageManager.getData("user_type", null);

        if ("ADMIN".equals(userRole)) {
            adminSection.setVisibility(View.VISIBLE);
        } else {
            adminSection.setVisibility(View.GONE);
        }
    }

    private void observeViewModel() {
        viewModel.getReport().observe(getViewLifecycleOwner(), this::displayReport);

        viewModel.getError().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                Toast.makeText(getContext(), error, Toast.LENGTH_LONG).show();
            }
        });

        viewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            btnFetchReport.setEnabled(!isLoading);
            if (adminSection.getVisibility() == View.VISIBLE) {
                btnFetchAllUsers.setEnabled(!isLoading);
                btnFetchAllDrivers.setEnabled(!isLoading);
            }
        });
    }

    private void fetchReport() {
        String from = inputFromDate.getText().toString().trim();
        String to = inputToDate.getText().toString().trim();

        if ("ADMIN".equals(userRole)) {
            String userIdStr = inputUserId.getText().toString().trim();
            String driverIdStr = inputDriverId.getText().toString().trim();

            if (!userIdStr.isEmpty()) {
                Long userId = Long.parseLong(userIdStr);
                viewModel.fetchAdminUserReport(userId, from.isEmpty() ? null : from, to.isEmpty() ? null : to);
            } else if (!driverIdStr.isEmpty()) {
                Long driverId = Long.parseLong(driverIdStr);
                viewModel.fetchAdminDriverReport(driverId, from.isEmpty() ? null : from, to.isEmpty() ? null : to);
            } else {
                viewModel.fetchAdminReport(from.isEmpty() ? null : from, to.isEmpty() ? null : to);
            }
        } else if ("DRIVER".equals(userRole)) {
            viewModel.fetchMyDriverReport(from.isEmpty() ? null : from, to.isEmpty() ? null : to);
        } else {
            viewModel.fetchMyReport(from.isEmpty() ? null : from, to.isEmpty() ? null : to);
        }
    }

    private void fetchAllUsersReport() {
        String from = inputFromDate.getText().toString().trim();
        String to = inputToDate.getText().toString().trim();
        viewModel.fetchAdminAllUsersReport(from.isEmpty() ? null : from, to.isEmpty() ? null : to);
    }

    private void fetchAllDriversReport() {
        String from = inputFromDate.getText().toString().trim();
        String to = inputToDate.getText().toString().trim();
        viewModel.fetchAdminAllDriversReport(from.isEmpty() ? null : from, to.isEmpty() ? null : to);
    }

    private void displayReport(RideReportDTO report) {
        if (report == null) return;

        reportSection.setVisibility(View.VISIBLE);

        // Summary
        tvTotalRides.setText("Total Rides: " + report.getTotalRides());
        tvTotalMoney.setText(String.format("Total Money: $%.2f", report.getTotalMoney()));
        tvTotalKm.setText(String.format("Total Distance: %.2f km", report.getTotalKilometers()));

        // Averages
        int days = report.getDaily() != null ? report.getDaily().size() : 1;
        double avgRides = report.getTotalRides() / (double) days;
        double avgMoney = report.getTotalMoney() / days;
        double avgKm = report.getTotalKilometers() / days;

        tvAvgRides.setText(String.format("Avg Rides / Day: %.2f", avgRides));
        tvAvgMoney.setText(String.format("Avg Money / Day: $%.2f", avgMoney));
        tvAvgKm.setText(String.format("Avg Distance / Day: %.2f km", avgKm));

        // Daily stats table
        if (report.getDaily() != null) {
            adapter.updateStats(report.getDaily());
        }

        // Charts
        setupCharts(report.getDaily());
    }

    private void setupCharts(List<DailyRideReportDTO> daily) {
        if (daily == null || daily.isEmpty()) return;

        List<BarEntry> ridesEntries = new ArrayList<>();
        List<BarEntry> moneyEntries = new ArrayList<>();
        List<BarEntry> distanceEntries = new ArrayList<>();
        List<String> labels = new ArrayList<>();

        for (int i = 0; i < daily.size(); i++) {
            DailyRideReportDTO stat = daily.get(i);
            ridesEntries.add(new BarEntry(i, stat.getRideCount()));
            moneyEntries.add(new BarEntry(i, (float) stat.getMoney()));
            distanceEntries.add(new BarEntry(i, (float) stat.getKm()));
            labels.add(stat.getDate().toString());
        }

        setupBarChart(chartRides, ridesEntries, labels, "Rides", Color.rgb(76, 175, 80));
        setupBarChart(chartMoney, moneyEntries, labels, "Money", Color.rgb(33, 150, 243));
        setupBarChart(chartDistance, distanceEntries, labels, "Distance", Color.rgb(255, 152, 0));
    }

    private void setupBarChart(BarChart chart, List<BarEntry> entries, List<String> labels, String label, int color) {
        BarDataSet dataSet = new BarDataSet(entries, label);
        dataSet.setColor(color);
        dataSet.setValueTextSize(10f);

        BarData barData = new BarData(dataSet);
        chart.setData(barData);

        XAxis xAxis = chart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setLabelRotationAngle(-45);

        chart.getDescription().setEnabled(false);
        chart.getLegend().setEnabled(false);
        chart.animateY(1000);
        chart.invalidate();
    }
}