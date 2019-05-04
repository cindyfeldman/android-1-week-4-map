package com.ucsdextandroid1.snapmap;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
//deserializer intercepts classes
public class ActiveUserLocationsDeserializer implements JsonDeserializer<ActiveLocationsUserResponse> {
    @Override
    public ActiveLocationsUserResponse deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
      ActiveLocationsUserResponse response = new ActiveLocationsUserResponse();
        List<UserLocationData> locations = new ArrayList<>();//list to return location
        for (Map.Entry<String, JsonElement> entry : json.getAsJsonObject().entrySet()) {
            JsonObject object = entry.getValue().getAsJsonObject();
            UserLocationData locationData =  context
                    .deserialize(entry.getValue(), UserLocationData.class);
            locationData.setUserId(entry.getKey());
            locations.add(locationData);
        }
    response.setUserLocation(locations);//sets user array to response then returns response'
        return response;
    }
}