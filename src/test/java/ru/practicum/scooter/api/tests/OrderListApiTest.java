package ru.practicum.scooter.api.tests;

import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.Before;
import org.junit.Test;
import ru.practicum.scooter.api.client.OrderApiClient;

import java.util.List;

import static org.apache.http.HttpStatus.SC_OK;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.notNullValue;

public class OrderListApiTest {

    private OrderApiClient orderClient;

    @Before
    public void setUp() {
        orderClient = new OrderApiClient();
    }

    @Test
    @DisplayName("Проверка получения списка заказов")
    @Description("Тест проверяет, что API возвращает непустой список заказов")
    public void getOrderListReturnsOrders() {

        ValidatableResponse response = orderClient.getOrderList();

        response
                .assertThat()
                .statusCode(SC_OK)
                .body("orders", notNullValue())
                .body("orders", instanceOf(List.class));
    }
}