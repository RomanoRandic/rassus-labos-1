package hr.fer.tel.rassus.sensor;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class GrpcServer {

  private static final Logger logger = Logger.getLogger(GrpcServer.class.getName());

  private Server server;
  private final ReadingExchangeService service;
  private final int port;

  public GrpcServer(ReadingExchangeService service, int port) {
    this.service = service;
    this.port = port;
  }

  public void start() throws IOException {
    server = ServerBuilder.forPort(port)
        .addService(service)
        .build()
        .start();
    logger.info("gRPC server started on " + port);

    Runtime.getRuntime().addShutdownHook(new Thread(() -> {
      System.err.println("Shutting down gRPC server since JVM is shutting down");
      try {
        GrpcServer.this.stop();
      } catch (InterruptedException e) {
        e.printStackTrace(System.err);
      }
      System.err.println("Server shut down");
    }));
  }

  public void stop() throws InterruptedException {
    if (server != null) {
      server.shutdown().awaitTermination(30, TimeUnit.SECONDS);
    }
  }
}
