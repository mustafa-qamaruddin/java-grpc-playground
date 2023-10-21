package org.qubits;

import com.google.protobuf.Timestamp;
import io.grpc.stub.StreamObserver;
import org.qubits.grpc.todo.CreateTodoRequest;
import org.qubits.grpc.todo.CreateTodoResponse;
import org.qubits.grpc.todo.Todo;
import org.qubits.grpc.todo.TodoOrBuilder;
import org.qubits.grpc.todo.TodoProto;
import org.qubits.grpc.todo.TodoServiceGrpc;

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
    Todo todo = Todo.newBuilder()
      .setCreatedDate(Timestamp.getDefaultInstance())
      .setUpdatedDate(Timestamp.getDefaultInstance())
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
}
