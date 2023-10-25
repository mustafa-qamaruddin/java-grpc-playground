package org.qubits;

import io.grpc.CallCredentials;
import io.grpc.Channel;

public class Main {
  public static void main(String[] args) {
    Channel channel = ChannelFactory.create(ChannelTypeEnum.PLAIN);
    AuthenticationCredentials authenticationCredentials = new AuthenticationCredentials();
    TodoClient todoClient = new TodoClient(channel, authenticationCredentials);
    long limit = 100;
    while (0<limit--) {
      todoClient.getTodo(limit);
      todoClient.createTodo();
    }
  }
}