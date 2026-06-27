package com.paypal.subscription.services;

import com.google.gson.JsonObject;
import com.paypal.subscription.auth.AuthClient;
import com.paypal.subscription.config.Env;
import com.paypal.subscription.utils.DataUtils;
import com.paypal.subscription.utils.LogsUtils;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class Subscription {

    private final String createProductEndpoint = "catalogs/products";
    private final String createPlanEndpoint = "billing/plans";
    private final String createSubscriptionEndpoint = "billing/subscriptions" ;
    private final String getSubscriptionEndpoint  = "billing/subscriptions/";
    // dependency
    public String createProduct() {
        String accessToken = new AuthClient().setAccessToken().getAccessToken();

        Response response = given()
                .auth().oauth2(accessToken)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(DataUtils.getJsonObject("create-product"))
                .when()
                .post(Env.get("BASE_URL") + createProductEndpoint);

        response.then().statusCode(201);
        String productId = response.jsonPath().getString("id");

        LogsUtils.info("Created product: " + productId);

        return productId;
    }

    // dependency
    public String createPlan(JsonObject planData) {
        String accessToken = new AuthClient().setAccessToken().getAccessToken();
        Response response = given()
                .auth().oauth2(accessToken)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(planData)
                .when()
                .post(Env.get("BASE_URL") + createPlanEndpoint);

        String planId = response.jsonPath().getString("id");

        return  planId;


    }

    public Response createSubscription(JsonObject subscriptionData){
        String accessToken = new AuthClient().setAccessToken().getAccessToken();
       return createSubscription(subscriptionData,accessToken);
    }

    public Response createSubscription(JsonObject subscriptionData , String accessToken){
        RequestSpecification req = given().
                contentType(ContentType.JSON).
                accept(ContentType.JSON).
                body(subscriptionData);
        if(accessToken != null && !accessToken.isBlank()){
            req.auth().oauth2(accessToken);

        }
        return req.when().post(Env.get("BASE_URL") + createSubscriptionEndpoint);

    }

    public Response getSubscription(String SubscriptionId){
        String accessToken = new AuthClient().setAccessToken().getAccessToken();
        Response response = given().
                auth().oauth2(accessToken).
                contentType(ContentType.JSON).
                accept(ContentType.JSON).
        when().get(Env.get("BASE_URL")+getSubscriptionEndpoint+SubscriptionId);

        return  response;
    }

}
