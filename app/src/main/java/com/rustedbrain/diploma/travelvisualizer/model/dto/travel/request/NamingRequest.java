package com.rustedbrain.diploma.travelvisualizer.model.dto.travel.request;

import java.io.Serializable;
import java.util.Objects;

public class NamingRequest implements Serializable {

    private String name;

    public NamingRequest() {
    }

    public NamingRequest(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NamingRequest that = (NamingRequest) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {

        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return "NamingRequest{" +
                "name='" + name + '\'' +
                '}';
    }
}
