package com.rustedbrain.diploma.travelvisualizer.model.dto.travel.request;

import com.rustedbrain.diploma.travelvisualizer.model.dto.travel.LatLngDTO;

public class NamingPlaceRequest extends NamingRequest {

    private LatLngDTO latLngDTO;

    public NamingPlaceRequest() {
    }

    public NamingPlaceRequest(String name, LatLngDTO latLngDTO) {
        super(name);
        this.latLngDTO = latLngDTO;
    }

    public LatLngDTO getLatLngDTO() {
        return latLngDTO;
    }

    public void setLatLngDTO(LatLngDTO latLngDTO) {
        this.latLngDTO = latLngDTO;
    }
}
