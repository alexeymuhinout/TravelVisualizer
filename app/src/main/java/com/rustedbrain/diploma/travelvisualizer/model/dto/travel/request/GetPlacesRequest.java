package com.rustedbrain.diploma.travelvisualizer.model.dto.travel.request;

import com.rustedbrain.diploma.travelvisualizer.model.dto.LatLngBoundsDTO;

public class GetPlacesRequest {

    private LatLngBoundsDTO latLngBoundsDTO;

    public GetPlacesRequest() {
    }

    public GetPlacesRequest(LatLngBoundsDTO latLngBoundsDTO) {
        this.latLngBoundsDTO = latLngBoundsDTO;
    }

    public LatLngBoundsDTO getLatLngBoundsDTO() {
        return latLngBoundsDTO;
    }

    public void setLatLngBoundsDTO(LatLngBoundsDTO latLngBoundsDTO) {
        this.latLngBoundsDTO = latLngBoundsDTO;
    }
}
