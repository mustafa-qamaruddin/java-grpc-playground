package org.qubits;

import io.grpc.Context;
import io.grpc.Contexts;
import io.grpc.Metadata;
import io.grpc.ServerCall;
import io.grpc.ServerCallHandler;
import io.grpc.ServerInterceptor;
import io.grpc.Status;

import static io.grpc.Metadata.ASCII_STRING_MARSHALLER;

public class AuthenticationInterceptor implements ServerInterceptor {

  public static final Metadata.Key<String> AUTH_HEADER_KEY = Metadata.Key.of("Authorization", ASCII_STRING_MARSHALLER);
  private final Context.Key<String> SUBJECT_ID_KEY = Context.key("Subject ID");

  @Override
  public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(
      ServerCall<ReqT, RespT> call,
      Metadata headers,
      ServerCallHandler<ReqT, RespT> next
  ) {
    // authenticated

    // extract token
    if (headers.containsKey(AUTH_HEADER_KEY)) {
      String token = headers.get(AUTH_HEADER_KEY);
      // validate token
      System.out.println(token);
      // return
      Context context = Context.current().withValue(SUBJECT_ID_KEY, "abc-xyz-123");
      return Contexts.interceptCall(context, call, headers, next);
    }

    // failed
    call.close(
        Status.UNAUTHENTICATED
            .augmentDescription("additional detail"),
        new Metadata()
    );

    return new ServerCall.Listener<ReqT>() {
      // noop
    };
  }
}
