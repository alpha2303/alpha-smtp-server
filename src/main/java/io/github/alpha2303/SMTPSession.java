package io.github.alpha2303;

import java.net.*;
import java.io.*;

public class SMTPSession implements Runnable {
  private final Socket socket;
  private String sender;
  private String recipient;
  private StringBuilder body;

  public SMTPSession(Socket socket) {
    this.socket = socket;
  }

  @Override
  public void run() {
    try (
        BufferedReader inBuffer = new BufferedReader(
            new InputStreamReader(socket.getInputStream()));
        PrintWriter outWriter = new PrintWriter(
            new OutputStreamWriter(socket.getOutputStream()), true);) {

      System.out.println("Starting New SMTP session with client: " + socket.getRemoteSocketAddress());
      outWriter.println("220 localhost SMTP Ready");

      String line;
      boolean isEHLO = false;

      while ((line = inBuffer.readLine()) != null) {
        if (line.startsWith("HELO")) {
          String[] msg_parts = line.split(" ");
          if (msg_parts.length != 2) {
            System.out.println("Client sent invalid HELO command: " + line);
            outWriter.println("501 syntax error in parameters: domain not recognized");
            continue;
          }
          String domain = msg_parts[1];
          System.out.println("Client sent HELO command with domain: " + domain);
          outWriter.println("250 Hello " + domain);
        } else if (line.startsWith("EHLO")) {
          String[] msg_parts = line.split(" ");
          if (msg_parts.length != 2) {
            System.out.println("Client sent invalid EHLO command: " + line);
            outWriter.println("501 syntax error in parameters: domain not recognized");
            continue;
          }
          String domain = msg_parts[1];
          System.out.println("Client sent EHLO command with domain: " + domain);
          isEHLO = true;
          outWriter.println("250-localhost Hello " + domain);
          // outWriter.println("250-SIZE 35882577");
          // outWriter.println("250-8BITMIME");
          // outWriter.println("250-PIPELINING");
          // outWriter.println("250 HELP");
        } else if (line.startsWith("MAIL")) {
          System.out.println("Client sent MAIL command: " + line);
          this.clearState();

          String[] msg_parts = line.split(":");
          if (msg_parts.length != 2 || !msg_parts[0].trim().equalsIgnoreCase("MAIL FROM")) {
            System.out.println("Client sent invalid MAIL command: " + line);
            outWriter.println("501 syntax error in parameters: MAIL FROM should be followed by sender email");
            continue;
          }
          sender = msg_parts[1].trim();
          System.out.println("MAIL FROM set to: " + sender);
          outWriter.println("250 OK");
        } else if (line.startsWith("RCPT")) {
          System.out.println("Client sent RCPT command: " + line);
          String[] msg_parts = line.split(":");
          if (msg_parts.length != 2 || !msg_parts[0].trim().equalsIgnoreCase("RCPT TO")) {
            System.out.println("Client sent invalid RCPT command: " + line);
            outWriter.println("501 syntax error in parameters: RCPT TO should be followed by recipient email");
            continue;
          }
          recipient = msg_parts[1].trim();
          System.out.println("RCPT TO set to: " + recipient);
          outWriter.println("250 OK");
        } else if (line.equalsIgnoreCase("DATA")) {
          System.out.println("Client sent DATA command");
          if (sender == null || recipient == null) {
            System.out.println("DATA command received before MAIL FROM and RCPT TO commands");
            outWriter.println(
                "503 Bad sequence of commands: DATA command must be preceded by MAIL FROM and RCPT TO commands");
            continue;
          }
          outWriter.println("354 Start mail input; end with <CRLF>.<CRLF>");
          body = new StringBuilder();
          while ((line = inBuffer.readLine()) != null) {
            if (line.equals(".")) {
              break;
            }
            body.append(line).append("\n");
          }
          System.out.println("Received email body:\n" + body.toString());
          outWriter.println("250 OK: Message accepted for delivery");
        } else if (line.equalsIgnoreCase("RSET") && isEHLO) {
          System.out.println("Client sent RSET command");
          this.clearState();
          outWriter.println("250 OK: Session reset");
        } else if (line.equalsIgnoreCase("QUIT")) {
          System.out.println("Client sent QUIT command");
          outWriter.println("221 localhost Service closing transmission channel");
          break;
        } else {
          System.out.println("Client sent unrecognized command: " + line);
          outWriter.println("500 command not recognized");
        }
      }
    } catch (IOException e) {
      System.err.println("Session error occurred: " + e.getMessage());
    }
  }

  private void clearState() {
    System.out.println("Clearing session state...");
    sender = null;
    recipient = null;
    body = null;
    System.out.println("Session state cleared.");
  }
}
