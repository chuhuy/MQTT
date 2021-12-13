import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

public class Broker {
  public static void main(String[] args) {
    ServerSocket serverSocket = null;

    try {
      serverSocket = new ServerSocket(9000);
      System.out.println("Waiting for client...");

      while (true) {
        Socket socket = serverSocket.accept();
        System.out.println("Received a connection from a client: " + socket.getPort());
        ServerThread serverThread = new ServerThread(socket);
        serverThread.start();

      }
    } catch (IOException e) {
      System.out.println("Internal Server Error: " + e.toString());
    }
  }
}

class MessageQueue {
  private static Map<String, Topic> topics = new HashMap<String, Topic>();

  public static void publish(String topicName, String message) {
    if (topics.containsKey(topicName)) {
      topics.get(topicName).addMessage(message);
    } else {
      Topic newTopic = new Topic();
      newTopic.addMessage(message);
      topics.put(topicName, newTopic);
    }
  }

  public static void subscribe(String topicName, int portNumber, DataOutputStream subscriber) {
    topics.get(topicName).addSubscriber(portNumber, subscriber);
  }

  public static void removeSubscriberFromAllTopic(int portNumber) {
    for (Map.Entry<String, Topic> topic : topics.entrySet()) {
      topic.getValue().removeSubscriber(portNumber);
    }
  }

  public static void unsubscribe(String topicName, int portNumber) {
    topics.get(topicName).removeSubscriber(portNumber);
  }
}

class Topic {
  private Map<Integer, DataOutputStream> subscribers;
  private Queue<String> messages;

  public Topic() {
    this.subscribers = new HashMap<Integer, DataOutputStream>();
    this.messages = new LinkedList<String>();
  }

  public void addSubscriber(int portNumber, DataOutputStream subscriber) {
    this.subscribers.put(portNumber, subscriber);

    if (!this.messages.isEmpty()) {
      this.sendMessagesToSubscribers();
    }
  }

  public void addMessage(String message) {
    this.messages.add(message);
    if (!this.subscribers.isEmpty()) {
      this.sendMessagesToSubscribers();
    }
  }

  private void sendMessagesToSubscribers() {
    String message;
    while ((message = messages.poll()) != null) {
      for (Map.Entry<Integer, DataOutputStream> subscriber : this.subscribers.entrySet()) {
        DataOutputStream dataOutputStream = subscriber.getValue();
        try {
          dataOutputStream.writeUTF(message);
          dataOutputStream.flush();
        } catch (Exception e) {
          System.out.println(e);
        }
      }
    }
  }

  public void removeSubscriber(int portNumber) {
    this.subscribers.remove(portNumber);
  }
}

class ServerThread extends Thread {
  private Socket socket;

  public ServerThread(Socket socket) {
    this.socket = socket;
  }

  @Override
  public void run() {
    try {
      DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
      DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());

      while (true) {
        try {
          String clientMessage = dataInputStream.readUTF();
          String[] messageParts = clientMessage.split(" ");
          System.out.println(clientMessage);

          if (clientMessage.equals("CONNECT")) {
            dataOutputStream.writeUTF("CONNACK");
            dataOutputStream.flush();
          } else if (messageParts[0].equals("SUBSCRIBE")) {
            if (messageParts.length != 2) {
              dataOutputStream.writeUTF("INVALID INPUT");
              dataOutputStream.flush();
            } else {
              MessageQueue.subscribe(messageParts[1], this.socket.getPort(), dataOutputStream);
            }
          } else if (messageParts[0].equals("PUBLISH")) {
            if (messageParts.length != 3) {
              dataOutputStream.writeUTF("INVALID INPUT");
              dataOutputStream.flush();
            } else {
              MessageQueue.publish(messageParts[1], messageParts[2]);
            }
          } else {
            dataOutputStream.writeUTF("INVALID INPUT");
            dataOutputStream.flush();
          }
        } catch (EOFException e) {
          System.out.println("Client " + this.socket.getPort() + " disconnected");
          dataInputStream.close();
          dataOutputStream.close();
          MessageQueue.removeSubscriberFromAllTopic(socket.getPort());
          socket.close();
          break;
        }
      }
    } catch (IOException e) {
      System.out.println("Server Process Error: " + e.toString());
    }
  }
}