package hr.fer.tel.rassus.sensor;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface SensorApi {

  @POST("/sensors")
  Call<Void> register(@Body SensorInfo sensor);

  @GET("/sensors/{id}/neighbour")
  Call<SensorInfo> getNeighbour(@Path("id") long sensorId);

  @POST("/sensors/{id}/readings")
  Call<Void> saveReading(@Path("id") long sensorId, @Body Reading reading);
}
