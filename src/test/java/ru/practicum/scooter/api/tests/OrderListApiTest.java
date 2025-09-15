import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class OrderListApiTest {

    private static final String BASE_URI = "https://qa-scooter.praktikum-services.ru/";
    private Gson gson;

    @Before
    public void setUp() {
        RestAssured.baseURI = BASE_URI;
        gson = new Gson();
    }

    @After
    public void tearDown() {
        // Если тесты ничего не создают, этот метод можно оставить пустым.
        // Если вы добавляете создание тестовых данных, здесь можно их удалить.
    }

    // Вспомогательный метод: выполнить GET /api/v1/orders и вернуть разобранный JsonObject
    private JsonObject getOrdersAsJson(String path) {
        Response resp = RestAssured
                .given()
                .when()
                .get(path)
                .then()
                .statusCode(200)
                .extract()
                .response();

        String body = resp.asString();
        return gson.fromJson(body, JsonObject.class);
    }

    @Test
    public void testResponseContainsOrdersListOnly() {
        // Тест: только проверка наличия списка заказов в ответе
        String path = "/api/v1/orders";

        JsonObject root = getOrdersAsJson(path);

        // 1) Корневой объект не должен быть null
        assertNotNull("Ответ не должен быть null", root);

        // 2) Поле orders должно существовать и быть массивом
        assertTrue("Должно быть поле 'orders'", root.has("orders"));

        JsonElement ordersElem = root.get("orders");
        assertNotNull("Поле 'orders' не должно быть null", ordersElem);
        assertTrue("Поле 'orders' должно быть массивом", ordersElem.isJsonArray());

        // Дополнительно можно привести к JsonArray если нужен доступ к элементам
        // JsonArray orders = ordersElem.getAsJsonArray();
    }
}