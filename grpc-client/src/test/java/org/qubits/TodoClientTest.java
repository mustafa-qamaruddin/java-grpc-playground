package org.qubits;

import com.google.protobuf.Timestamp;
import io.grpc.Channel;
import io.grpc.Metadata;
import io.grpc.ServerCall;
import io.grpc.ServerCallHandler;
import io.grpc.ServerInterceptor;
import io.grpc.ServerInterceptors;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.qubits.AuthenticationCredentials.AUTH_HEADER_KEY;

class TodoClientTest {

  /**
   * This rule manages automatic graceful shutdown for the registered servers and channels at the
   * end of test.
   */
  @Rule
  public final GrpcCleanupRule grpcCleanup = new GrpcCleanupRule();

  TodoClient client;
  private TodoServiceGrpc.TodoServiceImplBase serviceMock;
  private ServerInterceptor interceptorMock;

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

    // Mock interceptor
    interceptorMock = mock(
        ServerInterceptor.class,
        AdditionalAnswers.delegatesTo(new ServerInterceptor() {
          @Override
          public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(
              ServerCall<ReqT, RespT> call, Metadata headers, ServerCallHandler<ReqT, RespT> next
          ) {
            return next.startCall(call, headers);
          }
        })
    );

    // Register server for shutdown
    try {
      grpcCleanup.register(
          InProcessServerBuilder
              .forName(serverName)
              .addService(ServerInterceptors.intercept(serviceMock, interceptorMock))
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
    ArgumentCaptor<Metadata> interceptorCaptor = ArgumentCaptor.forClass(Metadata.class);

    // When
    Todo todo = client.getTodo(1);
    verify(serviceMock).readTodo(requestCaptor.capture(), ArgumentMatchers.any());
    verify(interceptorMock).interceptCall(any(), interceptorCaptor.capture(), any());

    // Then
    assertNotNull(todo);
    assertEquals(todo.getName(), name);
    assertEquals(todo.getDescription(), description);
    assertNotNull(todo.getDueDate());
    assertEquals(requestCaptor.getValue().getId(), 1);
    // headers must contain a token
    assertNotNull(interceptorCaptor.getValue().get(AUTH_HEADER_KEY));
  }
}