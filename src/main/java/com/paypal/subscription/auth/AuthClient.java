package com.paypal.subscription.auth;

import com.paypal.subscription.config.Env;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;


public class AuthClient {

    private String accessToken ;
    private final String authEndpoint = "oauth2/token";
    private final String baseUrl = Env.get("BASE_URL");
    private final String clientId = Env.get("CLIENT_ID");
    private final String clientSecret = Env.get("CLIENT_SECRET");

    public Response fetchAccessToken(String clientId , String clientSecret){

       return given().
                auth().preemptive().basic(clientId, clientSecret).
                contentType(ContentType.URLENC) .       // body is form-urlencoded
                accept(ContentType.JSON)   .            // we want JSON back
                formParam("grant_type", "client_credentials").
            when().
                post(baseUrl+authEndpoint).
            then().
                extract().response();

    }
    // override fetch access token function to use it in the positive test case as we don't need to pass any data
    public Response fetchAccessToken(){
        return fetchAccessToken(clientId,clientSecret);
    }

    public AuthClient setAccessToken(){
        accessToken = fetchAccessToken(clientId,clientSecret).path("access_token");
        return this ;
    }

    public String getAccessToken(){
        return accessToken;
    }



}
