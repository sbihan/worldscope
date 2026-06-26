package com.worldscope.worldscope.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Country {

    @JsonProperty("names")
    private Map<String, Object> names;

    @JsonProperty("flag")
    private Map<String, Object> flag;

    @JsonProperty("capitals")
    private List<Map<String, Object>> capitals;

    @JsonProperty("population")
    private long population;

    @JsonProperty("region")
    private String region;

    public String getCommonName() {
        if (names != null && names.containsKey("common")) {
            return (String) names.get("common");
        }
        return "Inconnu";
    }

    public String getFlagUrl() {
        if (flag != null && flag.containsKey("url_png")) {
            return (String) flag.get("url_png");
        }
        return "";
    }

    public String getCapitalName() {
        if (capitals != null && !capitals.isEmpty()) {
            Object name = capitals.get(0).get("name");
            return name != null ? (String) name : "N/A";
        }
        return "N/A";
    }
}