package com.rustedbrain.diploma.travelvisualizer.fragment;

import android.app.Fragment;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.rustedbrain.diploma.travelvisualizer.LoginActivity;
import com.rustedbrain.diploma.travelvisualizer.R;
import com.rustedbrain.diploma.travelvisualizer.model.dto.security.UserDTO;
import com.rustedbrain.diploma.travelvisualizer.model.dto.travel.PlaceDescriptionDTO;

import java.util.List;


public class MapPlaceDescriptionFragment extends Fragment {

    private static final String PLACE_DESCRIPTION_DTO = "place_description_dto";

    private OnFragmentInteractionListener mListener;
    private TextView placeDescriptionTextView;
    private TextView placeNameTextView;
    private UserDTO userDTO;
    private PlaceDescriptionDTO placeDescriptionDTO;
    private ImageView photoPreviewImageView;
    private LinearLayout photosLayout;

    public MapPlaceDescriptionFragment() {

    }

    public static MapPlaceDescriptionFragment newInstance(UserDTO userDTO, PlaceDescriptionDTO placeDescriptionDTO) {
        MapPlaceDescriptionFragment fragment = new MapPlaceDescriptionFragment();
        Bundle args = new Bundle();
        args.putSerializable(LoginActivity.USER_DTO_PARAM, userDTO);
        args.putSerializable(MapPlaceDescriptionFragment.PLACE_DESCRIPTION_DTO, placeDescriptionDTO);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            userDTO = (UserDTO) getArguments().getSerializable(LoginActivity.USER_DTO_PARAM);
            placeDescriptionDTO = (PlaceDescriptionDTO) getArguments().getSerializable(MapPlaceDescriptionFragment.PLACE_DESCRIPTION_DTO);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_map_place_description, container, false);

        photoPreviewImageView = view.findViewById(R.id.fragment_map_place_description_photo_preview);
        photosLayout = view.findViewById(R.id.fragment_map_place_description_photos_layout);
        showPhotos(placeDescriptionDTO.getPhotoList());

        placeNameTextView = view.findViewById(R.id.fragment_map_place_description_name_text_view);
        placeNameTextView.setText(placeDescriptionDTO.getName());

        placeDescriptionTextView = view.findViewById(R.id.fragment_map_place_description_description_text_view);
        placeDescriptionTextView.setText(placeDescriptionDTO.getDescription());

        return view;
    }

    private void showPhotos(List<byte[]> photoList) {
        for (byte[] photoBytes : photoList) {
            final Bitmap photo = mapByteArrayToBitmap(photoBytes);
            ImageView imageView = new ImageView(getContext());
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f);
            lp.setMargins(10, 10, 10, 10);
            imageView.setLayoutParams(lp);
            imageView.setImageBitmap(photo);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    photoPreviewImageView.setImageBitmap(photo);
                }
            });
            photosLayout.addView(imageView);
        }
    }

    private Bitmap mapByteArrayToBitmap(byte[] byteArray) {
        return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
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
        void onFragmentInteraction(Uri uri);
    }

}
