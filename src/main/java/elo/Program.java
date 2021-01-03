package elo;

import kong.unirest.Cookies;

public class Program {
    public static  void main(String[] args) {
        Cookies cookies = Authentication.getCookies();
        String accessToken = "Bearer " + Authentication.getAccessToken(cookies, "", "");
        String entitlementToken = Authentication.getEntitlement(accessToken);
        String userID = Authentication.getUserID(accessToken);


    }
}
