package com.rustedbrain.diploma.travelvisualizer.model.dto.travel;

import java.io.Serializable;
import java.util.List;

public class PlaceDescriptionDTO implements Serializable {

    private PlaceType type;
    private String name;
    private String description;
    private float rating;
    private LatLngDTO latLngDTO;
    private List<byte[]> photoList;
    private List<CommentDTO> commentList;
    private boolean ignoredByUser;

    public List<CommentDTO> getCommentList() {
        return commentList;
    }

    public void setCommentList(List<CommentDTO> commentList) {
        this.commentList = commentList;
    }

    public LatLngDTO getLatLngDTO() {
        return latLngDTO;
    }

    public void setLatLngDTO(LatLngDTO latLngDTO) {
        this.latLngDTO = latLngDTO;
    }

    public PlaceType getType() {
        return type;
    }

    public void setType(PlaceType type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isIgnoredByUser() {
        return ignoredByUser;
    }

    public void setIgnoredByUser(boolean ignoredByUser) {
        this.ignoredByUser = ignoredByUser;
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

    public List<byte[]> getPhotoList() {
        return photoList;
    }

    public void setPhotoList(List<byte[]> photoList) {
        this.photoList = photoList;
    }

    @Override
    public String toString() {
        return "PlaceDescriptionDTO{" +
                "type=" + type +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", rating=" + rating +
                '}';
    }
}
