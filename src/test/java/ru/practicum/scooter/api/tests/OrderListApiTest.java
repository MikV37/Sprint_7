package ru.practicum.scooter.api.tests;

import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.ValidatableResponse;
import org.junit.Before;
import org.junit.Test;
import java.util.List;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class OrderListApiTest {

    private static final String BASE_URI = "https://qa-scooter.praktikum-services.ru/";

    @Before
    public void setUp() {
        RestAssured.baseURI = BASE_URI;
    }


    @Step("Отправка GET-запроса на получение списка заказов")
    public ValidatableResponse getOrderListStep() {
        return given()
                .when()
                .get("/api/v1/orders")
                .then();
    }

    @Step("Проверка, что в теле ответа есть непустой список заказов")
    public void checkOrderListIsReturned(ValidatableResponse response) {
        response.assertThat()
                .statusCode(200)
                .body("orders", notNullValue())
                .body("orders", instanceOf(List.class));
    }

    @Test
    @DisplayName("Проверка получения списка заказов")
    @Description("Тест проверяет, что API возвращает список заказов (поле orders)")
    public void getOrderListReturnsOrders() {
        ValidatableResponse response = getOrderListStep();
        checkOrderListIsReturned(response);
    }
}