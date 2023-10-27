package org.qubits;

import com.google.protobuf.Any;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.rpc.Status;
import io.grpc.CallCredentials;
import io.grpc.Channel;
import io.grpc.Compressor;
import io.grpc.CompressorRegistry;
import io.grpc.Decompressor;
import io.grpc.DecompressorRegistry;
import io.grpc.protobuf.StatusProto;
import org.qubits.grpc.error.ErrorInfo;
import org.qubits.grpc.todo.CreateTodoRequest;
import org.qubits.grpc.todo.CreateTodoResponse;
import org.qubits.grpc.todo.ReadTodoRequest;
import org.qubits.grpc.todo.ReadTodoResponse;
import org.qubits.grpc.todo.Todo;
import org.qubits.grpc.todo.TodoServiceGrpc;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class TodoClient {

  private static final String COMPRESSION_GZIP = "gzip";
  private static final String COMPRESSION_CUSTOM = "custom";
  TodoServiceGrpc.TodoServiceBlockingStub todoServiceBlockingStub;

  public TodoClient(Channel channel, CallCredentials callCredentials) {
    // Add new compression algorithm
    CompressorRegistry.getDefaultInstance().register(new Compressor() {
      @Override
      public String getMessageEncoding() {
        return COMPRESSION_CUSTOM;
      }

      @Override
      public OutputStream compress(OutputStream os) throws IOException {
        // dummy compression for testing only
        return os;
      }
    });

    // Add new decompression algorithm
    DecompressorRegistry.getDefaultInstance().with(new Decompressor() {
      @Override
      public String getMessageEncoding() {
        return COMPRESSION_CUSTOM;
      }

      @Override
      public InputStream decompress(InputStream is) throws IOException {
        return is;
      }
    }, true);

    // create stub
    todoServiceBlockingStub = TodoServiceGrpc
        .newBlockingStub(channel)
        .withCallCredentials(callCredentials)
//        .withCompression(COMPRESSION_GZIP)
        .withCompression(COMPRESSION_CUSTOM);
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
