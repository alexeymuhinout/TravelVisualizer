package com.rustedbrain.diploma.travelvisualizer.model.dto.travel.request;

import com.rustedbrain.diploma.travelvisualizer.model.dto.travel.LatLngDTO;

public class GetPlaceDescriptionDTORequest {

    private LatLngDTO latLngDTO;

    public GetPlaceDescriptionDTORequest() {
    }

    public GetPlaceDescriptionDTORequest(LatLngDTO latLngDTO) {
        this.latLngDTO = latLngDTO;
    }

    public LatLngDTO getLatLngDTO() {
        return latLngDTO;
    }

    public void setLatLngDTO(LatLngDTO latLngDTO) {
        this.latLngDTO = latLngDTO;
    }


}
