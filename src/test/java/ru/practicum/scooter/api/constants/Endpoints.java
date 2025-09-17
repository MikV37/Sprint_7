package ru.practicum.scooter.api.constants;

public class Endpoints {
    public static final String BASE_URI = "https://qa-scooter.praktikum-services.ru";
    public static final String COURIER_PATH = "/api/v1/courier";
    public static final String COURIER_LOGIN_PATH = COURIER_PATH + "/login";
    public static final String COURIER_DELETE_PATH = COURIER_PATH + "/{id}";
    public static final String ORDERS_PATH = "/api/v1/orders";
    public static final String ORDER_CANCEL_PATH = ORDERS_PATH + "/cancel";
}
