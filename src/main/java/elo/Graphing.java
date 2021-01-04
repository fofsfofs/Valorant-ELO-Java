package elo;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Graphing {

    public static Scene getLineChart() {

        final NumberAxis xAxis = new NumberAxis(0, 20, 1);
        final NumberAxis yAxis = new NumberAxis(600, 900, 100);
        final LineChart<Number, Number> sc = new LineChart<>(xAxis, yAxis);
        xAxis.setLabel("Past Matches");
        yAxis.setLabel("ELO");
        sc.setTitle("ELO History");

        List<Integer> ranks = Arrays.asList(655, 674, 700, 743, 775, 806, 839, 816, 800, 800, 768, 776, 758, 776, 772, 740, 772);
        List<XYChart.Series> series = new ArrayList<>();
        List<XYChart.Data> tempElo = new ArrayList<>();
        int[] gainLoss = {19, 26, 43, 32, 31, 33, -23, -16, 0, -32, 8, -18, 18, -4, -32, 32};

        int iterate = 0;
        while (iterate < gainLoss.length) {
            while (gainLoss[iterate] >= 0) {
                tempElo.add(new XYChart.Data(iterate + 1, ranks.get(iterate)));
                tempElo.add(new XYChart.Data(iterate + 2, ranks.get(iterate + 1)));
                if (iterate + 1 < gainLoss.length){
                    iterate++;
                } else {
                    break;
                }
            }
            if (!tempElo.isEmpty()) {
                XYChart.Series s = new XYChart.Series();
                for (XYChart.Data data : tempElo) {
                    s.getData().add(data);
                }
                series.add(s);
                tempElo.clear();
            }
            while (gainLoss[iterate] < 0) {
                tempElo.add(new XYChart.Data(iterate + 1, ranks.get(iterate)));
                tempElo.add(new XYChart.Data(iterate + 2, ranks.get(iterate + 1)));
                if (iterate + 1 < gainLoss.length){
                    iterate++;
                } else {
                    break;
                }
            }
            if (!tempElo.isEmpty()) {
                XYChart.Series s = new XYChart.Series();
                for (XYChart.Data data : tempElo) {
                    s.getData().add(data);
                }
                series.add(s);
                tempElo.clear();
            }
            if (iterate == gainLoss.length-1) {
                if ((gainLoss[iterate] < 0 && gainLoss[iterate-1] >= 0) || gainLoss[iterate] >= 0 && gainLoss[iterate-1] < 0) {
                    tempElo.add(new XYChart.Data(iterate + 1, ranks.get(iterate)));
                    tempElo.add(new XYChart.Data(iterate + 2, ranks.get(iterate + 1)));
                    XYChart.Series s = new XYChart.Series();
                    XYChart.Series temp = new XYChart.Series();
                    for (XYChart.Data data : tempElo) {
                        s.getData().add(data);
                    }
                    if (gainLoss[iterate] < 0 && gainLoss[iterate - 1] >= 0) {
                        series.add(temp);
                    }
                    series.add(s);
                    tempElo.clear();
                }
                break;
            }
        }

        for (XYChart.Series i : series) {
            sc.getData().addAll(i);
        }
        sc.setLegendVisible(false);
        Scene scene = new Scene(sc, 800, 600);

        Platform.runLater(() -> {
            for (int i = 0; i < series.size(); i++) {
                ArrayList<XYChart.Data> pls = new ArrayList<>(series.get(i).getData());
                if (i % 2 == 0) {
                    for (XYChart.Data data : pls) {
                        data.getNode().setStyle("-fx-background-color: green, white;\n"
                                + "    -fx-background-insets: 0, 2;\n"
                                + "    -fx-background-radius: 5px;\n"
                                + "    -fx-padding: 5px;");
                    }
                    series.get(i).getNode().lookup(".chart-series-line").setStyle("-fx-stroke: green;");
                } else {
                    for (XYChart.Data data : pls) {
                        data.getNode().setStyle("-fx-background-color: red, white;\n"
                                + "    -fx-background-insets: 0, 2;\n"
                                + "    -fx-background-radius: 5px;\n"
                                + "    -fx-padding: 5px;");
                    }
                    series.get(i).getNode().lookup(".chart-series-line").setStyle("-fx-stroke: red;");
                }
            }
        });

        return scene;
    }
}
