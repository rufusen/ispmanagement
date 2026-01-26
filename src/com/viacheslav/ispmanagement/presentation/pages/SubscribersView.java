package com.viacheslav.ispmanagement.presentation.pages;

import com.viacheslav.ispmanagement.domain.SubscriberService;
import com.viacheslav.ispmanagement.dto.SubscriberCreateDto;
import com.viacheslav.ispmanagement.model.Subscriber;
import com.viacheslav.ispmanagement.model.User;
import com.viacheslav.ispmanagement.presentation.forms.AddSubscriberForm;
import com.viacheslav.ispmanagement.repository.UnitOfWork;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;

public class SubscribersView {

  private final Scanner scanner;
  private final SubscriberService subscriberService;
  private final User currentUser;

  public SubscribersView(Scanner scanner, User currentUser) {
    this.scanner = scanner;
    this.currentUser = currentUser;
    UnitOfWork unitOfWork = new UnitOfWork();
    this.subscriberService = new SubscriberService(unitOfWork);
  }

  public void show() {
    if (currentUser.getRole() == User.Role.SUBSCRIBER) {
      // SUBSCRIBER sees only their profile
      showSubscriberProfile();
      return;
    }

    // ADMIN and OPERATOR see full management menu
    while (true) {
      System.out.println("\n=== АБОНЕНТИ ===");
      System.out.println("1. Список усіх абонентів");
      System.out.println("2. Додати абонента");

      if (currentUser.getRole() == User.Role.ADMIN) {
        System.out.println("3. Редагувати абонента");
        System.out.println("4. Видалити абонента");
      }

      System.out.println("0. Назад");
      System.out.print("Оберіть опцію: ");

      String choice = scanner.nextLine().trim();

      switch (choice) {
        case "1":
          listSubscribers();
          break;
        case "2":
          addSubscriber();
          break;
        case "3":
          if (currentUser.getRole() == User.Role.ADMIN) {
            editSubscriber();
          } else {
            System.out.println("Невірний вибір.");
          }
          break;
        case "4":
          if (currentUser.getRole() == User.Role.ADMIN) {
            deleteSubscriber();
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

  private void showSubscriberProfile() {
    System.out.println("\n=== МІЙ ПРОФІЛЬ ===");
    Subscriber subscriber = subscriberService.findByUserId(currentUser.getId());

    // SubscriberService guarantees subscriber exists for SUBSCRIBER role
    if (subscriber == null) {
      System.out.println("Профіль абонента не знайдено.");
      System.out.println("0. Назад");
      String choice = scanner.nextLine().trim();
      return;
    }

    System.out.println("Ім'я: " + subscriber.getFullName());
    System.out.println("Email: " + currentUser.getEmail());
    System.out.println("Телефон: " + subscriber.getPhone());

    if (subscriber.getPlan() != null) {
      System.out.println("Тариф: " + subscriber.getPlan().getName());
      System.out.println("Швидкість: " + subscriber.getPlan().getSpeedMbps() + " Мбіт/с");
      System.out.println("Місячна плата: " + subscriber.getPlan().getPrice());
    } else {
      System.out.println("Тариф: не призначено");
      System.out.println("Швидкість: -");
      System.out.println("Місячна плата: -");
    }

    System.out.println("\nДодаткова інформація (тільки для читання):");
    System.out.println("--------------------------------");
    System.out.println("Для зміни тарифу або відключення послуги:");
    System.out.println("Будь ласка, зверніться до служби підтримки:");
    System.out.println("+380 44 000 00 00");
    System.out.println("--------------------------------");

    System.out.println("\n0. Назад");
    String choice = scanner.nextLine().trim();
  }

  private void listSubscribers() {
    System.out.println("\n--- Усі абоненти ---");
    List<Subscriber> subscribers = subscriberService.getAllSubscribers();

    if (subscribers.isEmpty()) {
      System.out.println("Абонентів не знайдено.");
      return;
    }

    for (int i = 0; i < subscribers.size(); i++) {
      Subscriber s = subscribers.get(i);
      System.out.println(formatSubscriber(i + 1, s));
    }
  }

  private String formatSubscriber(int index, Subscriber subscriber) {
    String planName = subscriber.getPlan() != null ? subscriber.getPlan().getName() : "Немає";
    return index + ". " + subscriber.getFullName() + " | " + subscriber.getPhone() +
        " | Тариф: " + planName;
  }

  private void addSubscriber() {
    System.out.println("\n--- Додати абонента ---");
    AddSubscriberForm form = new AddSubscriberForm(scanner);

    try {
      SubscriberCreateDto dto = form.collectSubscriberData(currentUser.getId());
      Subscriber subscriber = subscriberService.registerSubscriber(dto);
      System.out.println("Абонента додано успішно: " + subscriber.getFullName());
    } catch (IllegalArgumentException e) {
      System.out.println("Помилка: " + e.getMessage());
    } catch (Exception e) {
      System.out.println("Помилка: " + e.getMessage());
    }
  }

  private void editSubscriber() {
    System.out.println("\n--- Редагувати абонента ---");
    List<Subscriber> subscribers = subscriberService.getAllSubscribers();

    if (subscribers.isEmpty()) {
      System.out.println("Абонентів не знайдено.");
      return;
    }

    // Show list for selection
    for (int i = 0; i < subscribers.size(); i++) {
      System.out.println(formatSubscriber(i + 1, subscribers.get(i)));
    }

    System.out.print("Оберіть номер абонента для редагування: ");
    String indexStr = scanner.nextLine().trim();

    try {
      int index = Integer.parseInt(indexStr);
      if (index < 1 || index > subscribers.size()) {
        System.out.println("Невірний номер. Оберіть число від 1 до " + subscribers.size());
        return;
      }

      Subscriber subscriber = subscribers.get(index - 1);
      System.out.println("Поточний абонент: " + subscriber.getFullName());
      System.out.println("Поточний телефон: " + subscriber.getPhone());
      System.out.println(
          "Поточний тариф: " + (subscriber.getPlan() != null ? subscriber.getPlan().getName()
              : "Немає"));
      System.out.println();

      System.out.print("Нове повне ім'я (або натисніть Enter, щоб пропустити): ");
      String fullName = scanner.nextLine().trim();
      if (fullName.isEmpty()) {
        fullName = null;
      }

      System.out.print("Новий телефон (або натисніть Enter, щоб пропустити): ");
      String phone = scanner.nextLine().trim();
      if (phone.isEmpty()) {
        phone = null;
      }

      // Plan selection menu
      UUID planId = selectPlanForEdit();

      subscriberService.updateSubscriber(subscriber.getId(), fullName, phone, planId);
      System.out.println("Абонента оновлено успішно.");
    } catch (NumberFormatException e) {
      System.out.println("Помилка: Введіть число.");
    } catch (IllegalArgumentException e) {
      System.out.println("Помилка: " + e.getMessage());
    } catch (Exception e) {
      System.out.println("Помилка: " + e.getMessage());
    }
  }

  private UUID selectPlanForEdit() {
    UnitOfWork unitOfWork = new UnitOfWork();
    List<com.viacheslav.ispmanagement.model.Plan> plans = unitOfWork.plans().findAll();

    if (plans.isEmpty()) {
      return null;
    }

    System.out.println("\nОберіть новий тариф (або натисніть Enter, щоб пропустити):");
    for (int i = 0; i < plans.size(); i++) {
      com.viacheslav.ispmanagement.model.Plan p = plans.get(i);
      System.out.println((i + 1) + ". " + p.getName() + " – " + p.getPrice() + " грн");
    }

    System.out.print("Вибір: ");
    String choiceStr = scanner.nextLine().trim();

    if (choiceStr.isEmpty()) {
      return null;
    }

    try {
      int choice = Integer.parseInt(choiceStr);
      if (choice < 1 || choice > plans.size()) {
        System.out.println("Невірний вибір. Тариф не змінено.");
        return null;
      }
      return plans.get(choice - 1).getId();
    } catch (NumberFormatException e) {
      System.out.println("Невірний формат. Тариф не змінено.");
      return null;
    }
  }

  private void deleteSubscriber() {
    System.out.println("\n--- Видалити абонента ---");
    List<Subscriber> subscribers = subscriberService.getAllSubscribers();

    if (subscribers.isEmpty()) {
      System.out.println("Абонентів не знайдено.");
      return;
    }

    // Show list for selection
    for (int i = 0; i < subscribers.size(); i++) {
      System.out.println(formatSubscriber(i + 1, subscribers.get(i)));
    }

    System.out.print("Оберіть номер абонента для видалення: ");
    String indexStr = scanner.nextLine().trim();

    try {
      int index = Integer.parseInt(indexStr);
      if (index < 1 || index > subscribers.size()) {
        System.out.println("Невірний номер. Оберіть число від 1 до " + subscribers.size());
        return;
      }

      Subscriber subscriber = subscribers.get(index - 1);
      System.out.print(
          "Ви впевнені, що хочете видалити " + subscriber.getFullName() + "? (так/ні): ");
      String confirm = scanner.nextLine().trim().toLowerCase();

      if ("так".equals(confirm) || "yes".equals(confirm)) {
        subscriberService.deleteSubscriber(subscriber.getId());
        System.out.println("Абонента видалено успішно.");
      } else {
        System.out.println("Видалення скасовано.");
      }
    } catch (NumberFormatException e) {
      System.out.println("Помилка: Введіть число.");
    } catch (IllegalArgumentException e) {
      System.out.println("Помилка: " + e.getMessage());
    } catch (Exception e) {
      System.out.println("Помилка: " + e.getMessage());
    }
  }
}
