package org.qubits;

import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.IOException;

public class Main {
  public static void main(String[] args) {
    System.out.println("Starting grpc server");

    Server server = ServerBuilder
      .forPort(9089)
      .addService(new TodoService())
      .build(); // create a instance of server

    try {
      server.start();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    System.out.println("Server Started at "+ server.getPort());

    try {
      server.awaitTermination();
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
  }
}