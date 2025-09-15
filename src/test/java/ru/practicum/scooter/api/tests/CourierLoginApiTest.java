import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.Matchers.*;

public class CourierLoginApiTest {

    private static final String BASE_URI = "https://qa-scooter.praktikum-services.ru/";

    @Before
    public void setUp() {
        RestAssured.baseURI = BASE_URI;
    }

    // 1) Успешный логин возвращает id (код 200 и поле id)
    @Test
    public void testSuccessfulLoginReturnsId() {
        String login = "user_success_" + System.nanoTime();
        String password = "1234";

        // регистрация курьера
        String registerPayload = "{ \"login\": \"" + login + "\", \"password\": \"" + password + "\", \"firstName\": \"Test\" }";
        RestAssured.given()
                .contentType(ContentType.JSON)
                .body(registerPayload)
                .when()
                .post("/api/v1/courier")
                .then()
                .statusCode(201);

        // логин
        String loginPayload = "{ \"login\": \"" + login + "\", \"password\": \"" + password + "\" }";
        RestAssured.given()
                .contentType(ContentType.JSON)
                .body(loginPayload)
                .when()
                .post("/api/v1/courier/login")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("id", greaterThan(0));
    }

    // 2) Запрос без поля password: 400
    @Test
    public void testLoginMissingPasswordReturnsBadRequest() {
        String login = "user_missing_pwd_" + System.nanoTime();
        String password = "pwd";

        // регистрация
        String registerPayload = "{ \"login\": \"" + login + "\", \"password\": \"" + password + "\", \"firstName\": \"Test\" }";
        RestAssured.given()
                .contentType(ContentType.JSON)
                .body(registerPayload)
                .when()
                .post("/api/v1/courier")
                .then()
                .statusCode(201);

        // без password
        String payload = "{ \"login\": \"" + login + "\" }";
        RestAssured.given()
                .contentType(ContentType.JSON)
                .body(payload)
                .when()
                .post("/api/v1/courier/login")
                .then()
                .statusCode(400)
                .contentType(ContentType.JSON)
                .body("message", equalTo("Недостаточно данных для входа"));
    }

    // 3) Запрос без поля login: 400
    @Test
    public void testLoginMissingLoginReturnsBadRequest() {
        String password = "pwd";

        // без login
        String payload = "{ \"password\": \"" + password + "\" }";
        RestAssured.given()
                .contentType(ContentType.JSON)
                .body(payload)
                .when()
                .post("/api/v1/courier/login")
                .then()
                .statusCode(400)
                .contentType(ContentType.JSON)
                .body("message", equalTo("Недостаточно данных для входа"));
    }

    // 4) Запрос с несуществующей учетной записью: 404 и сообщение
    @Test
    public void testLoginNotFoundReturnsNotFound() {
        String login = "nonexistent_" + System.nanoTime();
        String password = "doesnotexist";

        String payload = "{ \"login\": \"" + login + "\", \"password\": \"" + password + "\" }";
        RestAssured.given()
                .contentType(ContentType.JSON)
                .body(payload)
                .when()
                .post("/api/v1/courier/login")
                .then()
                .statusCode(404)
                .contentType(ContentType.JSON)
                .body("message", equalTo("Учетная запись не найдена"));
    }
}