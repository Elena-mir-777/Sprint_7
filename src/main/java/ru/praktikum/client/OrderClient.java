package ru.praktikum.client;

import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.response.ValidatableResponse;
import ru.praktikum.client.base.ScooterRestClient;
import ru.praktikum.model.Order;

import static io.restassured.RestAssured.given;

public class OrderClient extends ScooterRestClient {
    private static final String ORDER_URI = BASE_URI + "orders";

    public OrderClient() {
        RestAssured.baseURI = BASE_URI;
    }

    @Step("Create order {order}")
    public ValidatableResponse create(Order order) {
        return given()
                .spec(getBaseRecSpec())
                .and()
                .body(order)
                .when()
                .post(ORDER_URI)
                .then();
    }
    @Step("Receive order by number {trackID}")
    public ValidatableResponse receive(Integer trackID) {
        return given()
                .when()
                .get(ORDER_URI+"/track?t="+ trackID)
                .then();
    }



    @Step("Cancel order {trackID}")
    public ValidatableResponse cancel(Integer trackID) {
        return given()
                .when()
                .put(ORDER_URI + "/cancel?track=" + trackID)
                .then();
    }

    @Step("Getting a list  of all receive orders")
    public ValidatableResponse getList() {
        return given()
                .when()
                .get(ORDER_URI)
                .then();
    }

}
