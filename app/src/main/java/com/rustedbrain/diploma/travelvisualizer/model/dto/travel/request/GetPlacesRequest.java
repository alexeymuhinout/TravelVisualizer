package com.rustedbrain.diploma.travelvisualizer.model.dto.travel.request;

import com.rustedbrain.diploma.travelvisualizer.model.dto.LatLngBoundsDTO;
import com.rustedbrain.diploma.travelvisualizer.model.dto.travel.TripsMapPlacesFilterDTO;

public class GetPlacesRequest {

    private LatLngBoundsDTO latLngBoundsDTO;
    private TripsMapPlacesFilterDTO tripsMapPlacesFilterDTO;

    public GetPlacesRequest() {
    }

    public GetPlacesRequest(LatLngBoundsDTO latLngBoundsDTO, TripsMapPlacesFilterDTO tripsMapPlacesFilterDTO) {
        this.latLngBoundsDTO = latLngBoundsDTO;
        this.tripsMapPlacesFilterDTO = tripsMapPlacesFilterDTO;
    }

    public TripsMapPlacesFilterDTO getTripsMapPlacesFilterDTO() {
        return tripsMapPlacesFilterDTO;
    }

    public void setTripsMapPlacesFilterDTO(TripsMapPlacesFilterDTO tripsMapPlacesFilterDTO) {
        this.tripsMapPlacesFilterDTO = tripsMapPlacesFilterDTO;
    }

    public LatLngBoundsDTO getLatLngBoundsDTO() {
        return latLngBoundsDTO;
    }

    public void setLatLngBoundsDTO(LatLngBoundsDTO latLngBoundsDTO) {
        this.latLngBoundsDTO = latLngBoundsDTO;
    }
}
