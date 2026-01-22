package com.viacheslav.ispmanagement.ui;

import com.viacheslav.ispmanagement.model.Equipment;
import com.viacheslav.ispmanagement.model.Payment;
import com.viacheslav.ispmanagement.model.Plan;
import com.viacheslav.ispmanagement.model.Subscriber;
import com.viacheslav.ispmanagement.model.Ticket;
import com.viacheslav.ispmanagement.model.Ticket.Status;
import com.viacheslav.ispmanagement.repository.UnitOfWork;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class TestRepository {

  public static void main(String[] args) {

    UnitOfWork uow = new UnitOfWork();

    // === CREATE & SAVE ===
    Plan plan = new Plan(
        UUID.randomUUID(),
        "Test Plan",
        BigDecimal.valueOf(500),
        500,
        "Test description"
    );
    uow.plans().save(plan);

    Subscriber subscriber = new Subscriber(
        UUID.randomUUID(),
        "Test User",
        "+380000000000",
        LocalDate.now(),
        null,
        plan,
        null,
        List.of(),
        List.of(),
        List.of()
    );
    uow.subscribers().save(subscriber);

    Payment payment = new Payment(
        UUID.randomUUID(),
        LocalDateTime.now(),
        BigDecimal.valueOf(500),
        "CARD"
    );
    uow.payments().save(payment);

    Equipment equipment = new Equipment(
        UUID.randomUUID(),
        "Router",
        "SN-12345",
        LocalDate.now()
    );
    uow.equipment().save(equipment);

    Ticket ticket = new Ticket(
        UUID.randomUUID(),
        LocalDateTime.now(),
        Status.OPEN,
        "No internet"
    );
    uow.tickets().save(ticket);

    // === READ ===
    System.out.println("ALL SUBSCRIBERS:");
    uow.subscribers().findAll().forEach(System.out::println);

    // === SEARCH ===
    System.out.println("\nSEARCH PLAN BY NAME:");
    uow.plans().findByName("Test").forEach(System.out::println);

    // === FILTER ===
    System.out.println("\nFILTER PAYMENTS >= 300:");
    uow.payments().filterByMinAmount(BigDecimal.valueOf(300))
        .forEach(System.out::println);

    System.out.println("\nFILTER TICKETS OPEN:");
    uow.tickets().filterByStatus(Status.OPEN)
        .forEach(System.out::println);

    // === SIMPLE ASSERTION ===
    if (uow.subscribers().findAll().isEmpty()) {
      throw new RuntimeException("Test failed: no subscribers found");
    }

    System.out.println("\nSTAGE 3 TEST COMPLETED SUCCESSFULLY");
  }
}
