package elo;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Map;

public class DisplayMatch {

    private String matchID;
    private LinkedTreeMap match;
    private HBox matchBox = new HBox();
    private StackPane root;

    public DisplayMatch(String matchID, StackPane root) {
        this.matchID = matchID;
        this.root = root;
        setMatch();
        addToMatchBox();
    }

    private void addToMatchBox() {
        Region r1 = new Region();
        Region r2 = new Region();
        HBox.setHgrow(r1, Priority.ALWAYS);
        HBox.setHgrow(r2, Priority.ALWAYS);
        setMap();
        matchBox.getChildren().addAll(displayIcons("Comrades"), nameKDA("Comrades"), r1, middleText(), r2, nameKDA("Enemies"), displayIcons("Enemies"));
        root.getChildren().add(matchBox);
    }

    private VBox displayIcons(String team) {
        VBox comradeIcons = new VBox();
        ImageView[] comradeImView = new ImageView[5];

        for (int i = 0; i < ((ArrayList<Map>) match.get(team)).size(); i++) {
            String agent = (String) (((ArrayList<Map>) match.get(team)).get(i)).get("Agent");
            comradeImView[i] = new ImageView(new Image((Program.class.getResourceAsStream("/Agents/" + agent + ".png")), 45, 45, true, true));
            comradeIcons.getChildren().add(comradeImView[i]);
        }

        if (team.equals("Comrades")) {
            comradeIcons.setPadding(new Insets(37, 0, 0, 5));
        } else if (team.equals("Enemies")) {
            comradeIcons.setPadding(new Insets(37, 5, 0, 0));
        }

        comradeIcons.setSpacing(25);
        return comradeIcons;
    }

    private Text initializeMiddleText(String str, Color color) {
        Text t = new Text();
        t.setText(str);
        t.setFont(Font.loadFont(Program.class.getResourceAsStream("/Fonts/GOTHIC_BOLD.TTF"), 35));
        t.setFill(color);
        return t;
    }

    private VBox middleText() {
        VBox middleBox = new VBox();
        TextFlow textFlowPane = new TextFlow();
        String score = (String) match.get("Score");
        String date = (String) match.get("MatchDate");
        String gameLength = (String) match.get("MatchLength");
        Text comradeScore;
        Text enemyScore = initializeMiddleText(score.substring(score.indexOf("-") + 1), Color.rgb(204, 95, 95));
        Text dash = initializeMiddleText(" - ", Color.WHITE);
        Text dateText = initializeMiddleText(" " + date, Color.WHITE);
        Text lengthText = initializeMiddleText(gameLength.substring(0, gameLength.indexOf(".")) + " mins", Color.WHITE);

        if (score.length() == 5) {
            comradeScore = initializeMiddleText(" " + score.substring(0, score.indexOf("-")), Color.rgb(95, 204, 116));
        } else {
            comradeScore = initializeMiddleText("  " + score.substring(0, score.indexOf("-")), Color.rgb(95, 204, 116));
        }


        textFlowPane.getChildren().addAll(comradeScore, dash, enemyScore);
        middleBox.setPadding(new Insets(125, 0, 0, 0));
        middleBox.getChildren().addAll(textFlowPane, dateText, lengthText);
        return middleBox;
    }

    private VBox nameKDA(String team) {
        VBox comradeNames = new VBox();
        Text[] comradeTextNames = new Text[5];
        HBox[] hBoxes = new HBox[5];

        for (int i = 0; i < ((ArrayList<Map>) match.get(team)).size(); i++) {
            String name = (String) (((ArrayList<Map>) match.get(team)).get(i)).get("Name");
            String kda = (String) (((ArrayList<Map>) match.get(team)).get(i)).get("KDA");


            comradeTextNames[i] = new Text();
            comradeTextNames[i].setFont(Font.loadFont(Program.class.getResourceAsStream("/Fonts/GOTHIC.TTF"), 35));
            comradeTextNames[i].setFill(Color.WHITE);
            if (team.equals("Comrades")) {
                comradeTextNames[i].setText(name + "  " + kda);
                comradeNames.setPadding(new Insets(37, 0, 0, 5));
                comradeNames.getChildren().add(comradeTextNames[i]);
            } else if (team.equals("Enemies")) {
                hBoxes[i] = new HBox();
                comradeTextNames[i].setText(kda + "  " + name);
                hBoxes[i].getChildren().add(comradeTextNames[i]);
                hBoxes[i].setAlignment(Pos.BASELINE_RIGHT);
                comradeNames.setPadding(new Insets(37, 5, 0, 0));
                comradeNames.getChildren().add(hBoxes[i]);
            }
        }

        comradeNames.setSpacing(27);
        return comradeNames;
    }

    private void setMap() {
        String mapID = (String) match.get("MapID");
        String mapStr = mapID.substring(mapID.lastIndexOf("/") + 1);

        for (Maps m : Maps.values()) {
            if (m.getCodeName().equals(mapStr)) {
                ImageView background = new ImageView(new Image((Program.class.getResourceAsStream(String.format("/Maps/%s.png", m.getRealName()))), 1000, 400, true, true));
                root.getChildren().add(background);
            }
        }
    }

    private void setMatch() {
        Gson gson = new Gson();
        try {
            Reader reader = Files.newBufferedReader(Paths.get(Login.getUsername() + ".json"));
            ArrayList<LinkedTreeMap> matchHistory = gson.fromJson(reader, ArrayList.class);
            reader.close();
            for (LinkedTreeMap m : matchHistory) {
                if (m.get("MatchID").equals(matchID)) {
                    match = m;
                }
            }
        } catch (IOException e1) {

        }
    }


}
