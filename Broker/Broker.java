import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.ClassNotFoundException;
import java.net.*;
import java.io.File;
import java.io.FileInputStream;

public enum MessageType {
  CONNECT,
  PUBLISH,
  SUBSCRIBE
}

class HandlePublisher extends Thread {
  Socket serverClient;
  int clientId;

  HandlePublisher(Socket inputServerClient, int inputClientId) {
    serverClient = inputServerClient;
    clientId = inputClientId;
  }

  public void run() {
    try {
      ObjectInputStream ois = new ObjectInputStream(serverClient.getInputStream());
      ObjectOutputStream oos = new ObjectOutputStream(serverClient.getOutputStream());

      while(true) {
        

      }
    } catch (Exception e) {
      System.out.println(e);
    }
  }
}

class HandleSubscriber extends Thread {
  Socket serverClient;
  int clientId;

  HandleSubscriber(Socket inputServerClient, int inputClientId) {
    serverClient = inputServerClient;
    clientId = inputClientId;
  }

  public void run() {
    
  }
}

public class Broker {
  private static ServerSocket server;
  private static int port = 8883;
  private static String ipAddress;

  public static void main(String args[]) throws IOException, ClassNotFoundException {
    try {
      InetAddress iAddress = InetAddress.getLocalHost();
      ipAddress = iAddress.getHostAddress();
      System.out.println("Server IP Address: " + ipAddress);
    } catch (Exception e) {
      System.out.println(e);
    }

    try {
      server = new ServerSocket(ipAddress, port);

      while(true) {
        counter++;
        Socket serverClient = server.accept();

        HandlePublisher thread = new HandlePublisher(serverClient, counter);
        thread.start();
      }
    } catch (Exception e) {
      System.out.println(e);
    }
  }
}