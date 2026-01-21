package com.viacheslav.ispmanagement.ui;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.viacheslav.ispmanagement.model.Address;
import com.viacheslav.ispmanagement.model.Equipment;
import com.viacheslav.ispmanagement.model.Payment;
import com.viacheslav.ispmanagement.model.Plan;
import com.viacheslav.ispmanagement.model.Subscriber;
import com.viacheslav.ispmanagement.model.Ticket;
import com.viacheslav.ispmanagement.model.User;
import com.viacheslav.ispmanagement.util.LocalDateAdapter;
import com.viacheslav.ispmanagement.util.LocalDateTimeAdapter;
import java.io.FileWriter;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import net.datafaker.Faker;

public class TestDataGenerator {

  public static void main(String[] args) {

    Gson gson = new GsonBuilder()
        .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
        .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
        .setPrettyPrinting()
        .create();

    Faker faker = new Faker();

    List<Subscriber> subscribers = new ArrayList<>();

    for (int i = 0; i < 5; i++) {

      Address address = new Address(
          UUID.randomUUID(),
          faker.address().city(),
          faker.address().streetName(),
          faker.address().buildingNumber(),
          faker.address().secondaryAddress()
      );

      Plan plan = new Plan(
          UUID.randomUUID(),
          "Plan " + faker.internet().domainWord(),
          BigDecimal.valueOf(300 + faker.number().numberBetween(0, 300)),
          faker.number().numberBetween(50, 1000),
          "Test internet plan"
      );

      User user = new User(
          UUID.randomUUID(),
          faker.internet().emailAddress(),
          "hashed_password",
          User.Role.SUBSCRIBER
      );

      Payment payment = new Payment(
          UUID.randomUUID(),
          LocalDateTime.now(),
          BigDecimal.valueOf(500),
          "CARD"
      );

      Equipment equipment = new Equipment(
          UUID.randomUUID(),
          "Router",
          faker.device().serial(),
          LocalDate.now()
      );

      Ticket ticket = new Ticket(
          UUID.randomUUID(),
          LocalDateTime.now(),
          Ticket.Status.OPEN,
          "Internet connection issue"
      );

      Subscriber subscriber = new Subscriber(
          UUID.randomUUID(),
          faker.name().fullName(),
          faker.phoneNumber().cellPhone(),
          LocalDate.now(),
          address,
          plan,
          user,
          List.of(payment),
          List.of(equipment),
          List.of(ticket)
      );

      subscribers.add(subscriber);
    }

    try (FileWriter writer = new FileWriter("data/subscribers.json")) {
      gson.toJson(subscribers, writer);
    } catch (Exception e) {
      e.printStackTrace();
    }

    System.out.println("Subscribers saved to data/subscribers.json");
  }
}
