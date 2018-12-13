package com.rustedbrain.diploma.travelvisualizer.fragment.travel;

import android.content.Context;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.rustedbrain.diploma.travelvisualizer.R;
import com.rustedbrain.diploma.travelvisualizer.model.dto.travel.PlaceDescriptionDTO;
import com.rustedbrain.diploma.travelvisualizer.model.dto.travel.PlaceMapDTO;
import com.rustedbrain.diploma.travelvisualizer.model.dto.travel.TravelDTO;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public abstract class TravelLayout extends LinearLayout {

    protected final boolean shared;
    private final List<Listener> listeners;
    protected TravelDTO travelDTO;
    //private boolean travelPlacesLayout
    private GridLayout travelPlacesLayout;
    private GridLayout sharedToUsersLayout;
    private GridLayout travelPlacesButtonslayout;
    private Map<String, Button> travelPlacesButtonsMap;

    public TravelLayout(Context context, final TravelDTO travelDTO, boolean shared, Listener listener) {
        super(context);
        this.travelDTO = travelDTO;
        this.shared = shared;
        this.listeners = Collections.singletonList(listener);
        super.setOrientation(LinearLayout.VERTICAL);
        if (!shared) {
            super.setBackgroundColor(Color.parseColor("#ffffff"));
        } else {
            super.setBackgroundColor(Color.LTGRAY);
        }
        initMainLayout();
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(5, 5, 5, 5);
        super.setLayoutParams(layoutParams);
    }

    private void initMainLayout() {
        super.removeAllViews();
        super.addView(getMainLayout());
    }

    private LinearLayout getMainLayout() {
        final LinearLayout mainLayout = new LinearLayout(getContext());
        mainLayout.setOrientation(LinearLayout.VERTICAL);
        mainLayout.addView(getTravelNameTextView());
        mainLayout.addView(getRouteLayout());
        mainLayout.addView(getUsersLayout());
        return mainLayout;
    }

    private LinearLayout getRouteLayout() {
        final LinearLayout travelLayout = new LinearLayout(getContext());
        travelLayout.setOrientation(LinearLayout.VERTICAL);

        Button showPlacesButton = new Button(getContext());
        LinearLayout.LayoutParams showPlacesButtonLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1);
        showPlacesButton.setLayoutParams(showPlacesButtonLayoutParams);
        showPlacesButton.setText("Route");
        showPlacesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (travelPlacesLayout != null && travelPlacesLayout.isAttachedToWindow()) {
                    travelLayout.removeView(travelPlacesLayout);
                } else {
                    TravelLayout.this.travelPlacesLayout = getTravelPlacesLayout();
                    travelLayout.addView(TravelLayout.this.travelPlacesLayout);
                }
            }
        });
        LinearLayout routeNavigationButtonsLayout = new LinearLayout(getContext());
        routeNavigationButtonsLayout.setOrientation(LinearLayout.HORIZONTAL);
        routeNavigationButtonsLayout.addView(showPlacesButton);
        routeNavigationButtonsLayout.addView(getTravelOperationButtonsPanel());

        travelLayout.addView(routeNavigationButtonsLayout);


        return travelLayout;
    }

    private LinearLayout getUsersLayout() {
        final LinearLayout usersLayout = new LinearLayout(getContext());
        usersLayout.setOrientation(LinearLayout.VERTICAL);

        Button usersButton = new Button(getContext());
        LinearLayout.LayoutParams userButtonLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1);
        usersButton.setLayoutParams(userButtonLayoutParams);
        usersButton.setText("Shared users");
        usersButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sharedToUsersLayout != null && sharedToUsersLayout.isAttachedToWindow()) {
                    usersLayout.removeView(sharedToUsersLayout);
                } else {
                    sharedToUsersLayout = getSharedToUsersLayout();
                    usersLayout.addView(TravelLayout.this.sharedToUsersLayout);
                }
            }
        });

        LinearLayout usersNavigationButtonsLayout = new LinearLayout(getContext());
        usersNavigationButtonsLayout.setOrientation(LinearLayout.HORIZONTAL);
        usersNavigationButtonsLayout.addView(usersButton);
        if (!shared) {
            usersNavigationButtonsLayout.addView(getShareOperationButtonsPanel());
        }
        usersLayout.addView(usersNavigationButtonsLayout);

        return usersLayout;
    }

    private TextView getTravelNameTextView() {
        TextView travelNameTextView = new TextView(getContext());
        if (!shared) {
            travelNameTextView.setText(travelDTO.getName());
        } else {
            travelNameTextView.setText(travelDTO.getName() + " (" + "SHARED" + ")");
        }
        travelNameTextView.setTextSize(16);
        return travelNameTextView;
    }

    private GridLayout getTravelPlacesLayout() {
        travelPlacesButtonslayout = new GridLayout(getContext());
        travelPlacesButtonsMap = new HashMap<>();
        travelPlacesButtonslayout.setColumnCount(1);
        for (final PlaceMapDTO placeMapDTO : travelDTO.getPlaces()) {
            final Button placeButton = new Button(getContext());
            TypedValue outValue = new TypedValue();
            getContext().getTheme().resolveAttribute(R.attr.selectableItemBackground, outValue, true);
            placeButton.setBackgroundResource(outValue.resourceId);
            placeButton.setText(placeMapDTO.getName());
            placeButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    PopupMenu popup = new PopupMenu(getContext(), placeButton);

                    //Inflating the Popup using xml file
                    popup.getMenuInflater().inflate(R.menu.popup_travel_place, popup.getMenu());

                    //registering popup with OnMenuItemClickListener
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.popup_travel_place_show_action: {
                                    for (Listener listener : listeners) {
                                        listener.onTravelPlaceShowClicked(placeMapDTO);
                                    }
                                }
                                break;
                                case R.id.delete_action: {
                                    for (Listener listener : listeners) {
                                        listener.onTravelPlaceDeleteClicked(travelDTO.getName(), placeMapDTO);
                                    }
                                }
                                break;
                            }
                            return true;
                        }
                    });

                    popup.show();//showing popup menu
                }
            });
            travelPlacesButtonslayout.addView(placeButton);
            travelPlacesButtonsMap.put(placeMapDTO.getName(), placeButton);
        }
        return travelPlacesButtonslayout;
    }

    private GridLayout getSharedToUsersLayout() {
        GridLayout layout = new GridLayout(getContext());
        layout.setColumnCount(1);
        for (final String username : travelDTO.getSharedToUsersUsernames()) {
            final Button placeButton = new Button(getContext());
            TypedValue outValue = new TypedValue();
            getContext().getTheme().resolveAttribute(R.attr.selectableItemBackground, outValue, true);
            placeButton.setBackgroundResource(outValue.resourceId);
            placeButton.setText(username);
            placeButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    PopupMenu popup = new PopupMenu(getContext(), placeButton);

                    //Inflating the Popup using xml file
                    popup.getMenuInflater().inflate(R.menu.popup_travel_place, popup.getMenu());

                    if (shared) {
                        popup.getMenu().removeItem(R.id.delete_action);
                    }

                    //registering popup with OnMenuItemClickListener
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.popup_travel_place_show_action: {
                                    for (Listener listener : listeners) {
                                        listener.onTravelSharedUserShowClicked(username);
                                    }
                                }
                                break;
                                case R.id.delete_action: {
                                    for (Listener listener : listeners) {
                                        listener.onTravelSharedUserDeleteClicked(travelDTO.getName(), username);
                                    }
                                }
                                break;
                            }
                            return true;
                        }
                    });

                    popup.show();//showing popup menu
                }
            });
            layout.addView(placeButton);
        }
        return layout;
    }

    protected ImageButton getShareButton() {
        ImageButton shareButton = new ImageButton(getContext());
        shareButton.setImageResource(R.drawable.ic_travel_share);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 3);
        shareButton.setLayoutParams(layoutParams);
        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (Listener listener : listeners) {
                    listener.onTravelShareButtonClicked(travelDTO.getName());
                }
            }
        });
        return shareButton;
    }

    public ImageButton getRestoreButton() {
        ImageButton showRouteButton = new ImageButton(getContext());
        showRouteButton.setImageResource(R.drawable.ic_travel_restore);
        LinearLayout.LayoutParams showRouteButtonLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 3);
        showRouteButton.setLayoutParams(showRouteButtonLayoutParams);
        showRouteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (Listener listener : listeners) {
                    listener.onTravelTrashButtonClicked(travelDTO.getName());
                }
            }
        });
        return showRouteButton;
    }

    public ImageButton getRouteButton() {
        ImageButton showRouteButton = new ImageButton(getContext());
        showRouteButton.setImageResource(R.drawable.ic_map_travel_route);
        LinearLayout.LayoutParams showRouteButtonLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 3);
        showRouteButton.setLayoutParams(showRouteButtonLayoutParams);
        showRouteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (Listener listener : listeners) {
                    listener.onTravelRouteButtonClicked(travelDTO.getPlaces());
                }
            }
        });
        return showRouteButton;
    }

    public ImageButton getTrashButton(final boolean shared) {
        ImageButton trashButton = new ImageButton(getContext());

        if (!shared) {
            trashButton.setImageResource(R.drawable.ic_map_travel_trash);
            trashButton.setTooltipText("Remove travel");
        } else {
            trashButton.setImageResource(R.drawable.ic_travel_left);
            trashButton.setTooltipText("Leave travel");
        }

        LinearLayout.LayoutParams trashRouteButtonLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 3);
        trashButton.setLayoutParams(trashRouteButtonLayoutParams);
        trashButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (Listener listener : listeners) {
                    if (!shared) {
                        listener.onTravelTrashButtonClicked(travelDTO.getName());
                    } else {
                        listener.onTravelLeaveButtonClicked(travelDTO.getOwnerUsername(), travelDTO.getName());
                    }
                }
            }
        });
        return trashButton;
    }

    public TravelDTO getTravelDTO() {
        return travelDTO;
    }

    public abstract LinearLayout getTravelOperationButtonsPanel();

    public abstract LinearLayout getShareOperationButtonsPanel();

    public void updateTravelDTO(TravelDTO travelDTO) {
        this.travelDTO = travelDTO;
        initMainLayout();
    }

    public void removeTravelPlace(PlaceDescriptionDTO placeDescriptionDTO) {
        Button button = travelPlacesButtonsMap.remove(placeDescriptionDTO.getName());
        travelPlacesButtonslayout.removeView(button);
        Iterator<PlaceMapDTO> placeMapDTOIterator = travelDTO.getPlaces().iterator();
        while (placeMapDTOIterator.hasNext()) {
            PlaceMapDTO placeMapDTO = placeMapDTOIterator.next();
            if (placeMapDTO.getName().equals(placeDescriptionDTO.getName())) {
                placeMapDTOIterator.remove();
            }
        }
    }

    public interface Listener {

        void onTravelRouteButtonClicked(List<PlaceMapDTO> placeMapDTOList);

        void onTravelTrashButtonClicked(String travelName);

        void onTravelPlaceShowClicked(PlaceMapDTO placeMapDTO);

        void onTravelSharedUserShowClicked(String username);

        void onTravelShareButtonClicked(String travelDTOName);

        void onTravelSharedUserDeleteClicked(String travelName, String username);

        void onTravelPlaceDeleteClicked(String travelName, PlaceMapDTO placeMapDTO);

        void onTravelLeaveButtonClicked(String ownerName, String travelName);
    }
}