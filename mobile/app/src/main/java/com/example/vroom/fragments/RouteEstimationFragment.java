package com.example.vroom.fragments;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vroom.R;
import com.example.vroom.adapters.SuggestionAdapter;
import com.example.vroom.viewmodels.RouteEstimationViewModel;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RouteEstimationFragment extends BottomSheetDialogFragment {

    private RouteEstimationViewModel routeEstVM;
    private LinearLayout stopsContainer;
    private int stopCount = 0;
    private final Handler searchHandler = new Handler(Looper.getMainLooper());
    private Runnable searchRunnable;
    EditText startInput;
    EditText endInput;
    private final Map<Integer, RecyclerView> inputToRecyclerMap = new HashMap<>();
    private final Map<Integer, EditText> inputIdEditTextMap = new HashMap<>();

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

        routeEstVM = new ViewModelProvider(requireActivity()).get(RouteEstimationViewModel.class);

        stopsContainer = view.findViewById(R.id.stops_container);

        View btnAddStop = view.findViewById(R.id.btn_add_stop);
        View btnCalculate = view.findViewById(R.id.btn_calculate);

        startInput = view.findViewById(R.id.input_start);
        endInput = view.findViewById(R.id.input_end);

        btnAddStop.setOnClickListener(v -> {
            hideAllSuggestions();
            addStopField();
        });
        btnCalculate.setOnClickListener(v -> {
            hideAllSuggestions();
            performCalculation();
        });

        setupSuggestions(
                view.findViewById(R.id.input_start),
                view.findViewById(R.id.suggestions_start)
        );
        setupSuggestions(
                view.findViewById(R.id.input_end),
                view.findViewById(R.id.suggestions_end)
        );


        observeQuote(view);
        observeSuggestions();
        setupOutsideTouchCloseSuggestions(view);
    }

    private void observeQuote(View view) {
        routeEstVM.getRouteQuote().observe(getViewLifecycleOwner(), quote -> {
            if (quote != null) {
                view.findViewById(R.id.loader_container).setVisibility(View.GONE);

                View infoBox = view.findViewById(R.id.estimation_info);
                infoBox.setVisibility(View.VISIBLE);

                TextView timeTxt = view.findViewById(R.id.text_est_time);
                TextView priceTxt = view.findViewById(R.id.text_est_price);

                timeTxt.setText(String.format("%.2f min", quote.getTime()));
                priceTxt.setText(String.format("%.2f RSD", quote.getPrice()));
            }
        });
    }

    private void hideAllSuggestions() {
        for (RecyclerView recycler : inputToRecyclerMap.values()) {
            recycler.setVisibility(View.GONE);
        }
    }

    private boolean isTouchInsideView(android.view.MotionEvent event, View view) {
        if (view == null || view.getVisibility() != View.VISIBLE)
            return false;

        int[] loc = new int[2];
        view.getLocationOnScreen(loc);

        float x = event.getRawX();
        float y = event.getRawY();

        return x >= loc[0] && x <= loc[0] + view.getWidth() && y >= loc[1] && y <= loc[1] + view.getHeight();
    }

    private void setupOutsideTouchCloseSuggestions(View root) {
        root.setOnTouchListener((v, event) -> {
            if (event.getAction() == android.view.MotionEvent.ACTION_DOWN) {
                v.performClick();

                boolean clickedInside = false;

                for (Map.Entry<Integer, EditText> entry : inputIdEditTextMap.entrySet()) {
                    EditText input = entry.getValue();
                    RecyclerView recycler = inputToRecyclerMap.get(entry.getKey());

                    if (isTouchInsideView(event, input) || isTouchInsideView(event, recycler)) {
                        clickedInside = true;
                        break;
                    }
                }

                if (!clickedInside) {
                    hideAllSuggestions();
                    View focused = root.findFocus();
                    if (focused != null) focused.clearFocus();
                }
            }
            return false;
        });
    }

    private void setInputFocusChangeListener(EditText input, RecyclerView recycler){
        input.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                hideAllSuggestions();

                String query = input.getText().toString().trim();

                if (query.length() > 3) {
                    routeEstVM.setActiveRecyclerId(input.getId());
                    routeEstVM.getAddressSuggestions(query);
                }
            } else {
                recycler.postDelayed(() -> recycler.setVisibility(View.GONE), 200);
            }
        });
    }

    private void setInputClickListener(EditText input){
        input.setOnClickListener(v -> {
            String query = input.getText().toString().trim();
            if (query.length() > 3) {
                routeEstVM.setActiveRecyclerId(input.getId());
                routeEstVM.getAddressSuggestions(query);
            }
        });
    }

    private void setupSuggestions(EditText input, RecyclerView recycler) {
        recycler.setLayoutManager(new LinearLayoutManager(getContext()));

        inputToRecyclerMap.put(input.getId(), recycler);
        inputIdEditTextMap.put(input.getId(), input);

        setInputFocusChangeListener(input, recycler);

        setInputClickListener(input);

        input.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (searchRunnable != null) searchHandler.removeCallbacks(searchRunnable);

                String query = s.toString().trim();
                if (query.length() > 3) {
                    searchRunnable = () -> {
                        routeEstVM.setActiveRecyclerId(input.getId());
                        routeEstVM.getAddressSuggestions(query);
                    };
                    searchHandler.postDelayed(searchRunnable, 500);
                } else {
                    recycler.setVisibility(View.GONE);
                }
            }
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}
        });
    }

    private void observeSuggestions() {
        routeEstVM.getSuggestions().observe(getViewLifecycleOwner(), list -> {
            int activeId = routeEstVM.getActiveRecyclerId();

            RecyclerView targetRecycler = inputToRecyclerMap.get(activeId);
            EditText currentInput = inputIdEditTextMap.get(activeId);

            if (targetRecycler == null || currentInput == null) return;

            if (list != null && !list.isEmpty()) {
                SuggestionAdapter adapter = new SuggestionAdapter(list, selectedDto -> {
                    String label = selectedDto.getLabel();
                    currentInput.setText(label);

                    routeEstVM.saveSelectedSuggestion(label, selectedDto);
                    targetRecycler.setVisibility(View.GONE);
                });

                targetRecycler.setAdapter(adapter);
                targetRecycler.setVisibility(View.VISIBLE);
            } else {
                targetRecycler.setVisibility(View.GONE);
            }
        });
    }

    private void addStopField() {
        stopCount++;
        View stopView = LayoutInflater.from(getContext()).inflate(R.layout.item_stop, stopsContainer, false);

        TextView labelStop = stopView.findViewById(R.id.label_stop);
        labelStop.setText("Stop " + stopCount);

        EditText inputStop = stopView.findViewById(R.id.input_stop);
        RecyclerView suggestionsStop = stopView.findViewById(R.id.suggestions_stop);

        inputStop.setTag("is_stop_input");

        int inputId = View.generateViewId();
        int recyclerId = View.generateViewId();
        inputStop.setId(inputId);
        suggestionsStop.setId(recyclerId);

        inputStop.setTag(R.id.suggestions_stop, recyclerId);

        setupSuggestions(inputStop, suggestionsStop);

        View btnRemove = stopView.findViewById(R.id.btn_remove_stop);
        btnRemove.setOnClickListener(v -> {
            String currentText = inputStop.getText().toString().trim();
            if (!currentText.isEmpty())
                routeEstVM.removeSelectedSuggestion(currentText);

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
        String startText = startInput.getText().toString().trim();
        String endText = endInput.getText().toString().trim();

        if (startText.isEmpty() || endText.isEmpty()) {
            Toast.makeText(requireContext(), "Please enter start and end location", Toast.LENGTH_SHORT).show();
            return;
        }

        List<String> stopsTexts = new ArrayList<>();

        for (int i = 0; i < stopsContainer.getChildCount(); i++) {
            View stopView = stopsContainer.getChildAt(i);

            EditText inputStop = stopView.findViewWithTag("is_stop_input");
            if (inputStop != null) {
                String val = inputStop.getText().toString().trim();
                if (!val.isEmpty()) {
                    stopsTexts.add(val);
                }
            }
        }


        View loader = getView().findViewById(R.id.loader_container);
        if (loader != null) loader.setVisibility(View.VISIBLE);
        getView().findViewById(R.id.estimation_info).setVisibility(View.GONE);

        routeEstVM.getResults(startText, endText, stopsTexts);
    }
}