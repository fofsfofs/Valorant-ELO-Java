package elo;

import com.sun.corba.se.impl.orbutil.graph.Graph;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import kong.unirest.Cookies;

public class Program extends Application {
    public static  void main(String[] args) {
        Cookies cookies = Authentication.getCookies();
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        Scene scene = Graphing.getLineChart();
        stage.setScene(scene);
        stage.setTitle("Insert Rank and RP here");
        stage.show();
    }
}
