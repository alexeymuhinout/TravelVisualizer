package com.rustedbrain.diploma.travelvisualizer;

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
import com.rustedbrain.diploma.travelvisualizer.fragment.TripsMapFragment;
import com.rustedbrain.diploma.travelvisualizer.fragment.home.ProfileFragment;
import com.rustedbrain.diploma.travelvisualizer.fragment.home.TripsListFragment;
import com.rustedbrain.diploma.travelvisualizer.fragment.place.PlaceCoordinatesFragment;
import com.rustedbrain.diploma.travelvisualizer.fragment.place.PlaceDescriptionFragment;
import com.rustedbrain.diploma.travelvisualizer.fragment.place.PlacePhotosFragment;

public class MainActivity extends AppCompatActivity implements PlacePhotosFragment.OnFragmentInteractionListener, TripsListFragment.OnListFragmentInteractionListener, HomeFragment.OnHomeFragmentButtonClickListener, PlaceCoordinatesFragment.OnFragmentInteractionListener, ProfileFragment.OnFragmentInteractionListener, PlaceDescriptionFragment.OnFragmentInteractionListener {

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
    }

    private void openHomeFragment() {
        Fragment selectedFragment = HomeFragment.newInstance();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_layout, selectedFragment);
        transaction.commit();
    }

    private void openTripMapFragment() {
        Fragment selectedFragment = TripsMapFragment.newInstance();
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
                Fragment selectedFragment = PlaceCoordinatesFragment.newInstance();
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.frame_layout, selectedFragment);
                transaction.commit();
            }
            case ADD_PLACE: {
                Fragment selectedFragment = PlaceCoordinatesFragment.newInstance();
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
        Fragment selectedFragment = PlaceDescriptionFragment.newInstance(placeLatitude, placeLongitude);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_layout, selectedFragment);
        transaction.commit();
    }

    private void openDescriptionFragment(String placeName, String placeDescription, float placeRating, double placeLatitude, double placeLongitude) {
        Fragment selectedFragment = PlaceDescriptionFragment.newInstance(placeName, placeDescription, placeRating, placeLatitude, placeLongitude);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_layout, selectedFragment);
        transaction.commit();
    }

    private void openPhotosFragment(String name, String description, float rating, double latitude, double longitude) {
        Fragment selectedFragment = PlacePhotosFragment.newInstance(name, description, rating, latitude, longitude);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_layout, selectedFragment);
        transaction.commit();
    }

    @Override
    public void onFragmentDescriptionButtonNextClicked(String name, String description, float rating, double latitude, double longitude) {
        openPhotosFragment(name, description, rating, latitude, longitude);
    }

    @Override
    public void onDescriptionCancelButtonClicked() {
        openHomeFragment();
    }

    @Override
    public void onFragmentDescriptionButtonBackClicked(double lat, double lng) {
        Fragment selectedFragment = PlaceCoordinatesFragment.newInstance(lat, lng);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_layout, selectedFragment);
        transaction.commit();
    }

    @Override
    public void onPlacePhotosFragmentCancelClicked() {
        openHomeFragment();
    }

    @Override
    public void onPlacePhotosFragmentBackClicked(String placeName, String placeDescription, float placeRating, double placeLatitude, double placeLongitude) {
        openDescriptionFragment(placeName, placeDescription, placeRating, placeLatitude, placeLongitude);
    }

    @Override
    public void onPlacePhotosFragmentNextClicked(double latitude, double longitude) {

    }
}
