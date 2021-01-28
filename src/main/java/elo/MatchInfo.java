package elo;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.stream.JsonReader;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import org.apache.commons.lang3.StringUtils;

import java.io.StringReader;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class MatchInfo {
    String at;
    ArrayList<Map> comrades = new ArrayList<>();
    ArrayList<Map> enemies = new ArrayList<>();
    String et;
    Gson gson = new GsonBuilder().setPrettyPrinting().create();
    Map<String, ArrayList> json;
    String mID;
    ArrayList<Map> players = new ArrayList<>();
    Map user;
    String reg;
    String team;
    String uID;


    public MatchInfo(String matchID, String accessToken, String entitlementToken, String userID, String region) {
        this.at = accessToken;
        this.et = entitlementToken;
        this.mID = matchID;
        this.reg = region;
        this.uID = userID;
        getMatchJson();
        getPlayersInfo();
        setTeamList();
    }

    public LinkedTreeMap addInfo(LinkedTreeMap match) {
        match.put("Agent", getUserAgent());
        match.put("Score", getScore());
        match.put("Victory", isVictory());
        match.put("Comrades", comrades);
        match.put("Enemies", enemies);
        match.put("MatchLength", String.valueOf(getMatchLength()));
        match.put("MatchDate", getMatchDate());
        return match;
    }

    public String getUserAgent() {
        return getAgent(user);
    }

    private String getAgent(Map player) {
        for (Agents a : Agents.values()) {
            if (a.getCodeName().toLowerCase().equals(player.get("characterId"))) {
                return a.getRealName();
            }
        }
        return "Agent not found";
    }

    private String getKDA(Map player) {
        return String.format("%d/%d/%d", getPlayerScore(player)[0], getPlayerScore(player)[1], getPlayerScore(player)[2]);
    }

    private void getMatchJson() {
        String url = String.format("https://pd.%s.a.pvp.net/match-details/v1/matches/%s", reg, mID);
        HttpResponse matchResponse = Unirest.get(url)
                .header("Authorization", at)
                .header("X-Riot-Entitlements-JWT", et)
                .header("X-Riot-ClientPlatform", "ew0KCSJwbGF0Zm9ybVR5cGUiOiAiUEMiLA0KCSJwbGF0Zm9ybU9TIjogIldpbmRvd3MiLA0KCSJwbGF0Zm9ybU9TVmVyc2lvbiI6ICIxMC4wLjE5MDQyLjEuMjU2LjY0Yml0IiwNCgkicGxhdGZvcm1DaGlwc2V0IjogIlVua25vd24iDQp9")
                .asJson();

        try {
            String matchResponseString = matchResponse.getBody().toString();
            for (int i = 1; i < 11; i++) {
                String name = matchResponseString.substring(StringUtils.ordinalIndexOf(matchResponseString, "gameName", i) + 11, StringUtils.ordinalIndexOf(matchResponseString, "tagLine", i) - 3);
                if (name.contains(" ") && name.split(" ").length >= 2) {
                    String replacement = "";
                    for (int j = 0; j < name.split(" ").length; j++) {
                        replacement += name.split(" ")[j];
                    }
                    matchResponseString = matchResponseString.replace(name, replacement);
                }
            }

            JsonReader reader = new JsonReader(new StringReader(matchResponseString));
            json = gson.fromJson(reader, Map.class);
//            try {
//                Writer writer = new FileWriter(String.format("%s.json", "test"));
//                gson.toJson(json, writer);
//                writer.close();
//            } catch (IOException e) {
//
//            }
        } catch (NullPointerException n) {
            System.out.println("Something went wrong");
        }
    }

    public String getMatchDate() {
        double gameDouble = Double.parseDouble(json.toString().substring(json.toString().indexOf("gameStartMillis") + 16, json.toString().indexOf("provisioningFlowID") - 5)) * Math.pow(10, 12);
        Long gameLong = (long) gameDouble;
        LocalDate date = Instant.ofEpochMilli(gameLong).atZone(ZoneId.systemDefault()).toLocalDate();
        String month = date.getMonth().toString();
        if (month.length() > 4) {
            return month.substring(0, 1) + month.substring(1, 3).toLowerCase() + " " + date.getDayOfMonth();
        } else {
            return month.substring(0, 1) + month.substring(1).toLowerCase() + " " + date.getDayOfMonth();
        }
    }

    private double getMatchLength() {
        return Math.round(Double.parseDouble(json.toString().substring(json.toString().indexOf("gameLengthMillis") + 17, json.toString().indexOf("gameStartMillis") - 2)) / 60000);
    }

    private void getPlayersInfo() {
        try {
            for (int i = 0; i < 10; i++) {
                try {
                    Map player = gson.fromJson(json.get("players").get(i).toString(), Map.class);
                    players.add(player);
                    if (player.get("subject").equals(uID)) {
                        user = player;
                        team = getTeam(user);
                    }
                } catch (JsonSyntaxException j) {

                }
            }
        } catch (NullPointerException n) {
            System.out.println("Something went wrong");
        }

    }

    private int[] getPlayerScore(Map player) {
        int scoreArray[] = new int[4];
        Gson gson = new Gson();
        Map<String, Double> kda = gson.fromJson(player.get("stats").toString(), Map.class);
        scoreArray[0] = kda.get("kills").intValue();
        scoreArray[1] = kda.get("deaths").intValue();
        scoreArray[2] = kda.get("assists").intValue();
        scoreArray[3] = kda.get("score").intValue();

        return scoreArray;
    }

    private String getScore() {
        String enemyScore, ourScore;
        Map team1 = gson.fromJson(json.get("teams").get(0).toString(), Map.class);
        Map team2 = gson.fromJson(json.get("teams").get(1).toString(), Map.class);

        if (team1.get("teamId").equals(team)) {
            ourScore = String.valueOf(((Double) team1.get("roundsWon")).intValue());
            enemyScore = String.valueOf(((Double) team2.get("roundsWon")).intValue());
        } else {
            ourScore = String.valueOf(((Double) team2.get("roundsWon")).intValue());
            enemyScore = String.valueOf(((Double) team1.get("roundsWon")).intValue());
        }
        return ourScore + "-" + enemyScore;
    }

    private String getTeam(Map player) {
        return (String) player.get("teamId");
    }

    private boolean isVictory() {
        int dashIndex = getScore().indexOf("-");
        int ourScore = Integer.parseInt(getScore().substring(0, dashIndex));
        int enemyScore = Integer.parseInt(getScore().substring(dashIndex + 1));

        return ourScore > enemyScore ? true : false;
    }

    private void setTeamList() {
        for (Map player : players) {
            Map<String, String> info = new HashMap<>();
            if (((String) player.get("gameName")).length() > 10) {
                info.put("Name", ((String) player.get("gameName")).substring(0, 10));
            } else {
                info.put("Name", (String) player.get("gameName"));
            }

            info.put("Agent", getAgent(player));
            info.put("KDA", getKDA(player));
            info.put("Score", String.valueOf(getPlayerScore(player)[3]));
            if (player.get("teamId").equals(team)) {
                comrades.add(info);
            } else {
                enemies.add(info);
            }
        }
        sortScores(comrades);
        sortScores(enemies);
    }

    private void sortScores(ArrayList<Map> list) {
        ArrayList<Integer> nums = new ArrayList<>();
        for (Map player : list) {
            nums.add(Integer.valueOf((String) player.get("Score")));
        }
        Collections.sort(nums);
        Collections.reverse(nums);
        for (int i = 0; i < list.size(); i++) {
            if (String.valueOf(nums.get(i)) != list.get(i).get("Score")) {
                for (Map player : list) {
                    if (String.valueOf(nums.get(i)).equals(player.get("Score"))) {
                        Collections.swap(list, i, list.indexOf(player));
                    }
                }
            }
        }
    }
}
