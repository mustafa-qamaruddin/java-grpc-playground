package org.qubits;

import io.grpc.Channel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.alts.AltsChannelBuilder;

public class ChannelFactory {
  public static Channel create(ChannelTypeEnum channelTypeEnum) {
    if (channelTypeEnum.equals(ChannelTypeEnum.PLAIN)) {
      return ManagedChannelBuilder
          .forAddress("localhost", 9089)
          .usePlaintext()
          .build();
    } else if (channelTypeEnum.equals(ChannelTypeEnum.ALTS)) {
      return AltsChannelBuilder
          .forTarget("localhost")
          .enableUntrustedAltsForTesting()
          .build();
    } else {
      throw new IllegalArgumentException("Channel type is not supported");
    }
  }
}
