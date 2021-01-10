package elo;

import javafx.application.Application;
import javafx.stage.Stage;

public class Program extends Application {
    final static String version = "1.23";

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        Updater update = new Updater(new Login(stage, getHostServices()));
//        If you would like to run/compile the program yourself comment out the code above and uncomment the code below
//        new Login(stage, getHostServices()).createLogin();
    }
}
