package elo;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;

import java.util.ArrayList;
import java.util.Map;

public class Store {
    String at;
    ArrayList<String> itemIDs;
    ArrayList<String> itemNames = new ArrayList<>();
    String et;
    String uID;


    public Store(String accessToken, String entitlementToken, String userID) {
        this.at = accessToken;
        this.et = entitlementToken;
        this.uID = userID;
        getStore();
        getLatestContent();
    }

    private void getStore() {
        String url = String.format("https://pd.na.a.pvp.net/store/v2/storefront/%s", uID);
        HttpResponse storeResponse = Unirest.get(url)
                .header("Authorization", at)
                .header("X-Riot-Entitlements-JWT", et)
                .asJson();


        Gson gson = new Gson();
        Map<String, LinkedTreeMap> json = new LinkedTreeMap<>();
        json = gson.fromJson(storeResponse.getBody().toString(), Map.class);
        itemIDs = (ArrayList<String>) json.get("SkinsPanelLayout").get("SingleItemOffers");
    }

    private void getLatestContent() {
        String url = String.format("https://shared.na.a.pvp.net/content-service/v2/content");
        HttpResponse contentResponse = Unirest.get(url)
                .header("Authorization", at)
                .header("X-Riot-Entitlements-JWT", et)
                .header("X-Riot-ClientPlatform", "ew0KCSJwbGF0Zm9ybVR5cGUiOiAiUEMiLA0KCSJwbGF0Zm9ybU9TIjogIldpbmRvd3MiLA0KCSJwbGF0Zm9ybU9TVmVyc2lvbiI6ICIxMC4wLjE5MDQyLjEuMjU2LjY0Yml0IiwNCgkicGxhdGZvcm1DaGlwc2V0IjogIlVua25vd24iDQp9")
                .header("X-Riot-ClientVersion", "release-02.02-shipping-8-517074")
                .asJson();

        Gson gson = new Gson();
        Map<String, ArrayList> json = new LinkedTreeMap<>();
        json = gson.fromJson(contentResponse.getBody().toString(), Map.class);
        ArrayList<LinkedTreeMap> weapons = json.get("SkinLevels");
        convertIDs(weapons);
    }

    private void convertIDs(ArrayList<LinkedTreeMap> weapons) {
        for (int i = 0; i < itemIDs.size(); i++) {
            for (LinkedTreeMap map : weapons) {
                if (map.get("ID").equals(itemIDs.get(i).toUpperCase())) {
                    itemNames.add((String) map.get("Name"));
                }
            }
        }
    }

    public ArrayList<String> getItemNames() {
        return itemNames;
    }
}
