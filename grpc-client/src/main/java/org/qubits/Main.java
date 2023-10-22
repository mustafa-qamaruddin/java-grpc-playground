package org.qubits;

import io.grpc.Channel;

public class Main {
  public static void main(String[] args) {
    Channel channel = ChannelFactory.create(ChannelTypeEnum.ALTS);
    TodoClient todoClient = new TodoClient(channel);
    int limit = 100;
    while (0<limit--) {
      todoClient.createTodo();
    }
  }
}