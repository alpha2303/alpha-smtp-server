package io.github.alpha2303;

import java.io.*;

/**
 * Hello world!
 */
public class App {
  public static void main(String[] args) throws IOException {
    int port = 2525;
    SMTPServer server = new SMTPServer(port);
    try {
      server.start();
    } catch (Exception e) {
      System.err.println("Error starting SMTP server: " + e.getMessage());
    }
  }
}
