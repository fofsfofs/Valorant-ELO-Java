package elo;

import javafx.application.Platform;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Graphing {

    private Stage stage;
    private Rank rank;

    public Graphing(Stage s, Rank r) {
        this.stage = s;
        this.rank = r;
        createGraph();
    }

    private void createGraph() {
        Scene scene = getLineChart(rank);
        stage.setScene(scene);
        stage.getIcons().add(new Image(Program.class.getResourceAsStream("/" + rank.getCurrentRank() + ".png")));
        stage.setTitle(String.format("%s | %s | RP: %d", Login.getUsername(), rank.getCurrentRank(), rank.getCurrentRP()));
        stage.setResizable(false);
        stage.show();
    }

    public Scene getLineChart(Rank rank) {

        List<Integer> eloHistory = rank.getELOHistory();
        List<XYChart.Series> series = new ArrayList<>();
        List<XYChart.Data> tempElo = new ArrayList<>();
        List<Integer> gainLoss = rank.getGainLoss();

        double upper = 0;
        double lower = 0;
        double minDiff = 0;

        int interval = Collections.min(eloHistory) - ((Collections.min(eloHistory) / 100) * 100);

        if (interval >= 0 && interval <= 25) {
            minDiff = Collections.min(eloHistory) - (Collections.min(eloHistory) / 100) * 100;
            lower = getBound(Collections.min(eloHistory), -0.25, 0, minDiff);
        } else if (interval > 25 && interval <= 50) {
            minDiff = Collections.min(eloHistory) - (Collections.min(eloHistory) / 100 + 0.25) * 100;
            lower = getBound(Collections.min(eloHistory), 0, 0.25, minDiff);
        } else if (interval > 50 && interval <= 75) {
            minDiff = Collections.min(eloHistory) - (Collections.min(eloHistory) / 100 + 0.5) * 100;
            lower = getBound(Collections.min(eloHistory), 0.25, 0.5, minDiff);
        } else if (interval > 75 && interval <= 99) {
            minDiff = Collections.min(eloHistory) - (Collections.min(eloHistory) / 100 + 0.75) * 100;
            lower = getBound(Collections.min(eloHistory), 0.5, 0.75, minDiff);
        }
        interval = ((Collections.max(eloHistory) / 100 + 1) * 100) - Collections.max(eloHistory);

        if (interval >= 0 && interval <= 25) {
            minDiff = (Collections.max(eloHistory) / 100 + 1) * 100 - Collections.max(eloHistory);
            upper = getBound(Collections.max(eloHistory), 1.25, 1, minDiff);
        } else if (interval > 25 && interval <= 50) {
            minDiff = (Collections.max(eloHistory) / 100 + 0.75) * 100 - Collections.max(eloHistory);
            upper = getBound(Collections.max(eloHistory), 1, 0.75, minDiff);
        } else if (interval > 50 && interval <= 75) {
            minDiff = (Collections.max(eloHistory) / 100 + 0.5) * 100 - Collections.max(eloHistory);
            upper = getBound(Collections.max(eloHistory), 0.75, 0.5, minDiff);
        } else if (interval > 75 && interval <= 99) {
            minDiff = (Collections.max(eloHistory) / 100 + 0.25) * 100 - Collections.max(eloHistory);
            upper = getBound(Collections.max(eloHistory), 0.5, 0.25, minDiff);
        }

        final NumberAxis xAxis = new NumberAxis(0, eloHistory.size() + 1, 1);
        final NumberAxis yAxis = new NumberAxis(lower, upper, 50);
        final LineChart<Number, Number> sc = new LineChart<>(xAxis, yAxis);
        xAxis.setLabel("Past Matches");
        yAxis.setLabel("ELO");
        sc.setTitle("ELO History");

        List positiveList = new ArrayList();
        List negativeList = new ArrayList();
        XYChart.Series positive = new XYChart.Series();
        XYChart.Series negative = new XYChart.Series();

        for (int i = 0; i < gainLoss.size(); i++) {
            if (gainLoss.get(i) >= 0) {
                if (i != 0 && gainLoss.get(i - 1) < 0) {
                    negativeList.add(negative);
                    negative = new XYChart.Series();
                }
                positive.getData().add(new XYChart.Data(i + 1, eloHistory.get(i)));
                positive.getData().add(new XYChart.Data(i + 2, eloHistory.get(i + 1)));
            } else if (gainLoss.get(i) < 0) {
                if (i != 0 && gainLoss.get(i - 1) >= 0) {
                    positiveList.add(positive);
                    positive = new XYChart.Series();
                }
                negative.getData().add(new XYChart.Data(i + 1, eloHistory.get(i)));
                negative.getData().add(new XYChart.Data(i + 2, eloHistory.get(i + 1)));
            }
        }
        positiveList.add(positive);
        negativeList.add(negative);

        for (Object i : positiveList) {
            sc.getData().addAll((XYChart.Series) i);
        }
        for (Object i : negativeList) {
            sc.getData().addAll((XYChart.Series) i);
        }
        sc.setLegendVisible(false);
        sc.setCursor(Cursor.CROSSHAIR);
        Scene scene = new Scene(sc, 800, 600);

        Platform.runLater(() -> {
            for (int i = 0; i < positiveList.size(); i++) {
                ArrayList<XYChart.Data> data = new ArrayList<>(((XYChart.Series) positiveList.get(i)).getData());
                for (XYChart.Data datum : data) {
                    datum.getNode().setStyle("-fx-background-color: green, white;\n"
                            + "    -fx-background-insets: 0, 2;\n"
                            + "    -fx-background-radius: 5px;\n"
                            + "    -fx-padding: 5px;");
                    XYChart.Series series1 = (XYChart.Series) positiveList.get(i);
                    series1.getNode().lookup(".chart-series-line").setStyle("-fx-stroke: green;");
                }
            }
            for (int i = 0; i < negativeList.size(); i++) {
                ArrayList<XYChart.Data> data = new ArrayList<>(((XYChart.Series) negativeList.get(i)).getData());
                for (XYChart.Data datum : data) {
                    datum.getNode().setStyle("-fx-background-color: red, white;\n"
                            + "    -fx-background-insets: 0, 2;\n"
                            + "    -fx-background-radius: 5px;\n"
                            + "    -fx-padding: 5px;");
                    XYChart.Series series1 = (XYChart.Series) negativeList.get(i);
                    series1.getNode().lookup(".chart-series-line").setStyle("-fx-stroke: red;");
                }
            }

        });

        return scene;
    }

    public double getBound(int elo, double offset1, double offset2, double minDiff) {
        if (minDiff <= 15) {
            return (elo / 100 + offset1) * 100;
        } else {
            return (elo / 100 + offset2) * 100;
        }

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

        private Label createDataThresholdLabel(int x, int value, String rank, int change) {
            final Label label = new Label("Match: " + x + "\nELO: " + value + "\n" + rank + "\nChange: " + change);
            label.getStyleClass().addAll("default-color8", "chart-line-symbol", "chart-series-line");
            label.setStyle("-fx-font-size: 9; -fx-font-weight: bold;");
            label.setMinSize(Label.USE_PREF_SIZE, Label.USE_PREF_SIZE);
            return label;
        }
    }
}


