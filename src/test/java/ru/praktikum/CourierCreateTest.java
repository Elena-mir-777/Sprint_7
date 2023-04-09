package ru.praktikum;

import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.praktikum.model.Courier;
import ru.praktikum.client.CourierClient;
import ru.praktikum.model.CourierCredentials;
import ru.praktikum.model.CourierGenerator;

import static java.net.HttpURLConnection.*;
import static org.junit.Assert.*;

public class CourierCreateTest {
    private Courier courier;
    private CourierClient courierClient;
    Integer courierId;
    @Before
    public void setUp() {
        courier = CourierGenerator.getRandom();
        courierClient = new CourierClient();
    }
    @After
    public void clearData() {
        courierClient.delete(courierId == null ? 0 : courierId);
    }

    @Test // Создание курьера (валидные логин, пароль и имя курьера)
    public void courierCanBeCreatedWithValidData() {

        ValidatableResponse createResponse = courierClient.create(courier);

        int statusCode = createResponse.extract().statusCode();
        boolean isCourierCreated = createResponse.extract().path("ok");

        assertEquals("Статус код некорректный", HTTP_CREATED, statusCode);
        assertTrue("Курьер не создан", isCourierCreated);

        ValidatableResponse loginResponse = courierClient.login(CourierCredentials.from(courier));
        courierId = loginResponse.extract().path("id");
        assertNotNull("Курьер не создан", courierId);

    }
    @Test // нельзя создать двух одинаковых курьеров
    public void couriersWithSameValidDataCanNotCreated() {

        courierClient.create(courier);

        ValidatableResponse loginResponse = courierClient.login(CourierCredentials.from(courier));
        courierId = loginResponse.extract().path("id");
       assertNotNull("Курьер не создан", courierId);

        ValidatableResponse createResponseTwo = courierClient.create(courier);

        int statusCode = createResponseTwo.extract().statusCode();
        String message = createResponseTwo.extract().path("message");

        assertEquals("Статус код некорректный",HTTP_CONFLICT , statusCode);
        assertEquals("Текст сообщения некорректный","Этот логин уже используется.", message);

        ValidatableResponse loginResponseTwo = courierClient.login(CourierCredentials.from(courier));
        Integer courierIdTwo = loginResponseTwo.extract().path("id");
        assertEquals("Создано два одинаковых курьера",courierId,courierIdTwo);

      }

    @Test // Создание курьера  без логина (валидные пароль и имя курьера)
    public void courierWithoutLoginCanNotCreated() {

        courier = new Courier("", courier.getPassword(), courier.getFirstName());
        ValidatableResponse createResponse = courierClient.create(courier);

        int statusCode = createResponse.extract().statusCode();
        String message = createResponse.extract().path("message");

        assertEquals("Статус код некорректный", HTTP_BAD_REQUEST, statusCode);
        assertEquals("Текст сообщения некорректный","Недостаточно данных для создания учетной записи", message);

        ValidatableResponse loginResponse = courierClient.login(CourierCredentials.from(courier));
        courierId = loginResponse.extract().path("id");
        assertNull("Создан курьер без логина", courierId );

    }
    @Test // Создание курьера  без пароля (валидные логин и имя курьера)
    public void courierWithoutPasswordCanNotCreated() {

        courier = new Courier(courier.getLogin(),"" , courier.getFirstName());
        ValidatableResponse createResponse = courierClient.create(courier);

        int statusCode = createResponse.extract().statusCode();
        String message = createResponse.extract().path("message");

        assertEquals("Статус код некорректный ", HTTP_BAD_REQUEST, statusCode);
        assertEquals("Текст сообщения некорректный","Недостаточно данных для создания учетной записи", message);

        ValidatableResponse loginResponse = courierClient.login(CourierCredentials.from(courier));
        courierId = loginResponse.extract().path("id");
        assertNull("Создан курьер без пароля", courierId);


    }

    @Test // Создание курьера  без имени (валидные пароль и логин)
    public void courierWithoutFirstNameCanNotCreated() {

        courier = new Courier(courier.getLogin(),courier.getPassword(),"");
        ValidatableResponse createResponse = courierClient.create(courier);

        int statusCode = createResponse.extract().statusCode();
        String message = createResponse.extract().path("message");

        assertEquals("Статус код некорректный ", HTTP_BAD_REQUEST, statusCode);
        assertEquals("Текст сообщения некорректный","Недостаточно данных для создания учетной записи", message);

        ValidatableResponse loginResponse = courierClient.login(CourierCredentials.from(courier));
        courierId = loginResponse.extract().path("id");
        assertNull("Создан курьер без имени", courierId);

    }

}
