package hr.fer.tel.rassus.sensor;

import hr.fer.tel.rassus.sensor.grpc.ReadingExchangeGrpc;
import hr.fer.tel.rassus.sensor.grpc.ReadingRequest;
import hr.fer.tel.rassus.sensor.grpc.ReadingResponse;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import java.util.logging.Logger;

public class GrpcClient {

  private static final Logger logger = Logger.getLogger(GrpcClient.class.getName());

  private final ManagedChannel channel;
  private final ReadingExchangeGrpc.ReadingExchangeBlockingStub stub;

  public GrpcClient(String host, int port) {
    this.channel = ManagedChannelBuilder.forAddress(host, port).usePlaintext().build();
    this.stub = ReadingExchangeGrpc.newBlockingStub(channel);
  }

  public ReadingResponse requestReading(long mySensorId) {
    ReadingRequest request = ReadingRequest.newBuilder().setSensorId(mySensorId).build();
    try {
      ReadingResponse response = stub.requestReading(request);
      logger.info("Neighbour reading: " + response.toString().replace("\n", " "));
      return response;
    } catch (StatusRuntimeException e) {
      logger.warning("gRPC connection to neighbour failed: " + e.getMessage());
      return null;
    }
  }
}
