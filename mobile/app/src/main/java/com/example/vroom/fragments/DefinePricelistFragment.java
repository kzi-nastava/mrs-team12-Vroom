package com.example.vroom.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.example.vroom.R;
import com.example.vroom.viewmodels.DefinePricelistViewModel;

public class DefinePricelistFragment extends Fragment {

    private DefinePricelistViewModel viewModel;
    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView tvStandardCurrent, tvLuxuryCurrent, tvMinivanCurrent;
    private EditText etStandardNew, etLuxuryNew, etMinivanNew;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_define_pricelist, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);
        setupViewModel();
        setupObservers();
        viewModel.loadPricelist();
    }

    private void initViews(View view) {
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(() -> viewModel.loadPricelist());

        View rowStandard = view.findViewById(R.id.rowStandardCurrent);
        ((TextView) rowStandard.findViewById(R.id.tvLabel)).setText("STANDARD");
        tvStandardCurrent = rowStandard.findViewById(R.id.tvValue);

        View rowLuxury = view.findViewById(R.id.rowLuxuryCurrent);
        ((TextView) rowLuxury.findViewById(R.id.tvLabel)).setText("LUXURY");
        tvLuxuryCurrent = rowLuxury.findViewById(R.id.tvValue);

        View rowMinivan = view.findViewById(R.id.rowMinivanCurrent);
        ((TextView) rowMinivan.findViewById(R.id.tvLabel)).setText("MINIVAN");
        tvMinivanCurrent = rowMinivan.findViewById(R.id.tvValue);

        View inputStandard = view.findViewById(R.id.inputStandard);
        ((TextView) inputStandard.findViewById(R.id.tvInputLabel)).setText("STANDARD");
        etStandardNew = inputStandard.findViewById(R.id.etPriceInput);

        View inputLuxury = view.findViewById(R.id.inputLuxury);
        ((TextView) inputLuxury.findViewById(R.id.tvInputLabel)).setText("LUXURY");
        etLuxuryNew = inputLuxury.findViewById(R.id.etPriceInput);

        View inputMinivan = view.findViewById(R.id.inputMinivan);
        ((TextView) inputMinivan.findViewById(R.id.tvInputLabel)).setText("MINIVAN");
        etMinivanNew = inputMinivan.findViewById(R.id.etPriceInput);

        view.findViewById(R.id.btnSubmit).setOnClickListener(v -> submitData());
    }

    private void setupViewModel() {
        viewModel = new ViewModelProvider(this).get(DefinePricelistViewModel.class);
    }

    private void setupObservers() {
        viewModel.getCurrentPricelist().observe(getViewLifecycleOwner(), data -> {
            swipeRefreshLayout.setRefreshing(false);
            if (data != null) {
                tvStandardCurrent.setText(String.format("%s RSD", data.getPriceStandard()));
                tvLuxuryCurrent.setText(String.format("%s RSD", data.getPriceLuxury()));
                tvMinivanCurrent.setText(String.format("%s RSD", data.getPriceMinivan()));
            }
        });

        viewModel.getUpdateStatus().observe(getViewLifecycleOwner(), success -> {
            if (success == null) return;

            if (success) {
                Toast.makeText(getContext(), "Pricelist updated successfully", Toast.LENGTH_SHORT).show();
                clearInputs();
            } else {
                Toast.makeText(getContext(), "Failed to update pricelist", Toast.LENGTH_SHORT).show();
            }

            viewModel.resetUpdateStatus();
        });
    }

    private void submitData() {
        double s = parseDouble(etStandardNew.getText().toString());
        double l = parseDouble(etLuxuryNew.getText().toString());
        double m = parseDouble(etMinivanNew.getText().toString());
        viewModel.savePricelist(s, l, m);
    }

    private double parseDouble(String val) {
        try {
            return Double.parseDouble(val);
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }

    private void clearInputs() {
        etStandardNew.setText("");
        etLuxuryNew.setText("");
        etMinivanNew.setText("");
    }
}