package com.rustedbrain.diploma.travelvisualizer.model.dto.travel.request;

import com.rustedbrain.diploma.travelvisualizer.model.dto.travel.PlaceType;

import java.util.List;

public class AddPlaceRequest {

    private String name;
    private String description;
    private double latitude;
    private double longitude;
    private List<byte[]> photoList;
    private PlaceType placeType;

    public AddPlaceRequest() {
    }

    public AddPlaceRequest(PlaceType placeType, String name, String description, double latitude, double longitude, List<byte[]> photoList) {
        this.placeType = placeType;
        this.name = name;
        this.description = description;
        this.latitude = latitude;
        this.longitude = longitude;
        this.photoList = photoList;
        this.placeType = placeType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public List<byte[]> getPhotoList() {
        return photoList;
    }

    public void setPhotoList(List<byte[]> photoList) {
        this.photoList = photoList;
    }

    public PlaceType getPlaceType() {
        return placeType;
    }

    public void setPlaceType(PlaceType placeType) {
        this.placeType = placeType;
    }
}
