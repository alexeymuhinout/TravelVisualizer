package com.rustedbrain.diploma.travelvisualizer.model.dto.travel;

import java.util.List;

public class PlaceMapDTOList {

    private List<PlaceMapDTO> placeMapDTOList;

    public PlaceMapDTOList() {
    }

    public PlaceMapDTOList( List<PlaceMapDTO> placeMapDTOList) {
        this.placeMapDTOList = placeMapDTOList;
    }

    public List<PlaceMapDTO> getPlaceMapDTOList() {
        return placeMapDTOList;
    }

    public void setPlaceMapDTOList(List<PlaceMapDTO> placeMapDTOList) {
        this.placeMapDTOList = placeMapDTOList;
    }
}