package io.github.alpha2303;

import java.net.*;

public class SMTPServer {
  private final int port;

  public SMTPServer(int port) {
    this.port = port;
  }

  public void start() throws Exception {
    try (ServerSocket serverSocker = new ServerSocket(port)) {
      System.out.println("SMTP listening on port: " + port);
      while (true) {
        Socket clientSocket = serverSocker.accept();
        new Thread(new SMTPSession(clientSocket)).start();
      }
    }
  }
}
