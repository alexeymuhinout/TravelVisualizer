package com.rustedbrain.diploma.travelvisualizer.model.dto.travel.request;

import com.rustedbrain.diploma.travelvisualizer.model.dto.travel.LatLngDTO;

public class TravelPlaceAddRequest extends NamingPlaceRequest {

    private String username;

    public TravelPlaceAddRequest() {
    }

    public TravelPlaceAddRequest(String name, LatLngDTO latLngDTO, String username) {
        super(name, latLngDTO);
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String toString() {
        return "TravelPlaceAddRequest{" +
                "username='" + username + '\'' +
                '}';
    }
}
