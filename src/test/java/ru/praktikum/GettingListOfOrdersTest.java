package ru.praktikum;

import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.praktikum.client.OrderClient;
import ru.praktikum.model.Order;
import ru.praktikum.model.OrderGeneration;

import java.util.List;

import static java.net.HttpURLConnection.HTTP_OK;
import static org.junit.Assert.*;

public class GettingListOfOrdersTest {
    private OrderClient orderClient;
    Integer trackID;
    @Before
    public void setUp() {
        Order order = OrderGeneration.getRandom();
        orderClient = new OrderClient();
        ValidatableResponse createResponse = orderClient.create(order);
        trackID = createResponse.extract().path("track");
    }
    @After
    public void clearData() {
        orderClient.cancel(trackID);
    }

    @Test // Получение списка всех зарегистрированных заказов
    public void gettingListOfAllReceivedOrders() {
        ValidatableResponse listResponse = orderClient.getList();

        int statusCode = listResponse.extract().statusCode();
        assertEquals("Статус код некорректный", HTTP_OK, statusCode);

        Object responseObj  = listResponse.extract().path("orders");
        assertNotNull("Не нашёлся list  of orders", responseObj);
        assertTrue("В качестве orders вернулся не List", responseObj instanceof List);
        List responseOrder = (List) responseObj;
        assertFalse("Orders пустой", responseOrder.isEmpty());
    }
}
