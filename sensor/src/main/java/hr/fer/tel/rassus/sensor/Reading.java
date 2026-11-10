package hr.fer.tel.rassus.sensor;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record Reading(Double temperature, Double pressure, Double humidity,
                      Double co, Double no2, Double so2) {
}
