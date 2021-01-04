package elo;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import kong.unirest.*;

import java.util.Map;

public class Authentication {
    static String getAccessToken(Cookies cookies, String username, String password) {
        String[] keys = {"type", "username", "password"};
        String[] values = {"auth", username, password};

        HttpResponse accessTokenRequest = Unirest.put("https://auth.riotgames.com/api/v1/authorization")
                .header("Content-type", "application/json")
                .body(getJson(keys, values))
                .cookie(cookies)
                .asJson();
        String accessTokenString = accessTokenRequest.getBody().toString();

        Gson gson = new Gson();

        return "Bearer " + accessTokenString.substring(accessTokenString.indexOf("access_token=") + 13, accessTokenString.indexOf("&scope="));
    }

    static Cookies getCookies() {
        Unirest.config().enableCookieManagement(false);
        String[] keys = {"client_id", "nonce", "redirect_uri", "response_type", "scope"};
        String[] values = {"play-valorant-web-prod", "1", "https://beta.playvalorant.com/opt_in", "token id_token", "account openid"};
        HttpResponse<JsonNode> cookieRequest = Unirest.post("https://auth.riotgames.com/api/v1/authorization")
                .body(getJson(keys, values)).header("Content-type", "application/json").asJson();
        return cookieRequest.getCookies();
    }

    static String getEntitlement(String accessToken) {
        HttpResponse entitlementRequest = Unirest.post("https://entitlements.auth.riotgames.com/api/token/v1")
                .header("Content-type", "application/json")
                .header("Authorization", accessToken)
                .body("{}")
                .asJson();

        Gson gson = new Gson();
        Map<String, String> json = gson.fromJson(entitlementRequest.getBody().toString(), Map.class);

        return json.get("entitlements_token");
    }

    static String getUserID(String accessToken) {
        HttpResponse userIDRequest = Unirest.post("https://auth.riotgames.com/userinfo")
                .header("Content-type", "application/json")
                .header("Authorization", accessToken)
                .body("{}")
                .asJson();

        Gson gson = new Gson();
        Map<String, String> json = gson.fromJson(userIDRequest.getBody().toString(), Map.class);

        return json.get("sub");
    }

    private static JsonObject getJson(String[] keys, String[] values)  {

        StringBuilder jsonStringBuilder = new StringBuilder("{");
        for (int i = 0; i < keys.length; i++) {
            jsonStringBuilder.append(i != keys.length - 1 ? "\"" + keys[i] + "\":" + "\"" + values[i] + "\"," : "\"" + keys[i] + "\":" + "\"" + values[i] + "\"");
        }
        String jsonString = jsonStringBuilder.toString();
        jsonString += "}";
//        System.out.println(jsonString);
        return new JsonParser().parse(jsonString).getAsJsonObject();
    }
}
