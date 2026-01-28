package com.viacheslav.ispmanagement.presentation.pages;

import com.viacheslav.ispmanagement.domain.PaymentService;
import com.viacheslav.ispmanagement.dto.PaymentCreateDto;
import com.viacheslav.ispmanagement.model.Payment;
import com.viacheslav.ispmanagement.model.Subscriber;
import com.viacheslav.ispmanagement.model.User;
import com.viacheslav.ispmanagement.repository.UnitOfWork;
import java.math.BigDecimal;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;

public class PaymentsView {

  private final Scanner scanner;
  private final PaymentService paymentService;
  private final User currentUser;

  public PaymentsView(Scanner scanner, User currentUser) {
    this.scanner = scanner;
    this.currentUser = currentUser;
    UnitOfWork unitOfWork = new UnitOfWork();
    this.paymentService = new PaymentService(unitOfWork);
  }

  public void show() {
    if (currentUser.getRole() == User.Role.SUBSCRIBER) {
      // SUBSCRIBER sees only their payments
      showMyPayments();
      return;
    }

    // ADMIN and OPERATOR see full management menu
    while (true) {
      System.out.println("\n=== ПЛАТЕЖІ ===");
      System.out.println("1. Список усіх платежів");
      System.out.println("2. Переглянути платежі за абонентом");

      if (currentUser.getRole() == User.Role.ADMIN || currentUser.getRole() == User.Role.OPERATOR) {
        System.out.println("3. Додати платіж");
      }

      System.out.println("0. Назад");
      System.out.print("Оберіть опцію: ");

      String choice = scanner.nextLine().trim();

      switch (choice) {
        case "1":
          listAllPayments();
          break;
        case "2":
          viewPaymentsBySubscriber();
          break;
        case "3":
          if (currentUser.getRole() == User.Role.ADMIN
              || currentUser.getRole() == User.Role.OPERATOR) {
            addPayment();
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

  private void showMyPayments() {
    System.out.println("\n=== МОЇ ПЛАТЕЖІ ===");
    // Use same UnitOfWork for all services to ensure consistency
    UnitOfWork unitOfWork = new UnitOfWork();
    com.viacheslav.ispmanagement.domain.SubscriberService subscriberService =
        new com.viacheslav.ispmanagement.domain.SubscriberService(unitOfWork);
    PaymentService localPaymentService = new PaymentService(unitOfWork);

    Subscriber subscriber = subscriberService.findByUserId(currentUser.getId());

    // SubscriberService guarantees subscriber exists for SUBSCRIBER role
    if (subscriber == null) {
      System.out.println("--------------------------------");
      System.out.println("У вас поки що немає платежів.");
      System.out.println("Ваша місячна плата з'явиться тут після оплати.");
      System.out.println("--------------------------------");
      System.out.println("\n0. Назад");
      String choice = scanner.nextLine().trim();
      return;
    }

    List<Payment> payments = localPaymentService.getPaymentsBySubscriber(subscriber.getId());

    if (payments.isEmpty()) {
      System.out.println("--------------------------------");
      System.out.println("У вас поки що немає платежів.");
      System.out.println("Ваша місячна плата з'явиться тут після оплати.");
      System.out.println("--------------------------------");
    } else {
      for (int i = 0; i < payments.size(); i++) {
        Payment p = payments.get(i);
        System.out.println((i + 1) + ". Сума: " + p.getAmount() +
            " | Метод: " + p.getMethod() +
            " | Дата: " + p.getPaymentDate());
      }

      BigDecimal total = localPaymentService.getTotalPaidBySubscriber(subscriber.getId());
      System.out.println("\nВсього сплачено: " + total);
    }

    System.out.println("\n0. Назад");
    String choice = scanner.nextLine().trim();
  }

  private void listAllPayments() {
    System.out.println("\n--- Усі платежі ---");
    UnitOfWork unitOfWork = new UnitOfWork();
    List<Payment> payments = unitOfWork.payments().findAll();

    if (payments.isEmpty()) {
      System.out.println("Платежів не знайдено.");
      return;
    }

    for (int i = 0; i < payments.size(); i++) {
      Payment p = payments.get(i);
      System.out.println(formatPayment(i + 1, p));
    }
  }

  private String formatPayment(int index, Payment payment) {
    return index + ". Сума: " + payment.getAmount() +
        " | Метод: " + payment.getMethod() +
        " | Дата: " + payment.getPaymentDate();
  }

  private void viewPaymentsBySubscriber() {
    System.out.println("\n--- Платежі за абонентом ---");
    // Use same UnitOfWork for consistency
    UnitOfWork unitOfWork = new UnitOfWork();
    com.viacheslav.ispmanagement.domain.SubscriberService subscriberService =
        new com.viacheslav.ispmanagement.domain.SubscriberService(unitOfWork);
    PaymentService localPaymentService = new PaymentService(unitOfWork);

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
      List<Payment> payments = localPaymentService.getPaymentsBySubscriber(subscriber.getId());

      if (payments.isEmpty()) {
        System.out.println("Платежів для цього абонента не знайдено.");
        return;
      }

      System.out.println("\nПлатежі для абонента " + subscriber.getFullName() + ":");
      for (int i = 0; i < payments.size(); i++) {
        Payment p = payments.get(i);
        System.out.println(formatPayment(i + 1, p));
      }

      BigDecimal total = localPaymentService.getTotalPaidBySubscriber(subscriber.getId());
      System.out.println("\nВсього сплачено: " + total);
    } catch (NumberFormatException e) {
      System.out.println("Помилка: Введіть число.");
    } catch (IllegalArgumentException e) {
      System.out.println("Помилка: " + e.getMessage());
    } catch (Exception e) {
      System.out.println("Помилка: " + e.getMessage());
      e.printStackTrace();
    }
  }

  private void addPayment() {
    System.out.println("\n--- Додати платіж ---");
    // Use same UnitOfWork for consistency
    UnitOfWork unitOfWork = new UnitOfWork();
    com.viacheslav.ispmanagement.domain.SubscriberService subscriberService =
        new com.viacheslav.ispmanagement.domain.SubscriberService(unitOfWork);
    PaymentService localPaymentService = new PaymentService(unitOfWork);

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

    System.out.print("Сума: ");
    String amountStr = scanner.nextLine().trim();

    System.out.print("Метод оплати: ");
    String method = scanner.nextLine().trim();

    try {
      int index = Integer.parseInt(indexStr);
      if (index < 1 || index > subscribers.size()) {
        System.out.println("Невірний номер. Оберіть число від 1 до " + subscribers.size());
        return;
      }

      UUID subscriberId = subscribers.get(index - 1).getId();
      BigDecimal amount = new BigDecimal(amountStr);

      PaymentCreateDto dto = new PaymentCreateDto(subscriberId, amount, method);
      Payment payment = localPaymentService.makePayment(dto);
      System.out.println("Платіж додано успішно: " + payment.getAmount());
    } catch (NumberFormatException e) {
      System.out.println("Помилка: Введіть коректне число.");
    } catch (IllegalArgumentException e) {
      System.out.println("Помилка: " + e.getMessage());
    } catch (Exception e) {
      System.out.println("Помилка: " + e.getMessage());
      e.printStackTrace();
    }
  }
}
