package ru.practicum.scooter.api.tests;

import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.practicum.scooter.api.pojo.Courier;
import ru.practicum.scooter.api.pojo.Login;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class CourierLoginApiTest {

    private static final String BASE_URI = "https://qa-scooter.praktikum-services.ru";
    private Courier courier;
    private Integer courierId;

    @Before
    public void setUp() {
        RestAssured.baseURI = BASE_URI;
        courier = new Courier(
                "user_" + System.currentTimeMillis(),
                "pwd_" + System.currentTimeMillis(),
                "Test"
        );
    }

    @After
    public void tearDown() {
        if (courierId != null) {
            deleteCourierStep(courierId);
        }
    }

    @Step("Создание курьера для теста")
    public void createCourierStep(Courier courier) {
        given()
                .contentType(ContentType.JSON)
                .body(courier)
                .when()
                .post("/api/v1/courier")
                .then()
                .statusCode(201);
    }

    @Step("Авторизация курьера с логином '{login.login}' и паролем '{login.password}'")
    public ValidatableResponse loginCourierStep(Login login) {
        return given()
                .contentType(ContentType.JSON)
                .body(login)
                .when()
                .post("/api/v1/courier/login")
                .then();
    }

    @Step("Удаление курьера по ID: {courierId}")
    public void deleteCourierStep(int courierId) {
        given()
                .delete("/api/v1/courier/{id}", courierId)
                .then()
                .statusCode(200);
    }

    @Test
    @DisplayName("Успешная авторизация курьера")
    @Description("Проверка, что при успешной авторизации возвращается ID курьера")
    public void successfulLoginReturnsId() {
        createCourierStep(courier);
        Login loginData = new Login(courier.getLogin(), courier.getPassword());
        ValidatableResponse loginResponse = loginCourierStep(loginData);
        loginResponse.statusCode(200).body("id", notNullValue());
        courierId = loginResponse.extract().path("id");
    }

    @Test
    @DisplayName("Авторизация без пароля")
    @Description("Проверка, что API возвращает ошибку 400 при попытке авторизации без пароля")
    public void loginMissingPasswordReturnsBadRequest() {
        createCourierStep(courier);

        Login loginData = new Login(courier.getLogin(), null);
        ValidatableResponse loginResponse = loginCourierStep(loginData);

        loginResponse.statusCode(400).body("message", equalTo("Недостаточно данных для входа"));

        courierId = loginCourierStep(new Login(courier.getLogin(), courier.getPassword()))
                .extract().path("id");
    }

    @Test
    @DisplayName("Авторизация с несуществующими данными")
    @Description("Проверка, что API возвращает ошибку 404 при попытке авторизации с несуществующим логином/паролем")
    public void loginWithNonexistentCredentialsReturnsNotFound() {
        Login loginData = new Login("nonexistent_" + System.nanoTime(), "password");
        ValidatableResponse loginResponse = loginCourierStep(loginData);

        loginResponse.statusCode(404).body("message", equalTo("Учетная запись не найдена"));
    }
}
