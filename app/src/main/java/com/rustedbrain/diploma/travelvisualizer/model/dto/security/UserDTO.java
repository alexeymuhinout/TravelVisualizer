package com.rustedbrain.diploma.travelvisualizer.model.dto.security;

import java.io.Serializable;
import java.util.List;

public class UserDTO implements Serializable {

    private String username;
    private List<String> roles;

    public UserDTO() {
    }

    public UserDTO(String username, List<String> roles) {
        this.roles = roles;
        this.username = username;
    }


    public List<String> getRoles() {
        return this.roles;
    }

    public String getUsername() {
        return this.username;
    }

    @Override
    public String toString() {
        return "UserDTO{" +
                "username='" + username + '\'' +
                ", roles=" + roles +
                '}';
    }
}
