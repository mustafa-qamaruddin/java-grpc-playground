package org.qubits;

import com.google.protobuf.Timestamp;
import io.grpc.Channel;
import io.grpc.inprocess.InProcessChannelBuilder;
import io.grpc.inprocess.InProcessServerBuilder;
import io.grpc.testing.GrpcCleanupRule;
import org.junit.Rule;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.qubits.grpc.todo.Todo;
import org.qubits.grpc.todo.TodoServiceGrpc;

import java.io.IOException;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;

class TodoClientTest {

  /**
   * This rule manages automatic graceful shutdown for the registered servers and channels at the
   * end of test.
   */
  @Rule
  public final GrpcCleanupRule grpcCleanup = new GrpcCleanupRule();

  TodoClient client;

  @BeforeEach
  void setUp() {
    // generate name
    String serverName = InProcessServerBuilder.generateName();

    // Mock server
    TodoServiceGrpc.TodoServiceImplBase serviceMock = mock(TodoServiceGrpc.TodoServiceImplBase.class, delegatesTo);

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
    Timestamp timestamp = Timestamp.newBuilder()
        .setNanos(
            Instant.now().getNano()
        )
        .build();

    // When
    Todo todo = client.getTodo(1);

    // Then
    assertNotNull(todo);
    assertEquals(todo.getName(), name);
    assertEquals(todo.getDescription(), description);
    assertEquals(todo.getDueDate(), timestamp);

    //
    ArgumentCaptor<HelloRequest> requestCaptor = ArgumentCaptor.forClass(HelloRequest.class);

    client.greet("test name");

    verify(serviceImpl)
        .sayHello(requestCaptor.capture(), ArgumentMatchers.<StreamObserver<HelloReply>>any());
    assertEquals("test name", requestCaptor.getValue().getName());
  }
}