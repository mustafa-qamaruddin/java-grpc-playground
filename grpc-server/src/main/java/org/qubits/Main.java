package org.qubits;

import io.grpc.Server;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;

public class Main {
  public static void main(String[] args) {
    System.out.println("Starting grpc server");

    Server server = ServerFactory.create(
        ServerType.PLAIN,
        Collections.singletonList(new TodoService()),
        Arrays.asList(
            new AuthenticationInterceptor(),
            new CompressionInterceptor()
        )
    );

    try {
      server.start();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    System.out.println("Server Started at " + server.getPort());

    try {
      server.awaitTermination();
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
  }
}