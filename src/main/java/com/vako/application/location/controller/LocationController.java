package com.vako.application.location.controller;

import com.vako.application.location.model.Location;
import com.vako.application.location.service.LocationService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/location")
@AllArgsConstructor
public class LocationController {

    private final LocationService locationService;

    @PostMapping("/store")
    public void storeLocation(@RequestBody final Location location) {
        locationService.storeLocation(location);
    }

    @GetMapping("/all")
    public ResponseEntity<List<Location>> getAllLocations() {
        return ResponseEntity.ok(locationService.getAllLocations());
    }
}
