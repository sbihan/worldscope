package com.worldscope.worldscope.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CountryResponse {

    @JsonProperty("data")
    private DataWrapper data;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class DataWrapper {
        @JsonProperty("objects")
        private List<Country> objects;
    }
}