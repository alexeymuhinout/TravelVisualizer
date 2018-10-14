package com.rustedbrain.diploma.travelvisualizer.model.dto.travel;

import com.rustedbrain.diploma.travelvisualizer.model.dto.HttpDTO;

import org.springframework.http.HttpStatus;

import java.util.List;

public class PlaceMapDTOList extends HttpDTO {

    private List<PlaceMapDTO> placeMapDTOList;

    public PlaceMapDTOList() {
    }

    public PlaceMapDTOList(HttpStatus status, List<PlaceMapDTO> placeMapDTOList) {
        super.setStatus(status);
        this.placeMapDTOList = placeMapDTOList;
    }

    public List<PlaceMapDTO> getPlaceMapDTOList() {
        return placeMapDTOList;
    }

    public void setPlaceMapDTOList(List<PlaceMapDTO> placeMapDTOList) {
        this.placeMapDTOList = placeMapDTOList;
    }
}