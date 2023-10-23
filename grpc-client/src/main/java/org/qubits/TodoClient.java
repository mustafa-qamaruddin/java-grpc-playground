package org.qubits;

import io.grpc.CallCredentials;
import io.grpc.Channel;
import org.qubits.grpc.todo.CreateTodoRequest;
import org.qubits.grpc.todo.CreateTodoResponse;
import org.qubits.grpc.todo.TodoServiceGrpc;

public class TodoClient {

  TodoServiceGrpc.TodoServiceBlockingStub todoServiceBlockingStub;

  public TodoClient(Channel channel, CallCredentials callCredentials) {
    todoServiceBlockingStub = TodoServiceGrpc
        .newBlockingStub(channel)
        .withCallCredentials(callCredentials);
  }

  public void createTodo() {
    CreateTodoResponse response = null;
    CreateTodoRequest createTodoRequest = null;
    createTodoRequest = CreateTodoRequest.newBuilder()
      .setName("todo")
      .setDescription("vip")
      //.setDueDate(Timestamp.parseFrom("2024-09-01T21:46:43Z".getBytes()))
      .build();
    response = todoServiceBlockingStub.createTodo(createTodoRequest);

    if (response.hasTodo()) {
      System.out.println(response.getTodo().getName());
    }
  }
}
