package elo;

import com.sun.corba.se.impl.orbutil.graph.Graph;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import kong.unirest.Cookies;

public class Program extends Application {
    public static  void main(String[] args) {
        Cookies cookies = Authentication.getCookies();
        String accessToken = "Bearer " + Authentication.getAccessToken(cookies, "f0fsf0fs", "0a8O93ImH$EC");
        String entitlementToken = Authentication.getEntitlement(accessToken);
        String userID = Authentication.getUserID(accessToken);

        Matches m = new Matches(accessToken, entitlementToken, userID);
        Rank rank = new Rank(m);
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
