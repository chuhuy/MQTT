import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.*;
import java.net.UnknownHostException;

public class BasicSubscriber {
  private static int port = 9000;
  private static String defaultIPAddress = "127.0.0.1";
  private final static String[] locations = { "bedroom", "bathroom", "garage", "kitchen", "living-room" };
  private final static String[] devices = { "temperature", "noise", "humidity", "light" };
  public static DataOutputStream oos;
  public static DataInputStream ois;
  public static Socket socket = new Socket();

  public static void main(String args[]) throws Exception {
    boolean connectStatus = connect(defaultIPAddress, port);
    if (!connectStatus) {
      disconnect();
    }
    subscribeTopic("bathroom/noise");

    disconnect();
  }

  public static void subscribeTopic(String topic) {
    try {
      oos.writeUTF("SUBSCRIBE " + topic);

      while (true) {
        String response = (String) ois.readUTF();
        System.out.println(response);
      }
    } catch (Exception e) {
      System.out.println(e);
    }
  }

  public static boolean connect(String ipAddress, int port) {
    try {
      System.out.println("Connecting to broker...");
      socket = new Socket(ipAddress, port);
      oos = new DataOutputStream(socket.getOutputStream());
      ois = new DataInputStream(socket.getInputStream());

      oos.writeUTF("CONNECT");
      String response = (String) ois.readUTF();

      if (response.equals("CONNACK")) {
        System.out.println("Connected!");
        return true;
      } else {
        System.out.println("Unable to connect!");
        return false;
      }
    } catch (Exception e) {
      System.out.println("Unable to connect!");
      return false;
    }
  }

  public static void disconnect() {
    try {
      oos.writeUTF("DISCONNECT");
      ois.close();
      oos.close();

      socket.close();
    } catch (Exception e) {
    }
  }
}
