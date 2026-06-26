package test;
import org.testng.annotations.Test;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

public class AuthenticationTests extends BaseTest {

    @Test(priority = 1)
    public void generateTokenWithMinimalRequest() {

        given()
                .auth().preemptive()
                .basic(CLIENT_ID, CLIENT_SECRET)
                .contentType("application/x-www-form-urlencoded")
                .formParam("grant_type", "client_credentials")

                .when()
                .post("/v1/oauth2/token")

                .then()
                .statusCode(200)
                .body("access_token", notNullValue())
                .body("token_type", equalTo("Bearer"));
    }

    @Test(priority = 2)
    public void generateTokenWithDetailedRequest() {

        given()
                .auth().preemptive()
                .basic(CLIENT_ID, CLIENT_SECRET)
                .contentType("application/x-www-form-urlencoded")
                .formParam("grant_type", "client_credentials")
                .formParam("ignoreCache", "true")
                .formParam("return_authn_schemes", "true")
                .formParam("return_client_metadata", "true")
                .formParam("return_unconsented_scopes", "true")

                .when()
                .post("/v1/oauth2/token")

                .then()
                .statusCode(200)
                .body("access_token", notNullValue())
                .body("token_type", equalTo("Bearer"));
    }

    @Test(priority = 3)
    public void unsupportedResponseType() {

        given()
                .auth().preemptive()
                .basic(CLIENT_ID, CLIENT_SECRET)
                .contentType("application/x-www-form-urlencoded")
                .formParam("grant_type", "client_credentials")
                .formParam("response_type", "uat")

                .when()
                .post("/v1/oauth2/token")

                .then()
                .statusCode(400)
                .body("error", equalTo("invalid_response_type"));
    }

    @Test(priority = 4)
    public void missingClientCredentials() {

        given()
                .contentType("application/x-www-form-urlencoded")
                .formParam("grant_type", "client_credentials")

                .when()
                .post("/v1/oauth2/token")

                .then()
                .statusCode(401);
    }

    @Test(priority = 5)
    public void terminateAccessTokenSuccess() {

        String accessToken = given()
                .auth().preemptive()
                .basic(CLIENT_ID, CLIENT_SECRET)
                .contentType("application/x-www-form-urlencoded")
                .formParam("grant_type", "client_credentials")

                .when()
                .post("/v1/oauth2/token")

                .then()
                .statusCode(200)
                .extract()
                .path("access_token");

        given()
                .auth().preemptive()
                .basic(CLIENT_ID, CLIENT_SECRET)
                .contentType("application/x-www-form-urlencoded")
                .formParam("token", accessToken)
                .formParam("token_type_hint", "ACCESS_TOKEN")

                .when()
                .post("/v1/oauth2/token/terminate")

                .then()
                .statusCode(200);
    }

    @Test(priority = 6)
    public void missingAccessToken() {

        given()
                .auth().preemptive()
                .basic(CLIENT_ID, CLIENT_SECRET)
                .contentType("application/x-www-form-urlencoded")
                .formParam("token_type_hint", "ACCESS_TOKEN")

                .when()
                .post("/v1/oauth2/token/terminate")

                .then()
                .statusCode(400);
    }

    @Test(priority = 7)
    public void invalidToken() {

        given()
                .auth().preemptive()
                .basic(CLIENT_ID, CLIENT_SECRET)
                .contentType("application/x-www-form-urlencoded")
                .formParam("token", "INVALID_TOKEN")
                .formParam("token_type_hint", "ACCESS_TOKEN")

                .when()
                .post("/v1/oauth2/token/terminate")

                .then()
                .statusCode(200);
    }

}