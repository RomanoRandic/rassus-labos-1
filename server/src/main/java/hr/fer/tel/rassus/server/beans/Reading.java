package hr.fer.tel.rassus.server.beans;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

@Entity
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Reading {
  @Id
  @GeneratedValue
  private Long id;
  private Double temperature;
  private Double pressure;
  private Double humidity;
  private Double co;
  private Double no2;
  private Double so2;

  @ManyToOne
  @JsonIgnore
  private Sensor sensor;

  public Reading() {
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Double getTemperature() {
    return temperature;
  }

  public void setTemperature(Double temperature) {
    this.temperature = temperature;
  }

  public Double getPressure() {
    return pressure;
  }

  public void setPressure(Double pressure) {
    this.pressure = pressure;
  }

  public Double getHumidity() {
    return humidity;
  }

  public void setHumidity(Double humidity) {
    this.humidity = humidity;
  }

  public Double getCo() {
    return co;
  }

  public void setCo(Double co) {
    this.co = co;
  }

  public Double getNo2() {
    return no2;
  }

  public void setNo2(Double no2) {
    this.no2 = no2;
  }

  public Double getSo2() {
    return so2;
  }

  public void setSo2(Double so2) {
    this.so2 = so2;
  }

  public Sensor getSensor() {
    return sensor;
  }

  public void setSensor(Sensor sensor) {
    this.sensor = sensor;
  }
}