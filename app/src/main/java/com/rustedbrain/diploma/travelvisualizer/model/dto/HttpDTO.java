package com.rustedbrain.diploma.travelvisualizer.model.dto;

import org.springframework.http.HttpStatus;

import java.io.Serializable;

public abstract class HttpDTO implements Serializable {

    private HttpStatus status;

    public HttpStatus getStatus() {
        return status;
    }

    public void setStatus(HttpStatus status) {
        this.status = status;
    }
}