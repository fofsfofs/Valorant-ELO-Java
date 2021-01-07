package elo;

import com.google.gson.internal.LinkedTreeMap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Rank {

    enum Ranks {
        IRON1(0, "Iron 1"),
        IRON2(1, "Iron 2"),
        IRON3(2, "Iron 3"),
        BROONZE1(3, "Bronze 1"),
        BRONZE2(4, "Bronze 2"),
        BRONZE3(5, "Bronze 3"),
        SILVER1(6, "Silver 1"),
        SILVER2(7, "Silver 2"),
        SILVER3(8, "Silver 3"),
        GOLD1(9, "Gold 1"),
        GOLD2(10, "Gold 2"),
        GOLD3(11, "Gold 3"),
        PLATINUM1(12, "Platinum 1"),
        PLATINUM2(13, "Platinum 2"),
        PLATINUM3(14, "Platinum 3"),
        DIAMOND1(15, "Diamond 1"),
        DIAMOND2(16, "Diamond 2"),
        DIAMOND3(17, "Diamond 3"),
        IMMORTAL1(18, "Immortal 1"),
        IMMORTAL2(19, "Immortal 2"),
        IMMORTAL3(20, "Immortal 3"),
        RADIANT(21, "Radiant");


        private int minELO;
        private String name;

        Ranks(int minELO, String name) {
            this.minELO = minELO;
            this.name = name;
        }
    }

    private Matches matches;

    public Rank(Matches matches) {
        this.matches = matches;
    }

    public int getCurrentELO() {
        LinkedTreeMap lastMatch = (LinkedTreeMap) matches.loadHistory().get(0);
        return getElO(lastMatch);
    }

    public int getCurrentRP() {
        LinkedTreeMap lastMatch = (LinkedTreeMap) matches.loadHistory().get(0);
        return getRP(lastMatch);
    }

    private int getElO(LinkedTreeMap match) {
        return ((Double) match.get("TierAfterUpdate")).intValue() * 100 - 300 + getRP(match);
    }

    public List getELOHistory() {
        List<Integer> eloHistory = new ArrayList<>();
        ArrayList<LinkedTreeMap> lastestMatches = matches.loadHistory();

        for (LinkedTreeMap match : lastestMatches) {
            eloHistory.add(getElO(match));
        }
        Collections.reverse(eloHistory);
        return eloHistory;
    }

    public List getGainLoss() {
        List<Integer> gainLoss = new ArrayList<>();
        ArrayList<LinkedTreeMap> lastestMatches = matches.loadHistory();

        for (int i = 0; i < lastestMatches.size(); i++) {
            if (i > 0) {
                gainLoss.add(getElO(lastestMatches.get(i - 1)) - getElO(lastestMatches.get(i)));
            }

        }
        Collections.reverse(gainLoss);
        return gainLoss;
    }

    public String getCurrentRank() {
        for (Ranks r : Ranks.values()) {
            if ((getCurrentELO() / 100) == r.minELO) {
                return r.name;
            }
        }
        return "";
    }

    public String getRank(int elo) {
        for (Ranks r : Ranks.values()) {
            if ((elo / 100) == r.minELO) {
                return r.name;
            }
        }
        return "";
    }

    private int getRP(LinkedTreeMap match) {
        return ((Double) match.get("TierProgressAfterUpdate")).intValue();
    }
}
