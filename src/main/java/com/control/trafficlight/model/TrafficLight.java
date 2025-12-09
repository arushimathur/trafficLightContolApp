package com.control.trafficlight.model;

public class TrafficLight {
    private String direction;  // NORTH, SOUTH, EAST, WEST
    private TrafficLightState currentState;
    private int duration;  // in seconds

    public TrafficLight(String direction, TrafficLightState initialState, int duration) {
        this.direction = direction;
        this.currentState = initialState;
        this.duration = duration;
    }

    // Changes the light to the next state in sequence: RED -> GREEN -> YELLOW -> RED
    public void changeState() {
        switch (currentState) {
            case RED:
                currentState = TrafficLightState.GREEN;
                break;
            case GREEN:
                currentState = TrafficLightState.YELLOW;
                break;
            case YELLOW:
                currentState = TrafficLightState.RED;
                break;
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
        this.currentState = state;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }
}
