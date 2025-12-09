package com.control.trafficlight.model;

import java.time.LocalDateTime;

public class LightStateRecord {
    private final TrafficLightState state;
    private final LocalDateTime timestamp;
    private final long durationInSeconds;

    public LightStateRecord(TrafficLightState state, LocalDateTime timestamp, long durationInSeconds) {
        this.state = state;
        this.timestamp = timestamp;
        this.durationInSeconds = durationInSeconds;
    }

    // Getters
    public TrafficLightState getState() { return state; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public long getDurationInSeconds() { return durationInSeconds; }
}