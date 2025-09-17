package ru.practicum.scooter.api.tests;

import com.github.javafaker.Faker;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.practicum.scooter.api.client.CourierApiClient;
import ru.practicum.scooter.api.pojo.Courier;
import ru.practicum.scooter.api.pojo.Login;
import static org.apache.http.HttpStatus.*;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

public class CourierApiTest {
    private CourierApiClient courierClient;
    private Courier courier;

    @Before
    public void setUp() {
        courierClient = new CourierApiClient();
        Faker faker = new Faker();
        courier = new Courier(
                faker.name().username(),
                faker.internet().password(),
                faker.name().firstName()
        );
    }

    @After
    public void tearDown() {
        if (courier.getLogin() != null && courier.getPassword() != null) {
            ValidatableResponse loginResponse = courierClient.login(Login.from(courier));
            int courierId = loginResponse.extract().path("id");
            if (courierId != 0) {
                courierClient.delete(courierId);
            }
        }
    }

    @Test
    @DisplayName("Успешное создание курьера")
    @Description("Проверка, что API позволяет создать курьера с валидными данными")
    public void courierCanBeCreatedWithValidData() {
        courierClient.create(courier)
                .assertThat()
                .statusCode(SC_CREATED)
                .and()
                .body("ok", is(true));
    }

    @Test
    @DisplayName("Создание дубликата курьера")
    @Description("Проверка, что API возвращает ошибку при попытке создать курьера с существующим логином")
    public void cannotCreateTwoCouriersWithSameLogin() {
        courierClient.create(courier);
        courierClient.create(courier)
                .assertThat()
                .statusCode(SC_CONFLICT)
                .and()
                .body("message", equalTo("Этот логин уже используется. Попробуйте другой."));
    }

    @Test
    @DisplayName("Создание курьера без обязательного поля (пароль)")
    @Description("Проверка, что API вернет ошибку, если не передать пароль")
    public void courierCannotBeCreatedWithoutPassword() {
        courier.setPassword(null);
        courierClient.create(courier)
                .assertThat()
                .statusCode(SC_BAD_REQUEST)
                .body("message", equalTo("Недостаточно данных для создания учетной записи"));
    }

    @Test
    @DisplayName("Создание курьера без обязательного поля (логин)")
    @Description("Проверка, что API вернет ошибку, если не передать логин")
    public void courierCannotBeCreatedWithoutLogin() {
        courier.setLogin(null);
        courierClient.create(courier)
                .assertThat()
                .statusCode(SC_BAD_REQUEST)
                .body("message", equalTo("Недостаточно данных для создания учетной записи"));
    }
}

