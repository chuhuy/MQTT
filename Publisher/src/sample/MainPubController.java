package sample;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class MainPubController implements Initializable {

    @FXML
    Button brokerBtn ;
    boolean brokerStatus = false;

    @FXML
    Button sendBtn;
    boolean sendBtnStatus = false;

    @FXML
    Button confirmBtn;
    boolean confirmBtnStatus = false;

    @FXML
    TextField inputValue;

    @FXML
    ChoiceBox<String> locationChoiceBox;

    @FXML
    ChoiceBox<String> sensorChoiceBox;

    ArrayList<String> locationList = new ArrayList<>(List.of("bedroom", "bathroom", "garage", "kitchen", "living room"));
    ArrayList<String> sensorList = new ArrayList<>(List.of("temperature", "noise", "humidity", "light"));
    String selectedLocation = "location";
    String selectedSensor = "sensor";

    //Publisher publisher;
    private static int port = 9000;
    private static String defaultIPAddress = "127.0.0.1";
    ;
    public static DataOutputStream oos;
    public static DataInputStream ois;
    public static Socket socket = new Socket();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //intialize choice box values
        for (String s:
                locationList) {
            locationChoiceBox.getItems().add(s);
        }
        for (String s:
                sensorList) {
            sensorChoiceBox.getItems().add(s);
        }
        locationChoiceBox.getSelectionModel().select(selectedLocation);
        sensorChoiceBox.getSelectionModel().select(selectedSensor);
    }

    public void brokerPress(){
        //settext
        if(brokerStatus){
            brokerBtn.setStyle("-fx-background-color:#000000,linear-gradient(#ff0000, #f72020),linear-gradient(#bf0b0b, #c41f1f),linear-gradient(#911111, #942121);" +
                    "-fx-background-radius: 2,2,2,2;" +
                    "-fx-padding: 5 8 5 7;" +
                    "-fx-text-fill: white;" +
                    "-fx-font-size: 13px;" );
            brokerBtn.setText(String.valueOf("CHƯA KẾT NỐI"));
            Publisher.disconnect();
            brokerStatus = !brokerStatus;
            System.out.println("CHƯA KẾT NỐI");
        }else{
            brokerBtn.setStyle("-fx-background-color:#000000,linear-gradient(#00ff11, #25fa34),linear-gradient(#12c91e, #29cc34),linear-gradient(#14a81e, #289e30);" +
                    "-fx-background-radius: 2,2,2,2;" +
                    "-fx-padding: 5 8 5 7;" +
                    "-fx-text-fill: white;" +
                    "-fx-font-size: 13px;");
            brokerBtn.setText(String.valueOf("ĐÃ KẾT NỐI"));
            Publisher.connect(defaultIPAddress, port);
            brokerStatus = !brokerStatus;
            System.out.println("ĐÃ KẾT NỐI");
        }
    }

    public void confirmBtnPress(){
        if("location" == locationChoiceBox.getValue() || "sensor" == sensorChoiceBox.getValue()){
            sendBtnStatus = false;
            //do nothing here
        }else if((selectedLocation != locationChoiceBox.getValue() || selectedSensor != sensorChoiceBox.getValue()) && brokerStatus){
            selectedLocation = locationChoiceBox.getValue();
            selectedSensor = sensorChoiceBox.getValue();
            String topic = selectedLocation + "/" + selectedSensor;
            sendBtnStatus = true;
            System.out.println(topic);
        }
    }

    public void sendBtnPress(){
        if(sendBtnStatus){
            String topic = selectedLocation + "/" + selectedSensor;
            String inputVal = inputValue.getText();
            if (Util.isNumeric(inputVal)) {
                Publisher.publish(topic, inputVal);
                System.out.println("sent");
            }
        }else{
            inputValue.setText("invalid value");
            System.out.println("cant send");
        }
    }
}
