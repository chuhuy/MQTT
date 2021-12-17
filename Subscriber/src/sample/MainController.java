package sample;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.io.*;

public class MainController implements Initializable{
    private Subscriber subscriber;

    //Scene change
   // Stage stage;
   // Scene scene;
   // Parent root;

    //choice box
    @FXML
    ChoiceBox<String> locationChoiceBox = new ChoiceBox<>();

    @FXML
    ChoiceBox<String> sensorChoiceBox = new ChoiceBox<>();

    ArrayList<String> dropdownLocationList = new ArrayList<>(List.of("bedroom", "bathroom", "garage", "kitchen", "living room"));
    ArrayList<String> dropdownSensorList = new ArrayList<>(List.of("temperature", "noise", "humidity", "light"));
    String selectedLocation = "location";
    String selectedSensor = "sensor";

    //table
    @FXML
    TableView<Data> table ;

    @FXML
    TableColumn<Data,Integer> value;

    @FXML
    TableColumn<Data,Long> time;

    @FXML
    LineChart<Long,Integer> lineChart;

    XYChart.Series<Long,Integer> series = new XYChart.Series<Long,Integer>();

    ObservableList<Data> data = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        try {
            subscriber = new Subscriber();
        } catch(Exception e) {
            e.printStackTrace();
        }

        //intialize choice box values
        for (String s:
                dropdownLocationList) {
            locationChoiceBox.getItems().add(s);
        }

        //set table values
        value.setCellValueFactory(new PropertyValueFactory<Data,Integer>("value"));
        time.setCellValueFactory(new PropertyValueFactory<Data,Long>("time"));
        table.setItems(data);

        for (String s:
                dropdownSensorList) {
            sensorChoiceBox.getItems().add(s);
        }
        locationChoiceBox.getSelectionModel().select(0);
        sensorChoiceBox.getSelectionModel().select(0);

        data.addListener(new ListChangeListener<Data>() {
            @Override
            public void onChanged(Change<? extends Data> change) {
                while(change.next()) {
                    System.out.println(change.getFrom());
                }
                series.getData().add(new XYChart.Data<Long,Integer>(data.get(data.size()-1).getTime(), data.get(data.size()-1).getValue()));
            }
        });

        // init chart
        series.setName(selectedSensor +" / "+ selectedLocation);
        lineChart.getData().add(series);
        lineChart.getStyleClass().add("custom-chart");
    }


    public void getChoices(){
        if(selectedLocation != locationChoiceBox.getValue() ||
                selectedSensor != sensorChoiceBox.getValue()){
            data.clear();

            selectedLocation = locationChoiceBox.getValue();
            selectedSensor = sensorChoiceBox.getValue();

            String topic = selectedLocation + "/" + selectedSensor;
            System.out.println(topic);

            try {
                subscriber = new Subscriber();
            } catch(Exception e) {
                e.printStackTrace();
            }
            subscriber.subscribeTopic(topic, data);

            lineChart.getData().clear();
            series = new XYChart.Series<Long,Integer>();
            series.setName(selectedSensor +" / "+ selectedLocation);
            lineChart.getData().add(series);
            lineChart.getStyleClass().add("custom-chart");
        }
    }
}
