package com.rustedbrain.diploma.travelvisualizer.model.dto.travel;

public class PlaceIgnoredDTO {

    private boolean placeIgnored;

    public PlaceIgnoredDTO() {
    }

    public PlaceIgnoredDTO(boolean placeIgnored) {
        this.placeIgnored = placeIgnored;
    }

    public boolean isPlaceIgnored() {
        return placeIgnored;
    }

    public void setPlaceIgnored(boolean placeIgnored) {
        this.placeIgnored = placeIgnored;
    }
}
