package elo;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import kong.unirest.Cookies;

public class Program extends Application {
    public static  void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        String username = "f0fsf0fs";
        Cookies cookies = Authentication.getCookies();
        String accessToken = Authentication.getAccessToken(cookies, username, "0a8O93ImH$EC");
        String entitlementToken = Authentication.getEntitlement(accessToken);
        String userID = Authentication.getUserID(accessToken);

        Matches m = new Matches(accessToken, entitlementToken, userID, username);
        Rank rank = new Rank(m);

        Scene scene = Graphing.getLineChart(rank);
        stage.setScene(scene);
        stage.setTitle(String.format("%s | %s | RP: %d", username, rank.getRank(), rank.getCurrentRP()));
        stage.setResizable(false);
        stage.show();
    }
}
