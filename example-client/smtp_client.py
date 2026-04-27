import socket

HOST = "localhost"
PORT = 2525

SENDER = "sender@example.com"
RECIPIENT = "recipient@example.com"
SUBJECT = "Test Email"
BODY = "Hello, this is a test email sent via SMTP."


def send_command(sock, command: str) -> str:
    print(f"Client: {command}")
    sock.sendall((command + "\r\n").encode())
    response = sock.recv(1024).decode()
    print(f"Server: {response}", end="")
    return response


if __name__ == "__main__":
    print(f"Connecting to SMTP server at {HOST}:{PORT}...")
    with socket.create_connection((HOST, PORT)) as sock:
        # Greeting
        greeting = sock.recv(1024).decode()
        print(f"Server: {greeting}", end="")

        # HELO
        send_command(sock, "HELO client")

        # MAIL FROM
        send_command(sock, f"MAIL FROM:{SENDER}")

        # RCPT TO
        send_command(sock, f"RCPT TO:{RECIPIENT}")

        # DATA
        send_command(sock, "DATA")

        # Message headers + body
        message = (
            f"From: {SENDER}\r\n"
            f"To: {RECIPIENT}\r\n"
            f"Subject: {SUBJECT}\r\n"
            f"\r\n"
            f"{BODY}\r\n"
            f".\r\n"
        )
        print(f"Client: (message body)")
        sock.sendall(message.encode())
        print(f"Server: {sock.recv(1024).decode()}", end="")

        # QUIT
        send_command(sock, "QUIT")
