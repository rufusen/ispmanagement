package com.viacheslav.ispmanagement.presentation.forms;

import com.viacheslav.ispmanagement.domain.PlanService;
import com.viacheslav.ispmanagement.dto.SubscriberCreateDto;
import com.viacheslav.ispmanagement.model.Address;
import com.viacheslav.ispmanagement.model.Plan;
import com.viacheslav.ispmanagement.repository.UnitOfWork;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;

public class AddSubscriberForm {

  private final Scanner scanner;
  private final PlanService planService;

  public AddSubscriberForm(Scanner scanner) {
    this.scanner = scanner;
    UnitOfWork unitOfWork = new UnitOfWork();
    this.planService = new PlanService(unitOfWork);
  }

  public SubscriberCreateDto collectSubscriberData(UUID userId) {
    System.out.print("Повне ім'я: ");
    String fullName = scanner.nextLine().trim();

    System.out.print("Телефон: ");
    String phone = scanner.nextLine().trim();

    System.out.print("Місто: ");
    String city = scanner.nextLine().trim();

    System.out.print("Вулиця: ");
    String street = scanner.nextLine().trim();

    System.out.print("Будинок: ");
    String house = scanner.nextLine().trim();

    System.out.print("Квартира: ");
    String apartment = scanner.nextLine().trim();

    Address address = new Address(UUID.randomUUID(), city, street, house, apartment);

    // Show plans menu instead of UUID input
    UUID planId = selectPlanFromMenu();

    return new SubscriberCreateDto(fullName, phone, address, planId, userId);
  }

  private UUID selectPlanFromMenu() {
    List<Plan> plans = planService.getAllPlans();

    if (plans.isEmpty()) {
      throw new IllegalArgumentException("Тарифів не знайдено. Спочатку додайте тариф.");
    }

    System.out.println("\nОберіть тариф:");
    for (int i = 0; i < plans.size(); i++) {
      Plan p = plans.get(i);
      System.out.println((i + 1) + ". " + p.getName() + " – " + p.getPrice() + " грн (" +
          p.getSpeedMbps() + " Мбіт/с)");
    }

    System.out.print("Вибір (1-" + plans.size() + "): ");
    String choiceStr = scanner.nextLine().trim();

    try {
      int choice = Integer.parseInt(choiceStr);
      if (choice < 1 || choice > plans.size()) {
        throw new IllegalArgumentException(
            "Невірний вибір. Оберіть число від 1 до " + plans.size());
      }
      return plans.get(choice - 1).getId();
    } catch (NumberFormatException e) {
      throw new IllegalArgumentException("Невірний формат. Введіть число від 1 до " + plans.size());
    }
  }
}
