package elo;

import javafx.application.Application;
import javafx.stage.Stage;

public class Program extends Application {
    public final static String version = "1.0";

    public static void main(String[] args) {
        System.out.println(Updater.updateNeeded());


        launch(args);
    }

    @Override
    public void start(Stage stage) {
        Login login = new Login(stage);
    }
}
