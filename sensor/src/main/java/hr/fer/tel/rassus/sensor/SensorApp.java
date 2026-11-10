package hr.fer.tel.rassus.sensor;

import hr.fer.tel.rassus.sensor.grpc.ReadingResponse;
import java.io.IOException;
import java.util.logging.Logger;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

public class SensorApp {

  private static final Logger logger = Logger.getLogger(SensorApp.class.getName());

  private static final String SERVER_URL = "http://localhost:8090";
  private static final long PERIOD_MILLIS = 2000;

  public static void main(String[] args) throws IOException, InterruptedException {
    int grpcPort = Integer.parseInt(args[0]);

    ReadingGenerator generator = new ReadingGenerator("readings.csv");

    double latitude = 45.75 + Math.random() * (45.85 - 45.75);
    double longitude = 15.87 + Math.random() * (16 - 15.87);

    GrpcServer grpcServer = new GrpcServer(new ReadingExchangeService(generator), grpcPort);
    grpcServer.start();

    SensorApi api = new Retrofit.Builder()
        .baseUrl(SERVER_URL)
        .addConverterFactory(JacksonConverterFactory.create())
        .build()
        .create(SensorApi.class);

    long id = register(api, new SensorInfo(latitude, longitude, "127.0.0.1", grpcPort));
    logger.info("Registered with id " + id + " at " + latitude + ", " + longitude);

    GrpcClient neighbour = null;
    while (true) {
      Reading own = generator.generate();
      logger.info("Own reading: " + own);

      if (neighbour == null) {
        neighbour = findNeighbour(api, id);
      }

      ReadingResponse response = null;
      if (neighbour != null) {
        response = neighbour.requestReading(id);
      }

      Reading toSave = (response == null) ? own : Calibrator.calibrate(own, response);
      save(api, id, toSave);

      Thread.sleep(PERIOD_MILLIS);
    }
  }

  private static long register(SensorApi api, SensorInfo sensor) {
    try {
      Response<Void> response = api.register(sensor).execute();
      if (!response.isSuccessful()) {
        logger.severe("Registration failed with HTTP " + response.code() + ", stopping");
        System.exit(1);
      }
      String location = response.headers().get("Location");
      return Long.parseLong(location.substring(location.lastIndexOf('/') + 1));
    } catch (IOException e) {
      logger.severe("Server is not reachable, stopping: " + e.getMessage());
      System.exit(1);
      return -1;
    }
  }

  private static GrpcClient findNeighbour(SensorApi api, long id) {
    try {
      SensorInfo info = api.getNeighbour(id).execute().body();
      if (info == null) {
        logger.info("No neighbour, readings will not be calibrated");
        return null;
      }
      logger.info("My neighbour is " + info);
      return new GrpcClient(info.ip(), info.port());
    } catch (IOException e) {
      logger.warning("Could not ask the server for a neighbour: " + e.getMessage());
      return null;
    }
  }

  private static void save(SensorApi api, long id, Reading reading) {
    try {
      Response<Void> response = api.saveReading(id, reading).execute();
      logger.info("Saved " + reading + " (HTTP " + response.code() + ")");
    } catch (IOException e) {
      logger.warning("Could not save reading: " + e.getMessage());
    }
  }
}
