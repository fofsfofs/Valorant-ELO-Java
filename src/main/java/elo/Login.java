package elo;

import javafx.application.HostServices;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import kong.unirest.Cookies;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.Scanner;

public class Login {

    private static String password;
    private static String username;
    private Stage stage;
    private HostServices hostServices;

    public Login(Stage s, HostServices hs) {
        this.stage = s;
        this.hostServices = hs;
    }

    private void authenticate() {
        Cookies cookies = Authentication.getCookies();
        String accessToken = Authentication.getAccessToken(cookies, Login.getUsername(), Login.getPass());
        if (!accessToken.equals("")) {
            String entitlementToken = Authentication.getEntitlement(accessToken);
            String userID = Authentication.getUserID(accessToken);

            Matches m = new Matches(accessToken, entitlementToken, userID, Login.getUsername());
            Rank rank = new Rank(m);

            Graphing graph = new Graphing(stage, rank, hostServices);

        } else {
            Alert incorrect = new Alert(Alert.AlertType.WARNING);
            incorrect.setTitle("Incorrect login");
            incorrect.setHeaderText(null);
            incorrect.setContentText("Username or password is incorrect!");
            incorrect.showAndWait();
        }
    }

    public void createLogin() {
        stage.getIcons().add(new Image(Program.class.getResourceAsStream("/logo.png")));
        VBox root = new VBox();
        MenuBar toolbar = new MenuBar();
        Menu profile = new Menu("Profiles");
        MenuItem p1 = new MenuItem("Profile 1");
        MenuItem p2 = new MenuItem("Profile 2");
        MenuItem p3 = new MenuItem("Profile 3");
        toolbar.getMenus().add(profile);
        profile.getItems().addAll(p1, p2, p3);
        GridPane grid = new GridPane();
        root.getChildren().addAll(toolbar, grid);

        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));


        Text scenetitle = new Text("Valorant ELO Tracker");
        scenetitle.setFont(Font.font("Tacoma", FontWeight.NORMAL, 30));
        grid.add(scenetitle, 0, 0, 2, 1);

        Label userName = new Label("Riot ID:");
        grid.add(userName, 0, 1);

        TextField userTextField = new TextField("");
        grid.add(userTextField, 1, 1);

        Label pw = new Label("Password:");
        grid.add(pw, 0, 2);

        PasswordField pwBox = new PasswordField();
        grid.add(pwBox, 1, 2);

        CheckBox cb = new CheckBox("Remember login");
        grid.add(cb, 0, 5);

        Label version = new Label("v" + Program.version);
        grid.add(version, 0, 6);

        if ((new File("profile.txt")).exists()) {
            setRemembered();
            userTextField.setText(username);
            pwBox.setText(password);
            cb.setSelected(true);
        }

        Button btn = new Button("Sign in");
        HBox hbBtn = new HBox(10);
        hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
        hbBtn.getChildren().add(btn);
        grid.add(hbBtn, 1, 4);


        btn.setOnAction(e -> {
            Login.setUsername(userTextField.getText());
            Login.setPass(pwBox.getText());
            if (cb.isSelected() && !(new File("profile.txt")).exists()) {
                rememberLogin();
                authenticate();
            } else {
                authenticate();
            }
        });

        pwBox.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent ke) {
                if (ke.getCode().equals(KeyCode.ENTER)) {
                    Login.setUsername(userTextField.getText());
                    Login.setPass(pwBox.getText());
                    if (cb.isSelected() && !(new File("profile.txt")).exists()) {
                        rememberLogin();
                        authenticate();
                    } else {
                        authenticate();
                    }
                }
            }
        });

        Scene login = new Scene(root, 425, 300);
        stage.setScene(login);
        stage.setTitle("Login");
        stage.setResizable(false);
        stage.show();
    }

    private static String getPass() {
        return password;
    }

    public static String getUsername() {
        return username;
    }

    private static void setRemembered() {
        String[] data = new String[2];
        try {
            File file = new File("profile.txt");
            Scanner scanner = new Scanner(file);
            for (int i = 0; i < data.length; i++) {
                data[i] = scanner.nextLine();
            }
            scanner.close();
        } catch (IOException e) {

        }

        Login.setUsername(data[0]);
        byte[] decodedBytes = Base64.getDecoder().decode(data[1]);
        String decodedString = new String(decodedBytes);
        Login.setPass(decodedString);
    }

    private static void rememberLogin() {
        try {
            FileWriter fileWriter = new FileWriter("profile.txt");
            fileWriter.write(String.format("%s\n%s", username, Base64.getEncoder().encodeToString(password.getBytes())));
            fileWriter.close();
            Path path = Paths.get("profile.txt");
            Files.setAttribute(path, "dos:hidden", true, LinkOption.NOFOLLOW_LINKS);
        } catch (IOException e) {

        }
    }

    private static void setPass(String pass) {
        password = pass;
    }

    private static void setUsername(String user) {
        username = user;
    }


}