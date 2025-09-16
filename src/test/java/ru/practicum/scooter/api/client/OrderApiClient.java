package ru.practicum.scooter.api.client;

import io.qameta.allure.Step;
import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;
import ru.practicum.scooter.api.pojo.Order;

import static io.restassured.RestAssured.given;
import static ru.practicum.scooter.api.constants.Endpoints.*;

public class OrderApiClient {

    @Step("Создание заказа")
    public ValidatableResponse create(Order order) {
        return given()
                .baseUri(BASE_URI)
                .contentType(ContentType.JSON)
                .body(order)
                .when()
                .post(ORDERS_PATH)
                .then();
    }

    @Step("Отмена заказа по трек-номеру: {track}")
    public ValidatableResponse cancel(int track) {
        return given()
                .baseUri(BASE_URI)
                .queryParam("track", track)
                .when()
                .put(ORDER_CANCEL_PATH)
                .then();
    }

    @Step("Получение списка заказов")
    public ValidatableResponse getOrderList() {
        return given()
                .baseUri(BASE_URI)
                .when()
                .get(ORDERS_PATH)
                .then();
    }
}


