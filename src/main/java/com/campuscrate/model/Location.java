package com.campuscrate.model;

public class Location {

    private Long locationId;
    private String name;

    public Location() {
    }

    public Location(Long locationId, String name) {
        this.locationId = locationId;
        this.name = name;
    }

    public Long getLocationId() {
        return locationId;
    }

    public void setLocationId(Long locationId) {
        this.locationId = locationId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}