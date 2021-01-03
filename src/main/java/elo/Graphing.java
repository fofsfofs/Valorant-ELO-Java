package elo;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.stage.Stage;

import java.util.Arrays;
import java.util.List;

public class Graphing {

    public static Scene getLineChart() {

        final NumberAxis xAxis = new NumberAxis(0, 17, 1);
        final NumberAxis yAxis = new NumberAxis(600, 900, 100);
        final LineChart<Number, Number> sc = new
                LineChart<>(xAxis, yAxis);
        xAxis.setLabel("Past Matches");
        yAxis.setLabel("ELO");
        sc.setTitle("ELO History");

        XYChart.Series series1 = new XYChart.Series();
        XYChart.Data[] pos = new XYChart.Data[16];
        List<Integer> positive = Arrays.asList(655, 674, 700, 743, 775, 806, 839, 816, 800, 800, 768, 776, 758, 776, 772, 740);
        int[] gainLoss = {19, 26, 43, 32, 31, 33, -23, -16, 0, -32, 8, -18, 18, -4, -32};


        for (int i = 0; i < positive.size(); i++) {
            pos[i] = new XYChart.Data(i + 1, positive.get(i));
        }

        series1.setName("Match");
        for (XYChart.Data data : pos) {
            series1.getData().add(data);
        }

        sc.getData().addAll(series1);
        return new Scene(sc, 500, 400);
    }
}
