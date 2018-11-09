package com.rustedbrain.diploma.travelvisualizer.model.dto.travel;

import com.rustedbrain.diploma.travelvisualizer.model.dto.HttpDTO;

import org.springframework.http.HttpStatus;

public class PlaceIgnoredDTO extends HttpDTO {

    private boolean placeIgnored;

    public PlaceIgnoredDTO() {
    }

    public PlaceIgnoredDTO(boolean placeIgnored, HttpStatus status) {
        super(status);
        this.placeIgnored = placeIgnored;
    }

    public boolean isPlaceIgnored() {
        return placeIgnored;
    }

    public void setPlaceIgnored(boolean placeIgnored) {
        this.placeIgnored = placeIgnored;
    }
}
