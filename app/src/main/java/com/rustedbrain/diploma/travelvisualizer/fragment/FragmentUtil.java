package com.rustedbrain.diploma.travelvisualizer.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;

public class FragmentUtil {

    public static void closeFragmentButtonClicked(Activity activity, Fragment fragment) {
        if (fragment != null) {
            FragmentManager fragmentManager = activity.getFragmentManager();
            android.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.remove(fragment);
            fragmentTransaction.commit();
        }
    }
}


