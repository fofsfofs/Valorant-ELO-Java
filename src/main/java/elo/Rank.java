package elo;

import com.google.gson.internal.LinkedTreeMap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Rank {

    enum Ranks {
        IRON1(3, "Iron 1"),
        IRON2(4, "Iron 2"),
        IRON3(5, "Iron 3"),
        BRONZE1(6, "Bronze 1"),
        BRONZE2(7, "Bronze 2"),
        BRONZE3(8, "Bronze 3"),
        SILVER1(9, "Silver 1"),
        SILVER2(10, "Silver 2"),
        SILVER3(11, "Silver 3"),
        GOLD1(12, "Gold 1"),
        GOLD2(13, "Gold 2"),
        GOLD3(14, "Gold 3"),
        PLATINUM1(15, "Platinum 1"),
        PLATINUM2(16, "Platinum 2"),
        PLATINUM3(17, "Platinum 3"),
        DIAMOND1(18, "Diamond 1"),
        DIAMOND2(19, "Diamond 2"),
        DIAMOND3(20, "Diamond 3"),
        IMMORTAL(21, "Immortal"),
        RADIANT(24, "Radiant");


        private int minELO;
        private String name;

        Ranks(int minELO, String name) {
            this.minELO = minELO;
            this.name = name;
        }
    }

    enum Maps {
        Ascent("Ascent", "Ascent"),
        Bind("Duality", "Bind"),
        Icebox("Port", "Icebox"),
        Haven("Triad", "Haven"),
        Split("Bonsai", "Split");

        private String cn;
        private String rn;

        Maps(String codeName, String realName) {
            this.cn = codeName;
            this.rn = realName;
        }

    }

    private Matches matches;

    public Rank(Matches matches) {
        this.matches = matches;
    }

    public List getMaps() {
        ArrayList<LinkedTreeMap> latestMatches = matches.loadHistory();
        List<String> maps = new ArrayList<>();
        for (LinkedTreeMap match : latestMatches) {
            String mapID = (String) match.get("MapID");
            String mapStr = mapID.substring(mapID.lastIndexOf("/") + 1);

            for (Maps m : Maps.values()) {
                if (m.cn.equals(mapStr)) {
                    maps.add(m.rn);
                }
            }
        }
        Collections.reverse(maps);
        return maps;
    }

    public int getCurrentELO() {
        LinkedTreeMap lastMatch = (LinkedTreeMap) matches.loadHistory().get(0);
        return getElO(lastMatch);
    }

    public int getCurrentRR() {
        LinkedTreeMap lastMatch = (LinkedTreeMap) matches.loadHistory().get(0);
        return getRR(lastMatch);
    }

    private int getElO(LinkedTreeMap match) {
        return ((Double) match.get("TierAfterUpdate")).intValue() * 100 + getRR(match);
    }

    public List getELOHistory() {
        List<Integer> eloHistory = new ArrayList<>();
        ArrayList<LinkedTreeMap> latestMatches = matches.loadHistory();

        for (LinkedTreeMap match : latestMatches) {
            eloHistory.add(getElO(match));
        }
        Collections.reverse(eloHistory);
        return eloHistory;
    }

    public List getGainLoss() {
        List<Integer> gainLoss = new ArrayList<>();
        ArrayList<LinkedTreeMap> latestMatches = matches.loadHistory();

        for (int i = 0; i < latestMatches.size(); i++) {
            if (i > 0) {
                gainLoss.add(getElO(latestMatches.get(i - 1)) - getElO(latestMatches.get(i)));
            }

        }
        Collections.reverse(gainLoss);
        return gainLoss;
    }

    public String getCurrentRank() {
        return getRank(getCurrentELO());
    }

    public String getRank(int elo) {
        for (Ranks r : Ranks.values()) {
            if ((elo / 100) == r.minELO) {
                return r.name;
            } else if ((elo / 100) >= 21 && (elo / 100) < 24) {
                return "Immortal";
            } else if ((elo / 100) >= 24) {
                return "Radiant";
            }
        }
        return "";
    }

    private int getRR(LinkedTreeMap match) {
        return ((Double) match.get("RankedRatingAfterUpdate")).intValue();
    }
}
