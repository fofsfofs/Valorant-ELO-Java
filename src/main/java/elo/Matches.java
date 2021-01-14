package elo;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.internal.LinkedTreeMap;
import javafx.scene.control.Alert;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Matches {
    private String at;
    private String et;
    private List newMatches = new ArrayList();
    private String uID;
    private String user;
    private String region = "na";

    public Matches(String accessToken, String entitlementToken, String userID, String username, String region) {
        this.at = accessToken;
        this.et = entitlementToken;
        this.uID = userID;
        this.user = username;
        this.region = region;
        updateMatchHistory();
    }


    private ArrayList getMatches(int index) {
        String url = String.format("https://pd.%s.a.pvp.net/mmr/v1/players/%s/competitiveupdates?startIndex=%d&endIndex=%d", region, uID, index, index + 20);
        HttpResponse matchResponse = Unirest.get(url)
                .header("Authorization", at)
                .header("X-Riot-Entitlements-JWT", et)
                .asJson();

        Gson gson = new Gson();
        Map<String, ArrayList> json = new LinkedTreeMap<>();
        if (matchResponse.getBody() == null) {
            if (index == 0) {
                Alert refresh = new Alert(Alert.AlertType.WARNING);
                refresh.setTitle("Stop refreshing");
                refresh.setHeaderText(null);
                refresh.setContentText("You have either refreshed or run the program too many times\nPlease wait 10-20 seconds");
                refresh.showAndWait();
            }
        } else {
            json = gson.fromJson(matchResponse.getBody().toString(), Map.class);
        }

        if (json.get("Matches") == null) {
            return new ArrayList();
        } else {
            return json.get("Matches");
        }
    }

    public ArrayList loadHistory() {
        Gson gson = new Gson();
        try {
            Reader reader = Files.newBufferedReader(Paths.get(user + ".json"));
            ArrayList matchHistory = gson.fromJson(reader, ArrayList.class);
            reader.close();
            return matchHistory;
        } catch (IOException e1) {
            try {
                Writer writer = new FileWriter(String.format("%s.json", user));
                gson.toJson(new ArrayList<>(), writer);
                writer.close();
                return new ArrayList();
            } catch (IOException e2) {

            }
        }
        return null;
    }

    public void updateMatchHistory() {
        ArrayList matchHistory = loadHistory();

        for (int i = 0; i < 6; i++) {
            ArrayList<LinkedTreeMap> allMatches = getMatches(i * 20);
            for (int j = 0; j < allMatches.size(); j++) {
                LinkedTreeMap match = allMatches.get(j);
                if (!match.get("TierAfterUpdate").toString().equals("0.0") && match.get("CompetitiveMovement").toString().equals("MOVEMENT_UNKNOWN") && !matchHistory.contains(match)) {
                    newMatches.add(match);
                }

            }
        }

        newMatches.addAll(matchHistory);

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try {
            Writer writer = new FileWriter(String.format("%s.json", user));
            gson.toJson(newMatches, writer);
            writer.close();
        } catch (IOException e) {

        }
        newMatches.clear();
    }
}
