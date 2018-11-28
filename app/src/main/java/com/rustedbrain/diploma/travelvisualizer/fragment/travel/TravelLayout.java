package com.rustedbrain.diploma.travelvisualizer.fragment.travel;

import android.content.Context;
import android.graphics.Color;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.rustedbrain.diploma.travelvisualizer.R;
import com.rustedbrain.diploma.travelvisualizer.model.dto.travel.PlaceMapDTO;
import com.rustedbrain.diploma.travelvisualizer.model.dto.travel.TravelDTO;

import java.util.Collections;
import java.util.List;

public abstract class TravelLayout extends LinearLayout {

    protected final TravelDTO travelDTO;
    private final Button showPlacesButton;
    private final List<Listener> listeners;
    //private boolean travelPlacesLayout
    private final GridLayout travelPlacesLayout;
    private final GridLayout sharedToUsersLayout;
    private final Button usersButton;

    public TravelLayout(Context context, final TravelDTO travelDTO, Listener listener) {
        super(context);
        this.travelDTO = travelDTO;
        this.listeners = Collections.singletonList(listener);

        final LinearLayout travelLayout = new LinearLayout(getContext());
        travelLayout.setOrientation(LinearLayout.VERTICAL);

        TextView travelNameTextView = new TextView(getContext());
        travelNameTextView.setText(travelDTO.getName());
        travelNameTextView.setTextSize(16);
        travelLayout.addView(travelNameTextView);

        this.travelPlacesLayout = getTravelPlacesLayout();

        showPlacesButton = new Button(getContext());
        LinearLayout.LayoutParams showPlacesButtonLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1);
        showPlacesButton.setLayoutParams(showPlacesButtonLayoutParams);
        showPlacesButton.setText("Route");
        showPlacesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (travelPlacesLayout.isAttachedToWindow()) {
                    travelLayout.removeView(travelPlacesLayout);
                } else {
                    travelLayout.addView(travelPlacesLayout);
                }
            }
        });

        this.sharedToUsersLayout = getSharedToUsersLayout();

        usersButton = new Button(getContext());
        LinearLayout.LayoutParams userButtonLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1);
        usersButton.setLayoutParams(userButtonLayoutParams);
        usersButton.setText("Shared users");
        usersButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sharedToUsersLayout.isAttachedToWindow()) {
                    travelLayout.removeView(sharedToUsersLayout);
                } else {
                    travelLayout.addView(sharedToUsersLayout);
                }
            }
        });

        LinearLayout routeNavigationButtonsLayout = new LinearLayout(getContext());
        routeNavigationButtonsLayout.setOrientation(LinearLayout.HORIZONTAL);
        routeNavigationButtonsLayout.addView(showPlacesButton);
        routeNavigationButtonsLayout.addView(getTravelOperationButtonsPanel());

        travelLayout.addView(routeNavigationButtonsLayout);

        LinearLayout usersNavigationButtonsLayout = new LinearLayout(getContext());
        usersNavigationButtonsLayout.setOrientation(LinearLayout.HORIZONTAL);
        usersNavigationButtonsLayout.addView(usersButton);
        usersNavigationButtonsLayout.addView(getShareOperationButtonsPanel());

        travelLayout.addView(usersNavigationButtonsLayout);

        super.setOrientation(LinearLayout.VERTICAL);
        super.setBackgroundColor(Color.parseColor("#ffffff"));
        super.addView(travelLayout);
        LinearLayout.LayoutParams commentLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        commentLayoutParams.setMargins(5, 5, 5, 5);
        super.setLayoutParams(commentLayoutParams);
    }

    private GridLayout getTravelPlacesLayout() {
        GridLayout layout = new GridLayout(getContext());
        layout.setColumnCount(1);
        for (final PlaceMapDTO placeMapDTO : travelDTO.getPlaces()) {
            final Button placeButton = new Button(getContext());
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

    private GridLayout getSharedToUsersLayout() {
        GridLayout layout = new GridLayout(getContext());
        layout.setColumnCount(1);
        for (final String username : travelDTO.getSharedToUsersUsernames()) {
            final Button placeButton = new Button(getContext());
            placeButton.setText(username);
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
                                        listener.onTravelSharedUserClicked(username);
                                    }
                                }
                                break;
                                case R.id.delete_action: {

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

    public ImageButton getTrashButton() {
        ImageButton trashButton = new ImageButton(getContext());
        trashButton.setImageResource(R.drawable.ic_map_travel_trash);
        LinearLayout.LayoutParams trashRouteButtonLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 3);
        trashButton.setLayoutParams(trashRouteButtonLayoutParams);
        trashButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (Listener listener : listeners) {
                    listener.onTravelTrashButtonClicked(travelDTO.getName());
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

    public interface Listener {

        void onTravelRouteButtonClicked(List<PlaceMapDTO> placeMapDTOList);

        void onTravelTrashButtonClicked(String travelName);

        void onTravelPlaceShowClicked(PlaceMapDTO placeMapDTO);

        void onTravelSharedUserClicked(String username);

        void onTravelShareButtonClicked(String travelDTOName);
    }
}