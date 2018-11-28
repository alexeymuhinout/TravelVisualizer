package com.rustedbrain.diploma.travelvisualizer.model.dto.travel.request;

import java.util.Objects;

public class AuthorizedNamingRequest extends NamingRequest {

    private String username;

    public AuthorizedNamingRequest() {
    }

    public AuthorizedNamingRequest(String username) {
        this.username = username;
    }

    public AuthorizedNamingRequest(String name, String username) {
        super(name);
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        AuthorizedNamingRequest that = (AuthorizedNamingRequest) o;
        return Objects.equals(username, that.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), username);
    }

    @Override
    public String toString() {
        return "AuthorizedNamingRequest{" +
                "username='" + username + '\'' +
                "} " + super.toString();
    }
}
