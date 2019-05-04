package com.ucsdextandroid1.snapmap;

import java.util.List;

public class ActiveLocationsUserResponse {
    private List<UserLocationData> userLocation;

    public List<UserLocationData> getUserLocations() {
        return userLocation;
    }

    public void setUserLocation(List<UserLocationData> userLocation) {
        this.userLocation = userLocation;
    }


}
