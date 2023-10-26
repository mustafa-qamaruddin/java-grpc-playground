package org.qubits;

import com.google.protobuf.Timestamp;
import io.grpc.Channel;
import io.grpc.inprocess.InProcessChannelBuilder;
import io.grpc.inprocess.InProcessServerBuilder;
import io.grpc.stub.StreamObserver;
import io.grpc.testing.GrpcCleanupRule;
import org.junit.Rule;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.AdditionalAnswers;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.qubits.grpc.todo.ReadTodoRequest;
import org.qubits.grpc.todo.ReadTodoResponse;
import org.qubits.grpc.todo.Todo;
import org.qubits.grpc.todo.TodoServiceGrpc;

import java.io.IOException;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class TodoClientTest {

  /**
   * This rule manages automatic graceful shutdown for the registered servers and channels at the
   * end of test.
   */
  @Rule
  public final GrpcCleanupRule grpcCleanup = new GrpcCleanupRule();

  TodoClient client;
  private TodoServiceGrpc.TodoServiceImplBase serviceMock;

  @BeforeEach
  void setUp() {
    // generate name
    String serverName = InProcessServerBuilder.generateName();

    // Mock server
    serviceMock = mock(
        TodoServiceGrpc.TodoServiceImplBase.class,
        AdditionalAnswers.delegatesTo(new TodoServiceGrpc.TodoServiceImplBase() {
          @Override
          public void readTodo(ReadTodoRequest request, StreamObserver<ReadTodoResponse> responseObserver) {
            String name = "sut";
            String description = "sut";
            Timestamp timestamp = Timestamp.newBuilder()
                .setNanos(
                    Instant.now().getNano()
                )
                .build();
            responseObserver.onNext(ReadTodoResponse.newBuilder()
                .setTodo(
                    Todo.newBuilder()
                        .setName(name)
                        .setDescription(description)
                        .setDueDate(timestamp)
                        .build())
                .build()
            );
            responseObserver.onCompleted();
          }
        })
    );

    // Register server for shutdown
    try {
      grpcCleanup.register(
          InProcessServerBuilder
              .forName(serverName)
              .addService(serviceMock)
              .directExecutor()
              .build()
              .start()
      );
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    // Register channel for shutdown
    Channel channel = grpcCleanup.register(
        InProcessChannelBuilder
            .forName(serverName)
            .directExecutor()
            .build()
    );

    // SUT
    client = new TodoClient(channel, new AuthenticationCredentials());
  }

  @AfterEach
  void tearDown() {
  }

  @Test
  void getTodo() {
    // Given
    String name = "sut";
    String description = "sut";
    ArgumentCaptor<ReadTodoRequest> requestCaptor = ArgumentCaptor.forClass(ReadTodoRequest.class);

    // When
    Todo todo = client.getTodo(1);
    verify(serviceMock).readTodo(requestCaptor.capture(), ArgumentMatchers.any());

    // Then
    assertNotNull(todo);
    assertEquals(todo.getName(), name);
    assertEquals(todo.getDescription(), description);
    assertNotNull(todo.getDueDate());
    assertEquals(requestCaptor.getValue().getId(), 1);
  }
}