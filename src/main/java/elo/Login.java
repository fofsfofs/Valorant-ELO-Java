package elo;

import javafx.application.HostServices;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
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
    private static ProgressBar progressBar = new ProgressBar(0);
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

            Task task = m.updateMatchHistory();
            progressBar.progressProperty().bind(task.progressProperty());
            task.setOnSucceeded(e -> {
                if (m.loadHistory().isEmpty()) {
                    Alert noMatches = new Alert(Alert.AlertType.WARNING);
                    noMatches.setTitle("No matches found");
                    noMatches.setHeaderText(null);
                    noMatches.setContentText("A competitive match was not found in your last 100 matches.\nYou must also play at least one game after your placements.");
                    noMatches.showAndWait();
                } else {
                    stage.getIcons().remove(0);
                    stage.getIcons().add(new Image(Program.class.getResourceAsStream("/" + new Rank(m).getCurrentRank() + ".png")));
                    Store store = new Store(accessToken, entitlementToken, userID);
                    progressBar.progressProperty().unbind();
                    progressBar.setProgress(0);
                    Graphing graph = new Graphing(m, stage, store, hostServices);
                }
            });
            Thread thread = new Thread(task);
            thread.start();

        } else {
            if (new File("profile.txt").exists()) {
                signOut(Login.getUsername());
            }
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


        Text title = new Text("Valorant ELO Tracker");
        title.setFont(Font.loadFont(Program.class.getResourceAsStream("/Fonts/Valorant_Font.ttf"), 30));

        VBox top = new VBox();
        HBox titleBox = new HBox();
        Region reg1 = new Region();
        Region reg2 = new Region();
        HBox.setHgrow(reg1, Priority.ALWAYS);
        HBox.setHgrow(reg2, Priority.ALWAYS);
        titleBox.getChildren().addAll(reg1, title, reg2);
        top.setSpacing(25);
        top.getChildren().addAll(toolbar, titleBox);

        TextField usernameBox = new TextField();
        usernameBox.setPromptText("Riot ID");

        PasswordField pwBox = new PasswordField();
        pwBox.setPromptText("Password");

        CheckBox cb = new CheckBox("Save login to profile");

        if ((new File("profile.txt")).exists() && !profileExists("Profile 1")) {
            Login.setUsernameAndPass(getRemembered(1));
            usernameBox.setText(username);
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

        HBox bottomMiddle = new HBox();
        bottomMiddle.setSpacing(120);
        bottomMiddle.getChildren().addAll(cb, btn);
        VBox middle = new VBox();
        progressBar.setPrefSize(350, 15);
        progressBar.getStylesheets().add("progress.css");
        middle.setPadding(new Insets(10, 75, 20, 75));
        middle.setSpacing(20);
        middle.getChildren().addAll(usernameBox, pwBox, bottomMiddle, progressBar);

        Label version = new Label("v" + Program.version);
        HBox bottom = new HBox();
        bottom.getChildren().addAll(version);

        setProfileAction(p1, 1);
        setProfileAction(p2, 2);
        setProfileAction(p3, 3);

        root.getChildren().addAll(top, middle, bottom);

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
            signInLogic(cb, usernameBox.getText(), pwBox.getText());
        });

        pwBox.setOnKeyPressed(ke -> {
            if (ke.getCode().equals(KeyCode.ENTER)) {
                signInLogic(cb, usernameBox.getText(), pwBox.getText());
            }
        });

        Scene login = new Scene(root, 500, 320);
        stage.setScene(login);
        stage.setTitle("Login");
        stage.setResizable(false);
        stage.show();
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
            try {
                authenticate();
            } catch (Exception e) {
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                e.printStackTrace(pw);
                Program.logger.error(sw);
            }
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

        try {
            authenticate();
        } catch (Exception e) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            Program.logger.error(sw);
        }
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

    private void updateProfileMenu(MenuItem profile, int profNumber) {
        if (getRemembered(profNumber)[0].equals("Profile " + profNumber)) {
            profile.setDisable(true);
        } else {
            profile.setText(getRemembered(profNumber)[0]);
            profile.setDisable(false);
        }
    }

}