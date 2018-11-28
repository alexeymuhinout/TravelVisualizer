package com.rustedbrain.diploma.travelvisualizer.fragment;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.Toast;

import com.directions.route.AbstractRouting;
import com.directions.route.Route;
import com.directions.route.RouteException;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.rustedbrain.diploma.travelvisualizer.LoginActivity;
import com.rustedbrain.diploma.travelvisualizer.R;
import com.rustedbrain.diploma.travelvisualizer.TravelAppUtils;
import com.rustedbrain.diploma.travelvisualizer.fragment.travel.TravelsFragment;
import com.rustedbrain.diploma.travelvisualizer.model.dto.security.AuthUserDTO;
import com.rustedbrain.diploma.travelvisualizer.model.dto.security.UserDTO;
import com.rustedbrain.diploma.travelvisualizer.model.dto.travel.PlaceDescriptionDTO;
import com.rustedbrain.diploma.travelvisualizer.model.dto.travel.PlaceMapDTO;
import com.rustedbrain.diploma.travelvisualizer.model.dto.travel.PlaceMapDTOList;
import com.rustedbrain.diploma.travelvisualizer.model.dto.travel.PlaceType;
import com.rustedbrain.diploma.travelvisualizer.model.dto.travel.TravelDTO;
import com.rustedbrain.diploma.travelvisualizer.model.dto.travel.TravelDTOList;
import com.rustedbrain.diploma.travelvisualizer.model.dto.travel.TripsMapPlacesFilterDTO;
import com.rustedbrain.diploma.travelvisualizer.task.place.GetPlaceDescriptionTask;
import com.rustedbrain.diploma.travelvisualizer.task.place.PlacesGetTask;
import com.rustedbrain.diploma.travelvisualizer.task.travel.GetUserTravelsTask;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class TripsMapFragment extends Fragment implements TravelsFragment.TravelsFragmentRouteButtonListener, GetUserTravelsTask.Listener, PlacesGetTask.PlacesGetTaskListener, GetPlaceDescriptionTask.Listener, GoogleMap.OnMarkerClickListener {

    public static final String PLACE_DTO_PARAM = "place";
    private static final int DEFAULT_CAMERA_FOCUS_ZOOM = 11;
    private MapView mapView;
    private OnFragmentInteractionListener interactionListener;
    private GoogleMap googleMap;
    private LatLng myLocation;
    private AuthUserDTO userDTO;
    private ProgressBar progressView;

    private PlacesGetTask placesGetTask;
    private GetUserTravelsTask getUserTravelsTask;

    private Map<PlaceMapDTO, Marker> placeMapDTOMarkers = new HashMap<>();

    private PlaceAutocompleteFragment autocompleteFragment;
    private android.app.Fragment currentMapInnerFragment;
    private LinearLayout mapInnerFragmentLayout;

    private GetPlaceDescriptionTask getPlaceDescriptionTask;

    private DrawerLayout drawer;
    private LinearLayout drawerLayout;

    private PlaceMapDTO focusPlaceMapDTO;
    private TravelDTO selectedTravel;
    private SeekBar commentsCountSeekBar;
    private SeekBar minRatingSeekBar;
    private SeekBar maxRatingSeekBar;
    private SeekBar photosCountSeekBar;
    private CheckBox showplaceCheckbox;
    private CheckBox sleepCheckbox;
    private CheckBox foodCheckbox;

    public static Fragment newInstance(UserDTO userDTO) {
        return newInstance(userDTO, null);
    }

    public static Fragment newInstance(UserDTO userDTO, PlaceMapDTO placeMapDTO) {
        TripsMapFragment fragment = new TripsMapFragment();
        Bundle args = new Bundle();
        args.putSerializable(LoginActivity.USER_DTO_PARAM, userDTO);
        args.putSerializable(TripsMapFragment.PLACE_DTO_PARAM, placeMapDTO);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if (getArguments() != null) {
            userDTO = (AuthUserDTO) getArguments().getSerializable(LoginActivity.USER_DTO_PARAM);
            focusPlaceMapDTO = (PlaceMapDTO) getArguments().getSerializable(TripsMapFragment.PLACE_DTO_PARAM);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (getActivity() != null) {
            FragmentUtil.closeFragmentButtonClicked(getActivity(), autocompleteFragment);
            closeMapInnerFragment();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            interactionListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement TravelsFragmentRouteButtonListener");
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                drawer.openDrawer(drawerLayout);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initPlaceAutocompleteFragment() {
        this.autocompleteFragment = new PlaceAutocompleteFragment();
        FragmentManager fragmentManager = getActivity().getFragmentManager();
        android.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.layout_place_autocomplete_fragment, autocompleteFragment);
        fragmentTransaction.commit();

        this.autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                TripsMapFragment.this.googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(place.getLatLng(), DEFAULT_CAMERA_FOCUS_ZOOM));
            }

            @Override
            public void onError(Status status) {
                Log.i(getClass().getSimpleName(), "An error occurred: " + status);
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_trips_map, container, false);
        Toolbar toolbar = rootView.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ActionBar actionbar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu_home);

        mapInnerFragmentLayout = rootView.findViewById(R.id.map_inner_fragment);

        drawer = rootView.findViewById(R.id.drawer);
        drawerLayout = rootView.findViewById(R.id.drawer_layout);

        NavigationView navigationView = rootView.findViewById(R.id.trips_map_navigation_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                item.setChecked(true);
                switch (item.getItemId()) {
                    case R.id.nav_travels: {
                        requestOpenTripsFragment();
                    }
                    break;
                }
                // Add code here to update the UI based on the item selected
                // For example, swap UI fragments here
                drawer.closeDrawer(drawerLayout);
                return true;
            }
        });

        initPlaceAutocompleteFragment();

        progressView = rootView.findViewById(R.id.trips_map_progress);

        showplaceCheckbox = rootView.findViewById(R.id.map_filter_checkbox_showplace);
        sleepCheckbox = rootView.findViewById(R.id.map_filter_checkbox_sleep);
        foodCheckbox = rootView.findViewById(R.id.map_filter_checkbox_food);

        minRatingSeekBar = rootView.findViewById(R.id.map_filter_settings_min_rating_seek_bar);
        maxRatingSeekBar = rootView.findViewById(R.id.map_filter_settings_max_rating_seek_bar);
        commentsCountSeekBar = rootView.findViewById(R.id.map_filter_settings_comments_count_seek_bar);
        photosCountSeekBar = rootView.findViewById(R.id.map_filter_settings_photos_count_seek_bar);

        mapView = rootView.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.onResume(); // needed to get the map to display immediately

        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {
                googleMap = mMap;
                // For showing a move to my location button
                if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                }
                googleMap.setMyLocationEnabled(true);

                LocationManager locationManager = (LocationManager)
                        getContext().getSystemService(Context.LOCATION_SERVICE);
                Location location = locationManager.getLastKnownLocation(locationManager
                        .getBestProvider(new Criteria(), false));

                myLocation = location != null ? new LatLng(location.getLatitude(), location.getLongitude()) : new LatLng(65.9667, -18.5333);

                googleMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
                    @Override
                    public void onCameraIdle() {
                        loadAndShowAreaShowplaces(googleMap.getProjection().getVisibleRegion().latLngBounds);
                    }
                });
                googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(LatLng arg0) {
                        closeMapInnerFragment();
                    }
                });
                googleMap.setOnMarkerClickListener(TripsMapFragment.this);

                if (selectedTravel == null) {
                    focusMapPlace();
                } else {
                    focusMapTravel(selectedTravel.getPlaces());
                }
            }
        });

        return rootView;
    }

    public void focusMapTravel(List<PlaceMapDTO> places) {
        List<LatLng> travelPlacesCoordinates = new ArrayList<>(Collections.singletonList(myLocation));

        for (PlaceMapDTO placeMapDTO : places) {
            travelPlacesCoordinates.add(new LatLng(placeMapDTO.getLatitude(), placeMapDTO.getLongitude()));
        }

        Routing routing = new Routing.Builder()
                .travelMode(AbstractRouting.TravelMode.DRIVING)
                .key("AIzaSyCvYaBmgvYI6Hs5YQs-Doww2YaWQdCIcPg")
                .travelMode(Routing.TravelMode.WALKING)
                .withListener(new RoutingListener() {
                    @Override
                    public void onRoutingFailure(RouteException e) {
                        Log.e(getClass().getSimpleName(), e.getMessage(), e);
                    }

                    @Override
                    public void onRoutingStart() {

                    }

                    @Override
                    public void onRoutingSuccess(ArrayList<Route> route, int shortestRouteIndex) {
                        Route shortestRoute = route.get(shortestRouteIndex);
                        TripsMapFragment.this.googleMap.addPolyline(shortestRoute.getPolyOptions());
                    }

                    @Override
                    public void onRoutingCancelled() {

                    }
                })
                .waypoints(travelPlacesCoordinates)
                .build();
        routing.execute();
        closeMapInnerFragment();
    }

    private void closeMapInnerFragment() {
        FragmentUtil.closeFragmentButtonClicked(getActivity(), currentMapInnerFragment);
        this.currentMapInnerFragment = null;
        this.mapInnerFragmentLayout.setVisibility(View.GONE);
    }

    private void focusMapPlace() {
        if (focusPlaceMapDTO == null) {
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLocation, DEFAULT_CAMERA_FOCUS_ZOOM));
        } else {
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(focusPlaceMapDTO.getPlaceLatLng(), DEFAULT_CAMERA_FOCUS_ZOOM));
            focusPlaceMapDTO = null;
        }
    }

    private void requestOpenTripsFragment() {
        if (interactionListener != null) {
            if (TripsMapFragment.this.getUserTravelsTask != null) {
                return;
            }
            showGetUserTravelsTaskProgress(true);
            TripsMapFragment.this.getUserTravelsTask = new GetUserTravelsTask(userDTO, TripsMapFragment.this);
            TripsMapFragment.this.getUserTravelsTask.execute((Void) null);
        }
    }

    private void loadAndShowAreaShowplaces(LatLngBounds bounds) {
        double distance = getBoundsDistance(bounds);
        Log.i(getClass().getSimpleName(), "User map view bounds coordinates distance: " + distance);
        startPlacesGetTask(bounds);
    }

    private void startPlacesGetTask(LatLngBounds bounds) {
        if (placesGetTask != null) {
            return;
        }
        // Show a progress spinner, and kick off a background task to
        // perform the user login attempt.
        showProgress(true);
        placesGetTask = new PlacesGetTask(getTripsMapPlacesFilterDTO(), bounds, userDTO);
        placesGetTask.addShowplacesGetTaskListener(this);
        placesGetTask.execute((Void) null);
    }

    private TripsMapPlacesFilterDTO getTripsMapPlacesFilterDTO() {
        List<PlaceType> placeTypes = new ArrayList<>();
        if (showplaceCheckbox.isChecked()) {
            placeTypes.add(PlaceType.SHOWPLACE);
        }
        if (foodCheckbox.isChecked()) {
            placeTypes.add(PlaceType.FOOD);
        }
        if (sleepCheckbox.isChecked()) {
            placeTypes.add(PlaceType.SLEEP);
        }

        int maxRating = maxRatingSeekBar.getProgress();
        int minRating = minRatingSeekBar.getProgress();
        int commentsCount = commentsCountSeekBar.getProgress();
        int photosCount = photosCountSeekBar.getProgress();
        return new TripsMapPlacesFilterDTO(placeTypes, photosCount, minRating, maxRating, commentsCount);
    }

    private double getBoundsDistance(LatLngBounds bounds) {
        LatLng upRightPoint = bounds.northeast;
        LatLng bottomLeftPoint = bounds.southwest;

        return TravelAppUtils.distance(upRightPoint.latitude, bottomLeftPoint.latitude, upRightPoint.longitude, bottomLeftPoint.longitude);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
            // other 'case' lines to check for other
            // permissions this app might request
        }
    }


    @Override
    public void onDetach() {
        super.onDetach();
        interactionListener = null;
    }

    @Override
    public void setPlacesGetTask(PlacesGetTask placesGetTask) {
        this.placesGetTask = placesGetTask;
    }

    @Override
    public void setGetPlaceDescriptionTask(GetPlaceDescriptionTask getPlaceDescriptionTask) {
        this.getPlaceDescriptionTask = getPlaceDescriptionTask;
    }

    @Override
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public void showProgress(final boolean show) {
        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

        progressView.setVisibility(show ? View.VISIBLE : View.GONE);
        progressView.animate().setDuration(shortAnimTime).alpha(
                show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                progressView.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        });
    }

    @Override
    public void showGetPlaceDescriptionDTOTaskError() {

    }

    public void showTravelsFragment(TravelDTOList travelDTOList) {
        this.currentMapInnerFragment = TravelsFragment.newInstance(userDTO, travelDTOList, false);
        FragmentManager fragmentManager = getActivity().getFragmentManager();
        android.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.map_inner_fragment, currentMapInnerFragment);
        fragmentTransaction.commit();
        this.mapInnerFragmentLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void showPlaceDescriptionDTO(PlaceDescriptionDTO placeDescriptionDTO) {
        this.currentMapInnerFragment = MapPlaceDescriptionFragment.newInstance(userDTO, placeDescriptionDTO);
        FragmentManager fragmentManager = getActivity().getFragmentManager();
        android.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.map_inner_fragment, currentMapInnerFragment);
        fragmentTransaction.commit();
        this.mapInnerFragmentLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void showPlacesGetTaskError() {
        Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showPlaceMapDTOList(PlaceMapDTOList placeMapDTOList) {
        List<PlaceMapDTO> placesToAdd = new ArrayList<>();
        for (PlaceMapDTO newPlaceMapDTO : placeMapDTOList.getPlaceMapDTOList()) {
            boolean alreadyAdded = false;
            for (PlaceMapDTO mapPlaceMapDTO : placeMapDTOMarkers.keySet()) {
                if (newPlaceMapDTO.equals(mapPlaceMapDTO)) {
                    alreadyAdded = true;
                }
            }
            if (!alreadyAdded) {
                placesToAdd.add(newPlaceMapDTO);
            }
        }
        addPlacesToMap(placesToAdd);

        List<PlaceMapDTO> placesToDelete = new ArrayList<>();
        for (PlaceMapDTO mapPlaceMapDTO : placeMapDTOMarkers.keySet()) {
            boolean toDelete = true;
            for (PlaceMapDTO newPlaceMapDTO : placeMapDTOList.getPlaceMapDTOList()) {
                if (newPlaceMapDTO.equals(mapPlaceMapDTO)) {
                    toDelete = false;
                }
            }
            if (toDelete) {
                placesToDelete.add(mapPlaceMapDTO);
            }
        }
        removePlacesFromMap(placesToDelete);
    }

    private void removePlacesFromMap(List<PlaceMapDTO> placesToDelete) {
        for (PlaceMapDTO placeMapDTO : placesToDelete) {
            placeMapDTOMarkers.remove(placeMapDTO).remove();
        }
    }

    private void addPlacesToMap(List<PlaceMapDTO> placeMapDTOS) {
        for (PlaceMapDTO placeMapDTO : placeMapDTOS) {
            int resourceId = R.drawable.places_ic_clear;
            switch (placeMapDTO.getType()) {
                case SHOWPLACE: {
                    resourceId = R.drawable.ic_place_showplace;
                    break;
                }
                case FOOD: {
                    resourceId = R.drawable.ic_place_food;
                    break;
                }
                case SLEEP: {
                    resourceId = R.drawable.ic_place_sleep;
                    break;
                }
            }

            Marker marker = googleMap.addMarker(new MarkerOptions()
                    .position(new LatLng(placeMapDTO.getLatitude(), placeMapDTO.getLongitude()))
                    .title(placeMapDTO.getName()).icon(BitmapDescriptorFactory.fromResource(resourceId)));
            placeMapDTOMarkers.put(placeMapDTO, marker);
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        for (Map.Entry<PlaceMapDTO, Marker> entry : placeMapDTOMarkers.entrySet()) {
            if (entry.getValue().equals(marker)) {
                loadAndShowPlaceDescription(entry.getKey());
                break;
            }
        }

        return false;
    }

    private void loadAndShowPlaceDescription(PlaceMapDTO placeMapDTO) {
        if (getPlaceDescriptionTask == null) {
            showProgress(true);
            getPlaceDescriptionTask = new GetPlaceDescriptionTask(placeMapDTO.getLatitude(), placeMapDTO.getLongitude(), userDTO, this);
            getPlaceDescriptionTask.execute((Void) null);
        }
    }

    @Override
    public void setGetUserTravelsTask(GetUserTravelsTask getUserTravelsTask) {
        this.getUserTravelsTask = getUserTravelsTask;
    }

    @Override
    public void showGetUserTravelsTaskProgress(boolean show) {

    }

    @Override
    public void showGetUserTravelsTaskError() {
        Toast.makeText(getContext(), "Error", Toast.LENGTH_LONG).show();
    }

    @Override
    public void showTravels(List<TravelDTO> travels) {
        showTravelsFragment(new TravelDTOList(travels));
    }

    @Override
    public void onTravelRouteButtonClicked(List<PlaceMapDTO> placeMapDTOList) {
        focusMapTravel(placeMapDTOList);
    }

    @Override
    public void onTravelPlaceShowClicked(PlaceMapDTO placeMapDTO) {
        loadAndShowPlaceDescription(placeMapDTO);
    }

    public interface OnFragmentInteractionListener {
        void onPlaceSelected(PlaceMapDTO placeMapDTO);
    }
}
