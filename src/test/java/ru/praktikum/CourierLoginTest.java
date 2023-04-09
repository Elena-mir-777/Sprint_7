package ru.praktikum;

import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;

import org.junit.Test;
import ru.praktikum.client.CourierClient;
import ru.praktikum.model.Courier;
import ru.praktikum.model.CourierCredentials;
import ru.praktikum.model.CourierGenerator;

import static java.net.HttpURLConnection.*;
import static org.junit.Assert.*;

public class CourierLoginTest {
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

    @Test // Авторизация зарегистрированного курьера. Пара логин-пароль существует

    public void courierCanLogInWithValidData() {
        courierClient.create(courier);

        ValidatableResponse loginResponse = courierClient.login(CourierCredentials.from(courier));
        int statusCode = loginResponse.extract().statusCode();

        assertEquals("Статус код некорректный",HTTP_OK, statusCode);
        courierId = loginResponse.extract().path("id");
        assertNotNull("Курьер не авторизован", courierId);

    }
    @Test // Авторизация незарегистрированного курьера.

    public void courierUnregisteredCanNotLogIn() {

        ValidatableResponse loginResponse = courierClient.login(CourierCredentials.from(courier));
        int statusCode = loginResponse.extract().statusCode();
        assertEquals("Статус код некорректный",HTTP_NOT_FOUND, statusCode);

        String message = loginResponse.extract().path("message");
        assertEquals("Текст сообщения некорректный","Учетная запись не найдена",message);

        courierId = loginResponse.extract().path("id");
        assertNull("Незарегистрированный курьер авторизуется", courierId);

    }
    @Test // Авторизация зарегистрированного курьера без логина - пароль существует.

    public void courierCanNotLogInWithoutLogin() {

        courierClient.create(courier);
        courier = new Courier("",courier.getPassword(),courier.getFirstName());

        ValidatableResponse loginResponse = courierClient.login(CourierCredentials.from(courier));
        int statusCode = loginResponse.extract().statusCode();
        assertEquals("Статус код некорректный",HTTP_BAD_REQUEST, statusCode);

        String message = loginResponse.extract().path("message");
        assertEquals("Текст сообщения некорректный","Недостаточно данных для входа",message);

        courierId = loginResponse.extract().path("id");
        assertNull("Курьер без логина авторизуется", courierId);

    }

    @Test // Авторизация зарегистрированного курьера без пароля - логин существует.

    public void courierCanNotLogInWithoutPassword() {

        courierClient.create(courier);
        courier = new Courier(courier.getLogin(),"",courier.getFirstName());

        ValidatableResponse loginResponse = courierClient.login(CourierCredentials.from(courier));
        int statusCode = loginResponse.extract().statusCode();
        assertEquals("Статус код некорректный",HTTP_BAD_REQUEST, statusCode);

        String message = loginResponse.extract().path("message");
        assertEquals("Текст сообщения некорректный","Недостаточно данных для входа",message);

        courierId = loginResponse.extract().path("id");
        assertNull("Курьер без пароля авторизуется", courierId);

    }
    @Test // Авторизация зарегистрированного курьера. Правильный логин - неправильный пароль

    public void courierWithInvalidPasswordCanNotLogIn() {
        courierClient.create(courier);
        courier = new Courier(courier.getLogin(),"123456",courier.getFirstName());

        ValidatableResponse loginResponse = courierClient.login(CourierCredentials.from(courier));
        int statusCode = loginResponse.extract().statusCode();
        assertEquals("Статус код некорректный",HTTP_NOT_FOUND, statusCode);

        String message = loginResponse.extract().path("message");
        assertEquals("Текст сообщения некорректный","Учетная запись не найдена",message);

        courierId = loginResponse.extract().path("id");
        assertNull("Курьер с неправильным паролем авторизуется", courierId);

    }

    @Test // Авторизация зарегистрированного курьера. Неправильный логин - правильный пароль

    public void courierWithInvalidLoginCanNotLogIn() {
        courierClient.create(courier);
        courier = new Courier("123456",courier.getPassword(),courier.getFirstName());

        ValidatableResponse loginResponse = courierClient.login(CourierCredentials.from(courier));
        int statusCode = loginResponse.extract().statusCode();
        assertEquals("Статус код некорректный",HTTP_NOT_FOUND, statusCode);

        String message = loginResponse.extract().path("message");
        assertEquals("Текст сообщения некорректный","Учетная запись не найдена",message);

        courierId = loginResponse.extract().path("id");
        assertNull("Курьер с неправильным логином авторизуется", courierId);

    }

}
