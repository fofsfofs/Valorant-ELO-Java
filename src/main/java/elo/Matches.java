package elo;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.internal.LinkedTreeMap;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Map;

public class Matches {
    private String at;
    private String et;
    private ArrayList newMatches = new ArrayList();
    private String uID;
    private String user;

    public Matches(String accessToken, String entitlementToken, String userID, String username) {
        this.at = accessToken;
        this.et = entitlementToken;
        this.uID = userID;
        this.user = username;
        updateMatchHistory();
    }

    public ArrayList getLatestMatchHistory() {
        return newMatches;
    }

    private ArrayList getMatches() {
        String url = String.format("https://pd.na.a.pvp.net/mmr/v1/players/%s/competitiveupdates?startIndex=0&endIndex=20", uID);
        HttpResponse matchResponse = Unirest.get(url)
                .header("Authorization", at)
                .header("X-Riot-Entitlements-JWT", et)
                .asJson();

        Gson gson = new Gson();
        Map<String, ArrayList> json = gson.fromJson(matchResponse.getBody().toString(), Map.class);
        return json.get("Matches");
    }

    private ArrayList loadHistory() {
        Gson gson = new Gson();
        try {
            Reader reader = Files.newBufferedReader(Paths.get("f0fsf0fs.json"));
            ArrayList matchHistory = gson.fromJson(reader, ArrayList.class);
            reader.close();
            return  matchHistory;
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

    private void updateMatchHistory() {
        ArrayList allMatches = getMatches();
        ArrayList matchHistory = loadHistory();

        for (int i = 0; i < allMatches.size(); i++) {
            LinkedTreeMap match = (LinkedTreeMap) allMatches.get(i);
            if (!match.get("CompetitiveMovement").toString().equals("MOVEMENT_UNKNOWN") && !matchHistory.contains(match)) {
                newMatches.add(allMatches.get(i));
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
    }
}
