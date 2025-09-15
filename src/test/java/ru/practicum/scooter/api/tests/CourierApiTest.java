package courier.api.tests;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.Matchers.*;

public class CourierApiTest {

    // Базовый URL тестовой среды. Измените на вашу среду, если требуется.
    private static final String BASE_URI = "https://qa-scooter.praktikum-services.ru/";

    @Before
    public void setUp() {
        RestAssured.baseURI = BASE_URI;
        // При необходимости можно установить базовый путь:
        // RestAssured.basePath = "/api/v1";
    }

    @After
    public void tearDown() {
        // Очистка тестовых данных после каждого теста, если API предоставляет удаление тестовых курьеров.
        // Например, можно вызвать DELETE /api/v1/courier/{login} или аналогичный эндпойнт.
        // Если такого эндпойнта нет, можно оставить пустым или реализовать очистку на уровне окружения.
    }

    // Вспомогательный метод генерации уникального логина
    private String uniqueLogin(String base) {
        // Используем nanoTime для большей уникальности
        return base + "_" + System.nanoTime();
    }

    @Test
    public void testSuccessfulCourierCreation_IsIndependent() {
        String login = uniqueLogin("ninja_ok");
        String payload = "{ \"login\": \"" + login + "\", \"password\": \"1234\", \"firstName\": \"saske\" }";

        RestAssured.given()
                .contentType(ContentType.JSON)
                .body(payload)
                .when()
                .post("/api/v1/courier")
                .then()
                .statusCode(201)
                .contentType(ContentType.JSON)
                .body("ok", is(true));
    }

    @Test
    public void testDuplicateCourierCreation_IsIndependent() {
        String login = uniqueLogin("ninja_dup");
        String payload = "{ \"login\": \"" + login + "\", \"password\": \"1234\", \"firstName\": \"saske\" }";

        // Первый успешный вызов в этом тесте
        RestAssured.given()
                .contentType(ContentType.JSON)
                .body(payload)
                .when()
                .post("/api/v1/courier")
                .then()
                .statusCode(201)
                .contentType(ContentType.JSON)
                .body("ok", is(true));

        // Второй вызов с тем же логином должен вернуть ошибку
        RestAssured.given()
                .contentType(ContentType.JSON)
                .body(payload)
                .when()
                .post("/api/v1/courier")
                .then()
                .statusCode(409)
                .contentType(ContentType.JSON)
                .body("message", equalTo("Этот логин уже используется"));
    }

    @Test
    public void testMissingRequiredFields_IsIndependent() {
        String login = uniqueLogin("ninja_missing");
        String payload = "{ \"login\": \"" + login + "\", \"firstName\": \"saske\" }";

        RestAssured.given()
                .contentType(ContentType.JSON)
                .body(payload)
                .when()
                .post("/api/v1/courier")
                .then()
                .statusCode(400)
                .contentType(ContentType.JSON)
                .body("message", containsString("Недостаточно данных"));
    }

    @Test
    public void testEmptyFieldValidation_IsIndependent() {
        // Обязательное поле пустое (логин пустой)
        String payload = "{ \"login\": \"\", \"password\": \"1234\", \"firstName\": \"saske\" }";

        RestAssured.given()
                .contentType(ContentType.JSON)
                .body(payload)
                .when()
                .post("/api/v1/courier")
                .then()
                .statusCode(400)
                .contentType(ContentType.JSON)
                .body("message", containsString("Недостаточно данных"));
    }
}