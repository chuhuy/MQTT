import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.*;
import java.net.UnknownHostException;

public class Publisher {
  private static int port = 9000;
  private static String defaultIPAddress = "127.0.0.1";
  private final static String[] locations = { "bedroom", "bathroom", "garage", "kitchen", "living room" };
  private final static String[] devices = { "temperature", "noise", "humidity", "light" };
  public static DataOutputStream oos;
  public static DataInputStream ois;
  public static Socket socket = new Socket();
  private static Scanner scanner = new Scanner(System.in);

  public static void main(String args[]) throws Exception {
    scanner = new Scanner(System.in);

    boolean isRunning = true;
    while (isRunning) {
      boolean connectStatus = connect(defaultIPAddress, port);
      if (!connectStatus) {
        disconnect();
        isRunning = promptRetry();
        continue;
      }

      boolean autoMode = isAutoMode();
      if (autoMode)
        autoPublish();
      else {
        oos.writeUTF("Test");
        manualPublish();
      }
        // manualPublish(oos, ois);

      disconnect();
      isRunning = promptRetry();
    }

    scanner.close();
  }

  public static void manualPublish() {
    String selectedLocation = "";
    String selectedDevice = "";
    String topic = "";

    // Print locations and devices
    System.out.print("Please select location and device to publish\nLocations: ");
    for (int index = 0; index < locations.length; index++) {
      System.out.print(index + " - " + locations[index] + " | ");
    }
    System.out.print("\nDevices: ");
    for (int index = 0; index < devices.length; index++) {
      System.out.print(index + " - " + devices[index] + " | ");
    }

    // Select location
    while (selectedLocation.equals("")) {
      System.out.print("\nLocation's number: ");
      String input = scanner.nextLine();

      if (!Util.isInt(input))
        continue;
      int selectedNumber = Integer.parseInt(input);

      if (selectedNumber >= 0 && selectedNumber < locations.length)
        selectedLocation = locations[selectedNumber];
    }
    System.out.println("Selected location: " + selectedLocation);

    // Select device
    while (selectedDevice.equals("")) {
      System.out.print("Device's number: ");
      String input = scanner.nextLine();

      if (!Util.isInt(input))
        continue;
      int selectedNumber = Integer.parseInt(input);

      if (selectedNumber >= 0 && selectedNumber < devices.length)
        selectedDevice = devices[selectedNumber];
    }
    System.out.println("Selected device: " + selectedDevice);

    topic = selectedLocation + "/" + selectedDevice;
    System.out.println("Publishing to topic: " + topic);

    while (true) {
      System.out.print("Input value (@ to stop publish): ");
      String input = scanner.nextLine();

      if (input.equals("@")) {
        break;
      } else if (Util.isNumeric(input)) {
        publish(topic, input, oos);
      } else {
        System.out.println("Invalid value");
      }
    }
  }

  public static void autoPublish() {
    String topic = "";

  }

  public static void publish(String topic, String value, DataOutputStream oos) {
    try {
      System.out.println("Publishing value " + value + " to topic " + topic);
      oos.writeUTF("PUBLISH " + topic + " " + value);
    } catch (Exception e) {
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

  public static boolean isAutoMode() {
    String input = "";

    System.out.print("Publish automatically (yes/no): ");
    input = scanner.nextLine();

    if (input.equals("yes"))
      return true;
    else
      return false;
  }

  public static boolean promptRetry() {
    String input = "";
    System.out.print("Wanna publish again?(yes/no): ");
    input = scanner.nextLine();

    if (input.equals("yes"))
      return true;
    else
      return false;
  }
}

class Util {
  public static boolean isInt(String input) {
    char[] inputString = input.toCharArray();

    for (int index = input.length() - 1; index >= 0; index--) {
      if (Character.isDigit(inputString[index]))
        continue;
      else if (inputString[index] == '-' && index == 0)
        continue;
      else
        return false;
    }
    return true;
  }

  public static boolean isNumeric(String input) {
    if (input == null || input.equals("")) {
      return false;
    }

    try {
      Double.parseDouble(input);
      return true;
    } catch (Exception e) {
    }
    return false;
  }
}
