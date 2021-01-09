package elo;

import javafx.application.HostServices;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import kong.unirest.Cookies;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.Scanner;

class Login {

    private static String password;
    private static String username;
    private static Menu profile;
    private Stage stage;
    private HostServices hostServices;
    private String region = "na";

    Login(Stage s, HostServices hs) {
        this.stage = s;
        this.hostServices = hs;
    }

    private void authenticate() {
        Cookies cookies = Authentication.getCookies();
        String accessToken = Authentication.getAccessToken(cookies, Login.getUsername(), Login.getPass());
        if (!accessToken.equals("")) {
            String entitlementToken = Authentication.getEntitlement(accessToken);
            String userID = Authentication.getUserID(accessToken);

            Matches m = new Matches(accessToken, entitlementToken, userID, Login.getUsername(), region);

            if (m.loadHistory().isEmpty()) {
                Alert noMatches = new Alert(Alert.AlertType.WARNING);
                noMatches.setTitle("No matches found");
                noMatches.setHeaderText(null);
                noMatches.setContentText("A competitive match was not found in your last 100 matches");
                noMatches.showAndWait();
            } else {
                stage.getIcons().remove(0);
                stage.getIcons().add(new Image(Program.class.getResourceAsStream("/" + new Rank(m).getCurrentRank() + ".png")));
                Graphing graph = new Graphing(m, stage, hostServices);
            }

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
        profile = new Menu("Profiles");
        Menu reg = new Menu("Region");
        MenuItem p1 = new MenuItem(getProfile(1));
        MenuItem p2 = new MenuItem(getProfile(2));
        MenuItem p3 = new MenuItem(getProfile(3));
        RadioMenuItem r1 = new RadioMenuItem("North America");
        RadioMenuItem r2 = new RadioMenuItem("Europe");
        RadioMenuItem r3 = new RadioMenuItem("Korea");
        RadioMenuItem r4 = new RadioMenuItem("Other");
        ToggleGroup toggleGroup = new ToggleGroup();
        r1.setSelected(true);
        toolbar.getMenus().addAll(profile, reg);
        profile.getItems().addAll(p1, p2, p3);
        reg.getItems().addAll(r1, r2, r3, r4);
        toggleGroup.getToggles().addAll(r1, r2, r3, r4);
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

        CheckBox cb = new CheckBox("Save login to profile");
        grid.add(cb, 0, 5);

        Label version = new Label("v" + Program.version);
        grid.add(version, 0, 6);

        if ((new File("profile.txt")).exists() && !profileExists("Profile 1")) {
            Login.setUsernameAndPass(getRemembered(1));
            userTextField.setText(username);
            pwBox.setText(password);
            cb.setSelected(true);
            updateProfileMenu(p2, 2);
            updateProfileMenu(p3, 3);
        } else {
            p1.setDisable(true);
            p2.setDisable(true);
            p3.setDisable(true);
        }

        Button btn = new Button("Sign in");
        HBox hbBtn = new HBox(10);
        hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
        hbBtn.getChildren().add(btn);
        grid.add(hbBtn, 1, 4);

        setProfileAction(p1, 1);
        setProfileAction(p2, 2);
        setProfileAction(p3, 3);

        r1.setOnAction(__ ->
        {
            region = "na";
        });

        r2.setOnAction(__ ->
        {
            region = "eu";
        });

        r3.setOnAction(__ ->
        {
            region = "kr";
        });

        r4.setOnAction(__ ->
        {
            region = "ap";
        });

        btn.setOnAction(e -> {
            signInLogic(cb, userTextField.getText(), pwBox.getText());
        });

        pwBox.setOnKeyPressed(ke -> {
            if (ke.getCode().equals(KeyCode.ENTER)) {
                signInLogic(cb, userTextField.getText(), pwBox.getText());
            }
        });

        Scene login = new Scene(root, 425, 300);
        stage.setScene(login);
        stage.setTitle("Login");
        stage.setResizable(false);
        stage.show();
    }

    private void updateProfileMenu(MenuItem profile, int profNumber) {
        if (getRemembered(profNumber)[0].equals("Profile " + profNumber)) {
            profile.setDisable(true);
        } else {
            profile.setText(getRemembered(profNumber)[0]);
            profile.setDisable(false);
        }
    }

    public static String getProfile(int profNumber) {
        if (new File("profile.txt").exists()) {
            setUsernameAndPass(getRemembered(profNumber));
            return Login.getUsername();
        } else {
            return String.format("Profile %d", profNumber);
        }
    }

    public static Menu getProfileMenu() {
        return profile;
    }

    private static String getPass() {
        return password;
    }

    static String getUsername() {
        return username;
    }

    private static String[] getRemembered(int profNumber) {
        String[] data = new String[6];
        try {
            File file = new File("profile.txt");
            Scanner scanner = new Scanner(file);
            for (int i = 0; i < data.length; i++) {
                data[i] = scanner.nextLine();
            }
            scanner.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        byte[] decodedBytes = Base64.getDecoder().decode(data[2 * profNumber - 1]);
        String decodedString = new String(decodedBytes);
        return new String[]{data[2 * profNumber - 2], decodedString};
    }

    private boolean profileExists(String userField) {
        BufferedReader reader = null;
        String data = "";

        try {
            reader = new BufferedReader(new FileReader("profile.txt"));

            String line = reader.readLine();

            while (line != null) {
                data += line + "\n";

                line = reader.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data.contains(userField) ? true : false;
    }

    private static void rememberLogin(int profNumber) {
        if (new File("profile.txt").exists()) {
            try {
                BufferedReader reader = new BufferedReader(new FileReader("profile.txt"));

                String oldContent = "";
                String line = reader.readLine();

                while (line != null) {
                    oldContent += line + "\n";

                    line = reader.readLine();
                }
                String passReplace = Base64.getEncoder().encodeToString(("Password " + profNumber).getBytes());

                String newContent = oldContent.replace("Profile " + profNumber, username).replace(passReplace, Base64.getEncoder().encodeToString(password.getBytes()));
                reader.close();

                new File("profile.txt").delete();

                FileWriter writer = new FileWriter("profile.txt");

                writer.write(newContent);

                writer.close();
                Path path = Paths.get("profile.txt");
                Files.setAttribute(path, "dos:hidden", true, LinkOption.NOFOLLOW_LINKS);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                FileWriter fileWriter = new FileWriter("profile.txt");
                String newLine = "\n";
                for (int i = 1; i < 4; i++) {
                    if (i == 3) {
                        newLine = "";
                    }
                    if (i == profNumber) {
                        fileWriter.write(String.format("%s\n%s%s", username, Base64.getEncoder().encodeToString(password.getBytes()), newLine));
                    } else {
                        fileWriter.write(String.format("Profile %d\n%s%s", i, Base64.getEncoder().encodeToString(("Password " + i).getBytes()), newLine));
                    }
                }
                fileWriter.close();
                Path path = Paths.get("profile.txt");
                Files.setAttribute(path, "dos:hidden", true, LinkOption.NOFOLLOW_LINKS);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static void setUsernameAndPass(String[] info) {
        username = info[0];
        password = info[1];
    }

    private void setProfileAction(MenuItem profile, int profNumber) {
        profile.setOnAction(__ -> {
            setUsernameAndPass(getRemembered(profNumber));
            authenticate();
        });
    }

    private void signInLogic(CheckBox cb, String userField, String passField) {
        boolean exit = false;
        setUsernameAndPass(new String[]{userField, passField});
        if (cb.isSelected() && new File("profile.txt").exists()) {
            if (!profileExists(userField)) {
                for (int i = 1; i < 4; i++) {
                    if (profileExists("Profile " + i) && !exit) {
                        rememberLogin(i);
                        updateProfileMenu(profile.getItems().get(i - 1), i);
                        exit = true;
                    }
                }
            }
        } else if (cb.isSelected() && !new File("profile.txt").exists()) {
            rememberLogin(1);
            updateProfileMenu(profile.getItems().get(0), 1);
        }
        authenticate();
    }

    public static void signOut(String user) {
        int profNumber = 0;
        for (int i = 0; i < profile.getItems().size(); i++) {
            if (profile.getItems().get(i).getText().equals(user)) {
                profNumber = i + 1;
            }
        }
        try {
            BufferedReader reader = new BufferedReader(new FileReader("profile.txt"));

            String oldContent = "";
            String line = reader.readLine();

            while (line != null) {
                oldContent += line + "\n";

                line = reader.readLine();
            }
            String passReplace = Base64.getEncoder().encodeToString(("Password " + profNumber).getBytes());

            String newContent = oldContent.replace(user, "Profile " + profNumber).replace(Base64.getEncoder().encodeToString(password.getBytes()), passReplace);
            reader.close();

            new File("profile.txt").delete();

            FileWriter writer = new FileWriter("profile.txt");

            writer.write(newContent);

            writer.close();
            Path path = Paths.get("profile.txt");
            Files.setAttribute(path, "dos:hidden", true, LinkOption.NOFOLLOW_LINKS);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}