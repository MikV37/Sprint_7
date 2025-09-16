package ru.practicum.scooter.api.tests;

import com.github.javafaker.Faker;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import ru.practicum.scooter.api.client.OrderApiClient;
import ru.practicum.scooter.api.pojo.Order;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.apache.http.HttpStatus.SC_CREATED;
import static org.apache.http.HttpStatus.SC_OK;
import static org.hamcrest.Matchers.notNullValue;

@RunWith(JUnitParamsRunner.class)
public class OrderCreationApiTests {

    private OrderApiClient orderClient;
    private Faker faker;
    private final List<Integer> orderTracks = new ArrayList<>();

    @Before
    public void setUp() {
        orderClient = new OrderApiClient();
        faker = new Faker();
    }

    @After

    public void tearDown() {
        for (Integer track : orderTracks) {
            try {
                orderClient.cancel(track)
                        .assertThat()
                        .statusCode(SC_OK);
            } catch (AssertionError e) {
                // Игнорируем ошибку, если отмена не удалась (например, для заказа без цвета)
                System.out.println("Не удалось отменить заказ с треком " + track + ". Ошибка: " + e.getMessage());
            }
        }
    }

    @Test
    @DisplayName("Создание заказа с различными цветами")
    @Description("Тест проверяет, что заказ можно успешно создать, указав один, два или ни одного цвета")
    @Parameters(method = "colorData")
    public void createOrderWithVariousColors(List<String> colors) {

        Order order = new Order(
                faker.name().firstName(),
                faker.name().lastName(),
                faker.address().streetAddress(),
                faker.number().numberBetween(1, 20),
                faker.phoneNumber().phoneNumber(),
                faker.number().numberBetween(1, 7),
                new SimpleDateFormat("yyyy-MM-dd").format(faker.date().future(10, TimeUnit.DAYS)), // deliveryDate
                faker.lorem().sentence(),
                colors
        );


        ValidatableResponse createResponse = orderClient.create(order);
        createResponse
                .assertThat()
                .statusCode(SC_CREATED)
                .and()
                .body("track", notNullValue());

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
