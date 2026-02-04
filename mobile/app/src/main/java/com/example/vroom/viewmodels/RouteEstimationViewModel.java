package com.example.vroom.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.vroom.DTOs.geocode.responses.AddressSuggestionResponseDTO;
import com.example.vroom.DTOs.route.responses.RouteQuoteResponseDTO;
import com.example.vroom.network.RetrofitClient;

import java.util.*;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RouteEstimationViewModel extends ViewModel {
    private int activeRecyclerId;
    private final MutableLiveData<List<AddressSuggestionResponseDTO>> suggestions = new MutableLiveData<>();

    private final Map<String, AddressSuggestionResponseDTO> selectedLocations = new HashMap<>();
    private final MutableLiveData<RouteQuoteResponseDTO> routeQuote = new MutableLiveData<>();

    // add object for map drawing which is of type mutable live data and has startLat, startLng, endLat, endLng, stopsLat, stopsLng
    public final LiveData<RouteQuoteResponseDTO> getRouteQuote() {
        return  routeQuote;
    }

    public void setActiveRecyclerId(int id) {
        this.activeRecyclerId = id;
    }

    public int getActiveRecyclerId() {
        return activeRecyclerId;
    }

    public LiveData<List<AddressSuggestionResponseDTO>> getSuggestions() {
        return suggestions;
    }

    public void saveSelectedSuggestion(String text, AddressSuggestionResponseDTO dto){
        selectedLocations.put(text, dto);
    }

    public void removeSelectedSuggestion(String text){
        selectedLocations.remove(text);
    }


    public void getAddressSuggestions(String text) {
        text = "Novi Sad, "+text;

        RetrofitClient.getGeoLocationService().geolocateMultipleAddresses(text).enqueue(new Callback<List<AddressSuggestionResponseDTO>>() {
            @Override
            public void onResponse(Call<List<AddressSuggestionResponseDTO>> call, Response<List<AddressSuggestionResponseDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    suggestions.setValue(response.body());
                }
            }

            @Override
            public void onFailure(Call<List<AddressSuggestionResponseDTO>> call, Throwable t) {
                suggestions.setValue(null);
            }
        });
    }

    private void getMissingCoordAndRetry(List<String> missing, String start, String end, List<String> stops) {
        if (missing.isEmpty()) return;

        String current = missing.get(0);

        RetrofitClient.getGeoLocationService().geolocateAddress(current).enqueue(new Callback<AddressSuggestionResponseDTO>() {
            @Override
            public void onResponse(Call<AddressSuggestionResponseDTO> call, Response<AddressSuggestionResponseDTO> response) {
                if (response.isSuccessful() && response.body() != null) {
                    selectedLocations.put(current, response.body());

                    missing.remove(0);

                    if (missing.isEmpty())
                        getResults(start, end, stops);
                    else
                        getMissingCoordAndRetry(missing, start, end, stops);

                }else{
                    missing.remove(0);

                    if (missing.isEmpty())
                        getResults(start, end, stops);
                    else
                        getMissingCoordAndRetry(missing, start, end, stops);
                }
            }
            @Override
            public void onFailure(Call<AddressSuggestionResponseDTO> call, Throwable t) {
                missing.remove(0);
                if (missing.isEmpty()) getResults(start, end, stops);
            }
        });
    }

    private String getCoords(String textKey, boolean stops) {
        AddressSuggestionResponseDTO dto = selectedLocations.get(textKey);
        if(dto != null){
            return dto.getLat() + "," + dto.getLon();
        }
        return "";
    }

    public void getResults(String startText, String endText, List<String> stopsTexts){
        String start = startText.trim();
        String end = endText.trim();

        List<String> missing = new ArrayList<>();

        if(!selectedLocations.containsKey(start))
            missing.add(start);
        if(!selectedLocations.containsKey(end))
            missing.add(end);

        for(String s : stopsTexts){
            String stop = s.trim();

            if(!selectedLocations.containsKey(stop))
                missing.add(stop);
        }

        if(missing.isEmpty()){
            String startCoords = getCoords(start, false);
            String endCoords = getCoords(end, false);
            List<String> stopsCoords = new ArrayList<>();

            for (String s : stopsTexts)
                stopsCoords.add(getCoords(s.trim(), true));

            requestQuote(startCoords, endCoords, stopsCoords);
        }else
            getMissingCoordAndRetry(missing, start, end, stopsTexts);
    }


    private void requestQuote(String startCoords, String endCoords, List<String> stopsCoords) {
        String stopsParam = String.join(";", stopsCoords);

        // send coordinates to map

        RetrofitClient.getRouteService().getQuote(startCoords, endCoords, stopsParam).enqueue(new Callback<RouteQuoteResponseDTO>() {
            @Override
            public void onResponse(Call<RouteQuoteResponseDTO> call, Response<RouteQuoteResponseDTO> response) {
                if (response.isSuccessful()) {
                    routeQuote.setValue(response.body());
                }
            }

            @Override
            public void onFailure(Call<RouteQuoteResponseDTO> call, Throwable t) { }
        });
    }
}