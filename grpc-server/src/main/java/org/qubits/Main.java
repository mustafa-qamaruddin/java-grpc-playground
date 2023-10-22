package org.qubits;

import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.IOException;
import java.util.List;

public class Main {
  public static void main(String[] args) {
    System.out.println("Starting grpc server");

    Server server = ServerFactory.create(
      ServerType.ALTS,
      List.of(new TodoService())
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