package ru.praktikum.client;

import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.response.ValidatableResponse;
import ru.praktikum.client.base.ScooterRestClient;
import ru.praktikum.model.Courier;
import ru.praktikum.model.CourierCredentials;

import static io.restassured.RestAssured.given;

public class CourierClient  extends ScooterRestClient {
    private static final String COURIER_URI = BASE_URI +  "courier/";

    public CourierClient() {
        RestAssured.baseURI = BASE_URI;
    }
    @Step("Create courier {courier}")
    public ValidatableResponse create(Courier courier) {
        return given()
                .spec(getBaseRecSpec())
                .and()
                .body(courier)
                .when()
                .post(COURIER_URI)
                .then();
    }

    @Step("Login as {courierCredentials}")
    public ValidatableResponse login(CourierCredentials courierCredentials) {
        return given()
                .spec(getBaseRecSpec())
                .and()
                .body(courierCredentials)
                .when()
                .post(COURIER_URI+"login/")
                .then();
    }
    @Step("Delete courier {courierId}")
    public ValidatableResponse delete(int courierId) {
        return given()
                .when()
                .delete(COURIER_URI+ courierId)
                .then();

    }
}
