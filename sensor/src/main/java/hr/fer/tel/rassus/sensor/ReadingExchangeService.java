package hr.fer.tel.rassus.sensor;

import hr.fer.tel.rassus.sensor.grpc.ReadingExchangeGrpc;
import hr.fer.tel.rassus.sensor.grpc.ReadingRequest;
import hr.fer.tel.rassus.sensor.grpc.ReadingResponse;
import io.grpc.stub.StreamObserver;
import java.util.logging.Logger;

public class ReadingExchangeService extends ReadingExchangeGrpc.ReadingExchangeImplBase {

  private static final Logger logger = Logger.getLogger(ReadingExchangeService.class.getName());

  private final ReadingGenerator generator;

  public ReadingExchangeService(ReadingGenerator generator) {
    this.generator = generator;
  }

  @Override
  public void requestReading(ReadingRequest request, StreamObserver<ReadingResponse> responseObserver) {
    logger.info("Sensor " + request.getSensorId() + " asked for my reading");

    Reading reading = generator.generate();
    ReadingResponse.Builder builder = ReadingResponse.newBuilder();
    if (reading.temperature() != null) builder.setTemperature(reading.temperature());
    if (reading.pressure() != null) builder.setPressure(reading.pressure());
    if (reading.humidity() != null) builder.setHumidity(reading.humidity());
    if (reading.co() != null) builder.setCo(reading.co());
    if (reading.no2() != null) builder.setNo2(reading.no2());
    if (reading.so2() != null) builder.setSo2(reading.so2());

    responseObserver.onNext(builder.build());
    responseObserver.onCompleted();
  }
}
