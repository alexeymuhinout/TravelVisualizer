package com.rustedbrain.diploma.travelvisualizer;

import android.Manifest;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.google.android.gms.maps.model.LatLng;
import com.rustedbrain.diploma.travelvisualizer.fragment.HomeFragment;
import com.rustedbrain.diploma.travelvisualizer.fragment.MapPlaceDescriptionFragment;
import com.rustedbrain.diploma.travelvisualizer.fragment.TripsMapFragment;
import com.rustedbrain.diploma.travelvisualizer.fragment.home.ProfileFragment;
import com.rustedbrain.diploma.travelvisualizer.fragment.place.PlaceCoordinatesFragment;
import com.rustedbrain.diploma.travelvisualizer.fragment.place.PlaceDescriptionFragment;
import com.rustedbrain.diploma.travelvisualizer.fragment.place.PlacePhotosFragment;
import com.rustedbrain.diploma.travelvisualizer.fragment.travel.TravelsFragment;
import com.rustedbrain.diploma.travelvisualizer.fragment.travel.TravelsShareFragment;
import com.rustedbrain.diploma.travelvisualizer.model.dto.security.AuthUserDTO;
import com.rustedbrain.diploma.travelvisualizer.model.dto.travel.LatLngDTO;
import com.rustedbrain.diploma.travelvisualizer.model.dto.travel.PlaceMapDTO;
import com.rustedbrain.diploma.travelvisualizer.model.dto.travel.PlaceType;
import com.rustedbrain.diploma.travelvisualizer.model.dto.travel.TravelDTO;
import com.rustedbrain.diploma.travelvisualizer.model.dto.travel.TravelDTOList;
import com.rustedbrain.diploma.travelvisualizer.task.travel.GetUserTravelsTask;
import com.rustedbrain.diploma.travelvisualizer.task.travel.SendUserCoordinatesTask;

import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity implements SendUserCoordinatesTask.Listener, GetUserTravelsTask.Listener, TravelsShareFragment.TravelsShareFragmentListener, PlacePhotosFragment.OnFragmentInteractionListener, HomeFragment.OnHomeFragmentButtonClickListener, PlaceCoordinatesFragment.OnFragmentInteractionListener, ProfileFragment.OnFragmentInteractionListener, PlaceDescriptionFragment.OnFragmentInteractionListener, TripsMapFragment.OnFragmentInteractionListener, MapPlaceDescriptionFragment.OnFragmentInteractionListener, TravelsFragment.TravelsFragmentRouteButtonListener {

    public static final String AUTH_TOKEN_HEADER_NAME = "X-AUTH-TOKEN";
    private AuthUserDTO userDTO;
    private TripsMapFragment tripsMapFragment;
    private TravelsFragment travelsFragment;
    private GetUserTravelsTask getUserTravelsTask;

    private Timer timer;
    private SendUserCoordinatesTask sendUserCoordinatesTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initIntentVariables();
        initSendUserCoordinatesTask();

        BottomNavigationView bottomNavigationView = findViewById(R.id.navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener
                (new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.action_item_home: {
                                openHomeFragment();
                            }
                            break;
                            case R.id.action_item_trip: {
                                openTripMapFragment();
                            }
                            break;
                        }
                        return true;
                    }
                });

        //Manually displaying the first fragment - one time only
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_layout, HomeFragment.newInstance(userDTO));
        transaction.commit();
        //Used to select an item programmatically
        //bottomNavigationView.getMenu().getItem(2).setChecked(true);
    }

    private void initSendUserCoordinatesTask() {
        if (timer!= null){
            timer.cancel();
        }
        timer = new Timer(true);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (sendUserCoordinatesTask != null) {
                    return;
                }

                if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                }
                LocationManager locationManager = (LocationManager)
                        getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
                Location location = locationManager.getLastKnownLocation(locationManager
                        .getBestProvider(new Criteria(), false));

                LatLng myLocation = location != null ? new LatLng(location.getLatitude(), location.getLongitude()) : new LatLng(65.9667, -18.5333);

                MainActivity.this.sendUserCoordinatesTask = new SendUserCoordinatesTask(userDTO, new LatLngDTO(myLocation.latitude, myLocation.longitude), MainActivity.this);
                MainActivity.this.sendUserCoordinatesTask.execute((Void) null);

            }
        }, 0, TimeUnit.SECONDS.toMillis(10));
    }

    private void initIntentVariables() {
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            userDTO = (AuthUserDTO) bundle.getSerializable(LoginActivity.USER_DTO_PARAM);
        }
    }

    @Override
    public void onProfileButtonClicked() {
        Fragment selectedFragment = ProfileFragment.newInstance();
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_layout, selectedFragment);
        transaction.commit();
    }

    @Override
    public void onSavedRoutesButtonClicked(List<TravelDTO> travels) {
        FragmentManager fragmentManager = this.getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        this.travelsFragment = TravelsFragment.newInstance(userDTO, new TravelDTOList(travels), false);
        fragmentTransaction.replace(R.id.frame_layout, travelsFragment);
        fragmentTransaction.commit();
    }

    @Override
    public void onAddPlaceButtonClicked() {
        android.app.FragmentTransaction transaction = this.getFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_layout, PlaceCoordinatesFragment.newInstance(userDTO));
        transaction.commit();
    }

    @Override
    public void onArchivedRoutesButtonClicked(List<TravelDTO> travels) {
        FragmentManager fragmentManager = this.getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        this.travelsFragment = TravelsFragment.newInstance(userDTO, new TravelDTOList(travels), true);
        fragmentTransaction.replace(R.id.frame_layout, travelsFragment);
        fragmentTransaction.commit();
    }

    @Override
    public void onMapPlaceSelected(LatLng latLng) {
        openDescriptionFragment(latLng.latitude, latLng.longitude);
    }

    @Override
    public void onPlaceCoordinatesFragmentCancelButtonClicked() {
        openHomeFragment();
    }

    private void openDescriptionFragment(double placeLatitude, double placeLongitude) {
        Fragment selectedFragment = PlaceDescriptionFragment.newInstance(userDTO, placeLatitude, placeLongitude);
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_layout, selectedFragment);
        transaction.commit();
    }

    private void openDescriptionFragment(PlaceType placeType, String placeName, String placeDescription, double placeLatitude, double placeLongitude) {
        Fragment selectedFragment = PlaceDescriptionFragment.newInstance(userDTO, placeType, placeName, placeDescription, placeLatitude, placeLongitude);
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_layout, selectedFragment);
        transaction.commit();
    }

    private void openPhotosFragment(PlaceType placeType, String name, String description, double latitude, double longitude) {
        Fragment selectedFragment = PlacePhotosFragment.newInstance(userDTO, placeType, name, description, latitude, longitude);
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_layout, selectedFragment);
        transaction.commit();
    }

    @Override
    public void onFragmentDescriptionButtonNextClicked(PlaceType placeType, String name, String description, double latitude, double longitude) {
        openPhotosFragment(placeType, name, description, latitude, longitude);
    }

    @Override
    public void onDescriptionCancelButtonClicked() {
        openHomeFragment();
    }

    @Override
    public void onFragmentDescriptionButtonBackClicked(double lat, double lng) {
        Fragment selectedFragment = PlaceCoordinatesFragment.newInstance(userDTO, lat, lng);
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_layout, selectedFragment);
        transaction.commit();
    }

    @Override
    public void onPlacePhotosFragmentCancelClicked() {
        openHomeFragment();
    }

    @Override
    public void onPlacePhotosFragmentBackClicked(PlaceType placeType, String placeName, String placeDescription, double placeLatitude, double placeLongitude) {
        openDescriptionFragment(placeType, placeName, placeDescription, placeLatitude, placeLongitude);
    }

    @Override
    public void onPlacePhotosFragmentNextClicked(PlaceMapDTO placeMapDTO) {
        openTripMapFragment(placeMapDTO);
    }

    private void openHomeFragment() {
        Fragment selectedFragment = HomeFragment.newInstance(userDTO);
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_layout, selectedFragment);
        transaction.commit();
    }

    private void openTripMapFragment() {
        if (getUserTravelsTask != null) {
            return;
        }

        this.getUserTravelsTask = new GetUserTravelsTask(userDTO, this);
        this.getUserTravelsTask.execute((Void) null);
    }

    private void openTripMapFragment(final PlaceMapDTO placeMapDTO) {
        if (getUserTravelsTask != null) {
            return;
        }

        this.getUserTravelsTask = new GetUserTravelsTask(userDTO, new GetUserTravelsTask.Listener() {
            @Override
            public void setGetUserTravelsTask(GetUserTravelsTask getUserTravelsTask) {
                MainActivity.this.setGetUserTravelsTask(getUserTravelsTask);
            }

            @Override
            public void showGetUserTravelsTaskProgress(boolean show) {
                MainActivity.this.showGetUserTravelsTaskProgress(show);
            }

            @Override
            public void showGetUserTravelsTaskError() {
                MainActivity.this.showGetUserTravelsTaskError();
            }

            @Override
            public void onTravelsLoadSuccess(List<TravelDTO> travels) {
                Fragment selectedFragment = TripsMapFragment.newInstance(userDTO, new TravelDTOList(travels), placeMapDTO);
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.frame_layout, selectedFragment);
                transaction.commit();
                tripsMapFragment = (TripsMapFragment) selectedFragment;
            }
        });
        this.getUserTravelsTask.execute((Void) null);
    }

    @Override
    public void onPlaceSelected(PlaceMapDTO placeMapDTO) {

    }

    @Override
    public void onTravelRouteButtonClicked(List<PlaceMapDTO> placeMapDTOList) {
        if (tripsMapFragment != null) {
            tripsMapFragment.onTravelRouteButtonClicked(placeMapDTOList);
        }
    }

    @Override
    public void onTravelPlaceShowClicked(PlaceMapDTO placeMapDTO) {
        if (tripsMapFragment != null) {
            tripsMapFragment.onTravelPlaceShowClicked(placeMapDTO);
        } else {
            openTripMapFragment(placeMapDTO);
        }
    }

    @Override
    public void onTravelsFragmentCloseButtonClicked() {
        if (tripsMapFragment != null) {
            tripsMapFragment.onTravelsFragmentCloseButtonClicked();
        }
    }

    @Override
    public void onTravelShareFragmentCancelButtonClicked() {
        if (travelsFragment != null) {
            travelsFragment.closeInnerFragment();
        }
    }

    @Override
    public void onTravelShareFragmentUsersShared(TravelDTO travelDTO) {
        if (travelsFragment != null) {
            travelsFragment.updateTravel(travelDTO);
            travelsFragment.closeInnerFragment();
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
        Fragment selectedFragment = TripsMapFragment.newInstance(userDTO, new TravelDTOList(Collections.EMPTY_LIST));
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_layout, selectedFragment);
        transaction.commit();
        tripsMapFragment = (TripsMapFragment) selectedFragment;
    }

    @Override
    public void onTravelsLoadSuccess(List<TravelDTO> travels) {
        Fragment selectedFragment = TripsMapFragment.newInstance(userDTO, new TravelDTOList(travels));
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_layout, selectedFragment);
        transaction.commit();
        tripsMapFragment = (TripsMapFragment) selectedFragment;
    }

    @Override
    public void onMapPlaceDescriptionFragmentButtonCloseClicked() {
        onTravelsFragmentCloseButtonClicked();
    }

    @Override
    public void setSendUserCoordinatesTask(SendUserCoordinatesTask sendUserCoordinatesTask) {
        this.sendUserCoordinatesTask = sendUserCoordinatesTask;
    }

    @Override
    public void showSendUserCoordinatesTaskProgress(boolean show) {

    }

    @Override
    public void showSendUserCoordinatesTaskError() {

    }

    @Override
    public void showUserCoordinates(LatLngDTO latLngDTO) {

    }
}
