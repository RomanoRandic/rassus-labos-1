package hr.fer.tel.rassus.sensor;

import hr.fer.tel.rassus.sensor.grpc.ReadingResponse;

public class Calibrator {

  public static Reading calibrate(Reading own, ReadingResponse neighbour) {
    return new Reading(
        average(own.temperature(), neighbour.getTemperature()),
        average(own.pressure(), neighbour.getPressure()),
        average(own.humidity(), neighbour.getHumidity()),
        average(own.co(), neighbour.getCo()),
        average(own.no2(), neighbour.getNo2()),
        average(own.so2(), neighbour.getSo2()));
  }

  private static Double average(Double own, double neighbour) {
    boolean hasOwn = own != null && own != 0;
    boolean hasNeighbour = neighbour != 0;
    if (hasOwn && hasNeighbour) {
      return (own + neighbour) / 2;
    }
    if (hasOwn) {
      return own;
    }
    if (hasNeighbour) {
      return neighbour;
    }
    return null;
  }
}
