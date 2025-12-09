package com.control.trafficlight.service;

import com.control.trafficlight.model.TrafficLight;
import com.control.trafficlight.model.TrafficLightState;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class TrafficLightService {
    private final Map<String, TrafficLight> trafficLights = new HashMap<>();
    private boolean isPaused = false;
    private final Map<String, List<TrafficLightState>> customSequences = new HashMap<>();
    
    public TrafficLightService() {
        // Initialize traffic lights for each direction
        initializeTrafficLights();
    }
    
    private void initializeTrafficLights() {
        // Default duration for each state (in seconds)
        int redDuration = 30;
        int greenDuration = 25;
        int yellowDuration = 5;
        
        // Create traffic lights for each direction
        trafficLights.put("NORTH", new TrafficLight("NORTH", TrafficLightState.RED, redDuration));
        trafficLights.put("SOUTH", new TrafficLight("SOUTH", TrafficLightState.RED, redDuration));
        trafficLights.put("EAST", new TrafficLight("EAST", TrafficLightState.GREEN, greenDuration));
        trafficLights.put("WEST", new TrafficLight("WEST", TrafficLightState.GREEN, greenDuration));
    }
    
    public void changeState(String direction) {
        TrafficLight light = trafficLights.get(direction.toUpperCase());
        if (light != null) {
            light.changeState();
        }
    }
    
    public TrafficLight getTrafficLight(String direction) {
        return trafficLights.get(direction.toUpperCase());
    }
    
    public Map<String, TrafficLightState> getAllLightsStatus() {
        Map<String, TrafficLightState> status = new HashMap<>();
        trafficLights.forEach((direction, light) -> 
            status.put(direction, light.getCurrentState())
        );
        return status;
    }

    // Pause all light changes
    public void pauseOperation() {
        this.isPaused = true;
    }

    // Resume normal operation
    public void resumeOperation() {
        this.isPaused = false;
    }

    // Check if system is paused
    public boolean isPaused() {
        return isPaused;
    }

    // Set a custom sequence for a specific light
    public void setLightSequence(String direction, List<TrafficLightState> sequence) {
        TrafficLight light = trafficLights.get(direction.toUpperCase());
        if (light != null) {
            light.setSequence(sequence);
            // Update the light's current state to match the new sequence
            if (!sequence.contains(light.getCurrentState())) {
                light.setCurrentState(sequence.get(0));
            }
        }
    }
    // Get the next state in sequence for a light
    public TrafficLightState getNextState(String direction) {
        List<TrafficLightState> sequence = customSequences.getOrDefault(
            direction.toUpperCase(), 
            Arrays.asList(TrafficLightState.RED, TrafficLightState.GREEN, TrafficLightState.YELLOW)
        );
        
        TrafficLight light = trafficLights.get(direction.toUpperCase());
        if (light == null) {
            throw new IllegalArgumentException("Invalid direction: " + direction);
        }
        
        int currentIndex = sequence.indexOf(light.getCurrentState());
        int nextIndex = (currentIndex + 1) % sequence.size();
        return sequence.get(nextIndex);
    }

    // Manually set a light to a specific state
    public void setLightState(String direction, TrafficLightState state) {
        if (isPaused) {
            throw new IllegalStateException("Cannot change state while system is paused");
        }
        TrafficLight light = trafficLights.get(direction.toUpperCase());
        if (light != null) {
            light.setCurrentState(state);
        }
    }
}
