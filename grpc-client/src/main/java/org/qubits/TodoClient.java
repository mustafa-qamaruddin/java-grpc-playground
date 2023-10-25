package org.qubits;

import com.google.protobuf.Any;
import com.google.protobuf.InvalidProtocolBufferException;
import io.grpc.CallCredentials;
import io.grpc.Channel;
import com.google.rpc.Status;
import io.grpc.protobuf.StatusProto;
import org.qubits.grpc.error.ErrorInfo;
import org.qubits.grpc.todo.CreateTodoRequest;
import org.qubits.grpc.todo.CreateTodoResponse;
import org.qubits.grpc.todo.ReadTodoRequest;
import org.qubits.grpc.todo.ReadTodoResponse;
import org.qubits.grpc.todo.Todo;
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

  public Todo getTodo(long id) {

    ReadTodoResponse response = null;

    try {
      response = todoServiceBlockingStub.readTodo(ReadTodoRequest.newBuilder()
          .setId(id)
          .build());
    } catch (Exception e) {
      // This is com.google.rpc.Status, not io.grpc.Status
      Status status = StatusProto.fromThrowable(e);

      if (status == null) {
        System.out.println("status null, " + e.getMessage());
      }

      if (status != null) {
        for (Any any : status.getDetailsList()) {
          if (any.is(ErrorInfo.class)) {
            try {
              ErrorInfo errorInfo = any.unpack(ErrorInfo.class);
              System.out.printf(
                  "ErrorInfo: %s, %s, %s, %s%n",
                  errorInfo.getTitle(),
                  errorInfo.getDescription(),
                  errorInfo.getTimestamp(),
                  errorInfo.getMetadataMap()
              );
            } catch (InvalidProtocolBufferException ex) {
              throw new RuntimeException(ex);
            }
          }
        }

        System.out.printf(
            "Status: %s, %s%n",
            io.grpc.Status.fromCodeValue(status.getCode()).getCode(),
            status.getMessage()
        );
      }
    }

    if (response != null && response.hasTodo()) {
      return response.getTodo();
    }

    return null;
  }
}
