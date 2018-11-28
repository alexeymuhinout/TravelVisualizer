package com.rustedbrain.diploma.travelvisualizer.fragment.travel;

import android.content.Context;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.rustedbrain.diploma.travelvisualizer.model.dto.travel.TravelDTO;

public class ArchivedTravelLayout extends TravelLayout {

    private ImageButton restoreTravelButton;

    public ArchivedTravelLayout(Context context, TravelDTO travelDTO, Listener listener) {
        super(context, travelDTO, listener);
    }

    @Override
    public LinearLayout getTravelOperationButtonsPanel() {
        LinearLayout layout = new LinearLayout(getContext());
        layout.setOrientation(LinearLayout.HORIZONTAL);

        restoreTravelButton = getRestoreButton();
        layout.addView(restoreTravelButton);
        return layout;
    }

    @Override
    public LinearLayout getShareOperationButtonsPanel() {
        return new LinearLayout(getContext());
    }
}
