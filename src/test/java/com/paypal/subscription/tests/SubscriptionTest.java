package com.paypal.subscription.tests;

import com.google.gson.JsonObject;
import com.paypal.subscription.config.Env;
import com.paypal.subscription.services.Subscription;
import com.paypal.subscription.utils.DataUtils;
import com.paypal.subscription.utils.LogsUtils;

import io.restassured.response.Response;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;


import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;


public class SubscriptionTest {

    private JsonObject subscriptionData ;

    @Test
    public void assertCreateSubscriptionSuccessfully(){
        Response response = new Subscription().createSubscription(subscriptionData);
        response
                .then().statusCode(201)
                .body("status",equalTo("APPROVAL_PENDING"));

        LogsUtils.info("Creating Subscription successfully ");
    }

    @Test
    public void assertRetrieveSubscriptionDetailsSuccessfully(){
        Response response = new Subscription().getSubscription(Env.get("ACTIVE_SUBSCRIPTION_ID"));
        response
                .then().statusCode(200)
                .body("status",equalTo("APPROVAL_PENDING"))
                .body("id",equalTo(Env.get("ACTIVE_SUBSCRIPTION_ID")));

    }
    @Test
    public void assertRetrieveSubscriptionFailedWithInvalidID(){
        Response response = new Subscription().getSubscription("invalid_id");
        response
                .then().statusCode(400)
                .body("name",equalTo("INVALID_REQUEST"));

    }

    @Test
    public void assertSubscriptionFailedWithMissingPlanID(){
        subscriptionData.remove("plan_id");
        Response response = new Subscription().createSubscription(subscriptionData);
        response
                .then().statusCode(400)
                .body("name",equalTo("INVALID_REQUEST"));

    }

    @Test
    public void assertSubscriptionFailedWithNonExistPlanID(){
        subscriptionData.addProperty("plan_id","not_exist");
        Response response = new Subscription()
                .createSubscription(subscriptionData);
        response
                .then().statusCode(400)
                .body("name",equalTo("INVALID_REQUEST"));

    }

    @Test
    public void assertSubscriptionRejectedWithInvalidToken(){
        Response response = new Subscription().createSubscription(subscriptionData,"badtoken");
        response
                .then().statusCode(401)
                .body("error",equalTo("invalid_token"));
    }

    @Test
    public void assertSubscriptionRejectedWithMissingAuthToken(){
        Response response = new Subscription().createSubscription(subscriptionData,null);
        response
                .then().statusCode(401)
                .body("name",equalTo("AUTHENTICATION_FAILURE"));
    }

    @BeforeClass
    public void setSubscriptionDependencies(){
        // create product and retrieve product id
        JsonObject plan = DataUtils.getJsonObject("create-plan");
        Subscription subInstance = new Subscription();
        plan.addProperty("product_id", subInstance.createProduct());

        // create plan and retrieve plan id
        JsonObject subscriptionJsonData = DataUtils.getJsonObject("create-subscription");
        subscriptionJsonData.addProperty("plan_id", subInstance.createPlan(plan));
        this.subscriptionData = subscriptionJsonData ;
    }
}
