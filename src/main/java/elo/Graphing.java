package elo;

import javafx.application.HostServices;
import javafx.application.Platform;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.MouseButton;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import org.gillius.jfxutils.chart.ChartPanManager;
import org.gillius.jfxutils.chart.JFXChartUtil;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Graphing {

    private Matches matches;
    static StackPane root = new StackPane();
    private Stage stage;
    private Store store;
    private Rank rank;
    private HostServices hostServices;
    private Scene scene;

    public Graphing(Matches m, Stage s, Store store, HostServices hs) {
        this.matches = m;
        this.stage = s;
        this.store = store;
        this.hostServices = hs;
        updateRank();
        createGraph("Dark Mode");
    }

    private void createGraph(String mode) {
        if (mode.equals("Dark Mode")) {
            scene = getLineChart("Dark Mode");
            scene.getRoot().setStyle("-fx-background-color: #212121");
            scene.getStylesheets().add("style.css");
        } else {
            scene = getLineChart("Light mode");
        }
        stage.setScene(scene);
        stage.setTitle(String.format("%s | %s | RR: %d", Login.getUsername(), rank.getCurrentRank(), rank.getCurrentRR()));
        stage.setResizable(false);
        stage.show();
    }

    public Scene getLineChart(String mode) {

        List<Integer> eloHistory = rank.getELOHistory();
        List<Integer> gainLoss = rank.getGainLoss();
        List<String> matchIDS = rank.getMatchIDs();

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
        } else if (interval > 75 && interval <= 100) {
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
        } else if (interval > 75 && interval <= 100) {
            minDiff = (Collections.max(eloHistory) / 100 + 0.25) * 100 - Collections.max(eloHistory);
            upper = getBound(Collections.max(eloHistory), 0.5, 0.25, minDiff);
        }

        int xTicks = eloHistory.size() >= 10 ? eloHistory.size() / 10 : 1;

        NumberAxis xAxis = new NumberAxis(0, eloHistory.size() + 1, xTicks);
        NumberAxis yAxis = new NumberAxis(lower, upper, 100);


        LineChart<Number, Number> sc = new LineChart<>(xAxis, yAxis);
        StringConverter stringConverter = new StringConverter<Number>() {
            @Override
            public String toString(Number object) {
                return object.intValue() + "";
            }

            @Override
            public Number fromString(String string) {
                return Integer.parseInt(string);
            }
        };

        xAxis.setTickLabelFormatter(stringConverter);
        yAxis.setTickLabelFormatter(stringConverter);

        xAxis.setLabel("Past Matches");
        yAxis.setLabel("ELO");
        sc.setTitle("ELO History");

        double area = Math.abs((xAxis.getUpperBound() - xAxis.getLowerBound()) * (yAxis.getUpperBound() - yAxis.getLowerBound()));

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
                XYChart.Data data = new XYChart.Data(i + 1, eloHistory.get(i));
                XYChart.Data data2 = new XYChart.Data(i + 2, eloHistory.get(i + 1));
                addHover(i, data, data2, positive, gainLoss, matchIDS);

            } else if (gainLoss.get(i) < 0) {
                if (i != 0 && gainLoss.get(i - 1) >= 0) {
                    positiveList.add(positive);
                    positive = new XYChart.Series();
                }
                XYChart.Data data = new XYChart.Data(i + 1, eloHistory.get(i));
                XYChart.Data data2 = new XYChart.Data(i + 2, eloHistory.get(i + 1));
                addHover(i, data, data2, negative, gainLoss, matchIDS);
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
        sc.setCursor(Cursor.HAND);

        MenuItem refresh = new MenuItem("Refresh");
        refresh.setAccelerator(new KeyCodeCombination(KeyCode.R, KeyCombination.CONTROL_DOWN));
        MenuItem about = new MenuItem("About");
        MenuItem modeMenu = new MenuItem();
        if (mode.equals("Dark Mode")) {
            modeMenu.setText("Light Mode");
        } else {
            modeMenu.setText("Dark Mode");
        }
        MenuItem signOut = new MenuItem("Sign out");
        MenuItem exit = new MenuItem("Exit");
        MenuItem addProfile = new MenuItem("Add Profile");
        MenuItem myShop = new MenuItem("My Shop");
        MenuItem resetView = new MenuItem("Reset View");
        resetView.setAccelerator(new KeyCodeCombination(KeyCode.R, KeyCombination.ALT_DOWN));
        MenuBar menuBar = new MenuBar();
        Menu file = new Menu("Options");
        Menu zoom = new Menu("Zoom: 100.00%");
        file.getItems().addAll(refresh, modeMenu, addProfile, myShop, signOut, about, exit);
        zoom.getItems().add(resetView);
        menuBar.getMenus().addAll(file, Login.getProfileMenu(), zoom);
        if (!Login.getProfileMenu().getItems().get(2).getText().equals("Profile 3")) {
            addProfile.setDisable(true);
        }

        resetMatchInfo();

        VBox vbox = new VBox();
        vbox.getChildren().addAll(menuBar, sc, root);

        resetView.setOnAction(__ -> {
            if (modeMenu.getText().equals("Light Mode")) {
                createGraph("Dark Mode");
            } else {
                createGraph("Light Mode");
            }
            zoom.setText("Zoom: 100.00%");
        });

        refresh.setOnAction(__ -> {
            resetMatchInfo();
            matches.updateMatchHistory();
            updateRank();
            if (modeMenu.getText().equals("Light Mode")) {
                createGraph("Dark Mode");
            } else {
                createGraph("Light Mode");
            }
        });

        about.setOnAction(__ ->
        {
            hostServices.showDocument("https://github.com/fofsfofs/Valorant-ELO-Java/blob/main/README.md");
        });

        addProfile.setOnAction(__ -> {
            stage.close();
            Platform.runLater(() -> new Program().start(new Stage()));
        });

        myShop.setOnAction(__ -> {
            VBox vBox = new VBox();
            Stage shopStage = new Stage();
            shopStage.getIcons().add(new Image(Program.class.getResourceAsStream("/logo.png")));
            shopStage.setTitle("My Shop");
            Text[] labels = new Text[4];
            int imageHeights = 0;
            for (int i = 0; i < 4; i++) {
                labels[i] = new Text();
                HBox hBox = new HBox();
                Region r1 = new Region();
                Region r2 = new Region();
                HBox.setHgrow(r1, Priority.ALWAYS);
                HBox.setHgrow(r2, Priority.ALWAYS);
                String itemName = store.getItemNames().get(i);
                String itemID = store.getItemIDs().get(i);
                if (itemName.contains("Gravitational")) {
                    labels[i].setText("G.U.N " + itemName.split(" ")[itemName.split(" ").length - 1]);
                } else {
                    labels[i].setText(store.getItemNames().get(i));
                }
                labels[i].setFont(Font.loadFont(Program.class.getResourceAsStream("/Fonts/GOTHIC.TTF"), 25));
                labels[i].setTextAlignment(TextAlignment.CENTER);
                hBox.getChildren().addAll(r1, labels[i], r2);
                Image im = new Image(String.format("https://media.valorant-api.com/weaponskinlevels/%s/displayicon.png", itemID.toLowerCase()));
                imageHeights += im.getHeight();
                if (im.getHeight() == 0) {
                    im = new Image(String.format("https://media.valorant-api.com/weaponskinchromas/%s/displayicon.png", itemID.toLowerCase()));
                }
                vBox.getChildren().addAll(new ImageView(im), hBox);
            }
            vbox.setSpacing(10);
            shopStage.setScene(new Scene(vBox, 500, imageHeights + 125));
            shopStage.show();
        });

        signOut.setOnAction(__ ->
        {
            stage.close();
            Login.signOut(Login.getUsername());
            Platform.runLater(() -> new Program().start(new Stage()));
        });

        exit.setOnAction(__ ->
        {
            stage.close();
        });

        modeMenu.setOnAction(__ ->
        {
            if (modeMenu.getText().equals("Light Mode")) {
                scene.getRoot().setStyle("-fx-background-color: #FFFFFF");
                scene.getStylesheets().remove("style.css");
                modeMenu.setText("Dark Mode");
            } else {
                scene.getRoot().setStyle("-fx-background-color: #212121");
                scene.getStylesheets().add("style.css");
                modeMenu.setText("Light Mode");
            }
        });

        Scene scene = new Scene(vbox, 1000, 825);

        Platform.runLater(() -> {
            setColors(positiveList, "green");
            setColors(negativeList, "red");
        });

        ChartPanManager panner = new ChartPanManager(sc);
        //while presssing the left mouse button, you can drag to navigate
        panner.setMouseFilter(mouseEvent -> {
            if (mouseEvent.getButton() == MouseButton.PRIMARY) {//set your custom combination to trigger navigation
                // let it through
            } else {
                mouseEvent.consume();
            }
        });
        panner.start();

        //holding the right mouse button will draw a rectangle to zoom to desired location
        JFXChartUtil.setupZooming(sc, mouseEvent -> {
            if (mouseEvent.getButton() != MouseButton.SECONDARY)//set your custom combination to trigger rectangle zooming
                mouseEvent.consume();
        });

        sc.setOnScroll((ScrollEvent event) -> {
            String zoomString = String.format("Zoom: %.2f", 100 * (area / Math.abs((xAxis.getUpperBound() - xAxis.getLowerBound()) * (yAxis.getUpperBound() - yAxis.getLowerBound()))));
            zoom.setText(zoomString + "%");
        });

        return scene;
    }

    public void addHover(int i, XYChart.Data data1, XYChart.Data data2, XYChart.Series s, List<Integer> g, List<String> matchIDs) {
        if (i != 0) {
            data1.setNode(new HoveredThresholdNode((Integer) data1.getXValue(), (Integer) data1.getYValue(), rank.getRank((Integer) data1.getYValue()), g.get(i - 1), matchIDs.get(i)));
        } else {
            data1.setNode(new HoveredThresholdNode((Integer) data1.getXValue(), (Integer) data1.getYValue(), rank.getRank((Integer) data1.getYValue()), 0, matchIDs.get(i)));
        }
        data2.setNode(new HoveredThresholdNode((Integer) data2.getXValue(), (Integer) data2.getYValue(), rank.getRank((Integer) data2.getYValue()), g.get(i), matchIDs.get(i + 1)));
        s.getData().add(data1);
        s.getData().add(data2);
    }

    private void resetMatchInfo() {
        root.getChildren().clear();
        Text defaultText = new Text();
        defaultText.setText("\n\n\nHover over a match point to display match details");
        defaultText.setFont(Font.loadFont(Program.class.getResourceAsStream("/Fonts/GOTHIC_BOLD.TTF"), 35));
        defaultText.setFill(Color.WHITE);
        root.getChildren().add(defaultText);
    }

    public void setColors(List list, String color) {
        for (int i = 0; i < list.size(); i++) {
            ArrayList<XYChart.Data> data = new ArrayList<>(((XYChart.Series) list.get(i)).getData());
            for (XYChart.Data datum : data) {
                datum.getNode().setStyle("-fx-background-color: " + color + ", white;\n"
                        + "    -fx-background-insets: 0, 2;\n"
                        + "    -fx-background-radius: 5px;\n"
                        + "    -fx-padding: 5px;");
                XYChart.Series series1 = (XYChart.Series) list.get(i);
                series1.getNode().lookup(".chart-series-line").setStyle("-fx-stroke: " + color + ";");
            }
        }
    }

    public double getBound(int elo, double offset1, double offset2, double minDiff) {
        if (minDiff <= 15) {
            return (elo / 100 + offset1) * 100;
        } else {
            return (elo / 100 + offset2) * 100;
        }
    }

    private void updateRank() {
        rank = new Rank(matches);
    }

    static class HoveredThresholdNode extends StackPane {
        HoveredThresholdNode(int x, int value, String rank, int change, String matchID) {
            setPrefSize(10, 10);

            final Label label = createDataThresholdLabel(x, value, rank, change, matchID);

            setOnMouseEntered(mouseEvent -> {
                root.getChildren().clear();
                try {
                    DisplayMatch dm = new DisplayMatch(matchID, root);
                } catch (Exception e) {
                    StringWriter sw = new StringWriter();
                    PrintWriter pw = new PrintWriter(sw);
                    e.printStackTrace(pw);
                    Program.logger.error(sw);
                }
                getChildren().setAll(label);
                setCursor(Cursor.NONE);
                toFront();
            });
            setOnMouseExited(mouseEvent -> {
                getChildren().clear();
                setCursor(Cursor.CROSSHAIR);
            });
        }

        private Label createDataThresholdLabel(int x, int value, String rank, int change, String matchID) {
            Label label;
            if (x != 1) {
                label = new Label(String.format("Match: %d\nELO: %d\n%s\nGain/Loss: %d", x, value, rank, change));
            } else {
                label = new Label(String.format("Match: %d\nELO: %d\n%s", x, value, rank));
            }
            if (change >= 0) {
                label.getStyleClass().addAll("default-color2", "chart-line-symbol");
                label.getStyleClass().add("match-label-green");
            } else {
                label.getStyleClass().addAll("default-color0", "chart-line-symbol");
                label.getStyleClass().add("match-label-red");
            }
            label.setStyle("-fx-font-size: 9; -fx-font-weight: bold; -fx-text-fill: black");

            label.setMinSize(Label.USE_PREF_SIZE, Label.USE_PREF_SIZE);
            return label;
        }
    }
}