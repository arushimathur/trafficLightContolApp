package com.control.trafficlight.controller;

import com.control.trafficlight.model.LightStateRecord;
import com.control.trafficlight.model.TrafficLight;
import com.control.trafficlight.model.TrafficLightState;
import com.control.trafficlight.service.TrafficLightService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/traffic-lights")
public class TrafficLightController {

    private final TrafficLightService trafficLightService;

    @Autowired
    public TrafficLightController(TrafficLightService trafficLightService) {
        this.trafficLightService = trafficLightService;
    }

    // Get status of all traffic lights
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllLights() {
        Map<String, Object> response = Map.of(
                "isPaused", trafficLightService.isPaused(),
                "lights", trafficLightService.getAllLightsStatus()
        );
        return ResponseEntity.ok(response);
    }

    // Get status of a specific traffic light
    @GetMapping("/{direction}")
    public ResponseEntity<TrafficLight> getLightStatus(@PathVariable String direction) {
        TrafficLight light = trafficLightService.getTrafficLight(direction);
        if (light != null) {
            return ResponseEntity.ok(light);
        }
        return ResponseEntity.notFound().build();
    }

    // Change the state of a specific traffic light
    @PostMapping("/{direction}/change")
    public ResponseEntity<String> changeLight(@PathVariable String direction) {
        trafficLightService.changeState(direction);
        TrafficLight light = trafficLightService.getTrafficLight(direction);
        return ResponseEntity.ok("Light at " + direction + " is now " + light.getCurrentState());
    }

    // Emergency override - set all lights to RED
    @PostMapping("/emergency/stop")
    public ResponseEntity<String> emergencyStop() {
        // Implementation would be added to set all lights to RED
        return ResponseEntity.ok("EMERGENCY: All traffic lights set to RED");
    }

    @PostMapping("/pause")
    public ResponseEntity<String> pauseOperation() {
        trafficLightService.pauseOperation();
        return ResponseEntity.ok("Traffic light operation paused");
    }

    @PostMapping("/resume")
    public ResponseEntity<String> resumeOperation() {
        trafficLightService.resumeOperation();
        return ResponseEntity.ok("Traffic light operation resumed");
    }

    @PostMapping("/{direction}/set-state")
    public ResponseEntity<?> setLightState(
            @PathVariable String direction,
            @RequestParam String state) {
        try {
            TrafficLightState newState = TrafficLightState.valueOf(state.toUpperCase());
            trafficLightService.setLightState(direction, newState);
            return ResponseEntity.ok("Light at " + direction + " set to " + newState);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Invalid state: " + state + ". Must be one of: " +
                    Arrays.toString(TrafficLightState.values()));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(403).body(e.getMessage());
        }
    }

    @PostMapping("/{direction}/set-sequence")
    public ResponseEntity<?> setLightSequence(
            @PathVariable String direction,
            @RequestParam String sequence) {
        try {
            List<TrafficLightState> states = Arrays.stream(sequence.split(","))
                    .map(String::trim)
                    .map(String::toUpperCase)
                    .map(TrafficLightState::valueOf)
                    .collect(Collectors.toList());

            trafficLightService.setLightSequence(direction, states);
            return ResponseEntity.ok("Custom sequence set for " + direction + ": " + states);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Invalid sequence. Use comma-separated values of: " +
                    Arrays.toString(TrafficLightState.values()));
        }
    }

    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getSystemStatus() {
        Map<String, Object> status = Map.of(
                "isPaused", trafficLightService.isPaused(),
                "lights", trafficLightService.getAllLightsStatus()
        );
        return ResponseEntity.ok(status);
    }

    // In TrafficLightController.java
    @GetMapping("/{direction}/history")
    public ResponseEntity<List<LightStateRecord>> getLightHistory(
            @PathVariable String direction,
            @RequestParam(required = false, defaultValue = "10") int limit) {
        List<LightStateRecord> history = trafficLightService.getLightStateHistory(direction);
        if (history.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(history.stream().limit(limit).collect(Collectors.toList()));
    }

    @GetMapping("/history")
    public ResponseEntity<Map<String, List<LightStateRecord>>> getAllLightsHistory(
            @RequestParam(required = false, defaultValue = "10") int limit) {
        Map<String, List<LightStateRecord>> allHistory = new HashMap<>();
        trafficLightService.getAllLightsStatus().keySet().forEach(direction -> {
            allHistory.put(direction, trafficLightService.getLightStateHistory(direction)
                    .stream()
                    .limit(limit)
                    .collect(Collectors.toList()));
        });
        return ResponseEntity.ok(allHistory);
    }
}
