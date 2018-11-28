package com.rustedbrain.diploma.travelvisualizer.model.dto.travel;

import java.util.List;
import java.util.Objects;

public class TripsMapPlacesFilterDTO {

    private List<PlaceType> placeTypes;
    private int photosCount;
    private float minRating;
    private float maxRating;
    private int commentsCount;

    public TripsMapPlacesFilterDTO() {
    }

    public TripsMapPlacesFilterDTO(List<PlaceType> placeTypes, int photosCount, float minRating, float maxRating, int commentsCount) {
        this.placeTypes = placeTypes;
        this.photosCount = photosCount;
        this.minRating = minRating;
        this.maxRating = maxRating;
        this.commentsCount = commentsCount;
    }

    public int getPhotosCount() {
        return photosCount;
    }

    public void setPhotosCount(int photosCount) {
        this.photosCount = photosCount;
    }

    public List<PlaceType> getPlaceTypes() {
        return placeTypes;
    }

    public void setPlaceTypes(List<PlaceType> placeTypes) {
        this.placeTypes = placeTypes;
    }

    public float getMinRating() {
        return minRating;
    }

    public void setMinRating(float minRating) {
        this.minRating = minRating;
    }

    public float getMaxRating() {
        return maxRating;
    }

    public void setMaxRating(float maxRating) {
        this.maxRating = maxRating;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TripsMapPlacesFilterDTO that = (TripsMapPlacesFilterDTO) o;
        return photosCount == that.photosCount &&
                Float.compare(that.minRating, minRating) == 0 &&
                Float.compare(that.maxRating, maxRating) == 0 &&
                commentsCount == that.commentsCount &&
                Objects.equals(placeTypes, that.placeTypes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(placeTypes, photosCount, minRating, maxRating, commentsCount);
    }

    @Override
    public String toString() {
        return "TripsMapPlacesFilterDTO{" +
                "placeTypes=" + placeTypes +
                ", photosCount=" + photosCount +
                ", minRating=" + minRating +
                ", maxRating=" + maxRating +
                ", commentsCount=" + commentsCount +
                '}';
    }
}
