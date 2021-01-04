package elo;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import kong.unirest.Cookies;

public class Login {

    private static String password;
    private static String username;
    private Stage stage;

    public Login(Stage s) {
        this.stage = s;
        createLogin();
    }

    private void authenticate() {
        Cookies cookies = Authentication.getCookies();
        String accessToken = Authentication.getAccessToken(cookies, Login.getUsername(), Login.getPass());
        if (!accessToken.equals("")) {
            String entitlementToken = Authentication.getEntitlement(accessToken);
            String userID = Authentication.getUserID(accessToken);

            Matches m = new Matches(accessToken, entitlementToken, userID, Login.getUsername());
            Rank rank = new Rank(m);

            Scene scene = Graphing.getLineChart(rank);
            stage.setScene(scene);
            stage.setTitle(String.format("%s | %s | RP: %d", Login.getUsername(), rank.getCurrentRank(), rank.getCurrentRP()));
            stage.setResizable(false);
            stage.show();
        } else {
            Alert incorrect = new Alert(Alert.AlertType.WARNING);
            incorrect.setTitle("Incorrect login");
            incorrect.setHeaderText(null);
            incorrect.setContentText("Username or password is incorrect!");
            incorrect.showAndWait();
        }
    }

    private void createLogin() {
        GridPane grid = new GridPane();

        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        Text scenetitle = new Text("Valorant ELO Tracker");
        scenetitle.setFont(Font.font("Tacoma", FontWeight.NORMAL, 20));
        grid.add(scenetitle, 0, 0, 2, 1);

        Label userName = new Label("Riot ID:");
        grid.add(userName, 0, 1);

        TextField userTextField = new TextField();
        grid.add(userTextField, 1, 1);

        Label pw = new Label("Password:");
        grid.add(pw, 0, 2);

        PasswordField pwBox = new PasswordField();
        grid.add(pwBox, 1, 2);

        Button btn = new Button("Sign in");
        HBox hbBtn = new HBox(10);
        hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
        hbBtn.getChildren().add(btn);
        grid.add(hbBtn, 1, 4);

        final Text actiontarget = new Text();
        grid.add(actiontarget, 1, 6);

        btn.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent e) {
                actiontarget.setFill(Color.GREY);
                Login.setUsername(userTextField.getText());
                Login.setPass(pwBox.getText());
                authenticate();
            }
        });

        Scene login = new Scene(grid, 300, 275);
        stage.setScene(login);
        stage.setTitle("Login");
        stage.show();
    }

    private static String getPass() {
        return password;
    }

    private static String getUsername() {
        return username;
    }

    private static void setPass(String pass) {
        password = pass;
    }

    private static void setUsername(String user) {
        username = user;
    }


}