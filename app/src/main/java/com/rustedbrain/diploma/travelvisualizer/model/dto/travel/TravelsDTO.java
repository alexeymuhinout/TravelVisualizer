package com.rustedbrain.diploma.travelvisualizer.model.dto.travel;

import com.rustedbrain.diploma.travelvisualizer.model.dto.HttpDTO;

import org.springframework.http.HttpStatus;

import java.util.List;

public class TravelsDTO extends HttpDTO {

    private List<String> travelsNames;

    public TravelsDTO() {
    }

    public TravelsDTO(HttpStatus status, List<String> travelsNames) {
        super(status);
        this.travelsNames = travelsNames;
    }

    public List<String> getTravelsNames() {
        return travelsNames;
    }

    public void setTravelsNames(List<String> travelsNames) {
        this.travelsNames = travelsNames;
    }
}
