package com.rustedbrain.diploma.travelvisualizer.model.dto.travel;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

public class TravelDTOList implements Serializable {

    private List<TravelDTO> travelDTOList;

    public TravelDTOList() {
    }

    public TravelDTOList(List<TravelDTO> travelDTOList) {
        this.travelDTOList = travelDTOList;
    }

    public List<TravelDTO> getTravelDTOList() {
        return travelDTOList;
    }

    public void setTravelDTOList(List<TravelDTO> travelDTOList) {
        this.travelDTOList = travelDTOList;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TravelDTOList that = (TravelDTOList) o;
        return Objects.equals(travelDTOList, that.travelDTOList);
    }

    @Override
    public int hashCode() {

        return Objects.hash(travelDTOList);
    }

    @Override
    public String toString() {
        return "TravelDTOList{" +
                "travelDTOList=" + travelDTOList +
                '}';
    }
}
