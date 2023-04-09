package ru.praktikum;

import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import ru.praktikum.client.OrderClient;
import ru.praktikum.model.Order;
import ru.praktikum.model.OrderGeneration;

import java.util.List;
import java.util.Map;

import static java.net.HttpURLConnection.HTTP_CREATED;
import static java.net.HttpURLConnection.HTTP_OK;
import static org.junit.Assert.*;

@RunWith(Parameterized.class)
public class CreateOrderParameterizedTest {
    private final List<String> color;
    private Order order;
    private OrderClient orderClient;
    Integer trackID;

    public CreateOrderParameterizedTest(List<String> color) {
        this.color = color;
    }
    @Parameterized.Parameters
    public static Object[] getColorParameters() {
        return new Object[]{
                List.of("BLACK"),
                List.of("GREY"),
                List.of("BLACK","GREY"),
                List.of("")
        };
    }
    @Before
    public void setUp() {
         order = OrderGeneration.getRandom();
        order.setColor(color);
         orderClient = new OrderClient();
    }
    @After
    public void clearData() {
        orderClient.cancel(trackID);
     }

    @Test // Успешное создание заказа с валидными данными
    public void orderCanBeCreatedWithValidData() {
        ValidatableResponse createResponse = orderClient.create(order);

        int statusCode = createResponse.extract().statusCode();
        assertEquals("Статус код некорректный", HTTP_CREATED, statusCode);

        trackID = createResponse.extract().path("track");
        assertNotNull("Заказ не создан", trackID);

        ValidatableResponse receiveResponse = orderClient.receive(trackID);
        int sCode = receiveResponse.extract().statusCode();
        assertEquals("Статус код некорректный", HTTP_OK, sCode);

        Object responseObj  = receiveResponse.extract().path("order");
        assertNotNull("Не нашёлся order", responseObj);
        assertTrue("В качестве order вернулся не Map", responseObj instanceof Map);
        Map responseOrder = (Map) responseObj;
        assertFalse("Order пустой", responseOrder.isEmpty());

    }
}
