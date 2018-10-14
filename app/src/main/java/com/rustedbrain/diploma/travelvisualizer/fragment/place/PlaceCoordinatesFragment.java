package com.rustedbrain.diploma.travelvisualizer.fragment.place;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.rustedbrain.diploma.travelvisualizer.LoginActivity;
import com.rustedbrain.diploma.travelvisualizer.R;
import com.rustedbrain.diploma.travelvisualizer.model.dto.security.UserDTO;

public class PlaceCoordinatesFragment extends Fragment {

    private MapView mMapView;
    private GoogleMap mMap;

    private OnFragmentInteractionListener mListener;
    private GoogleMap googleMap;

    private LatLng myLocation;
    private Marker selectedPlace;

    private UserDTO userDTO;
    private double latitude;
    private double longitude;

    public PlaceCoordinatesFragment() {
        // Required empty public constructor
    }

    public static PlaceCoordinatesFragment newInstance(UserDTO user, double lat, double lng) {
        PlaceCoordinatesFragment fragment = new PlaceCoordinatesFragment();
        Bundle args = new Bundle();
        args.putSerializable(LoginActivity.USER_DTO_PARAM, user);
        args.putDouble(PlaceDescriptionFragment.LAT_ARG_PARAM, lat);
        args.putDouble(PlaceDescriptionFragment.LNG_ARG_PARAM, lng);
        fragment.setArguments(args);
        return fragment;
    }

    public static PlaceCoordinatesFragment newInstance(UserDTO user) {
        PlaceCoordinatesFragment fragment = new PlaceCoordinatesFragment();
        Bundle args = new Bundle();
        args.putSerializable(LoginActivity.USER_DTO_PARAM, user);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            userDTO = (UserDTO) getArguments().getSerializable(LoginActivity.USER_DTO_PARAM);
            latitude = getArguments().getDouble(PlaceDescriptionFragment.LAT_ARG_PARAM);
            longitude = getArguments().getDouble(PlaceDescriptionFragment.LNG_ARG_PARAM);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_place_coordinates, container, false);

        Button markerOnMeButton = rootView.findViewById(R.id.button_marker_on_me);
        markerOnMeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onMarkerOnMeButtonClicked();
            }
        });
        Button cancelButton = rootView.findViewById(R.id.button_place_creation_cancel);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCancelButtonClicked();
            }
        });
        Button openDescriptionButton = rootView.findViewById(R.id.button_place_creation_open_description);
        openDescriptionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCoordinateSelectionConfirmDialog(selectedPlace.getPosition());
            }
        });

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

                if (latitude != 0.0 && longitude != 0.0) {
                    myLocation = new LatLng(latitude, longitude);
                    setMarkerPosition(myLocation);
                } else {
                    LocationManager locationManager = (LocationManager)
                            getContext().getSystemService(Context.LOCATION_SERVICE);
                    Location location = locationManager.getLastKnownLocation(locationManager
                            .getBestProvider(new Criteria(), false));
                    if (location == null) {
                        myLocation = new LatLng(31.28487, 51.50551);
                    } else {
                        myLocation = new LatLng(location.getLatitude(), location.getLongitude());
                    }
                }
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 12));

                googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(LatLng latLng) {
                        setMarkerPosition(latLng);
                    }
                });
            }
        });
        return rootView;
    }

    private void onCancelButtonClicked() {
        if (mListener != null) {
            mListener.onPlaceCoordinatesFragmentCancelButtonClicked();
        }
    }

    private void onMarkerOnMeButtonClicked() {
        setMarkerPosition(myLocation);
    }

    private void setMarkerPosition(LatLng latLng) {
        if (selectedPlace != null) {
            selectedPlace.remove();
        }
        selectedPlace = googleMap.addMarker(new MarkerOptions().position(latLng).title("Marker Title").snippet("Marker Description"));
    }

    @Override
    public void onStart() {
        AlertDialog.Builder adb = new AlertDialog.Builder(getActivity());

        adb.setTitle("Please mark place on map ");
        adb.setIcon(android.R.drawable.ic_dialog_alert);
        adb.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        adb.show();
        super.onStart();
    }

    private void showCoordinateSelectionConfirmDialog(final LatLng latLng) {
        AlertDialog.Builder adb = new AlertDialog.Builder(getActivity());

        adb.setTitle("Place coordinates: " + latLng.latitude + ", " + latLng.longitude);
        adb.setIcon(android.R.drawable.ic_dialog_alert);
        adb.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                if (mListener != null) {
                    mListener.onMapPlaceSelected(latLng);
                }
            }
        });

        adb.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        adb.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
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

    public interface OnFragmentInteractionListener {
        void onMapPlaceSelected(LatLng latLng);

        void onPlaceCoordinatesFragmentCancelButtonClicked();
    }
}
