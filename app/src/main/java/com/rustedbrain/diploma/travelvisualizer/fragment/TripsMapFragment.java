package com.rustedbrain.diploma.travelvisualizer.fragment;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.rustedbrain.diploma.travelvisualizer.R;


public class TripsMapFragment extends Fragment {

    public static final String ADD_PLACE_ARG = "add_place";

    MapView mMapView;
    private GoogleMap mMap;
    // TODO: Rename and change types of parameters
    private boolean addPlace;

    private OnFragmentInteractionListener mListener;
    private GoogleMap googleMap;

    private LatLng myLocation;
    private Marker selectedPlace;

    public TripsMapFragment() {
        // Required empty public constructor
    }

    public static TripsMapFragment newInstance(boolean addPlace) {
        TripsMapFragment fragment = new TripsMapFragment();
        Bundle args = new Bundle();
        args.putBoolean(ADD_PLACE_ARG, addPlace);
        fragment.setArguments(args);
        return fragment;
    }

    public static TripsMapFragment newInstance() {
        return new TripsMapFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.

        if (getArguments() != null) {
            addPlace = getArguments().getBoolean(ADD_PLACE_ARG);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_trips_map, container, false);

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
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 12));
            }
        });
        return rootView;
    }

    @Override
    public void onStart() {
        AlertDialog.Builder adb = new AlertDialog.Builder(getActivity());

        adb.setTitle("Place coordinates: ");
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

    public interface OnFragmentInteractionListener {
        void onMapPlaceSelected(LatLng latLng);
    }
}
