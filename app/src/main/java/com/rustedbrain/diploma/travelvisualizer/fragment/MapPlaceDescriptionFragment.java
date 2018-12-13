package com.rustedbrain.diploma.travelvisualizer.fragment;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.rustedbrain.diploma.travelvisualizer.LoginActivity;
import com.rustedbrain.diploma.travelvisualizer.R;
import com.rustedbrain.diploma.travelvisualizer.model.dto.security.AuthUserDTO;
import com.rustedbrain.diploma.travelvisualizer.model.dto.security.UserDTO;
import com.rustedbrain.diploma.travelvisualizer.model.dto.travel.CommentDTO;
import com.rustedbrain.diploma.travelvisualizer.model.dto.travel.LatLngDTO;
import com.rustedbrain.diploma.travelvisualizer.model.dto.travel.PlaceDescriptionDTO;
import com.rustedbrain.diploma.travelvisualizer.model.dto.travel.PlaceMapDTO;
import com.rustedbrain.diploma.travelvisualizer.model.dto.travel.TravelDTO;
import com.rustedbrain.diploma.travelvisualizer.task.comment.CommentAddTask;
import com.rustedbrain.diploma.travelvisualizer.task.place.PlaceIgnoreAddRemoveTask;
import com.rustedbrain.diploma.travelvisualizer.task.place.TravelPlaceModifyTask;
import com.rustedbrain.diploma.travelvisualizer.task.travel.GetUserTravelsTask;
import com.rustedbrain.diploma.travelvisualizer.task.travel.TravelAddTask;

import java.util.ArrayList;
import java.util.List;


public class MapPlaceDescriptionFragment extends Fragment implements TravelPlaceModifyTask.Listener, TravelAddTask.Listener, CommentAddTask.CommentAddTaskListener, PlaceIgnoreAddRemoveTask.Listener, GetUserTravelsTask.Listener {

    private static final String PLACE_DESCRIPTION_DTO = "place_description_dto";
    private static final String ADD = "+ ";
    private static final String REMOVE = "- ";
    private List<Bitmap> photos;
    private Bitmap selectedPhoto;
    private OnFragmentInteractionListener listener;
    private AuthUserDTO userDTO;
    private PlaceDescriptionDTO placeDescriptionDTO;
    private ImageView photoPreviewImageView;
    private LinearLayout photosLayout;
    private LinearLayout commentsLayout;
    private EditText placeDescriptionCommentEditText;
    private RatingBar placeDescriptionCommentRatingBar;
    private ProgressBar placeDescriptionCommentProgressBar;
    private Button commentSendButton;
    private CommentAddTask commentAddTask;
    private PlaceIgnoreAddRemoveTask placeIgnoreAddRemoveTask;
    private TravelAddTask travelAddTask;
    private GetUserTravelsTask getUserTravelsTask;
    private TravelPlaceModifyTask travelPlaceModifyTask;
    private List<CommentDTO> comments;
    private Button addToTripButton;
    private Button ignoreButton;
    private ProgressBar placeDescriptionPlaceOperationsProgressBar;

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
            userDTO = (AuthUserDTO) getArguments().getSerializable(LoginActivity.USER_DTO_PARAM);
            placeDescriptionDTO = (PlaceDescriptionDTO) getArguments().getSerializable(MapPlaceDescriptionFragment.PLACE_DESCRIPTION_DTO);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_map_place_description, container, false);

        ImageButton closeFragmentButton = view.findViewById(R.id.fragment_map_place_description_button_close);
        closeFragmentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCloseFragmentButtonClicked();
            }
        });

        RatingBar placeRatingBar = view.findViewById(R.id.fragment_map_place_description_rating_bar);
        placeRatingBar.setRating(placeDescriptionDTO.getRating());

        TextView placeNameTextView = view.findViewById(R.id.fragment_map_place_description_name_text_view);
        placeNameTextView.setText(placeDescriptionDTO.getName());

        photoPreviewImageView = view.findViewById(R.id.fragment_map_place_description_photo_preview);
        photosLayout = view.findViewById(R.id.fragment_map_place_description_photos_layout);
        initAndShowPhotos(placeDescriptionDTO.getPhotoList());

        addToTripButton = view.findViewById(R.id.fragment_map_place_description_button_add_trip);
        addToTripButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addToTripButtonClicked();
            }
        });

        placeDescriptionPlaceOperationsProgressBar = view.findViewById(R.id.fragment_map_place_description_place_operations_progress_view);
        ignoreButton = view.findViewById(R.id.fragment_map_place_description_button_add_ignore);
        ignoreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ignoreButtonClicked(placeDescriptionDTO.getLatLngDTO());
            }
        });
        setIgnoreButtonState(placeDescriptionDTO.isIgnoredByUser());

        TextView placeDescriptionTextView = view.findViewById(R.id.fragment_map_place_description_description_text_view);
        placeDescriptionTextView.setText(placeDescriptionDTO.getDescription());

        commentsLayout = view.findViewById(R.id.fragment_map_place_description_description_comments_layout);
        initAndShowComments(placeDescriptionDTO.getCommentList());

        placeDescriptionCommentProgressBar = view.findViewById(R.id.fragment_map_place_description_description_add_comment_progress_view);
        placeDescriptionCommentRatingBar = view.findViewById(R.id.fragment_map_place_description_add_comment_rating_bar);
        placeDescriptionCommentEditText = view.findViewById(R.id.fragment_map_place_description_add_comment_edit_text);

        Button commentCancelButton = view.findViewById(R.id.fragment_map_place_description_button_comment_cancel);
        commentCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                commentCancelButtonClicked();
            }
        });

        commentSendButton = view.findViewById(R.id.fragment_map_place_description_button_comment_send);
        commentSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                commentSendButtonClicked();
            }
        });

        return view;
    }

    private void onCloseFragmentButtonClicked() {
        if (listener != null) {
            listener.onMapPlaceDescriptionFragmentButtonCloseClicked();
        }
    }

    private void ignoreButtonClicked(LatLngDTO placeLatLng) {
        if (listener != null) {
            if (placeIgnoreAddRemoveTask != null) {
                return;
            }

            // Reset errors.
            this.ignoreButton.setError(null);

            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showPlaceIgnoreTaskProgress(true);
            this.placeIgnoreAddRemoveTask = new PlaceIgnoreAddRemoveTask(placeLatLng.getLatitude(), placeLatLng.getLongitude(), userDTO, this);
            this.placeIgnoreAddRemoveTask.execute((Void) null);
        }
    }

    private void addToTripButtonClicked() {
        if (listener != null) {
            if (getUserTravelsTask != null) {
                return;
            }
            // Reset errors.
            this.addToTripButton.setError(null);
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showGetUserTravelsTaskProgress(true);
            this.getUserTravelsTask = new GetUserTravelsTask(userDTO, this);
            this.getUserTravelsTask.execute((Void) null);
        }
    }

    private void initAndShowComments(List<CommentDTO> commentList) {
        this.comments = new ArrayList<>(commentList);
        showComments();
    }

    private void showComments() {
        for (CommentDTO comment : comments) {
            this.commentsLayout.addView(getCommentLayout(comment));
        }
    }

    private LinearLayout getCommentLayout(CommentDTO comment) {
        TextView authorTextView = new TextView(getContext());
        LinearLayout.LayoutParams authorTextViewLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        authorTextViewLayoutParams.setMargins(5, 5, 5, 5);
        authorTextView.setLayoutParams(authorTextViewLayoutParams);
        authorTextView.setText(comment.getAuthorLogin());

        RatingBar ratingBar = new RatingBar(getContext(), null, android.R.attr.ratingBarStyleSmall);
        LinearLayout.LayoutParams ratingBarLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        ratingBarLayoutParams.setMargins(5, 5, 5, 5);
        ratingBar.setLayoutParams(ratingBarLayoutParams);
        ratingBar.setRating(comment.getRating());

        TextView commentTextView = new TextView(getContext());
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(10, 10, 10, 10);
        lp.height = 64;
        commentTextView.setLayoutParams(lp);
        commentTextView.setText(comment.getText());

        LinearLayout commentLayout = new LinearLayout(getContext());
        commentLayout.setOrientation(LinearLayout.VERTICAL);
        commentLayout.setBackgroundColor(Color.parseColor("#ffffff"));
        commentLayout.addView(authorTextView);
        commentLayout.addView(ratingBar);
        commentLayout.addView(commentTextView);
        LinearLayout.LayoutParams commentLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f);
        commentLayoutParams.setMargins(5, 5, 5, 5);
        commentLayout.setLayoutParams(commentLayoutParams);

        return commentLayout;
    }

    @Override
    public void setCommentAddTask(CommentAddTask commentAddTask) {
        this.commentAddTask = commentAddTask;
    }

    @Override
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public void showPlaceIgnoreTaskProgress(final boolean show) {
        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

        placeDescriptionPlaceOperationsProgressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        placeDescriptionPlaceOperationsProgressBar.animate().setDuration(shortAnimTime).alpha(
                show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                placeDescriptionPlaceOperationsProgressBar.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        });
    }

    @Override
    public void setPlaceIgnoreAddRemoveTask(PlaceIgnoreAddRemoveTask placeIgnoreAddRemoveTask) {
        this.placeIgnoreAddRemoveTask = placeIgnoreAddRemoveTask;
    }

    @Override
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public void showProgress(final boolean show) {
        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

        placeDescriptionCommentProgressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        placeDescriptionCommentProgressBar.animate().setDuration(shortAnimTime).alpha(
                show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                placeDescriptionCommentProgressBar.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        });
    }

    @Override
    public void showGetPlaceDescriptionDTOTaskError() {
        ignoreButton.setError("Error");
        ignoreButton.requestFocus();
    }

    @Override
    public void showPlaceIgnore(boolean placeIgnored) {
        setIgnoreButtonState(placeIgnored);
    }

    private void setIgnoreButtonState(boolean placeIgnored) {
        if (placeIgnored) {
            ignoreButton.setText("Remove Ignore");
        } else {
            ignoreButton.setText("Ignore");
        }
    }

    @Override
    public void showCommentAddTaskError() {
        commentSendButton.setError("Error");
        commentSendButton.requestFocus();
    }

    @Override
    public void addCreatedComment(CommentDTO commentDTO) {

    }

    private void commentSendButtonClicked() {
        float rating = placeDescriptionCommentRatingBar.getRating();
        String comment = placeDescriptionCommentEditText.getText().toString();
        LatLngDTO placeLocation = placeDescriptionDTO.getLatLngDTO();

        if (listener != null) {
            if (commentAddTask != null) {
                return;
            }
            // Reset errors.
            this.commentSendButton.setError(null);
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showPlaceIgnoreTaskProgress(true);
            this.commentAddTask = new CommentAddTask(placeLocation, rating, comment, userDTO, this);
            this.commentAddTask.execute((Void) null);
        }
    }

    private void commentCancelButtonClicked() {
        placeDescriptionCommentEditText.setText("");
    }

    private void initAndShowPhotos(List<byte[]> photoList) {
        this.photos = new ArrayList<>();
        for (byte[] photoBytes : photoList) {
            this.photos.add(mapByteArrayToBitmap(photoBytes));
        }
        showPhotos();
    }

    private void showPhotos() {
        for (int i = 0; i < photos.size(); i++) {
            ImageView imageView = new ImageView(getContext());
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f);
            lp.setMargins(10, 10, 10, 10);
            lp.height = 64;
            lp.width = 64;
            imageView.setLayoutParams(lp);
            imageView.setImageBitmap(photos.get(i));
            final int photoIdx = i;
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectedPhoto = photos.get(photoIdx);
                    previewSelectedPhoto();
                }
            });
            photosLayout.addView(imageView);
        }
        if (!photos.isEmpty()) {
            selectedPhoto = photos.get(0);
            previewSelectedPhoto();
        }
    }

    private void previewSelectedPhoto() {
        photoPreviewImageView.setImageBitmap(selectedPhoto);
    }

    private Bitmap mapByteArrayToBitmap(byte[] byteArray) {
        return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
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
                    + " must implement TravelsFragmentRouteButtonListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    @Override
    public void setGetUserTravelsTask(GetUserTravelsTask getUserTravelsTask) {
        this.getUserTravelsTask = getUserTravelsTask;
    }

    @Override
    public void showGetUserTravelsTaskProgress(boolean show) {
        showPlaceIgnoreTaskProgress(show);
    }

    @Override
    public void showGetUserTravelsTaskError() {
        addToTripButton.setError("Error");
        addToTripButton.requestFocus();
    }

    @Override
    public void onTravelsLoadSuccess(List<TravelDTO> travels) {
        PopupMenu popup = new PopupMenu(getContext(), addToTripButton);

        Menu menu = popup.getMenu();
        for (TravelDTO travelDTO : travels) {
            List<String> placesNames = new ArrayList<>();
            if (travelDTO.getPlaces() != null) {
                for (PlaceMapDTO placeMapDTO : travelDTO.getPlaces()) {
                    placesNames.add(placeMapDTO.getName());
                }
            }
            if (placesNames.contains(placeDescriptionDTO.getName())) {
                SpannableString s = new SpannableString(REMOVE + travelDTO.getName());
                s.setSpan(new ForegroundColorSpan(Color.RED), 0, s.length(), 0);
                menu.add(s);
            } else {
                SpannableString s = new SpannableString(ADD + travelDTO.getName());
                s.setSpan(new ForegroundColorSpan(Color.GREEN), 0, s.length(), 0);
                menu.add(s);
            }
        }

        //Inflating the Popup using xml file
        popup.getMenuInflater().inflate(R.menu.popup_place_add_to_trip, popup.getMenu());

        //registering popup with OnMenuItemClickListener
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.new_trip_action: {
                        showNewTripNameInputDialog();
                    }
                    break;
                    default: {
                        fireTravelAdded(item.getTitle().toString().replaceAll(REMOVE, "").replaceAll("\\" + ADD, ""));
                    }
                    break;
                }
                return true;
            }
        });

        popup.show();//showing popup menu
    }

    private void showNewTripNameInputDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("New Trip Name");
// I'm using fragment here so I'm using getView() to provide ViewGroup
// but you can provide here any other instance of ViewGroup from your Fragment / Activity
        View viewInflated = LayoutInflater.from(getContext()).inflate(R.layout.dialog_naming, (ViewGroup) getView(), false);
// Set up the input
        final EditText input = viewInflated.findViewById(R.id.input);
// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        builder.setView(viewInflated);

// Set up the buttons
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                String travelName = input.getText().toString();
                if (listener != null) {
                    if (travelAddTask != null) {
                        return;
                    }
                    // Reset errors.
                    addToTripButton.setError(null);
                    // Show a progress spinner, and kick off a background task to
                    // perform the user login attempt.
                    showPlaceIgnoreTaskProgress(true);
                    travelAddTask = new TravelAddTask(travelName, userDTO, MapPlaceDescriptionFragment.this);
                    travelAddTask.execute((Void) null);
                }
            }
        });

        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    @Override
    public void setTravelAddTask(TravelAddTask travelAddTask) {
        this.travelAddTask = travelAddTask;
    }

    @Override
    public void showTravelAddTaskProgress(boolean show) {
        showPlaceIgnoreTaskProgress(show);
    }

    @Override
    public void showTravelAddTaskError() {
        addToTripButton.setError("Error");
        addToTripButton.requestFocus();
    }

    @Override
    public void fireTravelAdded(String travelName) {
        LatLngDTO latLngDTO = placeDescriptionDTO.getLatLngDTO();
        if (listener != null) {
            if (travelPlaceModifyTask != null) {
                return;
            }
            // Reset errors.
            addToTripButton.setError(null);
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showPlaceIgnoreTaskProgress(true);
            travelPlaceModifyTask = new TravelPlaceModifyTask(userDTO, travelName, latLngDTO, MapPlaceDescriptionFragment.this);
            travelPlaceModifyTask.execute((Void) null);
        }
    }

    @Override
    public void setTravelPlaceModifyTask(TravelPlaceModifyTask travelPlaceModifyTask) {
        this.travelPlaceModifyTask = travelPlaceModifyTask;
    }

    @Override
    public void showTravelPlaceAddTaskProgress(boolean show) {
        showPlaceIgnoreTaskProgress(show);
    }

    @Override
    public void showTravelPlaceAddTaskError() {
        addToTripButton.setError("Error");
        addToTripButton.requestFocus();
    }

    @Override
    public void fireTravelPlaceModified(PlaceDescriptionDTO placeDescriptionDTO) {
        Toast.makeText(getContext(), "Travel successfully modified", Toast.LENGTH_LONG).show();
    }

    public interface OnFragmentInteractionListener {

        void onMapPlaceDescriptionFragmentButtonCloseClicked();
    }
}
