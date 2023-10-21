package org.qubits;

import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Timestamp;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.qubits.grpc.todo.CreateTodoRequest;
import org.qubits.grpc.todo.CreateTodoResponse;
import org.qubits.grpc.todo.TodoServiceGrpc;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class TodoClient {

  TodoServiceGrpc.TodoServiceBlockingStub todoServiceBlockingStub;

  public TodoClient() {
    ManagedChannel channel = ManagedChannelBuilder
      .forAddress("localhost", 9089)
      .usePlaintext()
      .build();

    todoServiceBlockingStub = TodoServiceGrpc.newBlockingStub(channel);
  }

  public void createTodo() {
    CreateTodoResponse response = null;
    try {
      response = todoServiceBlockingStub.createTodo(
        CreateTodoRequest.newBuilder()
          .setName("todo")
          .setDescription("vip")
          .setDueDate(Timestamp.parseFrom("2023-12-25".getBytes()))
          .build()
      );
    } catch (InvalidProtocolBufferException e) {
      throw new RuntimeException(e);
    }

    if (response.hasTodo()) {
      System.out.println(response.getTodo().getName());
    }
  }
}
