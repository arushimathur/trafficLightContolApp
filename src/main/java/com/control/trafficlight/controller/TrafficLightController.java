package com.trafficlight.controller;

import com.control.trafficlight.model.TrafficLight;
import com.control.trafficlight.model.TrafficLightState;
import com.control.trafficlight.service.TrafficLightService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

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
    public ResponseEntity<Map<String, TrafficLightState>> getAllLights() {
        return ResponseEntity.ok(trafficLightService.getAllLightsStatus());
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
}
