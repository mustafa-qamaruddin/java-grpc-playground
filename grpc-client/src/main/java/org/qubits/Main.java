package org.qubits;

public class Main {
  public static void main(String[] args) {
    TodoClient todoClient = new TodoClient();
    int limit = 100;
    while (0<limit--) {
      todoClient.createTodo();
    }
  }
}