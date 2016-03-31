package com.shubh.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import com.shubh.server.api.Handler;
import com.shubh.server.http.InvalidRequestException;
import com.shubh.server.http.Request;
import com.shubh.server.http.Response;
import com.shubh.server.http.UnsupportedRequestException;

public class Server {
  private final int port;


  public Server(final int port) {
    this.port = port;
  }

  public void start() throws IOException {
    try (ServerSocket serverSocket = new ServerSocket(port)){
      for (; ;) {
        Socket clientSocket = serverSocket.accept();
        try {
          Request.Parser parser = new Request.Parser();
          Request request = parser.parse(clientSocket.getInputStream());
          clientSocket.shutdownInput();

          Handler handler = new FileHandler();
          Response response = handler.handle(request);
          response.write(clientSocket.getOutputStream());

          clientSocket.shutdownOutput();
        } catch (InvalidRequestException e) {
          try {
            Response.badRequest().write(clientSocket.getOutputStream());
          } catch (IOException ie) {
            e.printStackTrace();
          }
        } catch (UnsupportedRequestException e) {
          try {
            Response.notSupported().write(clientSocket.getOutputStream());
          } catch (IOException ie) {
            e.printStackTrace();
          }
        } catch (Exception e) {
          e.printStackTrace();
        } finally {
          clientSocket.close();
        }

      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
