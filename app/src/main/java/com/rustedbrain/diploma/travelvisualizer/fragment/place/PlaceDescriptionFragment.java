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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import com.rustedbrain.diploma.travelvisualizer.LoginActivity;
import com.rustedbrain.diploma.travelvisualizer.R;
import com.rustedbrain.diploma.travelvisualizer.RegistrationFormKeyListener;
import com.rustedbrain.diploma.travelvisualizer.model.dto.security.UserDTO;
import com.rustedbrain.diploma.travelvisualizer.model.dto.travel.PlaceType;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class PlaceDescriptionFragment extends Fragment implements RegistrationFormKeyListener.SuccessListener {

    public static final String LAT_ARG_PARAM = "lat";
    public static final String LNG_ARG_PARAM = "lng";
    public static final String NAME_ARG_PARAM = "name";
    public static final String DESCRIPTION_ARG_PARAM = "description";
    public static final String PLACE_TYPE_ARG_PARAM = "place_type";
    private static final String DEFAULT_PLACE_NAME = "";
    private static final String DEFAULT_PLACE_DESCRIPTION = "";
    private static final String DEFAULT_PLACE_TYPE = PlaceType.NOT_SELECTED.name();

    private LinkedList<Integer> secretQueue = new LinkedList<>();

    private OnFragmentInteractionListener listener;

    private CheckBox showplaceCheckBox;
    private CheckBox foodCheckBox;
    private CheckBox sleepCheckBox;
    private EditText placeNameEditText;
    private TextInputEditText placeDescriptionEditText;

    private UserDTO userDTO;
    private PlaceType placeType;
    private String placeName;
    private String placeDescription;
    private double placeLatitude;
    private double placeLongitude;


    public PlaceDescriptionFragment() {
        // Required empty public constructor
    }

    public static PlaceDescriptionFragment newInstance(UserDTO userDTO, double lat, double lng) {
        PlaceDescriptionFragment fragment = new PlaceDescriptionFragment();
        Bundle args = new Bundle();
        args.putSerializable(LoginActivity.USER_DTO_PARAM, userDTO);
        args.putDouble(LAT_ARG_PARAM, lat);
        args.putDouble(LNG_ARG_PARAM, lng);
        fragment.setArguments(args);
        return fragment;
    }

    public static PlaceDescriptionFragment newInstance(UserDTO userDTO, PlaceType placeType, String placeName, String placeDescription, double lat, double lng) {
        PlaceDescriptionFragment fragment = new PlaceDescriptionFragment();
        Bundle args = new Bundle();
        args.putSerializable(LoginActivity.USER_DTO_PARAM, userDTO);
        args.putString(PLACE_TYPE_ARG_PARAM, placeType.name());
        args.putString(NAME_ARG_PARAM, placeName);
        args.putString(DESCRIPTION_ARG_PARAM, placeDescription);
        args.putDouble(LAT_ARG_PARAM, lat);
        args.putDouble(LNG_ARG_PARAM, lng);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            userDTO = (UserDTO) getArguments().getSerializable(LoginActivity.USER_DTO_PARAM);
            placeType = PlaceType.valueOf(getArguments().getString(NAME_ARG_PARAM, DEFAULT_PLACE_TYPE));
            placeName = getArguments().getString(NAME_ARG_PARAM, DEFAULT_PLACE_NAME);
            placeDescription = getArguments().getString(DESCRIPTION_ARG_PARAM, DEFAULT_PLACE_DESCRIPTION);
            placeLatitude = getArguments().getDouble(LAT_ARG_PARAM);
            placeLongitude = getArguments().getDouble(LNG_ARG_PARAM);
            initViews();
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putSerializable(LoginActivity.USER_DTO_PARAM, userDTO);
        outState.putString(PLACE_TYPE_ARG_PARAM, getSelectedPlaceType().name());
        outState.putString(NAME_ARG_PARAM, placeNameEditText.getText().toString());
        outState.putString(DESCRIPTION_ARG_PARAM, placeDescriptionEditText.getText().toString());
        outState.putDouble(LAT_ARG_PARAM, placeLatitude);
        outState.putDouble(LNG_ARG_PARAM, placeLongitude);
        super.onSaveInstanceState(outState);
    }

    private void initViews() {
        switch (placeType) {
            case SHOWPLACE: {
                showplaceCheckBox.setChecked(true);
                break;
            }
            case FOOD: {
                foodCheckBox.setChecked(true);
                break;
            }
            case SLEEP: {
                sleepCheckBox.setChecked(true);
                break;
            }
        }
        placeNameEditText.setText(placeName);
        placeDescriptionEditText.setText(placeDescription);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            userDTO = (UserDTO) getArguments().getSerializable(LoginActivity.USER_DTO_PARAM);
            placeType = PlaceType.valueOf(getArguments().getString(PLACE_TYPE_ARG_PARAM, DEFAULT_PLACE_TYPE));
            placeName = getArguments().getString(NAME_ARG_PARAM, DEFAULT_PLACE_NAME);
            placeDescription = getArguments().getString(DESCRIPTION_ARG_PARAM, DEFAULT_PLACE_DESCRIPTION);
            placeLatitude = getArguments().getDouble(LAT_ARG_PARAM);
            placeLongitude = getArguments().getDouble(LNG_ARG_PARAM);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_place_description, container, false);

        placeNameEditText = view.findViewById(R.id.place_name_edit_text);
        placeNameEditText.setOnKeyListener(new RegistrationFormKeyListener(LoginActivity.SECRET_KEY_COMBINATION, secretQueue, this));
        placeNameEditText.setText(placeName);

        placeDescriptionEditText = view.findViewById(R.id.place_description_edit_text);
        placeDescriptionEditText.setOnKeyListener(new RegistrationFormKeyListener(LoginActivity.SECRET_KEY_COMBINATION, secretQueue, this));
        placeDescriptionEditText.setText(placeDescription);

        showplaceCheckBox = view.findViewById(R.id.checkBox_place_description_showplace);
        showplaceCheckBox.setOnKeyListener(new RegistrationFormKeyListener(LoginActivity.SECRET_KEY_COMBINATION, secretQueue, this));
        showplaceCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                onShowplaceCheckBoxChecked(isChecked);
            }
        });
        foodCheckBox = view.findViewById(R.id.checkBox_place_description_food);
        foodCheckBox.setOnKeyListener(new RegistrationFormKeyListener(LoginActivity.SECRET_KEY_COMBINATION, secretQueue, this));
        foodCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                onFoodCheckBoxChecked(isChecked);
            }
        });
        sleepCheckBox = view.findViewById(R.id.checkBox_place_description_sleep);
        sleepCheckBox.setOnKeyListener(new RegistrationFormKeyListener(LoginActivity.SECRET_KEY_COMBINATION, secretQueue, this));
        sleepCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                onSleepCheckBoxChecked(isChecked);
            }
        });

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

    private PlaceType getSelectedPlaceType() {
        if (showplaceCheckBox.isChecked()) {
            return PlaceType.SHOWPLACE;
        } else if (foodCheckBox.isChecked()) {
            return PlaceType.FOOD;
        } else if (sleepCheckBox.isChecked()) {
            return PlaceType.SLEEP;
        } else {
            return PlaceType.NOT_SELECTED;
        }
    }

    private void onSleepCheckBoxChecked(boolean isChecked) {
        if (isChecked) {
            showplaceCheckBox.setChecked(false);
            foodCheckBox.setChecked(false);
        }
    }

    private void onShowplaceCheckBoxChecked(boolean isChecked) {
        if (isChecked) {
            foodCheckBox.setChecked(false);
            sleepCheckBox.setChecked(false);
        }
    }

    private void onFoodCheckBoxChecked(boolean isChecked) {
        if (isChecked) {
            showplaceCheckBox.setChecked(false);
            sleepCheckBox.setChecked(false);
        }
    }

    private void nextButtonClicked() {
        if (listener != null) {
            String name = placeNameEditText.getText().toString().trim();
            String description = placeDescriptionEditText.getText().toString().trim();
            PlaceType placeType = getSelectedPlaceType();
            listener.onFragmentDescriptionButtonNextClicked(placeType, name, description, placeLatitude, placeLongitude);
        }
    }

    private void backButtonClicked() {
        if (listener != null) {
            listener.onFragmentDescriptionButtonBackClicked(placeLatitude, placeLongitude);
        }
    }

    private void cancelButtonClicked() {
        if (listener != null) {
            listener.onDescriptionCancelButtonClicked();
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
            listener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    @Override
    public void combinationSuccess() {
        Random random = new Random();
        PlaceType placeType = PlaceType.values()[random.nextInt(3)];
        switch (placeType) {
            case SHOWPLACE: {
                onShowplaceCheckBoxChecked(true);
                break;
            }
            case FOOD: {
                onFoodCheckBoxChecked(true);
                break;
            }
            case SLEEP: {
                onSleepCheckBoxChecked(true);
                break;
            }
        }
        int randomNumber = random.nextInt(1000);
        placeNameEditText.setText(placeType.name().toLowerCase() + "_" + randomNumber);

        List<String> testStrings = new ArrayList<>();
        for (int i = 0; i < random.nextInt(1000); i++) {
            testStrings.add("test string");
        }

        placeDescriptionEditText.setText(String.join(", ", testStrings));
    }

    public interface OnFragmentInteractionListener {
        void onFragmentDescriptionButtonNextClicked(PlaceType placeType, String name, String description, double latitude, double longitude);

        void onDescriptionCancelButtonClicked();

        void onFragmentDescriptionButtonBackClicked(double lat, double lng);
    }
}
