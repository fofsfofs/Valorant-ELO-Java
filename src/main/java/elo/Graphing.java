package elo;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
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
        final NumberAxis yAxis = new NumberAxis(lower, upper, 100);
        final LineChart<Number, Number> sc = new LineChart<>(xAxis, yAxis);
        xAxis.setLabel("Past Matches");
        yAxis.setLabel("ELO");
        sc.setTitle("ELO History");

        int iterate = 0;
        int counter = 1;
        while (iterate < gainLoss.size()) {
            while (gainLoss.get(iterate) >= 0) {
                tempElo.add(new XYChart.Data(iterate + 1, eloHistory.get(iterate)));
                tempElo.add(new XYChart.Data(iterate + 2, eloHistory.get(iterate + 1)));
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
                    if ((Integer) data.getXValue() == 1) {
                        data.setNode(new HoveredThresholdNode((Integer) data.getXValue(), (Integer) data.getYValue(), rank.getRank((Integer) data.getYValue()), 0));
                    } else {
                        data.setNode(new HoveredThresholdNode((Integer) data.getXValue(), (Integer) data.getYValue(), rank.getRank((Integer) data.getYValue()), gainLoss.get((Integer) data.getXValue() - 2)));
                    }
                    counter++;
                }
                series.add(s);
                tempElo.clear();
            }
            while (gainLoss.get(iterate) < 0) {
                tempElo.add(new XYChart.Data(iterate + 1, eloHistory.get(iterate)));
                tempElo.add(new XYChart.Data(iterate + 2, eloHistory.get(iterate + 1)));

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
                    if ((Integer) data.getXValue() == 1) {
                        data.setNode(new HoveredThresholdNode((Integer) data.getXValue(), (Integer) data.getYValue(), rank.getRank((Integer) data.getYValue()), 0));
                    } else {
                        data.setNode(new HoveredThresholdNode((Integer) data.getXValue(), (Integer) data.getYValue(), rank.getRank((Integer) data.getYValue()), gainLoss.get((Integer) data.getXValue() - 2)));
                    }
                    counter++;
                }
                series.add(s);
                tempElo.clear();
            }
            if (iterate == gainLoss.size() - 1) {
                if ((gainLoss.get(iterate) < 0 && gainLoss.get(iterate - 1) >= 0) || gainLoss.get(iterate) >= 0 && gainLoss.get(iterate - 1) < 0) {
                    tempElo.add(new XYChart.Data(iterate + 1, eloHistory.get(iterate)));
                    tempElo.add(new XYChart.Data(iterate + 2, eloHistory.get(iterate + 1)));
                    XYChart.Series s = new XYChart.Series();
                    XYChart.Series temp = new XYChart.Series();
                    for (XYChart.Data data : tempElo) {
                        s.getData().add(data);
                        data.setNode(new HoveredThresholdNode((Integer) data.getXValue(), (Integer) data.getYValue(), rank.getRank((Integer) data.getYValue()), gainLoss.get((Integer) data.getXValue() - 2)));
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

        Button signout = new Button("Sign out");
        VBox vbox = new VBox();
        HBox hbox = new HBox();
        hbox.setPadding(new Insets(0, 0, 0, 12));
        hbox.getChildren().add(signout);
        vbox.getChildren().add(sc);
        vbox.getChildren().add(hbox);

        signout.setOnAction( __ ->
        {
            stage.close();
            Platform.runLater( () -> new Program().start( new Stage() ) );
        } );

        Scene scene = new Scene(vbox, 800, 450);


        Platform.runLater(() -> {
            for (int i = 0; i < series.size(); i++) {
                ArrayList<XYChart.Data> pls = new ArrayList<>(series.get(i).getData());
                if (gainLoss.get(0) >= 0) {
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
                } else {
                    if (i % 2 == 0) {
                        for (XYChart.Data data : pls) {
                            data.getNode().setStyle("-fx-background-color: red, white;\n"
                                    + "    -fx-background-insets: 0, 2;\n"
                                    + "    -fx-background-radius: 5px;\n"
                                    + "    -fx-padding: 5px;");
                        }
                        series.get(i).getNode().lookup(".chart-series-line").setStyle("-fx-stroke: red;");
                    } else {
                        for (XYChart.Data data : pls) {
                            data.getNode().setStyle("-fx-background-color: green, white;\n"
                                    + "    -fx-background-insets: 0, 2;\n"
                                    + "    -fx-background-radius: 5px;\n"
                                    + "    -fx-padding: 5px;");
                        }
                        series.get(i).getNode().lookup(".chart-series-line").setStyle("-fx-stroke: green;");
                    }
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


