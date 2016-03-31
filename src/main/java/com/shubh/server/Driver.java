package com.shubh.server;

import java.io.IOException;

public class Driver {
  public static void main(String[] args) throws IOException {
    Server server = new Server(44444);
    server.start();
  }
}
