package com.rustedbrain.diploma.travelvisualizer.model.dto.travel.request;

import java.util.Collection;
import java.util.Objects;

public class TravelSharedUsersSetRequest extends AuthorizedNamingRequest {

    private Collection<String> usernames;

    public TravelSharedUsersSetRequest() {
    }

    public TravelSharedUsersSetRequest(String name, String username, Collection<String> usernames) {
        super(name, username);
        this.usernames = usernames;
    }

    public Collection<String> getUsernames() {
        return usernames;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        TravelSharedUsersSetRequest that = (TravelSharedUsersSetRequest) o;
        return Objects.equals(usernames, that.usernames);
    }

    @Override
    public int hashCode() {

        return Objects.hash(super.hashCode(), usernames);
    }

    @Override
    public String toString() {
        return "TravelSharedUsersSetRequest{" +
                "usernames=" + usernames +
                '}';
    }
}
