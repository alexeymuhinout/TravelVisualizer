package com.rustedbrain.diploma.travelvisualizer.fragment.travel;

import android.content.Context;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.rustedbrain.diploma.travelvisualizer.model.dto.travel.TravelDTO;

public class ActiveTravelLayout extends TravelLayout {

    private ImageButton trashButton;
    private ImageButton showRouteButton;
    private ImageButton shareButton;

    public ActiveTravelLayout(Context context, TravelDTO travelDTO, boolean shared, Listener listener) {
        super(context, travelDTO, shared, listener);
    }

    @Override
    public LinearLayout getTravelOperationButtonsPanel() {
        LinearLayout layout = new LinearLayout(getContext());
        layout.setOrientation(LinearLayout.HORIZONTAL);

        showRouteButton = getRouteButton();
        layout.addView(showRouteButton);

        trashButton = getTrashButton(shared);
        layout.addView(trashButton);
        return layout;
    }

    @Override
    public LinearLayout getShareOperationButtonsPanel() {
        LinearLayout layout = new LinearLayout(getContext());
        layout.setOrientation(LinearLayout.HORIZONTAL);

        shareButton = getShareButton();
        layout.addView(shareButton);

        return layout;
    }
}