package elo;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import kong.unirest.Cookies;
import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;

public class Authentication {
    public static String getAccessToken(Cookies cookies, String username, String password) {
        String[] keys = {"type", "username", "password"};
        String[] values = {"auth", "", ""};

        HttpResponse accessTokenRequest = Unirest.put("https://auth.riotgames.com/api/v1/authorization")
                .header("Content-type", "application/json")
                .body(getJson(keys, values))
                .cookie(cookies)
                .asJson();
        String accessTokenString = accessTokenRequest.getBody().toString();


        return "";
    }

    static Cookies getCookies() {
        String[] keys = {"client_id", "nonce", "redirect_uri", "response_type", "scope"};
        String[] values = {"play-valorant-web-prod", "1", "https://beta.playvalorant.com/opt_in", "token id_token", "account openid"};
        HttpResponse<JsonNode> cookieRequest = Unirest.post("https://auth.riotgames.com/api/v1/authorization")
                .body(getJson(keys, values)).header("Content-type", "application/json").asJson();
//        System.out.println(cookieRequest.getStatusText());
        return cookieRequest.getCookies();
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
