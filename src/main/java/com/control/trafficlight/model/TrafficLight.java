package com.control.trafficlight.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayDeque;
import java.util.Deque;

public class TrafficLight {
    private String direction;  // NORTH, SOUTH, EAST, WEST
    private TrafficLightState currentState;
    private int duration;  // in seconds
    private List<TrafficLightState> sequence;
    private LocalDateTime lastStateChangeTime;
    private Deque<LightStateRecord> stateHistory = new ArrayDeque<>();
    private static final int MAX_HISTORY_SIZE = 100;

    public TrafficLight(String direction, TrafficLightState initialState, int duration) {
        this.direction = direction;
        this.currentState = initialState;
        this.duration = duration;
        this.lastStateChangeTime = LocalDateTime.now();  // Initialize timestamp here
        this.sequence = new ArrayList<>(Arrays.asList(
                TrafficLightState.RED,
                TrafficLightState.GREEN,
                TrafficLightState.YELLOW
        ));
    }

    public void changeState() {
        // Record current state duration before changing
        if (currentState != null) {
            long duration = ChronoUnit.SECONDS.between(lastStateChangeTime, LocalDateTime.now());
            stateHistory.addFirst(new LightStateRecord(currentState, lastStateChangeTime, duration));
            if (stateHistory.size() > MAX_HISTORY_SIZE) {
                stateHistory.removeLast();
            }
        }

        // Change to next state
        int currentIndex = sequence.indexOf(currentState);
        int nextIndex = (currentIndex + 1) % sequence.size();
        TrafficLightState newState = sequence.get(nextIndex);

        // Only update if state is actually changing
        if (!newState.equals(currentState)) {
            this.currentState = newState;
            this.lastStateChangeTime = LocalDateTime.now();
        }
    }

    public void setSequence(List<TrafficLightState> newSequence) {
        if (newSequence != null && !newSequence.isEmpty()) {
            this.sequence = new ArrayList<>(newSequence);
            if (!sequence.contains(this.currentState)) {
                this.currentState = sequence.get(0);
            }
        }
    }

    public void setCurrentState(TrafficLightState state) {
        if (state == null) {
            throw new IllegalArgumentException("State cannot be null");
        }
        if (!sequence.contains(state)) {
            throw new IllegalArgumentException("State " + state + " is not in the current sequence");
        }

        // Only update if state is actually changing
        if (!state.equals(this.currentState)) {
            if (currentState != null) {
                long duration = ChronoUnit.SECONDS.between(lastStateChangeTime, LocalDateTime.now());
                stateHistory.addFirst(new LightStateRecord(currentState, lastStateChangeTime, duration));
                if (stateHistory.size() > MAX_HISTORY_SIZE) {
                    stateHistory.removeLast();
                }
            }
            this.currentState = state;
            this.lastStateChangeTime = LocalDateTime.now();
        }
    }

    // Getters
    public String getDirection() { return direction; }
    public TrafficLightState getCurrentState() { return currentState; }
    public List<LightStateRecord> getStateHistory() { return new ArrayList<>(stateHistory); }
    public long getCurrentStateDuration() {
        return ChronoUnit.SECONDS.between(lastStateChangeTime, LocalDateTime.now());
    }
    public int getDuration() { return duration; }
    public void setDuration(int duration) { this.duration = duration; }
}