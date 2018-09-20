package com.rustedbrain.diploma.travelvisualizer.model.dto.status;

public class ServiceInfo {

    private long id;
    private long timeMillis;

    public ServiceInfo() {
    }

    public ServiceInfo(long id, long timeMillis) {
        this.id = id;
        this.timeMillis = timeMillis;
    }

    public long getId() {
        return id;
    }


    public long getTimeMillis() {
        return timeMillis;
    }

    @Override
    public String toString() {
        return "ServiceInfo{" +
                "id=" + id +
                ", timeMillis=" + timeMillis +
                '}';
    }
}
