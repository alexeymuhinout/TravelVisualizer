package com.rustedbrain.diploma.travelvisualizer.model.dto.status;

public class GreetingServiceInfo extends ServiceInfo {

    private final String greeting;

    public GreetingServiceInfo(long id, Status status, long timeMillis, String greeting) {
        super(id, timeMillis);
        this.greeting = greeting;
    }

    public String getGreeting() {
        return greeting;
    }

    @Override
    public String toString() {
        return "GreetingServiceInfo{" +
                "greeting='" + greeting + '\'' +
                "} " + super.toString();
    }
}
