package com.example.vroom.activities;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;


import com.example.vroom.DTOs.map.MapRouteDTO;
import com.example.vroom.DTOs.ride.responses.RideResponseDTO;
import com.example.vroom.DTOs.route.responses.PointResponseDTO;
import com.example.vroom.R;
import com.example.vroom.network.RetrofitClient;
import com.example.vroom.viewmodels.LoginViewModel;
import com.example.vroom.viewmodels.MainViewModel;
import com.example.vroom.viewmodels.RouteEstimationViewModel;
import com.example.vroom.viewmodels.UserRideHistoryViewModel;
import com.google.gson.Gson;

import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polyline;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends BaseActivity {
    private MapView map = null;
    private Polyline routePolyline;
    private Map<Integer, Marker> driverMarkers = new HashMap<>();
    private MainViewModel viewModel;
    private RouteEstimationViewModel routeEstimationViewModel;
    private UserRideHistoryViewModel userRideHistoryViewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this));
        Configuration.getInstance().setUserAgentValue(getPackageName());

        setContentView(R.layout.activity_main);

        map = findViewById(R.id.map);
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.setMultiTouchControls(true);

        GeoPoint startPoint = new GeoPoint(45.2455, 19.8227);
        map.getController().setZoom(16.0);
        map.getController().setCenter(startPoint);

        routePolyline = new Polyline();
        routePolyline.getOutlinePaint().setColor(android.graphics.Color.parseColor("#2A2C24"));
        routePolyline.getOutlinePaint().setStrokeWidth(10f);
        map.getOverlays().add(routePolyline);

        // first we register all view models we will need to observe
        viewModel = new ViewModelProvider(this).get(MainViewModel.class);
        routeEstimationViewModel = new ViewModelProvider(this).get(RouteEstimationViewModel.class);
        userRideHistoryViewModel = new ViewModelProvider(this).get(UserRideHistoryViewModel.class);

        // setting up observers and what should they do
        setupObservers();

        // if first time creating activity
        // if needed to draw data
        handleIncomingIntent(getIntent());
    }

    @Override
    public void onLogoButtonClicked(){
    }
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        handleIncomingIntent(intent);
    }

    private void handleIncomingIntent(Intent intent){
        if(intent == null) return;

        // if has data for drawing route
        if(intent.hasExtra("ROUTE_DATA")){
            String json = intent.getStringExtra("ROUTE_DATA");
            RideResponseDTO ride = new Gson().fromJson(json, RideResponseDTO.class);

            userRideHistoryViewModel.sendRideData(ride);
        }
    }

    private void setupObservers(){
        // used for drawing OSRM routes
        viewModel.getRouteResult().observe(this, osrmRoute -> {
            if (osrmRoute != null && osrmRoute.geometry != null) {

                ArrayList<GeoPoint> roadPoints = new ArrayList<>();
                for (List<Double> coord : osrmRoute.geometry.coordinates) {
                    roadPoints.add(new GeoPoint(coord.get(1), coord.get(0)));
                }
                routePolyline.setPoints(roadPoints);

                map.post(() -> {
                    if (roadPoints.size() > 1) {
                        map.zoomToBoundingBox(BoundingBox.fromGeoPoints(roadPoints), true, 100);
                    }
                });
                map.invalidate();
            }
        });


        // observers for errors
        viewModel.getErrorMessage().observe(this, error -> {
            Toast.makeText(this, error, Toast.LENGTH_LONG).show();
        });


        // mimics action type on web, case MapActionType.DRAW_ROUTE
        routeEstimationViewModel.getRoute().observe(this, mapRouteDTO -> {
            if (mapRouteDTO != null) {
                drawRoute(mapRouteDTO, false);
            }
        });

        userRideHistoryViewModel.getRoute().observe(this, mapRouteDTO -> {
            if (mapRouteDTO != null) {
                drawRoute(mapRouteDTO, false);
            }
        });
    }

    // draw functions are used to draw on map, they can be combined or used seperately
    // draw functions should call main view model functions to get route, vehicle position, etc.
    public void drawVehicles(){

    }
    public void drawRide(MapRouteDTO payload, boolean clear){
        if(clear)
            clearMap();

        // draw one vehicle

        // draw route
        drawRoute(payload, false);
    }

    public void clearMap(){
        // removes line
        if (routePolyline != null) {
            routePolyline.setPoints(new ArrayList<>());
        }

        // removes markers
        map.getOverlays().removeIf(overlay -> overlay instanceof Marker);

        driverMarkers.clear();

        map.invalidate();
    }

    public void drawRoute(MapRouteDTO payload, boolean clear) {
        if (clear) {
            this.clearMap();
        }

        addRouteMarkers(payload);

        viewModel.getRouteCoordinates(payload);

        map.invalidate();
    }


    // add markers are used to draw icons on map,
    // they are wrappers around add one marker which actually adds marker
    private void addVehicleMarker(){

    }
    private void addRouteMarkers(MapRouteDTO payload) {
        if (payload.getStart() != null) {
            addMarker(payload.getStart(), "Start", R.drawable.ic_ride_start);
        }

        if (payload.getStops() != null) {
            for (int i = 0; i < payload.getStops().size(); i++) {
                addMarker(payload.getStops().get(i), "Stop " + (i+1), R.drawable.ic_ride_stop);
            }
        }

        if (payload.getEnd() != null) {
            addMarker(payload.getEnd(), "End", R.drawable.ic_ride_end);
        }
    }

    // adds marker on map
    private void addMarker(PointResponseDTO p, String title, int iconRes) {
        Marker m = new Marker(map);

        m.setPosition(new GeoPoint(p.getLat(), p.getLng()));
        m.setTitle(title);
        m.setIcon(ContextCompat.getDrawable(this, iconRes));

        map.getOverlays().add(m);
    }

    @Override
    public void onResume() {
        super.onResume();
        map.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        map.onPause();
    }
}