package com.rustedbrain.diploma.travelvisualizer.model.dto.travel.request;

import com.rustedbrain.diploma.travelvisualizer.model.dto.travel.LatLngDTO;

public class CommentAddRequest {

    private LatLngDTO placeLatLng;
    private float rating;
    private String text;

    public CommentAddRequest() {
    }

    public CommentAddRequest(LatLngDTO placeLatLng, float rating, String text) {
        this.placeLatLng = placeLatLng;
        this.rating = rating;
        this.text = text;

    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public LatLngDTO getPlaceLatLng() {
        return placeLatLng;
    }

    public void setPlaceLatLng(LatLngDTO placeLatLng) {
        this.placeLatLng = placeLatLng;
    }
}
