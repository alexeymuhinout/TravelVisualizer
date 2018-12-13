package com.rustedbrain.diploma.travelvisualizer.model.dto.travel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TravelDTO implements Serializable {

    private String ownerUsername;
    private String name;
    private boolean archived;
    private List<String> sharedToUsersUsernames = new ArrayList<>();
    private List<PlaceMapDTO> places = new ArrayList<>();

    public TravelDTO() {
    }

    public String getOwnerUsername() {
        return ownerUsername;
    }

    public void setOwnerUsername(String ownerUsername) {
        this.ownerUsername = ownerUsername;
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
        return Objects.equals(ownerUsername, travelDTO.ownerUsername) &&
                Objects.equals(name, travelDTO.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ownerUsername, name);
    }

    @Override
    public String toString() {
        return this.name;
    }
}

