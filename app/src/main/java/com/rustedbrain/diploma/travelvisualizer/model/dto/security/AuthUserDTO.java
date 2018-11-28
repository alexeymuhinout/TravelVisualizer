package com.rustedbrain.diploma.travelvisualizer.model.dto.security;

import java.util.List;

public class AuthUserDTO extends UserDTO {

    private String token;

    public AuthUserDTO() {
    }

    public AuthUserDTO(String token) {
        this.token = token;
    }

    public AuthUserDTO(String username, List<String> roles, String token) {
        super(username, roles);
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    @Override
    public String toString() {
        return "AuthUserDTO{" +
                "token='" + token + '\'' +
                "} " + super.toString();
    }
}
