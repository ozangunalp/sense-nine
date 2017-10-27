package com.mechanitis.demo.sense.client.mood;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.NumberAxis;

import java.net.URL;
import java.util.ResourceBundle;

public class HappinessController implements Initializable {
    @FXML private NumberAxis yAxis;
    @FXML private BarChart<String, Double> happinessOverTime;

    public void setData(HappinessChartData data) {
        happinessOverTime.getData().add(data.getDataSeries());
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }
}
