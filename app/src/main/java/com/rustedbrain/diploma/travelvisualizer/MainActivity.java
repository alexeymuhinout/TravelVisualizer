package com.rustedbrain.diploma.travelvisualizer;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.google.android.gms.maps.model.LatLng;
import com.rustedbrain.diploma.travelvisualizer.dummy.DummyContent;
import com.rustedbrain.diploma.travelvisualizer.fragment.HomeFragment;
import com.rustedbrain.diploma.travelvisualizer.fragment.MapPlaceDescriptionFragment;
import com.rustedbrain.diploma.travelvisualizer.fragment.TripsMapFragment;
import com.rustedbrain.diploma.travelvisualizer.fragment.home.ProfileFragment;
import com.rustedbrain.diploma.travelvisualizer.fragment.home.TripsListFragment;
import com.rustedbrain.diploma.travelvisualizer.fragment.place.PlaceCoordinatesFragment;
import com.rustedbrain.diploma.travelvisualizer.fragment.place.PlaceDescriptionFragment;
import com.rustedbrain.diploma.travelvisualizer.fragment.place.PlacePhotosFragment;
import com.rustedbrain.diploma.travelvisualizer.model.dto.security.UserDTO;
import com.rustedbrain.diploma.travelvisualizer.model.dto.travel.PlaceMapDTO;
import com.rustedbrain.diploma.travelvisualizer.model.dto.travel.PlaceType;

public class MainActivity extends AppCompatActivity implements PlacePhotosFragment.OnFragmentInteractionListener, TripsListFragment.OnListFragmentInteractionListener, HomeFragment.OnHomeFragmentButtonClickListener, PlaceCoordinatesFragment.OnFragmentInteractionListener, ProfileFragment.OnFragmentInteractionListener, PlaceDescriptionFragment.OnFragmentInteractionListener, TripsMapFragment.OnFragmentInteractionListener, MapPlaceDescriptionFragment.OnFragmentInteractionListener  {

    public static final String AUTH_TOKEN_HEADER_NAME = "X-AUTH-TOKEN";
    private UserDTO userDTO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_layout, HomeFragment.newInstance());
        transaction.commit();
        //Used to select an item programmatically
        //bottomNavigationView.getMenu().getItem(2).setChecked(true);
        initIntentVariables();
    }

    private void initIntentVariables() {
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            userDTO = (UserDTO) bundle.getSerializable(LoginActivity.USER_DTO_PARAM);
        }
    }

    private void openHomeFragment() {
        Fragment selectedFragment = HomeFragment.newInstance();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_layout, selectedFragment);
        transaction.commit();
    }

    @Override
    public void onButtonClicked(ClickedButton clickedButton) {
        switch (clickedButton) {
            case PROFILE: {
                Fragment selectedFragment = ProfileFragment.newInstance();
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.frame_layout, selectedFragment);
                transaction.commit();
            }
            break;
            case SAVED_ROUTES: {
                Fragment selectedFragment = TripsListFragment.newInstance();
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.frame_layout, selectedFragment);
                transaction.commit();
            }
            break;
            case ARCHIVED_ROUTES: {
                Fragment selectedFragment = PlaceCoordinatesFragment.newInstance(userDTO);
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.frame_layout, selectedFragment);
                transaction.commit();
            }
            case ADD_PLACE: {
                Fragment selectedFragment = PlaceCoordinatesFragment.newInstance(userDTO);
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.frame_layout, selectedFragment);
                transaction.commit();
            }
            break;
        }
    }

    @Override
    public void onListFragmentInteraction(DummyContent.DummyItem item) {

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
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_layout, selectedFragment);
        transaction.commit();
    }

    private void openDescriptionFragment(PlaceType placeType, String placeName, String placeDescription, double placeLatitude, double placeLongitude) {
        Fragment selectedFragment = PlaceDescriptionFragment.newInstance(userDTO, placeType, placeName, placeDescription, placeLatitude, placeLongitude);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_layout, selectedFragment);
        transaction.commit();
    }

    private void openPhotosFragment(PlaceType placeType, String name, String description, double latitude, double longitude) {
        Fragment selectedFragment = PlacePhotosFragment.newInstance(userDTO, placeType, name, description, latitude, longitude);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
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
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
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

    private void openTripMapFragment() {
        Fragment selectedFragment = TripsMapFragment.newInstance(userDTO);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_layout, selectedFragment);
        transaction.commit();
    }

    private void openTripMapFragment(PlaceMapDTO placeMapDTO) {
        Fragment selectedFragment = TripsMapFragment.newInstance(userDTO, placeMapDTO);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_layout, selectedFragment);
        transaction.commit();
    }

    @Override
    public void onPlaceSelected(PlaceMapDTO placeMapDTO) {

    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
