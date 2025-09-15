import com.google.gson.Gson;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.Matchers.*;

import junitparams.Parameters;
import junitparams.JUnitParamsRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@RunWith(JUnitParamsRunner.class)
public class OrderCreationApiTests {

    private static final String BASE_URI = "https://qa-scooter.praktikum-services.ru/";
    private final List<String> createdOrderTrackers = new ArrayList<>();
    private final Gson gson = new Gson();

    @Before
    public void setUp() {
        RestAssured.baseURI = BASE_URI;
    }

    @After
    public void tearDown() {
        // Очистка: удаляем созданные заказы по их трек-номеру
        // Реализуйте здесь вызов DELETE /api/v1/orders/track/{track}
        // Например: RestAssured.given().delete("/api/v1/orders/track/" + track);
        for (String track : createdOrderTrackers) {
            System.out.println("Заказ с треком " + track + " был бы удален здесь.");
        }
    }

    /**
     * Параметризованный тест: проверяем создание заказа с разными значениями color.
     */
    @Test
    @Parameters(method = "colorVariants")
    public void testCreateOrderWithVariousColors(String[] colors) {
        String payload = buildCreateOrderPayload(colors);

        String track = RestAssured.given()
                .contentType(ContentType.JSON)
                .body(payload)
                .when()
                .post("/api/v1/orders")
                .then()
                .statusCode(201)
                .contentType(ContentType.JSON)
                .body("track", notNullValue())
                .extract()
                .path("track").toString(); // Преобразуем число в строку

        if (track != null) {
            createdOrderTrackers.add(track);
        }
    }

    // Поставщик параметров для теста
    private Object[] colorVariants() {
        return new Object[]{
                new Object[]{new String[]{"BLACK"}},
                new Object[]{new String[]{"GREY"}},
                new Object[]{new String[]{"BLACK", "GREY"}},
                new Object[]{null} // без цвета
        };
    }

    // Формируем JSON-тело заказа с помощью Map и Gson
    private String buildCreateOrderPayload(String[] colors) {
        Map<String, Object> orderData = new HashMap<>();
        orderData.put("firstName", "Naruto");
        orderData.put("lastName", "Uchiha");
        orderData.put("address", "Konoha, 142 apt.");
        orderData.put("metroStation", 4);
        orderData.put("phone", "+7 800 355 35 35");
        orderData.put("rentTime", 5);
        orderData.put("deliveryDate", "2020-06-06");
        orderData.put("comment", "Saske, come back to Konoha");

        // Если цвета указаны, добавляем поле 'color'
        if (colors != null) {
            orderData.put("color", colors);
        }

        return gson.toJson(orderData);
    }
}