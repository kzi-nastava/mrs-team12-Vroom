package com.example.vroom.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vroom.R;
import com.example.vroom.adapters.SuggestionAdapter;
import com.example.vroom.enums.VehicleType;
import com.example.vroom.viewmodels.OrderRideViewModel;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrderRideFragment extends Fragment {

    private OrderRideViewModel viewModel;
    private CheckBox checkboxScheduleRide;
    private EditText inputStart, inputEnd, inputEmail;
    private RecyclerView suggestionsStart, suggestionsEnd;
    private Spinner vehicleTypeSpinner;
    private CheckBox checkboxChildren, checkboxPets;
    private TimePicker timePicker;
    private LinearLayout stopsContainer, estimatesContainer;
    private TextView textEstTime, textEstPrice;
    private Button btnCalculate, btnOrder;

    private int stopCount = 0;
    private final Handler searchHandler = new Handler(Looper.getMainLooper());
    private Runnable searchRunnable;

    private final Map<Integer, RecyclerView> inputToRecyclerMap = new HashMap<>();
    private final Map<Integer, EditText> inputIdEditTextMap = new HashMap<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_order_ride, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        checkboxScheduleRide = view.findViewById(R.id.checkbox_schedule_ride);
        timePicker = view.findViewById(R.id.time_picker_schedule);
        viewModel = new ViewModelProvider(this).get(OrderRideViewModel.class);

        initViews(view);
        setupVehicleTypeSpinner();
        setupSuggestions();
        observeViewModel();
        setupOutsideTouchCloseSuggestions(view);
        checkboxScheduleRide.setOnCheckedChangeListener((buttonView, isChecked) -> {
            timePicker.setVisibility(isChecked ? View.VISIBLE : View.GONE);
        });

        btnCalculate.setOnClickListener(v -> {
            hideAllSuggestions();
            calculateRoute();
        });

        btnOrder.setOnClickListener(v -> {
            hideAllSuggestions();
            orderRide();
        });
        Button btnAddStop = new Button(getContext());
        btnAddStop.setText("Add stop");
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0, 8, 0, 0);
        btnAddStop.setLayoutParams(params);

        stopsContainer.addView(btnAddStop, 0);

        btnAddStop.setOnClickListener(v -> addStopEditText());
    }

    private void addStopEditText() {
        stopCount++;

        EditText stopInput = new EditText(getContext());
        stopInput.setHint("Stop " + stopCount);
        stopInput.setTextSize(16f);
        stopInput.setPadding(16, 16, 16, 16);
        stopInput.setBackgroundResource(R.drawable.bg_txt_input_rounded);

        LinearLayout.LayoutParams editParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        editParams.setMargins(0, 8, 0, 0);
        stopInput.setLayoutParams(editParams);

        RecyclerView stopSuggestions = new RecyclerView(getContext());
        stopSuggestions.setLayoutManager(new LinearLayoutManager(getContext()));
        stopSuggestions.setVisibility(View.GONE);
        stopSuggestions.setBackgroundResource(R.drawable.bg_suggestion_list);

        LinearLayout.LayoutParams recyclerParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        stopSuggestions.setLayoutParams(recyclerParams);

        inputToRecyclerMap.put(stopInput.getId(), stopSuggestions);
        inputIdEditTextMap.put(stopInput.getId(), stopInput);

        stopsContainer.addView(stopInput, stopsContainer.getChildCount() - 1);
        stopsContainer.addView(stopSuggestions, stopsContainer.getChildCount() - 1);

        setupSuggestionForInput(stopInput, stopSuggestions);
    }
    private List<String> getStopsFromContainer() {
        List<String> stops = new ArrayList<>();
        for (int i = 0; i < stopsContainer.getChildCount(); i++) {
            View child = stopsContainer.getChildAt(i);
            if (child instanceof EditText) {
                String text = ((EditText) child).getText().toString().trim();
                if (!text.isEmpty()) {
                    stops.add(text);
                }
            }
        }
        return stops;
    }
    private void initViews(View view) {
        inputStart = view.findViewById(R.id.input_start);
        inputEnd = view.findViewById(R.id.input_end);
        inputEmail = view.findViewById(R.id.input_email);
        suggestionsStart = view.findViewById(R.id.suggestions_start);
        suggestionsEnd = view.findViewById(R.id.suggestions_end);
        vehicleTypeSpinner = view.findViewById(R.id.vehicle_type_spinner);
        checkboxChildren = view.findViewById(R.id.checkbox_children);
        checkboxPets = view.findViewById(R.id.checkbox_pets);
        timePicker = view.findViewById(R.id.time_picker_schedule);
        stopsContainer = view.findViewById(R.id.stops_container);
        estimatesContainer = view.findViewById(R.id.estimates_container);
        textEstTime = view.findViewById(R.id.text_est_time);
        textEstPrice = view.findViewById(R.id.text_est_price);
        btnCalculate = view.findViewById(R.id.btn_calculate);
        btnOrder = view.findViewById(R.id.btn_order);
    }

    private void setupVehicleTypeSpinner() {
        ArrayAdapter<VehicleType> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                VehicleType.values()
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        vehicleTypeSpinner.setAdapter(adapter);
    }

    private void setupSuggestions() {
        suggestionsStart.setLayoutManager(new LinearLayoutManager(getContext()));
        suggestionsEnd.setLayoutManager(new LinearLayoutManager(getContext()));

        setupSuggestionForInput(inputStart, suggestionsStart);
        setupSuggestionForInput(inputEnd, suggestionsEnd);
    }

    private void setupSuggestionForInput(EditText input, RecyclerView recycler) {
        inputToRecyclerMap.put(input.getId(), recycler);
        inputIdEditTextMap.put(input.getId(), input);

        input.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                hideAllSuggestions();
                String query = input.getText().toString().trim();
                if (query.length() > 3) {
                    viewModel.setActiveRecyclerId(input.getId());
                    viewModel.getAddressSuggestions(query);
                }
            } else {
                recycler.postDelayed(() -> recycler.setVisibility(View.GONE), 200);
            }
        });

        input.setOnClickListener(v -> {
            String query = input.getText().toString().trim();
            if (query.length() > 3) {
                viewModel.setActiveRecyclerId(input.getId());
                viewModel.getAddressSuggestions(query);
            }
        });

        input.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (searchRunnable != null) searchHandler.removeCallbacks(searchRunnable);

                String query = s.toString().trim();
                if (query.length() > 3) {
                    searchRunnable = () -> {
                        viewModel.setActiveRecyclerId(input.getId());
                        viewModel.getAddressSuggestions(query);
                    };
                    searchHandler.postDelayed(searchRunnable, 500);
                } else {
                    recycler.setVisibility(View.GONE);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void observeViewModel() {
        viewModel.getSuggestions().observe(getViewLifecycleOwner(), list -> {
            int activeId = viewModel.getActiveRecyclerId();
            RecyclerView targetRecycler = inputToRecyclerMap.get(activeId);
            EditText currentInput = inputIdEditTextMap.get(activeId);

            if (targetRecycler == null || currentInput == null) return;

            if (list != null && !list.isEmpty()) {
                SuggestionAdapter adapter = new SuggestionAdapter(list, selectedDto -> {
                    String label = selectedDto.getLabel();
                    currentInput.setText(label);
                    viewModel.saveSelectedSuggestion(label, selectedDto);
                    targetRecycler.setVisibility(View.GONE);
                });

                targetRecycler.setAdapter(adapter);
                targetRecycler.setVisibility(View.VISIBLE);
            } else {
                targetRecycler.setVisibility(View.GONE);
            }
        });

        viewModel.getRouteQuote().observe(getViewLifecycleOwner(), quote -> {
            if (quote != null) {
                estimatesContainer.setVisibility(View.VISIBLE);
                textEstTime.setText(String.format("~%.0f min", quote.getTime()));
                textEstPrice.setText(String.format("%.2f RSD", quote.getPrice()));
            }
        });

        viewModel.getOrderedRide().observe(getViewLifecycleOwner(), ride -> {
            if (ride != null) {
                Toast.makeText(getContext(), "Ride ordered successfully! ID: #" + ride.getRideID(), Toast.LENGTH_LONG).show();
                clearForm();
            }
        });

        viewModel.getError().observe(getViewLifecycleOwner(), errorMsg -> {
            if (errorMsg != null) {
                Toast.makeText(getContext(), errorMsg, Toast.LENGTH_LONG).show();
            }
        });

        viewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            btnCalculate.setEnabled(!isLoading);
            btnOrder.setEnabled(!isLoading);
        });
    }

    private void hideAllSuggestions() {
        for (RecyclerView recycler : inputToRecyclerMap.values()) {
            recycler.setVisibility(View.GONE);
        }
    }

    private boolean isTouchInsideView(android.view.MotionEvent event, View view) {
        if (view == null || view.getVisibility() != View.VISIBLE) return false;

        int[] loc = new int[2];
        view.getLocationOnScreen(loc);

        float x = event.getRawX();
        float y = event.getRawY();

        return x >= loc[0] && x <= loc[0] + view.getWidth() &&
                y >= loc[1] && y <= loc[1] + view.getHeight();
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

    private void calculateRoute() {
        String startText = inputStart.getText().toString().trim();
        String endText = inputEnd.getText().toString().trim();

        if (startText.isEmpty() || endText.isEmpty()) {
            Toast.makeText(getContext(), "Please enter start and end locations", Toast.LENGTH_SHORT).show();
            return;
        }

        List<String> stopsTexts = getStopsFromContainer();

        viewModel.calculateRoute(startText, endText, stopsTexts);
    }

    private void orderRide() {
        String startText = inputStart.getText().toString().trim();
        String endText = inputEnd.getText().toString().trim();
        String passengerEmail = inputEmail.getText().toString().trim();

        if (startText.isEmpty() || endText.isEmpty()) {
            Toast.makeText(getContext(), "Please enter start and end locations", Toast.LENGTH_SHORT).show();
            return;
        }

        VehicleType vehicleType = (VehicleType) vehicleTypeSpinner.getSelectedItem();
        Boolean babiesAllowed = checkboxChildren.isChecked();
        Boolean petsAllowed = checkboxPets.isChecked();

        List<String> passengerEmails = new ArrayList<>();
        if (!passengerEmail.isEmpty()) {
            passengerEmails.add(passengerEmail);
        }

        List<String> stopsTexts = getStopsFromContainer();

        Boolean scheduled = checkboxScheduleRide.isChecked();
        LocalDateTime scheduledTime;

        if (scheduled) {
            int hour = timePicker.getHour();
            int minute = timePicker.getMinute();
            LocalDateTime now = LocalDateTime.now();
            scheduledTime = LocalDateTime.of(now.getYear(), now.getMonth(), now.getDayOfMonth(), hour, minute);

            if (scheduledTime.isBefore(now)) {
                scheduledTime = scheduledTime.plusDays(1);
            }
        } else {
            scheduledTime = LocalDateTime.now();
        }


        viewModel.orderRide(startText, endText, stopsTexts,
                vehicleType, babiesAllowed, petsAllowed,
                passengerEmails, scheduled, scheduledTime);
    }

    private void clearForm() {
        inputStart.setText("");
        inputEnd.setText("");
        inputEmail.setText("");
        checkboxChildren.setChecked(false);
        checkboxPets.setChecked(false);
        estimatesContainer.setVisibility(View.GONE);
        viewModel.clearData();
    }
}