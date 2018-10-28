package com.rustedbrain.diploma.travelvisualizer.model.dto.travel;

import java.io.Serializable;
import java.util.Objects;

public class LatLngDTO implements Serializable {

    protected double latitude;
    protected double longitude;

    public LatLngDTO() {
    }

    public LatLngDTO(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LatLngDTO latLngDTO = (LatLngDTO) o;
        return Double.compare(latLngDTO.latitude, latitude) == 0 &&
                Double.compare(latLngDTO.longitude, longitude) == 0;
    }

    @Override
    public int hashCode() {

        return Objects.hash(latitude, longitude);
    }

    @Override
    public String toString() {
        return "BoundsLatLngDTO{" +
                "latitude=" + latitude +
                ", longitude=" + longitude +
                '}';
    }
}
