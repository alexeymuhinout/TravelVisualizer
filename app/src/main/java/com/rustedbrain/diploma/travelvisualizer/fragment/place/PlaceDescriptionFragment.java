package com.rustedbrain.diploma.travelvisualizer.fragment.place;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;

import com.rustedbrain.diploma.travelvisualizer.R;

public class PlaceDescriptionFragment extends Fragment {

    private static final String DEFAULT_PLACE_NAME = "";
    private static final String DEFAULT_PLACE_DESCRIPTION = "";
    private static final float DEFAULT_PLACE_RATING = 0;

    public static final String LAT_ARG_PARAM = "lat";
    public static final String LNG_ARG_PARAM = "lng";
    public static final String NAME_ARG_PARAM = "name";
    public static final String DESCRIPTION_ARG_PARAM = "description";
    public static final String RATING_ARG_PARAM = "rating";

    private OnFragmentInteractionListener mListener;

    private EditText placeNameEditText;
    private TextInputEditText placeDescriptionEditText;
    private RatingBar placeRatingBar;

    private String placeName;
    private String placeDescription;
    private double placeLatitude;
    private double placeLongitude;
    private float placeRating;

    public PlaceDescriptionFragment() {
        // Required empty public constructor
    }

    public static PlaceDescriptionFragment newInstance(double lat, double lng) {
        PlaceDescriptionFragment fragment = new PlaceDescriptionFragment();
        Bundle args = new Bundle();
        args.putDouble(LAT_ARG_PARAM, lat);
        args.putDouble(LNG_ARG_PARAM, lng);
        fragment.setArguments(args);
        return fragment;
    }

    public static PlaceDescriptionFragment newInstance(String placeName, String placeDescription, float placeRating, double lat, double lng) {
        PlaceDescriptionFragment fragment = new PlaceDescriptionFragment();
        Bundle args = new Bundle();
        args.putString(NAME_ARG_PARAM, placeName);
        args.putString(DESCRIPTION_ARG_PARAM, placeDescription);
        args.putFloat(RATING_ARG_PARAM, placeRating);
        args.putDouble(LAT_ARG_PARAM, lat);
        args.putDouble(LNG_ARG_PARAM, lng);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            placeName = getArguments().getString(NAME_ARG_PARAM, DEFAULT_PLACE_NAME);
            placeDescription = getArguments().getString(DESCRIPTION_ARG_PARAM, DEFAULT_PLACE_DESCRIPTION);
            placeRating = getArguments().getFloat(RATING_ARG_PARAM, DEFAULT_PLACE_RATING);
            placeLatitude = getArguments().getDouble(LAT_ARG_PARAM);
            placeLongitude = getArguments().getDouble(LNG_ARG_PARAM);
            initViews();
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putString(NAME_ARG_PARAM, placeNameEditText.getText().toString());
        outState.putString(DESCRIPTION_ARG_PARAM, placeDescriptionEditText.getText().toString());
        outState.putFloat(RATING_ARG_PARAM, placeRating);
        outState.putDouble(LAT_ARG_PARAM, placeLatitude);
        outState.putDouble(LNG_ARG_PARAM, placeLongitude);
        super.onSaveInstanceState(outState);
    }

    private void initViews(){
        placeNameEditText.setText(placeName);
        placeDescriptionEditText.setText(placeDescription);
        placeRatingBar.setRating(placeRating);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            placeName = getArguments().getString(NAME_ARG_PARAM, DEFAULT_PLACE_NAME);
            placeDescription = getArguments().getString(DESCRIPTION_ARG_PARAM, DEFAULT_PLACE_DESCRIPTION);
            placeRating = getArguments().getFloat(RATING_ARG_PARAM, DEFAULT_PLACE_RATING);
            placeLatitude = getArguments().getDouble(LAT_ARG_PARAM);
            placeLongitude = getArguments().getDouble(LNG_ARG_PARAM);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_place_description, container, false);

        placeNameEditText = view.findViewById(R.id.place_name_edit_text);
        placeNameEditText.setText(placeName);

        placeDescriptionEditText = view.findViewById(R.id.place_description_edit_text);
        placeDescriptionEditText.setText(placeDescription);

        placeRatingBar = view.findViewById(R.id.place_description_rating_bar);
        placeRatingBar.setRating(placeRating);

        Button cancelButton = view.findViewById(R.id.button_description_cancel);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelButtonClicked();
            }
        });
        Button backButton = view.findViewById(R.id.button_description_back);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backButtonClicked();
            }
        });
        Button nextButton = view.findViewById(R.id.button_description_next);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextButtonClicked();
            }
        });

        initViews();
        return view;
    }

    private void nextButtonClicked() {
        if (mListener != null) {
            String name = placeNameEditText.getText().toString().trim();
            String descr = placeDescriptionEditText.getText().toString().trim();
            float rating = placeRatingBar.getRating();

            mListener.onFragmentDescriptionButtonNextClicked(name, descr, rating, placeLatitude, placeLongitude);
        }
    }

    private void backButtonClicked() {
        if (mListener != null) {
            mListener.onFragmentDescriptionButtonBackClicked(placeLatitude, placeLongitude);
        }
    }

    private void cancelButtonClicked() {
        if (mListener != null) {
            mListener.onDescriptionCancelButtonClicked();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
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
        void onFragmentDescriptionButtonNextClicked(String name, String description, float rating, double latitude, double longitude);

        void onDescriptionCancelButtonClicked();

        void onFragmentDescriptionButtonBackClicked(double lat, double lng);
    }
}
