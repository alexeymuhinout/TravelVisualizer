package com.rustedbrain.diploma.travelvisualizer.model.dto.security;

import org.springframework.http.HttpStatus;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

public class UserDTO implements Serializable {

    private String username;
    private List<String> roles;
    private String token;
    private HttpStatus status;

    public UserDTO(String username, List<String> roles, String token, HttpStatus status) {
        this.roles = roles;
        this.token = token;
        this.username = username;
        this.status = status;
    }

    public UserDTO(HttpStatus status) {
        this("", Collections.<String>emptyList(), "", status);
    }

    public UserDTO() {
        this("", Collections.<String>emptyList(), "", HttpStatus.NOT_FOUND);
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

    public HttpStatus getStatus() {
        return this.status;
    }

    @Override
    public String toString() {
        return "UserDTO{" +
                "username='" + username + '\'' +
                ", roles=" + roles +
                ", status=" + status +
                '}';
    }
}
