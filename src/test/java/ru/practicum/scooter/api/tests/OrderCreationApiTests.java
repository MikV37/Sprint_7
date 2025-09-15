package ru.practicum.scooter.api.tests;

import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import ru.practicum.scooter.api.pojo.Order;
import java.util.ArrayList;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.notNullValue;

@RunWith(JUnitParamsRunner.class)
public class OrderCreationApiTests {
    private static final String BASE_URI = "https://qa-scooter.praktikum-services.ru/";
    private final List<Integer> orderTracks = new ArrayList<>();

    @Before
public void setUp() {
    RestAssured.baseURI = BASE_URI;
}

@After
public void tearDown() {
    for (Integer track : orderTracks) {
        cancelOrderStep(track);
    }
}

@Step("Создание заказа с цветом(ами): {colors}")
    public ValidatableResponse createOrderStep(List<String> colors) {

        Order order = new Order(

                "Naruto",
                "Uchiha",
                "Konoha, 142 apt.",
                4,
                "+7 800 355 35 35",
                5,
                "2025-09-15",
                "Saske, come back to Konoha",
                colors
        );

        return given()
                .contentType(ContentType.JSON)
                .body(order)
                .when()
                .post("/api/v1/orders")
                .then();
}

@Step("Отмена заказа по трек-номеру: {track}")
public void cancelOrderStep(int track) {
        given()
                .put("/api/v1/orders/cancel?track=" + track)
                .then()
                .statusCode(200);
    }

    @Test
    @DisplayName("Создание заказа с различными цветами")
    @Description("Тест проверяет, что заказ можно успешно создать, указав один цвет, два цвета или не указывая цвет совсем")
    @Parameters(method = "colorData")

public void createOrderWithVariousColors(List<String> colors) {
        ValidatableResponse createResponse = createOrderStep(colors);
        createResponse.statusCode(201).body("track", notNullValue());
        int track = createResponse.extract().path("track");

orderTracks.add(track);
}

        private Object[] colorData() {
    return new Object[]{
            List.of("BLACK"),
            List.of("GREY"),
            List.of("BLACK", "GREY"),
            new ArrayList<>()
    };
    }
}