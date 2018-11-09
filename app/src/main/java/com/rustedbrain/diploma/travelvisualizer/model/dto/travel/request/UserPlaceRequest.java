package com.rustedbrain.diploma.travelvisualizer.model.dto.travel.request;

import com.rustedbrain.diploma.travelvisualizer.model.dto.travel.LatLngDTO;

public class UserPlaceRequest extends UserRequest{

    private LatLngDTO latLngDTO;

    public UserPlaceRequest() {
    }

    public UserPlaceRequest(String username, LatLngDTO latLngDTO) {
        super(username);
        this.latLngDTO = latLngDTO;
    }

    public LatLngDTO getLatLngDTO() {
        return latLngDTO;
    }

    public void setLatLngDTO(LatLngDTO latLngDTO) {
        this.latLngDTO = latLngDTO;
    }
}
