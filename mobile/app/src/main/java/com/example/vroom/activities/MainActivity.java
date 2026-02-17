package com.example.vroom.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.example.vroom.DTOs.map.MapRouteDTO;
import com.example.vroom.DTOs.ride.responses.RideResponseDTO;
import com.example.vroom.DTOs.route.responses.PointResponseDTO;
import com.example.vroom.R;
import com.example.vroom.data.local.StorageManager;
import com.example.vroom.enums.DriverStatus;
import com.example.vroom.fragments.ActiveRidesFragment;
import com.example.vroom.fragments.ReviewRideFragment;
import com.example.vroom.fragments.RideTrackingFragment;
import com.example.vroom.fragments.RouteEstimationFragment;
import com.example.vroom.fragments.UserChatFragment;
import com.example.vroom.network.SocketProvider;
import com.example.vroom.viewmodels.ChatViewModel;
import com.example.vroom.viewmodels.MainViewModel;
import com.example.vroom.viewmodels.RideTrackingViewModel;
import com.example.vroom.viewmodels.RouteEstimationViewModel;
import com.example.vroom.viewmodels.UserRideHistoryViewModel;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polyline;

public class MainActivity extends BaseActivity implements RideNavigationListener {
    private MapView map = null;
    private Polyline routePolyline;
    private final Map<Long, Marker> driverMarkers = new HashMap<>();
    private MainViewModel viewModel;
    private RideTrackingViewModel rideTrackingViewModel;
    private RouteEstimationViewModel routeEstimationViewModel;
    private UserRideHistoryViewModel userRideHistoryViewModel;
    private ChatViewModel chatViewModel;
    private BottomSheetBehavior<View> bottomSheetBehavior;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        FirebaseMessaging.getInstance().subscribeToTopic("user")
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("FCM", "Subscribed to start_ride topic");
                    }
                });
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

        viewModel = new ViewModelProvider(this).get(MainViewModel.class);
        rideTrackingViewModel = new ViewModelProvider(this).get(RideTrackingViewModel.class);
        routeEstimationViewModel = new ViewModelProvider(this).get(RouteEstimationViewModel.class);
        userRideHistoryViewModel = new ViewModelProvider(this).get(UserRideHistoryViewModel.class);
        chatViewModel = new ViewModelProvider(this).get(ChatViewModel.class);

        setupObservers();

        viewModel.subscribeToLocationUpdates();
        subscribeToChat();
        checkAndStartDriverTracking();

        View bottomSheet = findViewById(R.id.bottom_sheet);
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        handleIncomingIntent(getIntent());
    }

    private void subscribeToChat(){
        String userType = StorageManager.getSharedPreferences(this).getString("user_type", null);
        String userID = StorageManager.getSharedPreferences(this).getString("user_id", "");
        if (userType != null && !"ADMIN".equals(userType) && !userID.isEmpty()){
            long userIDLong = Long.parseLong(userID);
            chatViewModel.userSubscribeToMessages(userIDLong);
        }
    }

    private void setupObservers() {
        viewModel.getDriverUpdate().observe(this, dto -> {
            if (dto != null && dto.getPoint() != null) {
                updateDriverMarker(dto.getDriverId(), dto.getStatus(), new GeoPoint(dto.getPoint().getLat(), dto.getPoint().getLng()));
            }
        });
        rideTrackingViewModel.getRideUpdate().observe(this, update -> {
            if (update != null && update.getCurrentLocation() != null) {
                updateDriverMarker(update.getDriverID(), DriverStatus.UNAVAILABLE,
                        new GeoPoint(update.getCurrentLocation().getLat(), update.getCurrentLocation().getLng()), true);
            }
        });
        rideTrackingViewModel.getActiveRoute().observe(this, route -> {
            if (route != null) {
                MapRouteDTO mapRoute = new MapRouteDTO();
                mapRoute.setStart(new PointResponseDTO(route.getStartLocationLat(), route.getStartLocationLng()));
                mapRoute.setEnd(new PointResponseDTO(route.getEndLocationLat(), route.getEndLocationLng()));
                mapRoute.setStops(route.getStops());
                drawRoute(mapRoute, true);
            }
        });
        viewModel.getRouteResult().observe(this, osrmRoute -> {
            if (osrmRoute != null && osrmRoute.geometry != null) {
                ArrayList<GeoPoint> roadPoints = new ArrayList<>();
                for (List<Double> coord : osrmRoute.geometry.coordinates) {
                    roadPoints.add(new GeoPoint(coord.get(1), coord.get(0)));
                }
                runOnUiThread(() -> {
                    routePolyline.setPoints(roadPoints);
                    if (roadPoints.size() > 1) {
                        map.postDelayed(() -> {
                            try {
                                map.zoomToBoundingBox(BoundingBox.fromGeoPoints(roadPoints), true, 100);
                            } catch (Exception e) {
                                map.getController().setCenter(roadPoints.get(0));
                            }
                        }, 200);
                    }
                    map.invalidate();
                });
            }
        });
        rideTrackingViewModel.getIsRideFinished().observe(this, finished -> {
            if (finished != null && finished) {
                String role = StorageManager.getSharedPreferences(this).getString("user_type", "");
                Long currentId = rideTrackingViewModel.getCurrentRideId().getValue();

                if (currentId != null) {
                    onRideFinished(currentId, role);
                }
            }
        });
        routeEstimationViewModel.getRoute().observe(this, payload -> drawRoute(payload, true));
        userRideHistoryViewModel.getRoute().observe(this, payload -> drawRoute(payload, true));
        viewModel.getErrorMessage().observe(this, error -> Toast.makeText(this, error, Toast.LENGTH_LONG).show());
    }

    public void updateDriverMarker(Long driverID, DriverStatus status, GeoPoint position, boolean activeRide) {
        Marker m = driverMarkers.get(driverID);
        if (m == null) {
            m = new Marker(map);
            m.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
            m.setTitle("Driver #" + driverID);
            map.getOverlays().add(m);
            driverMarkers.put(driverID, m);
        }
        m.setPosition(position);
        int res = (status == DriverStatus.AVAILABLE) ? R.drawable.ic_available_taxi : R.drawable.ic_unavailable_taxi;
        if (activeRide) res = R.drawable.ic_designated_taxi;
        m.setIcon(ContextCompat.getDrawable(this, res));
        map.invalidate();
    }

    private void updateDriverMarker(Long driverID, DriverStatus status, GeoPoint position) {
        updateDriverMarker(driverID, status, position, false);
    }

    public void drawRoute(MapRouteDTO payload, boolean clear) {
        if (clear) clearMap();
        addRouteMarkers(payload);
        viewModel.getRouteCoordinates(payload);
        map.invalidate();
    }

    public void clearMap() {
        if (routePolyline != null) routePolyline.setPoints(new ArrayList<>());
        map.getOverlays().removeIf(overlay -> overlay instanceof Marker);
        driverMarkers.clear();
        map.invalidate();
    }

    private void addRouteMarkers(MapRouteDTO payload) {
        if (payload.getStart() != null) addMarker(payload.getStart(), "Start", R.drawable.ic_ride_start);
        if (payload.getStops() != null) {
            for (int i = 0; i < payload.getStops().size(); i++) {
                addMarker(payload.getStops().get(i), "Stop " + (i + 1), R.drawable.ic_ride_stop);
            }
        }
        if (payload.getEnd() != null) addMarker(payload.getEnd(), "End", R.drawable.ic_ride_end);
    }

    private void addMarker(PointResponseDTO p, String title, int iconRes) {
        Marker m = new Marker(map);
        m.setPosition(new GeoPoint(p.getLat(), p.getLng()));
        m.setTitle(title);
        m.setIcon(ContextCompat.getDrawable(this, iconRes));
        map.getOverlays().add(m);
    }

    private void checkAndStartDriverTracking() {
        String userType = StorageManager.getSharedPreferences(this).getString("user_type", "");
        if ("DRIVER".equals(userType)) checkLocationPermissions();
    }

    private void checkLocationPermissions() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1001);
        } else {
            viewModel.startTracking(LocationServices.getFusedLocationProviderClient(this));
        }
    }

    private void handleIncomingIntent(Intent intent) {
        if (intent == null) return;

        if (intent.hasExtra("ROUTE_DATA")) {
            RideResponseDTO ride = new Gson().fromJson(intent.getStringExtra("ROUTE_DATA"), RideResponseDTO.class);
            userRideHistoryViewModel.sendRideData(ride);
        } else if (intent.hasExtra("TRACK_RIDE_DATA")){
            // should be used for admin to track ride both for track ride & panic ride
            Long rideId = new Gson().fromJson(intent.getStringExtra("TRACK_RIDE_DATA"), Long.class);

            rideTrackingViewModel.loadRoute(rideId);
            rideTrackingViewModel.subscribeToRideUpdates(rideId);
        } else if (intent.hasExtra("OPEN_ESTIMATION")){
            RouteEstimationFragment fragment = RouteEstimationFragment.newInstance();
            fragment.show(getSupportFragmentManager(), "RouteEstimationBottomSheet");
        }
        if (intent.hasExtra("RIDE_ID")) {
            long rideId = intent.getLongExtra("RIDE_ID", -1);
            if (rideId != -1) updateUIForRideState(rideId);
        }
        if (intent.hasExtra("USER_CHAT") && intent.getBooleanExtra("USER_CHAT", false)) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.content_frame, new UserChatFragment())
                    .addToBackStack(null)
                    .commit();
        }
    }

    public void updateUIForRideState(Long rideId) {
        rideTrackingViewModel.subscribeToRideUpdates(rideId);
        String userType = StorageManager.getSharedPreferences(this).getString("user_type", "");
        viewModel.setRideTrackingActive(true, userType);
        Fragment fragment = RideTrackingFragment.newInstance(rideId, userType);

        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        bottomSheetBehavior.setHideable(false);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.sheet_content_container, fragment)
                .commit();
    }

    @Override
    public void onResume() {
        super.onResume();
        map.onResume();
        if (!viewModel.getIsRideTrackingActive()) {
            viewModel.subscribeToLocationUpdates();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        map.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        handleIncomingIntent(intent);
    }

    public void onRideFinished(Long rideID, String userRole) {
        rideTrackingViewModel.unsubscribeFromRideUpdates();

        viewModel.setRideTrackingActive(false, userRole);

        clearMap();

        Fragment nextFragment;
        if ("REGISTERED_USER".equals(userRole)) {
            nextFragment = ReviewRideFragment.newInstance(rideID);
        } else {
            bottomSheetBehavior.setHideable(true);
            resetToHomeState();
            if ("DRIVER".equals(userRole)) {
                viewModel.setRideTrackingActive(true, "DRIVER");
            }
            return;
        }

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.sheet_content_container, nextFragment)
                .commitAllowingStateLoss();

        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
    }

    public void resetToHomeState() {
        bottomSheetBehavior.setHideable(true);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.sheet_content_container);
        if (currentFragment != null) {
            getSupportFragmentManager().beginTransaction().remove(currentFragment).commit();
        }
        String userRole = StorageManager.getSharedPreferences(this).getString("user_type", "");
        clearMap();
        viewModel.setRideTrackingActive(false, userRole);
        viewModel.subscribeToLocationUpdates();

        checkAndStartDriverTracking();
    }
}