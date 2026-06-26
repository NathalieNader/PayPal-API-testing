package BaseTest;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.testng.annotations.BeforeClass;

import static io.restassured.RestAssured.given;

public class BaseTest {

    protected static final String BASE_URL = "https://api-m.sandbox.paypal.com";

    protected static final String CLIENT_ID = "AUv8rrc_P-EbP2E0mpb49BV7rFt3Usr-vdUZO8VGOnjRehGHBXkSzchr37SYF2GNdQFYSp72jh5QUhzG";
    protected static final String CLIENT_SECRET = "EMnAWe06ioGtouJs7gLYT9chK9-2jJ--7MKRXpI8FesmY_2Kp-d_7aCqff7M9moEJBvuXoBO4clKtY0v";

    protected String accessToken;

    @BeforeClass
    public void setUp() {
        RestAssured.baseURI = BASE_URL;
        accessToken = generateAccessToken();
    }

    private String generateAccessToken() {

        Response response =
                given()
                        .auth()
                        .preemptive()
                        .basic(CLIENT_ID, CLIENT_SECRET)
                        .contentType("application/x-www-form-urlencoded")
                        .formParam("grant_type", "client_credentials")
                        .when()
                        .post("/v1/oauth2/token");

        response.then().statusCode(200);

        return response.jsonPath().getString("access_token");
    }
}