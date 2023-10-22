package org.qubits;

import io.grpc.BindableService;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.alts.AltsServerBuilder;
import io.grpc.netty.shaded.io.grpc.netty.NettyServerBuilder;

import java.util.List;

public class ServerFactory {
  public static Server create(ServerType serverType, List<BindableService> bindableServices) {
    if (serverType.equals(ServerType.PLAIN)) {
      ServerBuilder<?> serverBuilder = ServerBuilder
        .forPort(9089);
      bindableServices.forEach(s -> serverBuilder.addService(s));
      return serverBuilder.build(); // create a instance of server
    } else if (serverType.equals(ServerType.ALTS)) {
      ServerBuilder<AltsServerBuilder> altsServerBuilder = AltsServerBuilder
        .forPort(443)
        .enableUntrustedAltsForTesting();
      bindableServices.forEach(s -> altsServerBuilder.addService(s));
      return altsServerBuilder.build();
    } else {
      throw new IllegalArgumentException("Server type is not supported");
    }
  }
}
