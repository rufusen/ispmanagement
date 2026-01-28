package com.viacheslav.ispmanagement.presentation.pages;

import com.viacheslav.ispmanagement.domain.TicketService;
import com.viacheslav.ispmanagement.dto.TicketCreateDto;
import com.viacheslav.ispmanagement.model.Subscriber;
import com.viacheslav.ispmanagement.model.Ticket;
import com.viacheslav.ispmanagement.model.User;
import com.viacheslav.ispmanagement.repository.UnitOfWork;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;

public class TicketsView {

  private final Scanner scanner;
  private final TicketService ticketService;
  private final User currentUser;

  public TicketsView(Scanner scanner, User currentUser) {
    this.scanner = scanner;
    this.currentUser = currentUser;
    UnitOfWork unitOfWork = new UnitOfWork();
    this.ticketService = new TicketService(unitOfWork);
  }

  public void show() {
    if (currentUser.getRole() == User.Role.SUBSCRIBER) {
      // SUBSCRIBER sees only their tickets
      showMyTickets();
      return;
    }

    // ADMIN and OPERATOR see full management menu
    while (true) {
      System.out.println("\n=== КВИТКИ ===");
      System.out.println("1. Список усіх квитків");
      System.out.println("2. Переглянути відкриті квитки");
      System.out.println("3. Переглянути квитки за абонентом");

      if (currentUser.getRole() == User.Role.ADMIN || currentUser.getRole() == User.Role.OPERATOR) {
        System.out.println("4. Додати квиток");
        System.out.println("5. Оновити статус квитка");
      }

      System.out.println("0. Назад");
      System.out.print("Оберіть опцію: ");

      String choice = scanner.nextLine().trim();

      switch (choice) {
        case "1":
          listAllTickets();
          break;
        case "2":
          listOpenTickets();
          break;
        case "3":
          viewTicketsBySubscriber();
          break;
        case "4":
          if (currentUser.getRole() == User.Role.ADMIN
              || currentUser.getRole() == User.Role.OPERATOR) {
            addTicket();
          } else {
            System.out.println("Невірний вибір.");
          }
          break;
        case "5":
          if (currentUser.getRole() == User.Role.ADMIN
              || currentUser.getRole() == User.Role.OPERATOR) {
            updateTicketStatus();
          } else {
            System.out.println("Невірний вибір.");
          }
          break;
        case "0":
          return;
        default:
          System.out.println("Невірний вибір. Спробуйте ще раз.");
      }
    }
  }

  private void showMyTickets() {
    System.out.println("\n=== МОЇ КВИТКИ ===");
    // Use same UnitOfWork for all services to ensure consistency
    UnitOfWork unitOfWork = new UnitOfWork();
    com.viacheslav.ispmanagement.domain.SubscriberService subscriberService =
        new com.viacheslav.ispmanagement.domain.SubscriberService(unitOfWork);
    TicketService localTicketService = new TicketService(unitOfWork);

    Subscriber subscriber = subscriberService.findByUserId(currentUser.getId());

    // SubscriberService guarantees subscriber exists for SUBSCRIBER role
    if (subscriber == null) {
      System.out.println("--------------------------------");
      System.out.println("У вас немає квитків підтримки.");
      System.out.println(
          "Якщо у вас виникли проблеми, будь ласка, зверніться до служби підтримки.");
      System.out.println("--------------------------------");
      System.out.println("\n0. Назад");
      String choice = scanner.nextLine().trim();
      return;
    }

    List<Ticket> tickets = localTicketService.getTicketsBySubscriber(subscriber.getId());

    if (tickets.isEmpty()) {
      System.out.println("--------------------------------");
      System.out.println("У вас немає квитків підтримки.");
      System.out.println(
          "Якщо у вас виникли проблеми, будь ласка, зверніться до служби підтримки.");
      System.out.println("--------------------------------");
    } else {
      for (int i = 0; i < tickets.size(); i++) {
        Ticket t = tickets.get(i);
        System.out.println((i + 1) + ". Статус: " + t.getStatus() +
            " | Створено: " + t.getCreatedAt() +
            " | Опис: " + t.getDescription());
      }
    }

    System.out.println("\n0. Назад");
    String choice = scanner.nextLine().trim();
  }

  private void listAllTickets() {
    System.out.println("\n--- Усі квитки ---");
    UnitOfWork unitOfWork = new UnitOfWork();
    List<Ticket> tickets = unitOfWork.tickets().findAll();

    if (tickets.isEmpty()) {
      System.out.println("Квитків не знайдено.");
      return;
    }

    for (int i = 0; i < tickets.size(); i++) {
      Ticket t = tickets.get(i);
      System.out.println(formatTicket(i + 1, t));
    }
  }

  private String formatTicket(int index, Ticket ticket) {
    return index + ". Статус: " + ticket.getStatus() +
        " | Створено: " + ticket.getCreatedAt() +
        " | Опис: " + ticket.getDescription();
  }

  private void listOpenTickets() {
    System.out.println("\n--- Відкриті квитки ---");
    List<Ticket> tickets = ticketService.getOpenTickets();

    if (tickets.isEmpty()) {
      System.out.println("Відкритих квитків не знайдено.");
      return;
    }

    for (int i = 0; i < tickets.size(); i++) {
      Ticket t = tickets.get(i);
      System.out.println((i + 1) + ". Створено: " + t.getCreatedAt() +
          " | Опис: " + t.getDescription());
    }
  }

  private void viewTicketsBySubscriber() {
    System.out.println("\n--- Квитки за абонентом ---");
    // Use same UnitOfWork for consistency
    UnitOfWork unitOfWork = new UnitOfWork();
    com.viacheslav.ispmanagement.domain.SubscriberService subscriberService =
        new com.viacheslav.ispmanagement.domain.SubscriberService(unitOfWork);
    TicketService localTicketService = new TicketService(unitOfWork);

    List<Subscriber> subscribers = subscriberService.getAllSubscribers();

    if (subscribers.isEmpty()) {
      System.out.println("Абонентів не знайдено.");
      return;
    }

    // Show subscribers for selection
    for (int i = 0; i < subscribers.size(); i++) {
      Subscriber s = subscribers.get(i);
      System.out.println((i + 1) + ". " + s.getFullName() + " | " + s.getPhone());
    }

    System.out.print("Оберіть номер абонента: ");
    String indexStr = scanner.nextLine().trim();

    try {
      int index = Integer.parseInt(indexStr);
      if (index < 1 || index > subscribers.size()) {
        System.out.println("Невірний номер. Оберіть число від 1 до " + subscribers.size());
        return;
      }

      Subscriber subscriber = subscribers.get(index - 1);
      List<Ticket> tickets = localTicketService.getTicketsBySubscriber(subscriber.getId());

      if (tickets.isEmpty()) {
        System.out.println("Квитків для цього абонента не знайдено.");
        return;
      }

      System.out.println("\nКвитки для абонента " + subscriber.getFullName() + ":");
      for (int i = 0; i < tickets.size(); i++) {
        Ticket t = tickets.get(i);
        System.out.println(formatTicket(i + 1, t));
      }
    } catch (NumberFormatException e) {
      System.out.println("Помилка: Введіть число.");
    } catch (IllegalArgumentException e) {
      System.out.println("Помилка: " + e.getMessage());
    } catch (Exception e) {
      System.out.println("Помилка: " + e.getMessage());
      e.printStackTrace();
    }
  }

  private void addTicket() {
    System.out.println("\n--- Додати квиток ---");
    // Use same UnitOfWork for consistency
    UnitOfWork unitOfWork = new UnitOfWork();
    com.viacheslav.ispmanagement.domain.SubscriberService subscriberService =
        new com.viacheslav.ispmanagement.domain.SubscriberService(unitOfWork);
    TicketService localTicketService = new TicketService(unitOfWork);

    List<Subscriber> subscribers = subscriberService.getAllSubscribers();

    if (subscribers.isEmpty()) {
      System.out.println("Абонентів не знайдено.");
      return;
    }

    // Show subscribers for selection
    for (int i = 0; i < subscribers.size(); i++) {
      Subscriber s = subscribers.get(i);
      System.out.println((i + 1) + ". " + s.getFullName() + " | " + s.getPhone());
    }

    System.out.print("Оберіть номер абонента: ");
    String indexStr = scanner.nextLine().trim();

    System.out.print("Опис: ");
    String description = scanner.nextLine().trim();

    try {
      int index = Integer.parseInt(indexStr);
      if (index < 1 || index > subscribers.size()) {
        System.out.println("Невірний номер. Оберіть число від 1 до " + subscribers.size());
        return;
      }

      UUID subscriberId = subscribers.get(index - 1).getId();
      TicketCreateDto dto = new TicketCreateDto(subscriberId, description);
      Ticket ticket = localTicketService.openTicket(dto);
      System.out.println("Квиток створено успішно.");
    } catch (NumberFormatException e) {
      System.out.println("Помилка: Введіть число.");
    } catch (IllegalArgumentException e) {
      System.out.println("Помилка: " + e.getMessage());
    } catch (Exception e) {
      System.out.println("Помилка: " + e.getMessage());
      e.printStackTrace();
    }
  }

  private void updateTicketStatus() {
    System.out.println("\n--- Оновити статус квитка ---");
    // Use same UnitOfWork for consistency
    UnitOfWork unitOfWork = new UnitOfWork();
    TicketService localTicketService = new TicketService(unitOfWork);
    List<Ticket> tickets = unitOfWork.tickets().findAll();

    if (tickets.isEmpty()) {
      System.out.println("Квитків не знайдено.");
      return;
    }

    // Show tickets for selection
    for (int i = 0; i < tickets.size(); i++) {
      Ticket t = tickets.get(i);
      System.out.println(formatTicket(i + 1, t));
    }

    System.out.print("Оберіть номер квитка: ");
    String indexStr = scanner.nextLine().trim();

    System.out.println("Новий статус:");
    System.out.println("1. OPEN");
    System.out.println("2. IN_PROGRESS");
    System.out.println("3. CLOSED");
    System.out.print("Оберіть статус: ");
    String statusChoice = scanner.nextLine().trim();

    try {
      int index = Integer.parseInt(indexStr);
      if (index < 1 || index > tickets.size()) {
        System.out.println("Невірний номер. Оберіть число від 1 до " + tickets.size());
        return;
      }

      UUID ticketId = tickets.get(index - 1).getId();
      Ticket ticket;

      switch (statusChoice) {
        case "1":
          ticket = localTicketService.findById(ticketId);
          if (ticket != null) {
            ticket.setStatus(Ticket.Status.OPEN);
            unitOfWork.tickets().save(ticket);
            System.out.println("Статус квитка оновлено на OPEN.");
          } else {
            System.out.println("Квиток не знайдено.");
          }
          break;
        case "2":
          ticket = localTicketService.startTicket(ticketId);
          System.out.println("Статус квитка оновлено на IN_PROGRESS.");
          break;
        case "3":
          ticket = localTicketService.closeTicket(ticketId);
          System.out.println("Статус квитка оновлено на CLOSED.");
          break;
        default:
          System.out.println("Невірний вибір статусу.");
      }
    } catch (NumberFormatException e) {
      System.out.println("Помилка: Введіть число.");
    } catch (IllegalArgumentException e) {
      System.out.println("Помилка: " + e.getMessage());
    } catch (Exception e) {
      System.out.println("Помилка: " + e.getMessage());
      e.printStackTrace();
    }
  }
}
