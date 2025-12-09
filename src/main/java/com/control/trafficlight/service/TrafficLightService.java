package com.control.trafficlight.service;

import com.control.trafficlight.model.LightStateRecord;
import com.control.trafficlight.model.TrafficLight;
import com.control.trafficlight.model.TrafficLightState;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class TrafficLightService {
    private final Map<String, TrafficLight> trafficLights = new HashMap<>();
    private boolean isPaused = false;
    private final Map<String, List<TrafficLightState>> customSequences = new HashMap<>();
    private static final Map<String, List<String>> CONFLICTING_DIRECTIONS = Map.of(
            "NORTH", List.of("SOUTH"),
            "SOUTH", List.of("NORTH"),
            "EAST", List.of("WEST"),
            "WEST", List.of("EAST")
    );

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

    private void validateNoConflictingGreens(String direction, TrafficLightState newState) {
        if (newState == TrafficLightState.GREEN) {
            List<String> conflictingDirs = CONFLICTING_DIRECTIONS.getOrDefault(direction, List.of());
            for (String conflictDir : conflictingDirs) {
                TrafficLight conflictLight = trafficLights.get(conflictDir);
                if (conflictLight != null && conflictLight.getCurrentState() == TrafficLightState.GREEN) {
                    throw new IllegalStateException(
                            String.format("Cannot set %s to GREEN while %s is GREEN", direction, conflictDir)
                    );
                }
            }
        }
    }

    public void setLightState(String direction, TrafficLightState state) {
        if (isPaused) {
            throw new IllegalStateException("Cannot change state while system is paused");
        }
        direction = direction.toUpperCase();
        validateNoConflictingGreens(direction, state);

        TrafficLight light = trafficLights.get(direction);
        if (light != null) {
            light.setCurrentState(state);
        }
    }


    public void changeState(String direction) {
        TrafficLight light = trafficLights.get(direction.toUpperCase());
        if (light != null) {
            TrafficLightState currentState = light.getCurrentState();
            light.changeState();
            TrafficLightState newState = light.getCurrentState();

            // If the new state is GREEN, validate no conflicts
            if (newState == TrafficLightState.GREEN) {
                try {
                    validateNoConflictingGreens(direction.toUpperCase(), newState);
                } catch (IllegalStateException e) {
                    // Revert to previous state if conflict detected
                    light.setCurrentState(currentState);
                    throw e;
                }
            }
        }
    }


    public TrafficLight getTrafficLight(String direction) {
        return trafficLights.get(direction.toUpperCase());
    }
    
    public Map<String, Object> getAllLightsStatus() {
        Map<String, Object> status = new HashMap<>();
        trafficLights.forEach((direction, light) ->
                {
                    Map<String, Object> lightStatus = new HashMap<>();
                    lightStatus.put("state", light.getCurrentState());
                    lightStatus.put("durationInCurrentState", light.getCurrentStateDuration());
                    status.put(direction, lightStatus);
                }
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

    public List<LightStateRecord> getLightStateHistory(String direction) {
        TrafficLight light = trafficLights.get(direction.toUpperCase());
        if (light != null) {
            return light.getStateHistory();
        }
        return List.of();
    }


}
