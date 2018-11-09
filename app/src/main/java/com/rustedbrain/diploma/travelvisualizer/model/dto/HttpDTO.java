package com.rustedbrain.diploma.travelvisualizer.model.dto;

import org.springframework.http.HttpStatus;

import java.io.Serializable;

public abstract class HttpDTO implements Serializable {

    private HttpStatus status;

    public HttpDTO(HttpStatus status) {
        this.status = status;
    }

    public HttpDTO() {
    }

    public HttpStatus getStatus() {
        return status;
    }

    public void setStatus(HttpStatus status) {
        this.status = status;
    }
}