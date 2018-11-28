package com.rustedbrain.diploma.travelvisualizer.model.dto.travel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TravelDTO implements Serializable {

    private String name;
    private boolean archived;
    private List<String> sharedToUsersUsernames = new ArrayList<>();
    private List<PlaceMapDTO> places = new ArrayList<>();

    public TravelDTO(String name) {
        this(name, false);
    }

    public TravelDTO(String name, boolean archived) {
        this(name, archived, null, null);
    }


    public TravelDTO(String name, boolean archived, List<String> sharedToUsersUsernames, List<PlaceMapDTO> places) {
        this.name = name;
        this.archived = archived;
        this.sharedToUsersUsernames = sharedToUsersUsernames;
        this.places = places;
    }

    public TravelDTO() {
    }

    public List<String> getSharedToUsersUsernames() {
        return sharedToUsersUsernames;
    }

    public void setSharedToUsersUsernames(List<String> sharedToUsersUsernames) {
        this.sharedToUsersUsernames = sharedToUsersUsernames;
    }

    public List<PlaceMapDTO> getPlaces() {
        return places;
    }

    public void setPlaces(List<PlaceMapDTO> places) {
        this.places = places;
    }

    public boolean isArchived() {
        return archived;
    }

    public void setArchived(boolean archived) {
        this.archived = archived;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TravelDTO travelDTO = (TravelDTO) o;
        return archived == travelDTO.archived &&
                Objects.equals(name, travelDTO.name) &&
                Objects.equals(places, travelDTO.places);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, archived, places);
    }

    @Override
    public String toString() {
        return "TravelDTO{" +
                "name='" + name + '\'' +
                ", archived=" + archived +
                ", places=" + places +
                '}';
    }
}
