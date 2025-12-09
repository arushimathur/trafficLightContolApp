package com.control.trafficlight.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TrafficLight {
    private String direction;  // NORTH, SOUTH, EAST, WEST
    private TrafficLightState currentState;
    private int duration;  // in seconds
    private List<TrafficLightState> sequence;

    public TrafficLight(String direction, TrafficLightState initialState, int duration) {
        this.direction = direction;
        this.currentState = initialState;
        this.duration = duration;
        // Default sequence
        this.sequence = new ArrayList<>(Arrays.asList(
            TrafficLightState.RED, 
            TrafficLightState.GREEN, 
            TrafficLightState.YELLOW
        ));
    }

    // Changes the light to the next state in the current sequence
    public void changeState() {
        int currentIndex = sequence.indexOf(currentState);
        int nextIndex = (currentIndex + 1) % sequence.size();
        this.currentState = sequence.get(nextIndex);
    }
    
    // Set a custom sequence of states
    public void setSequence(List<TrafficLightState> newSequence) {
        if (newSequence != null && !newSequence.isEmpty()) {
            this.sequence = new ArrayList<>(newSequence);
            // If current state is not in the new sequence, set to first state in sequence
            if (!sequence.contains(this.currentState)) {
                this.currentState = sequence.get(0);
            }
        }
    }

    // Getters and Setters
    public String getDirection() {
        return direction;
    }

    public TrafficLightState getCurrentState() {
        return currentState;
    }
    
    public void setCurrentState(TrafficLightState state) {
        if (state == null) {
            throw new IllegalArgumentException("State cannot be null");
        }
        // Only allow states that are in the current sequence
        if (!sequence.contains(state)) {
            throw new IllegalArgumentException("State " + state + " is not in the current sequence: " + sequence);
        }
        this.currentState = state;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }
}
