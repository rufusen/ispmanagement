package com.viacheslav.ispmanagement.ui;

import com.viacheslav.ispmanagement.domain.AuthService;
import com.viacheslav.ispmanagement.domain.EmailService;
import com.viacheslav.ispmanagement.domain.PaymentService;
import com.viacheslav.ispmanagement.domain.PlanService;
import com.viacheslav.ispmanagement.domain.SubscriberService;
import com.viacheslav.ispmanagement.domain.TicketService;
import com.viacheslav.ispmanagement.dto.PaymentCreateDto;
import com.viacheslav.ispmanagement.dto.SubscriberCreateDto;
import com.viacheslav.ispmanagement.dto.TicketCreateDto;
import com.viacheslav.ispmanagement.dto.UserRegisterDto;
import com.viacheslav.ispmanagement.model.Address;
import com.viacheslav.ispmanagement.model.Plan;
import com.viacheslav.ispmanagement.model.Subscriber;
import com.viacheslav.ispmanagement.model.User;
import com.viacheslav.ispmanagement.repository.UnitOfWork;
import java.math.BigDecimal;
import java.util.UUID;

public class TestDomainLayer {

  public static void main(String[] args) {
    System.out.println("=== STAGE 4: DOMAIN LAYER DEMO ===\n");

    UnitOfWork unitOfWork = new UnitOfWork();
    EmailService emailService = new EmailService();
    AuthService authService = new AuthService(unitOfWork, emailService);
    SubscriberService subscriberService = new SubscriberService(unitOfWork);
    PlanService planService = new PlanService(unitOfWork);
    PaymentService paymentService = new PaymentService(unitOfWork);
    TicketService ticketService = new TicketService(unitOfWork);

    try {
      // === 1. REGISTER USER ===
      System.out.println("1. REGISTERING USER");
      System.out.println("-------------------");
      UserRegisterDto userDto = new UserRegisterDto(
          "john.doe@example.com",
          "password123",
          User.Role.SUBSCRIBER
      );
      User registeredUser = authService.registerUser(userDto);
      System.out.println("Registered user: " + registeredUser);
      System.out.println();

      // === 2. AUTHENTICATE USER ===
      System.out.println("2. AUTHENTICATING USER");
      System.out.println("----------------------");
      User authenticatedUser = authService.login("john.doe@example.com", "password123");
      if (authenticatedUser != null) {
        System.out.println("Login successful: " + authenticatedUser);
      } else {
        System.out.println("Login failed!");
      }
      System.out.println();

      // === 3. GET OR CREATE PLAN ===
      System.out.println("3. GETTING PLAN");
      System.out.println("---------------");
      Plan plan;
      if (planService.getAllPlans().isEmpty()) {
        System.out.println("No plans found. Creating a test plan...");
        plan = new Plan(
            UUID.randomUUID(),
            "Basic Plan",
            BigDecimal.valueOf(500),
            100,
            "Basic internet plan"
        );
        unitOfWork.plans().save(plan);
        System.out.println("Created plan: " + plan);
      } else {
        plan = planService.getAllPlans().get(0);
        System.out.println("Using existing plan: " + plan);
      }
      System.out.println();

      // === 4. CREATE SUBSCRIBER VIA DTO ===
      System.out.println("4. CREATING SUBSCRIBER VIA DTO");
      System.out.println("------------------------------");
      Address address = new Address(
          UUID.randomUUID(),
          "Kyiv",
          "Main Street",
          "10",
          "5A"
      );
      SubscriberCreateDto subscriberDto = new SubscriberCreateDto(
          "John Doe",
          "+380501234567",
          address,
          plan.getId(),
          registeredUser.getId()
      );
      Subscriber subscriber = subscriberService.registerSubscriber(subscriberDto);
      System.out.println("Created subscriber: " + subscriber);
      System.out.println("Address: " + subscriber.getAddress());
      System.out.println();

      // === 5. ASSIGN PLAN TO SUBSCRIBER ===
      System.out.println("5. ASSIGNING PLAN TO SUBSCRIBER");
      System.out.println("-------------------------------");
      Subscriber updatedSubscriber = subscriberService.assignPlan(subscriber.getId(), plan.getId());
      System.out.println("Subscriber with plan: " + updatedSubscriber);
      System.out.println();

      // === 6. GET ACTIVE SUBSCRIBERS ===
      System.out.println("6. GETTING ACTIVE SUBSCRIBERS");
      System.out.println("-----------------------------");
      var activeSubscribers = subscriberService.getActiveSubscribers();
      System.out.println("Active subscribers count: " + activeSubscribers.size());
      activeSubscribers.forEach(
          s -> System.out.println("  - " + s.getFullName() + " (" + s.getPlan().getName() + ")"));
      System.out.println();

      // === 7. CREATE PAYMENT ===
      System.out.println("7. CREATING PAYMENT");
      System.out.println("-------------------");
      PaymentCreateDto paymentDto = new PaymentCreateDto(
          subscriber.getId(),
          BigDecimal.valueOf(500),
          "CARD"
      );
      var payment = paymentService.makePayment(paymentDto);
      System.out.println("Created payment: " + payment);
      System.out.println();

      // === 8. GET PAYMENTS BY SUBSCRIBER ===
      System.out.println("8. GETTING PAYMENTS BY SUBSCRIBER");
      System.out.println("---------------------------------");
      var payments = paymentService.getPaymentsBySubscriber(subscriber.getId());
      System.out.println("Payments count: " + payments.size());
      payments.forEach(p -> System.out.println("  - " + p));
      System.out.println(
          "Total paid: " + paymentService.getTotalPaidBySubscriber(subscriber.getId()));
      System.out.println();

      // === 9. OPEN TICKET ===
      System.out.println("9. OPENING TICKET");
      System.out.println("-----------------");
      TicketCreateDto ticketDto = new TicketCreateDto(
          subscriber.getId(),
          "Internet connection is slow"
      );
      var ticket = ticketService.openTicket(ticketDto);
      System.out.println("Opened ticket: " + ticket);
      System.out.println();

      // === 10. GET TICKETS BY SUBSCRIBER ===
      System.out.println("10. GETTING TICKETS BY SUBSCRIBER");
      System.out.println("---------------------------------");
      var tickets = ticketService.getTicketsBySubscriber(subscriber.getId());
      System.out.println("Tickets count: " + tickets.size());
      tickets.forEach(t -> System.out.println("  - " + t));
      System.out.println();

      // === 11. CLOSE TICKET ===
      System.out.println("11. CLOSING TICKET");
      System.out.println("------------------");
      var closedTicket = ticketService.closeTicket(ticket.getId());
      System.out.println("Closed ticket: " + closedTicket);
      System.out.println();

      // === SUMMARY ===
      System.out.println("=== SUMMARY ===");
      System.out.println("✓ User registered and authenticated");
      System.out.println("✓ Subscriber created via DTO");
      System.out.println("✓ Plan assigned to subscriber");
      System.out.println("✓ Payment created");
      System.out.println("✓ Ticket opened and closed");
      System.out.println("\nSTAGE 4 TEST COMPLETED SUCCESSFULLY!");

    } catch (Exception e) {
      System.err.println("ERROR: " + e.getMessage());
      e.printStackTrace();
    }
  }
}
