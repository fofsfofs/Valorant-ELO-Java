package elo;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import kong.unirest.Cookies;

public class Program extends Application {
    public static  void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        String username = "torkoal64";
        String accessToken;
        Cookies cookies = Authentication.getCookies();
        try {
            accessToken = Authentication.getAccessToken(cookies, username, "jubbathehut221");
            String entitlementToken = Authentication.getEntitlement(accessToken);
            String userID = Authentication.getUserID(accessToken);

            Matches m = new Matches(accessToken, entitlementToken, userID, username);
            Rank rank = new Rank(m);

            Scene scene = Graphing.getLineChart(rank);
            Scene login = Login.createLogin();
            stage.setScene(login);
            stage.setTitle("Login");
            stage.show();
            boolean credentialCheck = true;
            if (credentialCheck) {
                stage.setScene(scene);
                stage.getIcons().add(new Image(Program.class.getResourceAsStream("/"+ rank.getCurrentRank()+".png")));
                stage.setTitle(String.format("%s | %s | RP: %d", username, rank.getCurrentRank(), rank.getCurrentRP()));
                stage.setResizable(false);
                stage.show();
            }
        } catch (StringIndexOutOfBoundsException e) {
            System.out.println("Incorrect login!");
        }

    }
}
