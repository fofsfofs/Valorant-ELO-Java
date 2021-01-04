package elo;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import kong.unirest.Cookies;

public class Program extends Application {
    public static  void main(String[] args) {
//        launch(args);
    }

    @Override
    public void start(Stage stage) {
        String username = "f0fsf0fs";
        String accessToken;
        Cookies cookies = Authentication.getCookies();
        try {
            accessToken = Authentication.getAccessToken(cookies, username, "0a8O93ImH$EC");
            String entitlementToken = Authentication.getEntitlement(accessToken);
            String userID = Authentication.getUserID(accessToken);

            Matches m = new Matches(accessToken, entitlementToken, userID, username);
            Rank rank = new Rank(m);

            Scene scene = Graphing.getLineChart(rank);
            Scene login = Login.createLogin();
            stage.setScene(login);
            stage.setTitle("Login");
            stage.show();
            boolean credentialCheck = false;
            if (credentialCheck) {
                stage.setScene(scene);
                stage.setTitle(String.format("%s | %s | RP: %d", username, rank.getCurrentRank(), rank.getCurrentRP()));
                stage.setResizable(false);
                stage.show();
            }
        } catch (StringIndexOutOfBoundsException e) {
            System.out.println("Incorrect login!");
        }

    }
}
