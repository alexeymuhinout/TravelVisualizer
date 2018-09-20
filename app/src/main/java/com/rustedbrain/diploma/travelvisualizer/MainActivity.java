package com.rustedbrain.diploma.travelvisualizer;

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
import com.rustedbrain.diploma.travelvisualizer.fragment.ProfileFragment;
import com.rustedbrain.diploma.travelvisualizer.fragment.TripsFragment;
import com.rustedbrain.diploma.travelvisualizer.fragment.TripsListFragment;

public class MainActivity extends AppCompatActivity implements TripsListFragment.OnListFragmentInteractionListener, HomeFragment.OnHomeFragmentButtonClickListener, TripsFragment.OnFragmentInteractionListener, ProfileFragment.OnFragmentInteractionListener {

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
                                Fragment selectedFragment = HomeFragment.newInstance();
                                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                                transaction.replace(R.id.frame_layout, selectedFragment);
                                transaction.commit();
                            }
                            break;
                            case R.id.action_item_trip: {
                                Fragment selectedFragment = TripsFragment.newInstance();
                                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                                transaction.replace(R.id.frame_layout, selectedFragment);
                                transaction.commit();
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
                Fragment selectedFragment = TripsFragment.newInstance();
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.frame_layout, selectedFragment);
                transaction.commit();
            }
            case ADD_PLACE: {
                Fragment selectedFragment = TripsFragment.newInstance(true);
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
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onMapPlaceSelected(LatLng latLng) {

    }
}
