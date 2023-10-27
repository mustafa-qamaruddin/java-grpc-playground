package org.qubits;

import io.grpc.Metadata;
import io.grpc.ServerCall;
import io.grpc.ServerCallHandler;
import io.grpc.ServerInterceptor;

public class CompressionInterceptor implements ServerInterceptor {
  private static final String COMPRESSION_GZIP = "gzip";

  @Override
  public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(
      ServerCall<ReqT, RespT> call, Metadata headers, ServerCallHandler<ReqT, RespT> next
  ) {
    call.setCompression(COMPRESSION_GZIP);
    return next.startCall(call, headers);
  }
}
