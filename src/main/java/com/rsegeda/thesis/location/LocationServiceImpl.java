package com.rsegeda.thesis.location;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Created by Roman Segeda on 02/07/2017.
 */
@Service
public class LocationServiceImpl implements LocationService {

    private LocationRepository locationRepository;
    private LocationMapper locationMapper;

    @Autowired
    public LocationServiceImpl(LocationRepository locationRepository, LocationMapper locationMapper) {
        this.locationRepository = locationRepository;
        this.locationMapper = locationMapper;
    }

    @Override
    public List<LocationDto> getLocationList() {

        List<Location> locations = locationRepository.findAll();
        return locationMapper.toDtos(locations);
    }

    @Override
    public Optional<LocationDto> getLocation(Long id) {
        Optional<Location> locationDtoOptional = locationRepository.findById(id);

        if (!locationDtoOptional.isPresent()) {
            return Optional.empty();
        }

        Location location = locationDtoOptional.get();
        return Optional.of(locationMapper.toDto(location));
    }

    @Override
    public Optional<LocationDto> getLocationByPlaceId(String placeId) {
        Optional<Location> locationDtoOptional = locationRepository.findByPlaceId(placeId);
        return locationDtoOptional.map(location -> locationMapper.toDto(location));
    }

    @Override
    public Optional<LocationDto> saveLocation(LocationDto locationDto) {
        Optional<Location> location = locationRepository.save(locationMapper.toDomain(locationDto));

        if (location.isPresent()) {
            return Optional.of(locationMapper.toDto(location.get()));
        }

        return Optional.empty();
    }

    @Override
    public void deleteLocation(Long id) {
        locationRepository.deleteById(id);
    }
}
