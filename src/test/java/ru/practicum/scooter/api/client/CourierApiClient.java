package ru.practicum.scooter.api.client;

import io.qameta.allure.Step;
import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;
import ru.practicum.scooter.api.pojo.Courier;
import ru.practicum.scooter.api.pojo.Login;
import static ru.practicum.scooter.api.constants.Endpoints.*;
import static io.restassured.RestAssured.given;

public class CourierApiClient {
    @Step("Создание курьера")
    public ValidatableResponse create(Courier courier) {
        return given()
                .baseUri(BASE_URI)
                .contentType(ContentType.JSON)
                .body(courier)
                .when()
                .post(COURIER_PATH)
                .then();
    }


    @Step("Авторизация курьера")
    public ValidatableResponse login(Login login) {
        return given()
                .baseUri(BASE_URI)
                .contentType(ContentType.JSON)
                .body(login)
                .when()
                .post(COURIER_LOGIN_PATH)
                .then();
    }

    @Step("Удаление курьера по ID: {courierId}")
    public ValidatableResponse delete(Integer courierId) {
        return given()
                .baseUri(BASE_URI)
                .delete(COURIER_DELETE_PATH, courierId)
                .then();
    }

}