package com.rustedbrain.diploma.travelvisualizer.fragment;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.rustedbrain.diploma.travelvisualizer.LoginActivity;
import com.rustedbrain.diploma.travelvisualizer.R;
import com.rustedbrain.diploma.travelvisualizer.model.dto.security.UserDTO;
import com.rustedbrain.diploma.travelvisualizer.model.dto.travel.PlaceMapDTO;
import com.rustedbrain.diploma.travelvisualizer.model.dto.travel.PlaceMapDTOList;
import com.rustedbrain.diploma.travelvisualizer.task.PlacesGetTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class TripsMapFragment extends Fragment implements PlacesGetTask.PlacesGetTaskListener {

    public static final String PLACE_DTO_PARAM = "place";

    MapView mMapView;

    private OnFragmentInteractionListener mListener;
    private GoogleMap googleMap;

    private LatLng myLocation;
    private UserDTO userDTO;
    private PlaceMapDTO focusPlaceMapDTO;
    private ProgressBar progressView;
    private PlacesGetTask placesGetTask;
    private Map<PlaceMapDTO, Marker> placeMapDTOMarkers = new HashMap<>();
    private PlaceAutocompleteFragment autocompleteFragment;

    public TripsMapFragment() {
        // Required empty public constructor
    }

    public static TripsMapFragment newInstance(UserDTO userDTO) {
        TripsMapFragment fragment = new TripsMapFragment();
        Bundle args = new Bundle();
        args.putSerializable(LoginActivity.USER_DTO_PARAM, userDTO);
        fragment.setArguments(args);
        return fragment;
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

        if (getArguments() != null) {
            userDTO = (UserDTO) getArguments().getSerializable(LoginActivity.USER_DTO_PARAM);
            focusPlaceMapDTO = (PlaceMapDTO) getArguments().getSerializable(TripsMapFragment.PLACE_DTO_PARAM);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_trips_map, container, false);

        autocompleteFragment = (PlaceAutocompleteFragment) getActivity().getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                Log.i(getClass().getSimpleName(), "Place: " + place.getName());
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i(getClass().getSimpleName(), "An error occurred: " + status);
            }
        });

        progressView = rootView.findViewById(R.id.trips_map_progress);

        mMapView = rootView.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);

        mMapView.onResume(); // needed to get the map to display immediately

        mMapView.getMapAsync(new OnMapReadyCallback() {
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
                myLocation = new LatLng(location.getLatitude(), location.getLongitude());

                googleMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
                    @Override
                    public void onCameraIdle() {
                        LatLngBounds bounds = googleMap.getProjection().getVisibleRegion().latLngBounds;
                        loadAndShowAreaShowplaces(bounds);
                    }
                });
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 12));
            }
        });

        return rootView;
    }

    private void loadAndShowAreaShowplaces(LatLngBounds bounds) {
        double distance = getBoundsDistance(bounds);
        Log.i(getClass().getSimpleName(), "User map view bounds coordinates distance: " + distance);
        lol(bounds);
    }


    private void lol(LatLngBounds bounds) {
        if (mListener != null) {
            if (placesGetTask != null) {
                return;
            }
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            placesGetTask = new PlacesGetTask(bounds, userDTO);
            placesGetTask.addShowplacesGetTaskListener(this);
            placesGetTask.execute((Void) null);
        }
    }

    private double getBoundsDistance(LatLngBounds bounds) {
        LatLng upRightPoint = bounds.northeast;
        LatLng bottomLeftPoint = bounds.southwest;

        return distance(upRightPoint.latitude, bottomLeftPoint.latitude, upRightPoint.longitude, bottomLeftPoint.longitude);
    }


    private double distance(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        dist = dist * 1.609344;
        return (dist);
    }

    /*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    /*::  This function converts decimal degrees to radians             :*/
    /*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    /*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    /*::  This function converts radians to decimal degrees             :*/
    /*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
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
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void setPlacesGetTask(PlacesGetTask placesGetTask) {
        this.placesGetTask = placesGetTask;
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
            for (PlaceMapDTO gettedPlaceMapDTO : placeMapDTOList.getPlaceMapDTOList()) {
                if (gettedPlaceMapDTO.equals(mapPlaceMapDTO)) {
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

            BitmapDescriptor markerIcon = BitmapDescriptorFactory.fromResource(resourceId);
            Marker marker = googleMap.addMarker(new MarkerOptions()
                    .position(new LatLng(placeMapDTO.getLatitude(), placeMapDTO.getLongitude()))
                    .title(placeMapDTO.getName())
                    .snippet("Population: 4,137,400").icon(markerIcon));
            placeMapDTOMarkers.put(placeMapDTO, marker);
        }
    }

    public interface OnFragmentInteractionListener {
        void onPlaceSelected(PlaceMapDTO placeMapDTO);
    }
}
