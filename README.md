# Alpha SMTP Server

A simple SMTP server implementation in Java 25.

## Installation

1. Clone the repository:
   ```bash
   git clone https://github.com/yourusername/alpha-smtp-server.git
   cd alpha-smtp-server
   ```

2. Install dependencies:
   ```bash
   mvn install
   ```

## Running the Server

### Option 1: Run directly with Maven

```bash
mvn compile exec:java
```

The SMTP server will start on port **2525**.

### Option 2: Build and run JAR

```bash
mvn package
java -jar target/alpha-smtp-server-1.0-SNAPSHOT.jar
```

## Testing with the Client

In a separate terminal, run the Python client to send a test email:

```bash
python example-client/smtp_client.py
```

## Default Configuration

- **Port**: 2525
- **Sender**: sender@example.com
- **Recipient**: recipient@example.com

