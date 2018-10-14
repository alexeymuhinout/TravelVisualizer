package com.rustedbrain.diploma.travelvisualizer.model.dto.security;

import com.rustedbrain.diploma.travelvisualizer.model.dto.HttpDTO;

import org.springframework.http.HttpStatus;

import java.util.Collections;
import java.util.List;

public class UserDTO extends HttpDTO {

    private String username;
    private List<String> roles;
    private String token;

    public UserDTO(HttpStatus status, String username, List<String> roles, String token) {
        super.setStatus(status);
        this.roles = roles;
        this.token = token;
        this.username = username;
    }

    public UserDTO(HttpStatus status) {
        this(status, "", Collections.<String>emptyList(), "");
    }

    public UserDTO() {
        this(HttpStatus.NOT_FOUND, "", Collections.<String>emptyList(), "");
    }

    public List<String> getRoles() {
        return this.roles;
    }

    public String getToken() {
        return this.token;
    }

    public String getUsername() {
        return this.username;
    }

    @Override
    public String toString() {
        return "UserDTO{" +
                "username='" + username + '\'' +
                ", roles=" + roles +
                ", token='" + token + '\'' +
                "} " + super.toString();
    }
}
