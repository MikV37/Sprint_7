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

public class CourierApiTest {

    private static final String BASE_URI = "https://qa-scooter.praktikum-services.ru";
    private Courier courier;
    private Integer courierId;

    @Before
    public void setUp() {
        RestAssured.baseURI = BASE_URI;
        courier = new Courier(
                "ninja-" + System.currentTimeMillis(),
                "1234",
                "Saske"
        );
    }

    @After
    public void tearDown() {
        if (courierId != null) {
            deleteCourierStep(courierId);
        }
    }

    @Step("Создание курьера с логином '{courier.login}'")
    public ValidatableResponse createCourierStep(Courier courier) {
        return given().contentType(ContentType.JSON).body(courier).when().post("/api/v1/courier").then();
    }

    @Step("Авторизация курьера с логином '{login.login}'")
    public ValidatableResponse loginCourierStep(Login login) {
        return given().contentType(ContentType.JSON).body(login).when().post("/api/v1/courier/login").then();
    }

    @Step("Удаление курьера по ID: {courierId}")
    public void deleteCourierStep(int courierId) {
        given().delete("/api/v1/courier/{id}", courierId).then().statusCode(200);
    }

    @Test
    @DisplayName("Успешное создание курьера")
    @Description("Проверка, что API позволяет создать курьера с валидными данными")
    public void courierCanBeCreatedWithValidData() {
        ValidatableResponse createResponse = createCourierStep(courier);
        createResponse.assertThat().statusCode(201).and().body("ok", is(true));

        ValidatableResponse loginResponse = loginCourierStep(new Login(courier.getLogin(), courier.getPassword()));
        courierId = loginResponse.extract().path("id");
    }

    @Test
    @DisplayName("Создание дубликата курьера")
    @Description("Проверка, что API возвращает ошибку при попытке создать курьера с существующим логином")
    public void cannotCreateTwoCouriersWithSameLogin() {
        createCourierStep(courier);
        ValidatableResponse loginResponse = loginCourierStep(new Login(courier.getLogin(), courier.getPassword()));
        courierId = loginResponse.extract().path("id");

        ValidatableResponse duplicateResponse = createCourierStep(courier);
        duplicateResponse.assertThat().statusCode(409).and().body("message", equalTo("Этот логин уже используется. Попробуйте другой."));
    }

    @Test
    @DisplayName("Создание курьера без обязательного поля (пароль)")
    @Description("Проверка, что API вернет ошибку, если не передать пароль")
    public void courierCannotBeCreatedWithoutPassword() {
        courier.setPassword(null);
        ValidatableResponse response = createCourierStep(courier);
        response.assertThat().statusCode(400).body("message", equalTo("Недостаточно данных для создания учетной записи"));
    }

    @Test
    @DisplayName("Создание курьера без обязательного поля (логин)")
    @Description("Проверка, что API вернет ошибку, если не передать логин")
    public void courierCannotBeCreatedWithoutLogin() {
        courier.setLogin(null);
        ValidatableResponse response = createCourierStep(courier);
        response.assertThat()
                .statusCode(400)
                .body("message", equalTo("Недостаточно данных для создания учетной записи"));
    }
}

