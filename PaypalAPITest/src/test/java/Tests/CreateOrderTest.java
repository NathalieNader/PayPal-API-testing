package Tests;

import BaseTest.BaseTest;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class CreateOrderTest extends BaseTest {

    @Test
    public void createOrder_ValidData() {

        String requestBody = """
        {
            "intent": "CAPTURE",
            "payment_source": {
                "paypal": {
                    "experience_context": {
                        "return_url": "https://developer.paypal.com",
                        "cancel_url": "https://www.bing.com",
                        "user_action": "PAY_NOW"
                    }
                }
            },
            "purchase_units": [
                {
                    "amount": {
                        "currency_code": "USD",
                        "value": "100.00"
                    }
                }
            ]
        }
        """;

        Response response =
                given()
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(ContentType.JSON)
                        .body(requestBody)
                        .when()
                        .post("/v2/checkout/orders");

        response.prettyPrint();

        response.then()
                .statusCode(anyOf(equalTo(200), equalTo(201)))

                .body("id", notNullValue())
                .body("id", instanceOf(String.class))
                .body("id", matchesPattern("^[A-Z0-9]{17}$"))

                .body("status", equalTo("PAYER_ACTION_REQUIRED"))

                .body("payment_source", notNullValue())
                .body("payment_source.paypal", notNullValue())

                .body("links", notNullValue())
                .body("links", hasSize(2))

                .body("links.find { it.rel == 'self' }.href", containsString("/v2/checkout/orders/"))
                .body("links.find { it.rel == 'self' }.href", containsString(response.jsonPath().getString("id")))
                .body("links.find { it.rel == 'self' }.method", equalTo("GET"))

                .body("links.find { it.rel == 'payer-action' }.href", containsString("https://www.sandbox.paypal.com/checkoutnow"))
                .body("links.find { it.rel == 'payer-action' }.href", containsString("token=" + response.jsonPath().getString("id")))
                .body("links.find { it.rel == 'payer-action' }.method", equalTo("GET"))

                .time(lessThan(3000L));

        String orderId = response.jsonPath().getString("id");

        System.out.println("Order ID: " + orderId);
    }

    //invalid Token
    @Test
    public void createOrder_InvalidToken_ShouldReturn401() {

        String requestBody = """
    {
        "intent": "CAPTURE",
        "purchase_units": [
            {
                "amount": {
                    "currency_code": "USD",
                    "value": "100.00"
                }
            }
        ]
    }
    """;

        given()
                .header("Authorization", "Bearer invalid_token")
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/v2/checkout/orders")
                .then()
                .statusCode(401)
                .body("error", notNullValue());
    }

    //Missing intents
    @Test
    public void createOrder_MissingIntent_ShouldReturnError() {

        String requestBody = """
    {
        "purchase_units": [
            {
                "amount": {
                    "currency_code": "USD",
                    "value": "100.00"
                }
            }
        ]
    }
    """;

        given()
                .header("Authorization", "Bearer " + accessToken)
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/v2/checkout/orders")
                .then()
                .statusCode(anyOf(equalTo(400), equalTo(422)))
                .body("name", notNullValue())
                .body("message", notNullValue());
    }

    //Invalid Intent
    @Test
    public void createOrder_InvalidIntent_ShouldReturnError() {

        String requestBody = """
    {
        "intent": "INVALID",
        "purchase_units": [
            {
                "amount": {
                    "currency_code": "USD",
                    "value": "100.00"
                }
            }
        ]
    }
    """;

        given()
                .header("Authorization", "Bearer " + accessToken)
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/v2/checkout/orders")
                .then()
                .statusCode(anyOf(equalTo(400), equalTo(422)))
                .body("name", notNullValue())
                .body("message", notNullValue());
    }

    //Missing Purchase
    @Test
    public void createOrder_MissingPurchaseUnits_ShouldReturnError() {

        String requestBody = """
    {
        "intent": "CAPTURE"
    }
    """;

        given()
                .header("Authorization", "Bearer " + accessToken)
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/v2/checkout/orders")
                .then()
                .statusCode(anyOf(equalTo(400), equalTo(422)))
                .body("name", notNullValue())
                .body("message", notNullValue());
    }

    //Empty purchase Unit
    @Test
    public void createOrder_EmptyPurchaseUnits_ShouldReturnError() {

        String requestBody = """
    {
        "intent": "CAPTURE",
        "purchase_units": []
    }
    """;

        given()
                .header("Authorization", "Bearer " + accessToken)
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/v2/checkout/orders")
                .then()
                .statusCode(anyOf(equalTo(400), equalTo(422)))
                .body("name", notNullValue())
                .body("message", notNullValue());
    }

    //Missing amount
    @Test
    public void createOrder_MissingAmount_ShouldReturnError() {

        String requestBody = """
    {
        "intent": "CAPTURE",
        "purchase_units": [
            {}
        ]
    }
    """;

        given()
                .header("Authorization", "Bearer " + accessToken)
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/v2/checkout/orders")
                .then()
                .statusCode(anyOf(equalTo(400), equalTo(422)))
                .body("name", notNullValue())
                .body("message", notNullValue());
    }

    //Missing Code
    @Test
    public void createOrder_MissingCurrencyCode_ShouldReturnError() {

        String requestBody = """
    {
        "intent": "CAPTURE",
        "purchase_units": [
            {
                "amount": {
                    "value": "100.00"
                }
            }
        ]
    }
    """;

        given()
                .header("Authorization", "Bearer " + accessToken)
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/v2/checkout/orders")
                .then()
                .statusCode(anyOf(equalTo(400), equalTo(422)))
                .body("name", notNullValue())
                .body("message", notNullValue());
    }


}
