package elo;

import javafx.application.Application;
import javafx.stage.Stage;

public class Program extends Application {
    public final static String version = "1.11";

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        Updater update = new Updater(new Login(stage));
    }
}
