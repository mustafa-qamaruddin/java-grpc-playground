package org.qubits;

import com.google.protobuf.Timestamp;
import io.grpc.inprocess.InProcessChannelBuilder;
import io.grpc.inprocess.InProcessServerBuilder;
import io.grpc.testing.GrpcCleanupRule;
import org.junit.Rule;
import org.qubits.grpc.todo.CreateTodoRequest;
import org.qubits.grpc.todo.CreateTodoResponse;
import org.qubits.grpc.todo.Todo;
import org.qubits.grpc.todo.TodoServiceGrpc;

import java.io.IOException;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class TodoServiceTest {

  /**
   * This rule manages automatic graceful shutdown for the registered servers and channels at the
   * end of test.
   */
  @Rule
  public final GrpcCleanupRule grpcCleanup = new GrpcCleanupRule();

  TodoServiceGrpc.TodoServiceBlockingStub todoServiceBlockingStub;

  @org.junit.jupiter.api.BeforeEach
  void setUp() {
    String serverName = InProcessServerBuilder.generateName();

    // register server for cleanup rule
    try {
      grpcCleanup.register(
          InProcessServerBuilder
              .forName(serverName)
              .addService(new TodoService())
              .build()
              .start()
      );
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    // register client for cleanup rule
    todoServiceBlockingStub = TodoServiceGrpc.newBlockingStub(
        grpcCleanup.register(
            InProcessChannelBuilder
                .forName(serverName)
                .directExecutor()
                .build()
        )
    );
  }

  @org.junit.jupiter.api.AfterEach
  void tearDown() {
  }

  @org.junit.jupiter.api.Test
  void createTodo() {
    // Given
    Todo todo = Todo.newBuilder()
        .setName("sut")
        .setDescription("sut")
        .setDueDate(
            Timestamp.newBuilder()
                .setNanos(Instant.now().getNano())
                .build()
        )
        .build();

    // When
    CreateTodoResponse createTodoResponse = todoServiceBlockingStub.createTodo(
        CreateTodoRequest.newBuilder()
            .setName(todo.getName())
            .setDescription(todo.getDescription())
            .setDueDate(todo.getDueDate())
            .build()
    );

    // Then
    assertTrue(createTodoResponse.hasTodo());
    assertEquals(createTodoResponse.getTodo().getName(), todo.getName());
    assertEquals(createTodoResponse.getTodo().getDescription(), todo.getDescription());
    assertEquals(createTodoResponse.getTodo().getDueDate(), todo.getDueDate());
    assertNotNull(createTodoResponse.getTodo().getCreatedDate());
    assertNotNull(createTodoResponse.getTodo().getUpdatedDate());
  }
}