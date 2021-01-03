package elo;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import kong.unirest.Cookies;
import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;

public class eloHistory {
    public static void main(String[] args) {
        String jsonString = "{\"client_id\":\"play-valorant-web-prod\",\"nonce\":\"1\",\"redirect_uri\":\"https://beta.playvalorant.com/opt_in" + "\",\"response_type\":\"token id_token\",\"scope\":\"account openid\"}";
        JsonObject json = JsonParser.parseString(jsonString).getAsJsonObject();
        System.out.println(json.toString());

        HttpResponse<JsonNode> cookieRequest = Unirest.post("https://auth.riotgames.com/api/v1/authorization")
                .body(json).asJson();
        System.out.println(cookieRequest.getStatusText());
        Cookies cookies = cookieRequest.getCookies();
//        System.out.println(cookies.toString());

//        MultipartBody accessTokenRequest = Unirest.put("https://auth.riotgames.com/api/v1/authorization")
//                .field("type", "auth")
//                .field("username", "f0fsf0fs")
//                .field("password", "0a8O93ImH$EC")
//                .cookie(cookies);
//        System.out.println(accessTokenRequest.asJson().getStatus());
    }
}
