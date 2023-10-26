package org.qubits;

import io.grpc.CallCredentials;
import io.grpc.Metadata;

import java.util.concurrent.Executor;

import static io.grpc.Metadata.ASCII_STRING_MARSHALLER;

public class AuthenticationCredentials extends CallCredentials {
  public static final Metadata.Key<String> AUTH_HEADER_KEY = Metadata.Key.of("Authorization", ASCII_STRING_MARSHALLER);

  @Override
  public void applyRequestMetadata(RequestInfo requestInfo, Executor appExecutor, MetadataApplier applier) {
    Metadata metadata = new Metadata();
    metadata.put(AUTH_HEADER_KEY, "super-secret-token");
    applier.apply(metadata);
  }
}
