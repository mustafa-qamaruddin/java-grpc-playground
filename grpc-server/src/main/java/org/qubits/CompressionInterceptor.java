package org.qubits;

import io.grpc.Compressor;
import io.grpc.CompressorRegistry;
import io.grpc.Decompressor;
import io.grpc.DecompressorRegistry;
import io.grpc.Metadata;
import io.grpc.ServerCall;
import io.grpc.ServerCallHandler;
import io.grpc.ServerInterceptor;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class CompressionInterceptor implements ServerInterceptor {
  private static final String COMPRESSION_GZIP = "gzip";
  private static final String COMPRESSION_CUSTOM = "custom";

  public CompressionInterceptor() {
    // Register compressor
    CompressorRegistry.getDefaultInstance().register(new Compressor() {
      @Override
      public String getMessageEncoding() {
        return COMPRESSION_CUSTOM;
      }

      @Override
      public OutputStream compress(OutputStream os) throws IOException {
        return os;
      }
    });

    // Register Decompressor
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
  }

  @Override
  public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(
      ServerCall<ReqT, RespT> call, Metadata headers, ServerCallHandler<ReqT, RespT> next
  ) {
//    call.setCompression(COMPRESSION_GZIP);
    call.setCompression(COMPRESSION_CUSTOM);
    return next.startCall(call, headers);
  }
}
