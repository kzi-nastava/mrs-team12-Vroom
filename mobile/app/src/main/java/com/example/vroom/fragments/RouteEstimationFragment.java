package com.example.vroom.fragments;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.vroom.R;
import com.example.vroom.viewmodels.RouteEstimationViewModel;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class RouteEstimationFragment extends BottomSheetDialogFragment {

    private RouteEstimationViewModel mViewModel;
    private LinearLayout stopsContainer;
    private int stopCount = 0;

    public static RouteEstimationFragment newInstance() {
        return new RouteEstimationFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_route_estimation, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mViewModel = new ViewModelProvider(this).get(RouteEstimationViewModel.class);

        stopsContainer = view.findViewById(R.id.stops_container);
        View btnAddStop = view.findViewById(R.id.btn_add_stop);
        View btnCalculate = view.findViewById(R.id.btn_calculate);

        EditText inputStart = view.findViewById(R.id.input_start);
        EditText inputEnd = view.findViewById(R.id.input_end);

        btnAddStop.setOnClickListener(v -> addStopField());

        btnCalculate.setOnClickListener(v -> {
            performCalculation();
        });
    }

    private void addStopField() {
        stopCount++;

        View stopView = LayoutInflater.from(getContext()).inflate(R.layout.item_stop, stopsContainer, false);

        TextView labelStop = stopView.findViewById(R.id.label_stop);
        labelStop.setText("Stop " + stopCount);

        View btnRemove = stopView.findViewById(R.id.btn_remove_stop);
        btnRemove.setOnClickListener(v -> {
            stopsContainer.removeView(stopView);
            reorderStops();
        });

        stopsContainer.addView(stopView);
    }

    private void reorderStops() {
        stopCount = stopsContainer.getChildCount();
        for (int i = 0; i < stopCount; i++) {
            View child = stopsContainer.getChildAt(i);
            TextView label = child.findViewById(R.id.label_stop);
            if (label != null) {
                label.setText("Stop " + (i + 1));
            }
        }
    }

    private void performCalculation() {
        
    }

}