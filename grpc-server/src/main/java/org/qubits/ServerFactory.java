package org.qubits;

import io.grpc.BindableService;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.ServerInterceptor;
import io.grpc.alts.AltsServerBuilder;

import java.io.File;
import java.util.List;

public class ServerFactory {
  public static Server create(
      ServerType serverType,
      List<BindableService> bindableServices,
      List<ServerInterceptor> serverInterceptors) {
    if (serverType.equals(ServerType.PLAIN)) {
      ServerBuilder<?> serverBuilder = ServerBuilder
          .forPort(9089);
      bindableServices.forEach(serverBuilder::addService);
      serverInterceptors.forEach(serverBuilder::intercept);
      return serverBuilder.build(); // create a instance of server
    } else if (serverType.equals(ServerType.ALTS)) {
      ServerBuilder<AltsServerBuilder> altsServerBuilder = AltsServerBuilder
          .forPort(443)
          .enableUntrustedAltsForTesting();
      bindableServices.forEach(altsServerBuilder::addService);
      return altsServerBuilder.build();
    } else {
      throw new IllegalArgumentException("Server type is not supported");
    }
  }
}
