package sample;

import javafx.beans.Observable;
import javafx.collections.ObservableList;

import java.io.*;
import java.net.*;
import java.util.*;

public class Subscriber {
    private int port = 9000;
    private String defaultIPAddress = "127.0.0.1";
    private static DataInputStream dataInputStream;
    private static DataOutputStream dataOutputStream;
    private static Socket socket;

    public Subscriber() {
        try {
            this.socket = new Socket(defaultIPAddress, port);
            this.dataInputStream = new DataInputStream(socket.getInputStream());
            this.dataOutputStream = new DataOutputStream(socket.getOutputStream());

            boolean connectStatus = connect();
            // if(!connectStatus) disconnect();

        } catch (Exception e) {
            e.printStackTrace();
            disconnect();
        }
    }

    public void subscribeTopic(String topic, ObservableList<Data> data) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                int count = 1;
                try {
                    dataOutputStream.writeUTF("SUBSCRIBE " + topic);
                    while (true) {
                        String message = dataInputStream.readUTF();
                        data.add(new Data(Integer.parseInt(message), count));
                        System.out.println("Message from broker: " + message);
                        count ++;
                    }
                } catch(Exception e) {
                    e.printStackTrace();
                    unsubscribeTopic(topic);
                }
            }
        }).start();
    }

    public void unsubscribeTopic(String topic) {
        try {
            dataOutputStream.writeUTF("UNSUBSCRIBE " + topic);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public boolean connect() {
        try {
            dataOutputStream.writeUTF("CONNECT");
            String response = dataInputStream.readUTF();
            System.out.println(response);

            if(response.equals("CONNACK")) return true;
            else return false;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return false;
        }
    }

    public void disconnect() {
        try {
            dataOutputStream.writeUTF("DISCONNECT");

            dataInputStream.close();
            dataOutputStream.close();
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
