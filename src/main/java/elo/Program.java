package elo;

import javafx.application.Application;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Program extends Application {
    final static String version = "2.31";
    static final Logger logger = LogManager.getLogger(Program.class);

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
