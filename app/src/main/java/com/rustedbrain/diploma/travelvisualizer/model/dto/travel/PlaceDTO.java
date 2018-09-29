package com.rustedbrain.diploma.travelvisualizer.model.dto.travel;

import org.springframework.http.HttpStatus;

import java.io.Serializable;
import java.util.List;

public class PlaceDTO implements Serializable {

    private String name;
    private String description;
    private float rating;
    private double latitude;
    private double longitude;
    private List<byte[]> photos;
    private HttpStatus status;

    public PlaceDTO(String name, String description, float rating, double latitude, double longitude, List<byte[]> photos, HttpStatus status) {
        this.name = name;
        this.description = description;
        this.rating = rating;
        this.latitude = latitude;
        this.longitude = longitude;
        this.photos = photos;
        this.status = status;
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

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public List<byte[]> getPhotos() {
        return photos;
    }

    public void setPhotos(List<byte[]> photos) {
        this.photos = photos;
    }

    @Override
    public String toString() {
        return "PlaceDTO{" +
                "name='" + name + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                '}';
    }
}
