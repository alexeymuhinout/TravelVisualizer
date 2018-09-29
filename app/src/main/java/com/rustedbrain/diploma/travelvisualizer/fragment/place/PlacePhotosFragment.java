package com.rustedbrain.diploma.travelvisualizer.fragment.place;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.PermissionChecker;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rustedbrain.diploma.travelvisualizer.HttpUtils;
import com.rustedbrain.diploma.travelvisualizer.LoginActivity;
import com.rustedbrain.diploma.travelvisualizer.R;
import com.rustedbrain.diploma.travelvisualizer.model.dto.travel.PlaceDTO;

import org.springframework.http.HttpStatus;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class PlacePhotosFragment extends Fragment {

    private static final int CAMERA_REQUEST = 1888;
    private static final int MY_CAMERA_PERMISSION_CODE = 100;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private PlaceAddTask placeAddTask;
    private ImageView photoPreviewImageView;
    private LinearLayout photosLayout;

    private OnFragmentInteractionListener mListener;

    private String placeName;
    private String placeDescription;
    private float placeRating;
    private double placeLatitude;
    private double placeLongitude;
    private HashMap<ImageView, Bitmap> imageViewsPhotos = new HashMap<>();
    private Button nextButton;
    private ProgressBar progressView;

    public PlacePhotosFragment() {
        // Required empty public constructor
    }

    public static PlacePhotosFragment newInstance(String name, String description, float rating, double latitude, double longitude) {
        PlacePhotosFragment fragment = new PlacePhotosFragment();
        Bundle args = new Bundle();
        args.putString(PlaceDescriptionFragment.NAME_ARG_PARAM, name);
        args.putString(PlaceDescriptionFragment.DESCRIPTION_ARG_PARAM, description);
        args.putFloat(PlaceDescriptionFragment.RATING_ARG_PARAM, rating);
        args.putDouble(PlaceDescriptionFragment.LAT_ARG_PARAM, latitude);
        args.putDouble(PlaceDescriptionFragment.LNG_ARG_PARAM, longitude);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            placeName = getArguments().getString(PlaceDescriptionFragment.NAME_ARG_PARAM);
            placeDescription = getArguments().getString(PlaceDescriptionFragment.DESCRIPTION_ARG_PARAM);
            placeRating = getArguments().getFloat(PlaceDescriptionFragment.RATING_ARG_PARAM);
            placeLatitude = getArguments().getDouble(PlaceDescriptionFragment.LAT_ARG_PARAM);
            placeLongitude = getArguments().getDouble(PlaceDescriptionFragment.LNG_ARG_PARAM);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_place_photos, container, false);

        progressView = view.findViewById(R.id.place_add_progress);

        photoPreviewImageView = view.findViewById(R.id.fragment_place_photos_photo_preview);

        photosLayout = view.findViewById(R.id.fragment_place_photos_taken_photos_layout);

        Button cancelButton = view.findViewById(R.id.fragment_place_photos_cancel_button);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelButtonClicked();
            }
        });

        Button backButton = view.findViewById(R.id.fragment_place_photos_back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backButtonClicked();
            }
        });

        nextButton = view.findViewById(R.id.fragment_place_photos_next_button);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextButtonClicked();
            }
        });

        Button createPhotoButton = view.findViewById(R.id.button_create_photos);
        createPhotoButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (PermissionChecker.checkSelfPermission(getContext(), Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.CAMERA},
                            MY_CAMERA_PERMISSION_CODE);
                } else {
                    Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, CAMERA_REQUEST);
                }
            }
        });

        return view;
    }

    private void nextButtonClicked() {
        if (mListener != null) {
            if (placeAddTask != null) {
                return;
            }

            // Reset errors.
            nextButton.setError(null);

            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            placeAddTask = new PlaceAddTask(placeName, placeDescription, placeRating, placeLatitude, placeLongitude, imageViewsPhotos.values(), this);
            placeAddTask.execute((Void) null);

        }
    }

    private void cancelButtonClicked() {
        if (mListener != null) {
            mListener.onPlacePhotosFragmentCancelClicked();
        }
    }

    private void backButtonClicked() {
        if (mListener != null) {
            mListener.onPlacePhotosFragmentBackClicked(placeName, placeDescription, placeRating, placeLatitude, placeLongitude);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_CAMERA_PERMISSION_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getContext(), "camera permission granted", Toast.LENGTH_LONG).show();
                Intent cameraIntent = new
                        Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            } else {
                Toast.makeText(getContext(), "camera permission denied", Toast.LENGTH_LONG).show();
            }
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            final Bitmap photo = (Bitmap) data.getExtras().get("data");
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
            imageView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    showPhotoPopup(v);
                    return false;
                }
            });
            photosLayout.addView(imageView);
            imageViewsPhotos.put(imageView, photo);
        }
    }

    private void showPhotoPopup(final View v) {
        PopupMenu popup = new PopupMenu(getContext(), v);
        // Inflate the menu from xml
        popup.inflate(R.menu.popup_photos_actions);
        // Setup menu item selection
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.delete_action:
                        photosLayout.removeView(v);
                        return true;
                    default:
                        return false;
                }
            }
        });
        // Handle dismissal with: popup.setOnDismissListener(...);
        // Show the menu
        popup.show();
    }

    public Button getNextButton() {
        return nextButton;
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

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

        progressView.setVisibility(show ? View.VISIBLE : View.GONE);
        progressView.animate().setDuration(shortAnimTime).alpha(
                show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                progressView.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        });
    }

    public OnFragmentInteractionListener getmListener() {
        return mListener;
    }

    public void setPlaceAddTask(PlaceAddTask placeAddTask) {
        this.placeAddTask = placeAddTask;
    }

    public interface OnFragmentInteractionListener {

        void onPlacePhotosFragmentCancelClicked();

        void onPlacePhotosFragmentBackClicked(String name, String description, float rating, double latitude, double longitude);

        void onPlacePhotosFragmentNextClicked(double latitude, double longitude);
    }

    private static class PlaceAddTask extends AsyncTask<Void, Void, PlaceDTO> {

        private final String name;
        private final String description;
        private final float rating;
        private final double latitude;
        private final double longitude;
        private final Collection<Bitmap> photos;

        private final PlacePhotosFragment placePhotosFragment;

        private PlaceAddTask(String name, String description, float rating, double latitude, double longitude, Collection<Bitmap> photos, PlacePhotosFragment placePhotosFragment) {
            this.name = name;
            this.description = description;
            this.rating = rating;
            this.latitude = latitude;
            this.longitude = longitude;
            this.photos = photos;
            this.placePhotosFragment = placePhotosFragment;
        }

        private byte[] mapBitmapToByteArray(Bitmap bitmap) {
            ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, arrayOutputStream);
            return arrayOutputStream.toByteArray();
        }

        @Override
        protected PlaceDTO doInBackground(Void... params) {
            try {
                List<byte[]> photoList = new ArrayList<>();
                for (Bitmap photo : photos) {
                    photoList.add(mapBitmapToByteArray(photo));
                }

                RestTemplate restTemplate = new RestTemplate();
                restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
                PlaceDTO request = new PlaceDTO(name, description, rating, latitude, longitude, photoList);

                return restTemplate.postForObject(new URI(HttpUtils.getAbsoluteUrl(HttpUtils.PLACE_ADD_URL)),
                        request, PlaceDTO.class);
            } catch (HttpClientErrorException e) {
                Log.e(LoginActivity.class.getName(), e.getMessage(), e);
                try {
                    return new ObjectMapper().readValue(e.getResponseBodyAsString(), PlaceDTO.class);
                } catch (IOException e1) {
                    return null;
                }
            } catch (ResourceAccessException | URISyntaxException ex) {
                Log.d(LoginActivity.class.getName(), ex.getMessage());
                return null;
            }
        }

        @Override
        protected void onPostExecute(final PlaceDTO responseEntity) {
            placePhotosFragment.setPlaceAddTask(null);
            placePhotosFragment.showProgress(false);

            if (responseEntity == null) {
                Toast.makeText(placePhotosFragment.getContext(), getString(R.string.error_invalid_url), Toast.LENGTH_LONG).show();
            } else if (HttpStatus.OK.equals(responseEntity.getStatus())) {
                placePhotosFragment.getmListener().onPlacePhotosFragmentNextClicked(latitude, longitude);
            } else {
                placePhotosFragment.getNextButton().setError("Error");
                placePhotosFragment.getNextButton().requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            placePhotosFragment.setPlaceAddTask(null);
            placePhotosFragment.showProgress(false);
        }
    }
}
