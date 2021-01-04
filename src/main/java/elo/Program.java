package elo;

import kong.unirest.Cookies;

public class Program {
    public static  void main(String[] args) {
        Cookies cookies = Authentication.getCookies();
        String accessToken = "Bearer " + Authentication.getAccessToken(cookies, "f0fsf0fs", "0a8O93ImH$EC");
        String entitlementToken = Authentication.getEntitlement(accessToken);
        String userID = Authentication.getUserID(accessToken);

        Matches m = new Matches(accessToken, entitlementToken, userID);
        Rank rank = new Rank(m);
    }
}
