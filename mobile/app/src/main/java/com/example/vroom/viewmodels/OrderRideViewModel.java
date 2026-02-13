package com.example.vroom.viewmodels;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;


import com.example.vroom.DTOs.geocode.responses.AddressSuggestionResponseDTO;
import com.example.vroom.DTOs.ride.requests.RideRequestDTO;
import com.example.vroom.DTOs.ride.responses.GetRideResponseDTO;
import com.example.vroom.DTOs.route.responses.GetRouteResponseDTO;
import com.example.vroom.DTOs.route.responses.PointResponseDTO;
import com.example.vroom.DTOs.route.responses.RouteQuoteResponseDTO;
import com.example.vroom.enums.VehicleType;
import com.example.vroom.network.RetrofitClient;
import com.example.vroom.services.GeoLocationService;
import com.example.vroom.services.RideService;
import com.example.vroom.services.RouteService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderRideViewModel extends ViewModel {

    private int activeRecyclerId;
    private final MutableLiveData<List<AddressSuggestionResponseDTO>> suggestions = new MutableLiveData<>();
    private final Map<String, AddressSuggestionResponseDTO> selectedLocations = new HashMap<>();

    private final MutableLiveData<RouteQuoteResponseDTO> routeQuote = new MutableLiveData<>();
    private final MutableLiveData<GetRideResponseDTO> orderedRide = new MutableLiveData<>();
    private final MutableLiveData<String> error = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();

    private final GeoLocationService geoService = RetrofitClient.getGeoLocationService();
    private final RouteService routeService = RetrofitClient.getRouteService();
    private final RideService rideService = RetrofitClient.getRideService();

    public LiveData<List<AddressSuggestionResponseDTO>> getSuggestions() {
        return suggestions;
    }

    public LiveData<RouteQuoteResponseDTO> getRouteQuote() {
        return routeQuote;
    }

    public LiveData<GetRideResponseDTO> getOrderedRide() {
        return orderedRide;
    }

    public LiveData<String> getError() {
        return error;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public void setActiveRecyclerId(int id) {
        this.activeRecyclerId = id;
    }

    public int getActiveRecyclerId() {
        return activeRecyclerId;
    }

    public void saveSelectedSuggestion(String text, AddressSuggestionResponseDTO dto) {
        selectedLocations.put(text, dto);
    }

    public void removeSelectedSuggestion(String text) {
        selectedLocations.remove(text);
    }


    public void getAddressSuggestions(String text) {
        String query = "Novi Sad, " + text;

        geoService.geolocateMultipleAddresses(query).enqueue(new Callback<List<AddressSuggestionResponseDTO>>() {
            @Override
            public void onResponse(Call<List<AddressSuggestionResponseDTO>> call, Response<List<AddressSuggestionResponseDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    suggestions.setValue(response.body());
                } else {
                    suggestions.setValue(new ArrayList<>());
                }
            }

            @Override
            public void onFailure(Call<List<AddressSuggestionResponseDTO>> call, Throwable t) {
                suggestions.setValue(new ArrayList<>());
            }
        });
    }


    public void calculateRoute(String startText, String endText, List<String> stopsTexts) {
        isLoading.setValue(true);

        List<String> missing = new ArrayList<>();

        if (!selectedLocations.containsKey(startText.trim())) {
            missing.add(startText.trim());
        }
        if (!selectedLocations.containsKey(endText.trim())) {
            missing.add(endText.trim());
        }

        for (String stop : stopsTexts) {
            String stopTrimmed = stop.trim();
            if (!selectedLocations.containsKey(stopTrimmed)) {
                missing.add(stopTrimmed);
            }
        }

        if (missing.isEmpty()) {
            performCalculation(startText.trim(), endText.trim(), stopsTexts);
        } else {
            fetchMissingCoordinates(missing, startText, endText, stopsTexts);
        }
    }

    private void fetchMissingCoordinates(List<String> missing, String start, String end, List<String> stops) {
        if (missing.isEmpty()) {
            performCalculation(start, end, stops);
            return;
        }

        String current = missing.get(0);

        geoService.geolocateAddress(current).enqueue(new Callback<AddressSuggestionResponseDTO>() {
            @Override
            public void onResponse(Call<AddressSuggestionResponseDTO> call, Response<AddressSuggestionResponseDTO> response) {
                if (response.isSuccessful() && response.body() != null) {
                    selectedLocations.put(current, response.body());
                }

                missing.remove(0);

                if (missing.isEmpty()) {
                    performCalculation(start, end, stops);
                } else {
                    fetchMissingCoordinates(missing, start, end, stops);
                }
            }

            @Override
            public void onFailure(Call<AddressSuggestionResponseDTO> call, Throwable t) {
                missing.remove(0);
                if (missing.isEmpty()) {
                    performCalculation(start, end, stops);
                } else {
                    fetchMissingCoordinates(missing, start, end, stops);
                }
            }
        });
    }

    private void performCalculation(String startText, String endText, List<String> stopsTexts) {
        String startCoords = getCoords(startText);
        String endCoords = getCoords(endText);

        List<String> stopsCoords = new ArrayList<>();
        for (String stop : stopsTexts) {
            String coords = getCoords(stop.trim());
            if (!coords.isEmpty()) {
                stopsCoords.add(coords);
            }
        }

        String stopsParam = stopsCoords.isEmpty() ? null : String.join(";", stopsCoords);

        routeService.getQuote(startCoords, endCoords, stopsParam)
                .enqueue(new Callback<RouteQuoteResponseDTO>() {
                    @Override
                    public void onResponse(Call<RouteQuoteResponseDTO> call, Response<RouteQuoteResponseDTO> response) {
                        isLoading.setValue(false);
                        if (response.isSuccessful() && response.body() != null) {
                            routeQuote.setValue(response.body());
                        } else {
                            error.setValue("Failed to calculate route");
                        }
                    }

                    @Override
                    public void onFailure(Call<RouteQuoteResponseDTO> call, Throwable t) {
                        isLoading.setValue(false);
                        error.setValue("Network error: " + t.getMessage());
                    }
                });
    }

    private String getCoords(String textKey) {
        AddressSuggestionResponseDTO dto = selectedLocations.get(textKey);
        if (dto != null) {
            return dto.getLat() + "," + dto.getLon();
        }
        return "";
    }

    public void orderRide(String startText, String endText, List<String> stopsTexts,
                          VehicleType vehicleType, Boolean babiesAllowed, Boolean petsAllowed,
                          List<String> passengerEmails, Boolean scheduled, LocalDateTime scheduledTime) {

        isLoading.setValue(true);

        AddressSuggestionResponseDTO startDto = selectedLocations.get(startText.trim());
        AddressSuggestionResponseDTO endDto = selectedLocations.get(endText.trim());

        if (startDto == null || endDto == null) {
            isLoading.setValue(false);
            error.setValue("Please select valid start and end locations");
            return;
        }

        GetRouteResponseDTO route = new GetRouteResponseDTO();
        route.setStartLocationLat(startDto.getLat());
        route.setStartLocationLng(startDto.getLon());
        route.setStartAddress(startDto.getLabel());
        route.setEndLocationLat(endDto.getLat());
        route.setEndLocationLng(endDto.getLon());
        route.setEndAddress(endDto.getLabel());

        List<PointResponseDTO> stops = new ArrayList<>();
        for (String stopText : stopsTexts) {
            AddressSuggestionResponseDTO stopDto = selectedLocations.get(stopText.trim());
            if (stopDto != null) {
                PointResponseDTO point = new PointResponseDTO();
                point.setLat(stopDto.getLat());
                point.setLng(stopDto.getLon());
                stops.add(point);
            }
        }
        route.setStops(stops);

        RideRequestDTO request = new RideRequestDTO();
        request.setRoute(route);
        request.setVehicleType(vehicleType);
        request.setBabiesAllowed(babiesAllowed);
        request.setPetsAllowed(petsAllowed);
        request.setPassengersEmails(passengerEmails);
        request.setScheduled(scheduled);
        request.setScheduledTime(scheduledTime);

        rideService.orderRide(request).enqueue(new Callback<GetRideResponseDTO>() {
            @Override
            public void onResponse(Call<GetRideResponseDTO> call, Response<GetRideResponseDTO> response) {
                isLoading.setValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    orderedRide.setValue(response.body());
                } else if (response.code() == 409) {
                    error.setValue("No available drivers or scheduling conflict");
                } else if (response.code() == 400) {
                    error.setValue("Invalid ride request");
                } else {
                    error.setValue("Failed to order ride: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<GetRideResponseDTO> call, Throwable t) {
                isLoading.setValue(false);
                error.setValue("Network error: " + t.getMessage());
            }
        });
    }

    public void clearData() {
        selectedLocations.clear();
        routeQuote.setValue(null);
        orderedRide.setValue(null);
        error.setValue(null);
    }
}
