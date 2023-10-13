//package com.vako.application.location.service;
//
//import com.vako.application.location.model.Location;
//import com.vako.application.location.repository.LocationRepository;
//import jakarta.transaction.Transactional;
//import lombok.AllArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//
//@Service
//@AllArgsConstructor
//@Slf4j
//public class LocationService {
//
//    private final LocationRepository locationRepository;
//
//    public List<Location> getAllLocations() {
//        return locationRepository.findAll();
//    }
//
//    @Transactional
//    public void storeLocation(final Location location) {
//        if (locationRepository.existsByUserId(location.getUserId()))
//            locationRepository.updateUserLocation(location.getLongitude(), location.getLatitude(), location.getUserId());
//        else locationRepository.save(location);
//    }
//}
