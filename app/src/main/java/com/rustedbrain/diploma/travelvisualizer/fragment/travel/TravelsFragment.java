package com.rustedbrain.diploma.travelvisualizer.fragment.travel;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.rustedbrain.diploma.travelvisualizer.LoginActivity;
import com.rustedbrain.diploma.travelvisualizer.R;
import com.rustedbrain.diploma.travelvisualizer.fragment.FragmentUtil;
import com.rustedbrain.diploma.travelvisualizer.model.dto.security.AuthUserDTO;
import com.rustedbrain.diploma.travelvisualizer.model.dto.travel.LatLngDTO;
import com.rustedbrain.diploma.travelvisualizer.model.dto.travel.PlaceDescriptionDTO;
import com.rustedbrain.diploma.travelvisualizer.model.dto.travel.PlaceMapDTO;
import com.rustedbrain.diploma.travelvisualizer.model.dto.travel.TravelDTO;
import com.rustedbrain.diploma.travelvisualizer.model.dto.travel.TravelDTOList;
import com.rustedbrain.diploma.travelvisualizer.task.place.TravelPlaceModifyTask;
import com.rustedbrain.diploma.travelvisualizer.task.travel.ArchiveTravelTask;
import com.rustedbrain.diploma.travelvisualizer.task.travel.RemoveTravelSharedUserTask;
import com.rustedbrain.diploma.travelvisualizer.task.travel.SetSharedUsersTask;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class TravelsFragment extends Fragment implements SetSharedUsersTask.Listener, TravelPlaceModifyTask.Listener, ArchiveTravelTask.Listener, RemoveTravelSharedUserTask.Listener, TravelLayout.Listener {

    public static final String TRAVEL_DTO_LIST_PARAM = "travel_dto_list";
    public static final String TRAVELS_ARCHIVED_LAYOUT_PARAM = "travels_archived_layout";

    private android.app.Fragment currentMapInnerFragment;
    private LinearLayout travelsInnerFragmentLayout;

    private boolean archivedTravelsLayout;
    private AuthUserDTO user;
    private TravelDTOList travelDTOList;

    private ArchiveTravelTask archiveTravelTask;
    private RemoveTravelSharedUserTask removeTravelSharedUserTask;

    private TravelsFragmentRouteButtonListener listener;

    private List<TravelLayout> travelLayoutList;
    private LinearLayout travelsLayout;
    private RelativeLayout mainLayout;
    private ProgressBar progressBar;
    private TravelPlaceModifyTask travelPlaceModifyTask;
    private SetSharedUsersTask travelLeaveTask;

    public TravelsFragment() {
    }

    public static TravelsFragment newInstance(AuthUserDTO user, TravelDTOList travelDTOList, boolean archivedTravelsLayout) {
        TravelsFragment fragment = new TravelsFragment();
        Bundle args = new Bundle();
        args.putSerializable(LoginActivity.USER_DTO_PARAM, user);
        args.putSerializable(TravelsFragment.TRAVEL_DTO_LIST_PARAM, travelDTOList);
        args.putBoolean(TravelsFragment.TRAVELS_ARCHIVED_LAYOUT_PARAM, archivedTravelsLayout);
        fragment.setArguments(args);
        return fragment;
    }

    public void showTravelsShareFragment(String travelName) {
        this.currentMapInnerFragment = TravelsShareFragment.newInstance(user, travelName);
        FragmentManager fragmentManager = getActivity().getFragmentManager();
        android.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.trips_inner_fragment, currentMapInnerFragment);
        fragmentTransaction.commit();
        this.travelsInnerFragmentLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            user = (AuthUserDTO) getArguments().getSerializable(LoginActivity.USER_DTO_PARAM);
            travelDTOList = (TravelDTOList) getArguments().getSerializable(TravelsFragment.TRAVEL_DTO_LIST_PARAM);
            archivedTravelsLayout = getArguments().getBoolean(TravelsFragment.TRAVELS_ARCHIVED_LAYOUT_PARAM);
        }
    }

    private void showTravels(List<TravelDTO> travelDTOList) {
        this.travelLayoutList = new ArrayList<>(travelDTOList.size());
        for (TravelDTO travelDTO : travelDTOList) {
            TravelLayout layout = archivedTravelsLayout ? new ArchivedTravelLayout(getContext(), travelDTO, !user.getUsername().equals(travelDTO.getOwnerUsername()), this) : new ActiveTravelLayout(getContext(), travelDTO, !user.getUsername().equals(travelDTO.getOwnerUsername()), this);
            this.travelsLayout.addView(layout);
            this.travelLayoutList.add(layout);
        }
    }

    @Override
    public void onTravelTrashButtonClicked(String travelName) {
        if (listener != null) {
            if (archiveTravelTask != null) {
                return;
            }

            showArchiveTravelTaskProgress(true);
            this.archiveTravelTask = new ArchiveTravelTask(user.getUsername(), travelName, user, this);
            this.archiveTravelTask.execute((Void) null);
        }
    }

    @Override
    public void onTravelPlaceShowClicked(PlaceMapDTO placeMapDTO) {
        if (listener != null) {
            listener.onTravelPlaceShowClicked(placeMapDTO);
        }
    }

    @Override
    public void onTravelSharedUserShowClicked(String username) {

    }

    @Override
    public void onTravelShareButtonClicked(String travelName) {
        showTravelsShareFragment(travelName);
    }

    @Override
    public void onTravelSharedUserDeleteClicked(String travelName, String username) {
        if (listener != null) {
            if (removeTravelSharedUserTask != null) {
                return;
            }

            showArchiveTravelTaskProgress(true);
            this.removeTravelSharedUserTask = new RemoveTravelSharedUserTask(user, user.getUsername(), travelName, Collections.singletonList(username), this);
            this.removeTravelSharedUserTask.execute((Void) null);
        }
    }

    @Override
    public void onTravelPlaceDeleteClicked(String travelName, PlaceMapDTO placeMapDTO) {
        if (listener != null) {
            if (travelPlaceModifyTask != null) {
                return;
            }

            showArchiveTravelTaskProgress(true);
            this.travelPlaceModifyTask = new TravelPlaceModifyTask(user, travelName, new LatLngDTO(placeMapDTO.getPlaceLatLng().latitude, placeMapDTO.getPlaceLatLng().longitude), this);
            this.travelPlaceModifyTask.execute((Void) null);
        }
    }

    @Override
    public void onTravelLeaveButtonClicked(String ownerUsername, String travelName) {
        if (listener != null) {
            if (travelLeaveTask != null) {
                return;
            }

            showArchiveTravelTaskProgress(true);
            this.travelLeaveTask = new SetSharedUsersTask(user, ownerUsername, travelName, Collections.singletonList(user.getUsername()), this);
            this.travelLeaveTask.execute((Void) null);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_travels, container, false);

        travelsInnerFragmentLayout = view.findViewById(R.id.trips_inner_fragment);
        progressBar = view.findViewById(R.id.fragment_travels_progress);

        ImageButton closeFragmentButton = view.findViewById(R.id.fragment_travels_button_close);
        closeFragmentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCloseFragmentButtonClicked();
            }
        });

        travelsLayout = view.findViewById(R.id.fragment_travels_travels_layout);
        showTravels(travelDTOList.getTravelDTOList());

        return view;
    }

    private void onCloseFragmentButtonClicked() {
        if (listener != null) {
            listener.onTravelsFragmentCloseButtonClicked();
        }
    }

    @Override
    public void onTravelRouteButtonClicked(List<PlaceMapDTO> placeMapDTOList) {
        if (placeMapDTOList.isEmpty()) {
            Toast.makeText(getContext(), "Travel not contains any places", Toast.LENGTH_LONG).show();
        } else {
            if (listener != null) {
                listener.onTravelRouteButtonClicked(placeMapDTOList);
            }
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof TravelsFragmentRouteButtonListener) {
            listener = (TravelsFragmentRouteButtonListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement TravelsFragmentRouteButtonListener");
        }

//        this.currentMapInnerFragment = null;
//        this.mapInnerFragmentLayout.setVisibility(View.GONE);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    @Override
    public void setArchiveTravelTask(ArchiveTravelTask archiveTravelTask) {
        this.archiveTravelTask = archiveTravelTask;
    }

    @Override
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public void showArchiveTravelTaskProgress(final boolean show) {
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
    public void showArchiveTravelTaskError() {
        Toast.makeText(getContext(), "Error occurred during travel archiving", Toast.LENGTH_LONG).show();
    }

    @Override
    public void fireTravelArchived(TravelDTO travelDTO) {
        Iterator<TravelLayout> travelLayoutIterator = travelLayoutList.iterator();
        while (travelLayoutIterator.hasNext()) {
            TravelLayout travelLayout = travelLayoutIterator.next();
            if (travelLayout.getTravelDTO().getName().equals(travelDTO.getName())) {
                travelsLayout.removeView(travelLayout);
                travelLayoutIterator.remove();
            }
        }
    }

    public void closeInnerFragment() {
        FragmentUtil.closeFragmentButtonClicked(getActivity(), currentMapInnerFragment);
        this.currentMapInnerFragment = null;
        this.travelsInnerFragmentLayout.setVisibility(View.GONE);
    }

    public void updateTravel(TravelDTO travelDTO) {
        for (TravelLayout travelLayout : travelLayoutList) {
            if (travelLayout.travelDTO.getName().equals(travelDTO.getName())) {
                travelLayout.updateTravelDTO(travelDTO);
            }
        }
    }

    @Override
    public void setRemoveTravelSharedUserTask(RemoveTravelSharedUserTask removeTravelSharedUserTask) {
        this.removeTravelSharedUserTask = removeTravelSharedUserTask;
    }

    @Override
    public void showRemoveTravelSharedUserTaskProgress(boolean show) {
        this.showArchiveTravelTaskProgress(show);
    }

    @Override
    public void showRemoveTravelSharedUserTaskError() {
        Toast.makeText(getContext(), "Error occured during removing shared user from travel", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showRemovedSharedUserTravel(TravelDTO travelDTO) {
        for (TravelLayout travelLayout : travelLayoutList) {
            if (travelLayout.getTravelDTO().getName().equals(travelDTO.getName())) {
                travelLayout.updateTravelDTO(travelDTO);
            }
        }
    }

    @Override
    public void setTravelPlaceModifyTask(TravelPlaceModifyTask travelPlaceModifyTask) {
        this.travelPlaceModifyTask = travelPlaceModifyTask;
    }

    @Override
    public void showTravelPlaceAddTaskProgress(boolean show) {
        showArchiveTravelTaskProgress(show);
    }

    @Override
    public void showTravelPlaceAddTaskError() {
        Toast.makeText(getContext(), "Error occurred during travel modifying", Toast.LENGTH_LONG).show();
    }

    @Override
    public void fireTravelPlaceModified(PlaceDescriptionDTO placeDescriptionDTO) {
        double placeLatitude = placeDescriptionDTO.getLatLngDTO().getLatitude();
        double placeLongitude = placeDescriptionDTO.getLatLngDTO().getLongitude();
        for (TravelLayout travelLayout : travelLayoutList) {
            for (PlaceMapDTO placeMapDTO : travelLayout.getTravelDTO().getPlaces()) {
                if (placeMapDTO.getLatitude() == placeLatitude && placeMapDTO.getLongitude() == placeLongitude) {
                    travelLayout.removeTravelPlace(placeDescriptionDTO);
                    return;
                }
            }
        }
    }

    @Override
    public void setSetSharedUsersTask(SetSharedUsersTask setSharedUsersTask) {

    }

    @Override
    public void showSetSharedUsersTaskProgress(boolean show) {

    }

    @Override
    public void showSetSharedUsersTaskError() {

    }

    @Override
    public void showTravel(TravelDTO travelDTO) {

    }

    public interface TravelsFragmentRouteButtonListener {

        void onTravelRouteButtonClicked(List<PlaceMapDTO> placeMapDTOList);

        void onTravelPlaceShowClicked(PlaceMapDTO placeMapDTO);

        void onTravelsFragmentCloseButtonClicked();
    }
}
