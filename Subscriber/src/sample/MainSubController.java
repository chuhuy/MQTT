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
import java.util.*;
import java.io.*;

public class MainSubController implements Initializable{
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

    ArrayList<String> dropdownLocationList = new ArrayList<>(List.of("bedroom", "bathroom", "garage", "kitchen", "living-room"));
    ArrayList<String> dropdownSensorList = new ArrayList<>(List.of("all","temperature", "noise", "humidity", "light"));
    String selectedLocation = "location";
    String selectedSensor = "sensor";

    Map<String, Integer> countMap = new HashMap<String, Integer>();

    //table
    @FXML
    TableView<Data> table ;

    @FXML
    TableColumn<Data,Integer> value;

    @FXML
    TableColumn<Data,String> topic;

    @FXML
    LineChart<Long,Integer> lineChart;

    XYChart.Series<Long,Integer> series0 = new XYChart.Series<Long,Integer>();
    XYChart.Series<Long,Integer> series1 = new XYChart.Series<Long,Integer>();
    XYChart.Series<Long,Integer> series2 = new XYChart.Series<Long,Integer>();
    XYChart.Series<Long,Integer> series3 = new XYChart.Series<Long,Integer>();

    ObservableList<Data> data = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        for(int index = 1; index < dropdownSensorList.size() ; index++){
            countMap.put(dropdownSensorList.get(index), 0);
        }

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
        topic.setCellValueFactory(new PropertyValueFactory<Data,String>("topic"));
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
                String currentSensor = data.get(data.size()-1).getTopic().split("/")[1];
                switch (currentSensor){
                    case "temperature":
                        series0.getData().add(new XYChart.Data<Long,Integer>(data.get(data.size()-1).getTime(), data.get(data.size()-1).getValue()));
                        break;

                    case "noise":
                        series1.getData().add(new XYChart.Data<Long,Integer>(data.get(data.size()-1).getTime(), data.get(data.size()-1).getValue()));
                        break;

                    case "humidity":
                        series2.getData().add(new XYChart.Data<Long,Integer>(data.get(data.size()-1).getTime(), data.get(data.size()-1).getValue()));
                        break;

                    case "light":
                        series3.getData().add(new XYChart.Data<Long,Integer>(data.get(data.size()-1).getTime(), data.get(data.size()-1).getValue()));
                        break;
                }
            }
        });

        // init chart
       // series0.setName(selectedSensor +" / "+ selectedLocation);
        lineChart.getData().addAll(series0,series1,series2,series3);
        lineChart.setLegendVisible(false);
        lineChart.getStyleClass().add("custom-chart");
    }


    public void getChoices(){
        if(selectedLocation != locationChoiceBox.getValue() ||
                selectedSensor != sensorChoiceBox.getValue()){
            data.clear();
            for(int index = 1; index < dropdownSensorList.size() ; index++){
                countMap.put(dropdownSensorList.get(index), 0);
            }

            selectedLocation = locationChoiceBox.getValue();
            selectedSensor = sensorChoiceBox.getValue();
            if(selectedSensor.equals("all")) selectedSensor = "*";

            String topic = selectedLocation + "/" + selectedSensor;
            System.out.println(topic);

            try {
                subscriber = new Subscriber();
            } catch(Exception e) {
                e.printStackTrace();
            }
            subscriber.subscribeTopic(topic, data, countMap);

            lineChart.getData().clear();
            series0 = new XYChart.Series<Long,Integer>();
            series1 = new XYChart.Series<Long,Integer>();
            series2 = new XYChart.Series<Long,Integer>();
            series3 = new XYChart.Series<Long,Integer>();

            lineChart.getData().addAll(series0,series1,series2,series3);
            lineChart.getStyleClass().add("custom-chart");
            lineChart.setLegendVisible(false);
        }

    }
}
