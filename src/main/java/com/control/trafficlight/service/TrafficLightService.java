package com.control.trafficlight.service;

import com.control.trafficlight.model.TrafficLight;
import com.control.trafficlight.model.TrafficLightState;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class TrafficLightService {
    private final Map<String, TrafficLight> trafficLights = new HashMap<>();
    
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
}
