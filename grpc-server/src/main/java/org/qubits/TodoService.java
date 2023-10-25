package org.qubits;

import com.google.protobuf.Any;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Timestamp;
import com.google.rpc.Status;
import io.grpc.protobuf.StatusProto;
import io.grpc.stub.StreamObserver;

import org.qubits.grpc.error.ErrorInfo;
import org.qubits.grpc.todo.CreateTodoRequest;
import org.qubits.grpc.todo.CreateTodoResponse;
import org.qubits.grpc.todo.ReadTodoRequest;
import org.qubits.grpc.todo.ReadTodoResponse;
import org.qubits.grpc.todo.Todo;
import org.qubits.grpc.todo.TodoServiceGrpc;

import java.sql.Time;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TodoService extends TodoServiceGrpc.TodoServiceImplBase {

  List<Todo> todos;

  public TodoService() {
    super();
    this.todos = new ArrayList<>();
  }

  @Override
  public void createTodo(CreateTodoRequest request, StreamObserver<CreateTodoResponse> responseObserver) {
    // current timestamp
    Timestamp timestamp = Timestamp
        .newBuilder()
        .setNanos(Instant.now().getNano())
        .build();

    // create todo
    Todo todo = Todo.newBuilder()
        .setCreatedDate(timestamp)
        .setUpdatedDate(timestamp)
        .setDueDate(request.getDueDate())
        .setName(request.getName())
        .setDescription(request.getDescription())
        .build();

    todos.add(todo);

    responseObserver.onNext(CreateTodoResponse.newBuilder()
        .setTodo(todo)
        .build());
    responseObserver.onCompleted();
  }

  @Override
  public void readTodo(ReadTodoRequest request, StreamObserver<ReadTodoResponse> responseObserver) {
    long id = request.getId();
    ErrorInfo errorInfo = null;
    try {
      Instant instant = Instant.now();
      Timestamp timestamp = Timestamp.newBuilder()
          .setNanos(instant.getNano())
          .build();
      errorInfo = ErrorInfo.newBuilder()
          // uncomment to force parse error
          .setTimestamp(Timestamp.parseFrom("2023-12-12T00:00:00.000Z".getBytes()))
//        .setTimestamp(timestamp)
          .setTitle("todo is not found")
          .setDescription("this endpoint is a demo of gRPC error handling")
          .putMetadata("id", String.valueOf(id))
          .build();
    } catch (InvalidProtocolBufferException e) {
      // This is com.google.rpc.Status, not io.grpc.Status
      Status status = Status.newBuilder()
          .setCode(io.grpc.Status.INTERNAL.getCode().value())
          .setMessage(e.getMessage())
          .build();
      responseObserver.onError(StatusProto.toStatusRuntimeException(status));
      responseObserver.onCompleted();
      return;
    }

    if (errorInfo == null) {
      // This is com.google.rpc.Status, not io.grpc.Status
      Status status = Status.newBuilder()
          .setCode(io.grpc.Status.INVALID_ARGUMENT.getCode().value())
          .setMessage("lorem ipsum amet dolor")
          .build();
      responseObserver.onError(StatusProto.toStatusRuntimeException(status));
      responseObserver.onCompleted();
      return;
    } else {
      // This is com.google.rpc.Status, not io.grpc.Status
      Status status = Status.newBuilder()
          .setCode(io.grpc.Status.INVALID_ARGUMENT.getCode().value())
          .setMessage("lorem ipsum amet dolor")
          .addDetails(Any.pack(errorInfo))
          .build();
      responseObserver.onError(StatusProto.toStatusRuntimeException(status));
      responseObserver.onCompleted();
      return;
    }
  }
}
