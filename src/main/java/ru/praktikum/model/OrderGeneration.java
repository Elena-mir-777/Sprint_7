package ru.praktikum.model;

import org.apache.commons.lang3.RandomStringUtils;
import java.util.Random;

public class OrderGeneration {
private static String twoDigit(int i) {
    if (i < 10) {
        return "0" + i;
    } else {
        return String.valueOf(i);
    }
}
    public static Order getRandom() {
        Random random = new Random();
        String firstName = RandomStringUtils.randomAlphabetic(6);
        String lastName = RandomStringUtils.randomAlphabetic(6);
        String address = RandomStringUtils.randomAlphabetic(10);
        Integer metroStation = random.nextInt(10);
        String phone = RandomStringUtils.randomNumeric(12);
        int rentTime = random.nextInt(10);
        String deliveryDate = (2023 + random.nextInt(5)) + "-" + twoDigit(random.nextInt(12) + 1) + "-" + twoDigit(random.nextInt(28) + 1);
        String comment = RandomStringUtils.randomAlphabetic(15);

        return new Order(firstName,lastName,address,metroStation,phone,rentTime,deliveryDate,comment);
    }
}
