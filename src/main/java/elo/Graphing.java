package elo;

import javafx.application.Platform;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Graphing {

    public static Scene getLineChart(Rank rank) {

        List<Integer> ranks = rank.getELOHistory();
        List<XYChart.Series> series = new ArrayList<>();
        List<XYChart.Data> tempElo = new ArrayList<>();
        List<Integer> gainLoss = rank.getGainLoss();

        double upper = 0;
        double lower  = 0;
        if (((Collections.max(ranks) / 100 + 1) * 100 - Collections.max(ranks)) > 50) {
            upper =  (Collections.max(ranks) / 100 + 0.5) * 100;
        } else {
            upper = (Collections.max(ranks) / 100 + 1) * 100;
        }
        if (((Collections.min(ranks) / 100 + 1) * 100 - Collections.min(ranks)) > 50) {
            lower =  (Collections.min(ranks) / 100) * 100;
        } else {
            lower = (Collections.min(ranks) / 100 + 0.5) * 100;
        }

        final NumberAxis xAxis = new NumberAxis(0, ranks.size() +  1, 1);
        final NumberAxis yAxis = new NumberAxis(lower, upper, 100);
        final LineChart<Number, Number> sc = new LineChart<>(xAxis, yAxis);
        xAxis.setLabel("Past Matches");
        yAxis.setLabel("ELO");
        sc.setTitle("ELO History");


        int iterate = 0;
        int counter = 1;
        while (iterate < gainLoss.size()) {
            while (gainLoss.get(iterate) >= 0) {
                tempElo.add(new XYChart.Data(iterate + 1, ranks.get(iterate)));
                tempElo.add(new XYChart.Data(iterate + 2, ranks.get(iterate + 1)));
                if (iterate + 1 < gainLoss.size()) {
                    iterate++;
                } else {
                    break;
                }
            }
            if (!tempElo.isEmpty()) {
                XYChart.Series s = new XYChart.Series();
                for (XYChart.Data data : tempElo) {
                    s.getData().add(data);
                    if ((Integer)data.getXValue() == 1) {
                        data.setNode(new HoveredThresholdNode((Integer) data.getXValue(), (Integer) data.getYValue(), "variable", 0));
                    } else {
                        data.setNode(new HoveredThresholdNode((Integer) data.getXValue(), (Integer) data.getYValue(), "variable", gainLoss.get((Integer) data.getXValue()-2)));
                    }
                    counter++;
                }
                series.add(s);
                tempElo.clear();
            }
            while (gainLoss.get(iterate) < 0) {
                tempElo.add(new XYChart.Data(iterate + 1, ranks.get(iterate)));
                tempElo.add(new XYChart.Data(iterate + 2, ranks.get(iterate + 1)));

                if (iterate + 1 < gainLoss.size()) {
                    iterate++;
                } else {
                    break;
                }
            }
            if (!tempElo.isEmpty()) {
                XYChart.Series s = new XYChart.Series();
                for (XYChart.Data data : tempElo) {
                    s.getData().add(data);
                    if ((Integer)data.getXValue() == 1) {
                        data.setNode(new HoveredThresholdNode((Integer) data.getXValue(), (Integer) data.getYValue(), rank.getRank((Integer) data.getYValue()), 0));
                    } else {
                        data.setNode(new HoveredThresholdNode((Integer) data.getXValue(), (Integer) data.getYValue(), rank.getRank((Integer) data.getYValue()), gainLoss.get((Integer) data.getXValue()-2)));
                    }
                    counter++;
                }
                series.add(s);
                tempElo.clear();
            }
            if (iterate == gainLoss.size() - 1) {
                if ((gainLoss.get(iterate) < 0 && gainLoss.get(iterate - 1) >= 0) || gainLoss.get(iterate) >= 0 && gainLoss.get(iterate - 1) < 0) {
                    tempElo.add(new XYChart.Data(iterate + 1, ranks.get(iterate)));
                    tempElo.add(new XYChart.Data(iterate + 2, ranks.get(iterate + 1)));
                    XYChart.Series s = new XYChart.Series();
                    XYChart.Series temp = new XYChart.Series();
                    for (XYChart.Data data : tempElo) {
                        s.getData().add(data);
                        data.setNode(new HoveredThresholdNode((Integer) data.getXValue(), (Integer) data.getYValue(), rank.getRank((Integer) data.getYValue()), gainLoss.get((Integer) data.getXValue()-2)));
                    }

                    if (gainLoss.get(iterate) < 0 && gainLoss.get(iterate - 1) >= 0) {

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
        sc.setCursor(Cursor.CROSSHAIR);
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

    static class HoveredThresholdNode extends StackPane {
        HoveredThresholdNode(int x, int value, String rank, int change) {
            setPrefSize(10, 10);

            final Label label = createDataThresholdLabel(x, value, rank, change);

            setOnMouseEntered(mouseEvent -> {
                getChildren().setAll(label);
                setCursor(Cursor.NONE);
                toFront();
            });
            setOnMouseExited(mouseEvent -> {
                getChildren().clear();
                setCursor(Cursor.CROSSHAIR);
            });
        }

        private Label createDataThresholdLabel(int x,int value, String rank, int change) {
            final Label label = new Label("Match: " + x + "\nELO: " + value + "\n" + rank + "\nChange: " + change);
            label.getStyleClass().addAll("default-color8", "chart-line-symbol", "chart-series-line");
            label.setStyle("-fx-font-size: 9; -fx-font-weight: bold;");
            label.setMinSize(Label.USE_PREF_SIZE, Label.USE_PREF_SIZE);
            return label;
        }
    }

}


