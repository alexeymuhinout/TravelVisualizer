package com.rustedbrain.diploma.travelvisualizer.fragment;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.rustedbrain.diploma.travelvisualizer.LoginActivity;
import com.rustedbrain.diploma.travelvisualizer.R;
import com.rustedbrain.diploma.travelvisualizer.model.dto.security.AuthUserDTO;
import com.rustedbrain.diploma.travelvisualizer.model.dto.travel.TravelDTO;
import com.rustedbrain.diploma.travelvisualizer.task.travel.GetUserTravelsTask;

import java.util.Iterator;
import java.util.List;

public class HomeFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private GetUserTravelsTask getUserTravelsTask;

    private OnHomeFragmentButtonClickListener mListener;

    private Button profileButton;
    private Button savedRoutesButton;
    private Button archivedRoutesButton;
    private Button addPlaceButton;
    private AuthUserDTO userDTO;
    private ProgressBar progressBar;

    public HomeFragment() {
    }

    public static HomeFragment newInstance(AuthUserDTO userDTO) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putSerializable(LoginActivity.USER_DTO_PARAM, userDTO);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            this.userDTO = (AuthUserDTO) getArguments().getSerializable(LoginActivity.USER_DTO_PARAM);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        progressBar = view.findViewById(R.id.fragment_home_progress_bar);

        profileButton = view.findViewById(R.id.button_profile);
        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onProfileButtonClicked();
            }
        });
        savedRoutesButton = view.findViewById(R.id.button_saved_routes);
        savedRoutesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSavedRoutesButtonClicked();
            }
        });
        archivedRoutesButton = view.findViewById(R.id.button_archived_routes);
        archivedRoutesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onArchivedRoutesButtonClicked();
            }
        });
        addPlaceButton = view.findViewById(R.id.button_add_place);
        addPlaceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onAddPlaceButtonClicked();
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public void onProfileButtonClicked() {
        if (mListener != null) {
            mListener.onProfileButtonClicked();
        }
    }

    public void onSavedRoutesButtonClicked() {
        if (mListener != null) {
            if (getUserTravelsTask != null) {
                return;
            }

            GetUserTravelsTaskListener listener = new GetUserTravelsTaskListener(true);
            listener.showGetUserTravelsTaskProgress(true);
            getUserTravelsTask = new GetUserTravelsTask(userDTO, listener);
            getUserTravelsTask.execute((Void) null);
        }
    }

    public void onArchivedRoutesButtonClicked() {
        if (mListener != null) {
            if (getUserTravelsTask != null) {
                return;
            }

            GetUserTravelsTaskListener listener = new GetUserTravelsTaskListener(false);
            listener.showGetUserTravelsTaskProgress(true);
            getUserTravelsTask = new GetUserTravelsTask(userDTO, listener);
            getUserTravelsTask.execute((Void) null);
        }
    }

    public void onAddPlaceButtonClicked() {
        if (mListener != null) {
            mListener.onAddPlaceButtonClicked();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnHomeFragmentButtonClickListener) {
            mListener = (OnHomeFragmentButtonClickListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement TravelsFragmentRouteButtonListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnHomeFragmentButtonClickListener {

        void onProfileButtonClicked();

        void onSavedRoutesButtonClicked(List<TravelDTO> travels);

        void onAddPlaceButtonClicked();

        void onArchivedRoutesButtonClicked(List<TravelDTO> travels);
    }

    private class GetUserTravelsTaskListener implements GetUserTravelsTask.Listener {

        private boolean activeRoutes;

        public GetUserTravelsTaskListener(boolean activeRoutes) {
            this.activeRoutes = activeRoutes;
        }

        @Override
        public void setGetUserTravelsTask(GetUserTravelsTask getUserTravelsTask) {
            HomeFragment.this.getUserTravelsTask = getUserTravelsTask;
        }

        @Override
        public void showGetUserTravelsTaskProgress(final boolean show) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
            progressBar.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        }

        @Override
        public void showGetUserTravelsTaskError() {
            Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void showTravels(List<TravelDTO> travels) {
            if (activeRoutes) {

                Iterator<TravelDTO> travelDTOIterator = travels.iterator();
                while (travelDTOIterator.hasNext()) {
                    TravelDTO travelDTO = travelDTOIterator.next();
                    if (travelDTO.isArchived()) {
                        travelDTOIterator.remove();
                    }
                }
                if (travels.isEmpty()) {
                    Toast.makeText(getContext(), "Active routes list is empty", Toast.LENGTH_LONG).show();
                } else {
                    mListener.onSavedRoutesButtonClicked(travels);
                }

            } else {
                Iterator<TravelDTO> travelDTOIterator = travels.iterator();
                while (travelDTOIterator.hasNext()) {
                    TravelDTO travelDTO = travelDTOIterator.next();
                    if (!travelDTO.isArchived()) {
                        travelDTOIterator.remove();
                    }
                }
                if (travels.isEmpty()) {
                    Toast.makeText(getContext(), "Archived routes list is empty", Toast.LENGTH_LONG).show();
                } else {
                    mListener.onArchivedRoutesButtonClicked(travels);
                }
            }
        }
    }
}
