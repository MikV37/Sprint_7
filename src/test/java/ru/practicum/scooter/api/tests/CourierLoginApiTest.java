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
import static org.hamcrest.Matchers.notNullValue;

public class CourierLoginApiTest {

    private CourierApiClient courierClient;
    private Courier courier;
    private Integer courierId;

    @Before
    public void setUp() {

        courierClient = new CourierApiClient();
        Faker faker = new Faker();
        courier = new Courier(
                faker.name().username(),
                faker.internet().password(),
                faker.name().firstName()
        );

        courierClient.create(courier);
        ValidatableResponse loginResponse = courierClient.login(Login.from(courier));
        courierId = loginResponse.extract().path("id");
    }

    @After
    public void tearDown() {

        if (courierId != null) {
            courierClient.delete(courierId);
        }
    }

    @Test
    @DisplayName("Успешная авторизация курьера")
    @Description("Проверка, что курьер может авторизоваться с валидными данными")
    public void successfulLoginReturnsId() {

        ValidatableResponse loginResponse = courierClient.login(Login.from(courier));
        loginResponse
                .assertThat()
                .statusCode(SC_OK)
                .and()
                .body("id", notNullValue());
    }

    @Test
    @DisplayName("Авторизация без логина")
    @Description("Проверка, что API возвращает ошибку 400 при авторизации без логина")
    public void loginMissingLoginReturnsBadRequest() {

        Login loginData = new Login(null, courier.getPassword());
        ValidatableResponse loginResponse = courierClient.login(loginData);
        loginResponse
                .assertThat()
                .statusCode(SC_BAD_REQUEST)
                .and()
                .body("message", equalTo("Недостаточно данных для входа"));
    }

    @Test
    @DisplayName("Авторизация без пароля")
    @Description("Проверка, что API возвращает ошибку 400 при авторизации без пароля")
    public void loginMissingPasswordReturnsBadRequest() {

        Login loginData = new Login(courier.getLogin(), null);
        ValidatableResponse loginResponse = courierClient.login(loginData);
        loginResponse
                .assertThat()
                .statusCode(SC_BAD_REQUEST)
                .and()
                .body("message", equalTo("Недостаточно данных для входа"));
    }

    @Test
    @DisplayName("Авторизация с верным логином и неверным паролем")
    @Description("Проверка, что API возвращает ошибку 404, если пароль неверный")
    public void loginWithCorrectLoginAndIncorrectPasswordReturnsNotFound() {

        String incorrectPassword = courier.getPassword() + "extra";
        Login loginData = new Login(courier.getLogin(), incorrectPassword);
        ValidatableResponse loginResponse = courierClient.login(loginData);


        loginResponse
                .assertThat()
                .statusCode(SC_NOT_FOUND)
                .and()
                .body("message", equalTo("Учетная запись не найдена"));
    }

    @Test
    @DisplayName("Авторизация с неверным логином")
    @Description("Проверка, что API возвращает ошибку 404, если логин не существует")
    public void loginWithIncorrectLoginReturnsNotFound() {

        Faker faker = new Faker();
        Login loginData = new Login(faker.name().username(), faker.internet().password());
        ValidatableResponse loginResponse = courierClient.login(loginData);

        loginResponse
                .assertThat()
                .statusCode(SC_NOT_FOUND)
                .and()
                .body("message", equalTo("Учетная запись не найдена"));
    }
}