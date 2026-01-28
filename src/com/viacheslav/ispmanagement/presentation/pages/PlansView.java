package com.viacheslav.ispmanagement.presentation.pages;

import com.viacheslav.ispmanagement.domain.PlanService;
import com.viacheslav.ispmanagement.model.Plan;
import com.viacheslav.ispmanagement.model.User;
import com.viacheslav.ispmanagement.presentation.forms.AddPlanForm;
import com.viacheslav.ispmanagement.repository.UnitOfWork;
import java.math.BigDecimal;
import java.util.List;
import java.util.Scanner;

public class PlansView {

  private final Scanner scanner;
  private final PlanService planService;
  private final User currentUser;

  public PlansView(Scanner scanner, User currentUser) {
    this.scanner = scanner;
    this.currentUser = currentUser;
    UnitOfWork unitOfWork = new UnitOfWork();
    this.planService = new PlanService(unitOfWork);
  }

  public void show() {
    // SUBSCRIBER should not access this view
    if (currentUser.getRole() == User.Role.SUBSCRIBER) {
      System.out.println("Доступ заборонено. Абоненти не можуть переглядати тарифи через це меню.");
      return;
    }

    while (true) {
      System.out.println("\n=== ТАРИФИ ===");
      System.out.println("1. Список усіх тарифів");

      if (currentUser.getRole() == User.Role.ADMIN) {
        System.out.println("2. Додати тариф");
      }

      System.out.println("0. Назад");
      System.out.print("Оберіть опцію: ");

      String choice = scanner.nextLine().trim();

      switch (choice) {
        case "1":
          listPlans();
          break;
        case "2":
          if (currentUser.getRole() == User.Role.ADMIN) {
            addPlan();
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

  private void listPlans() {
    System.out.println("\n--- Усі тарифи ---");
    List<Plan> plans = planService.getAllPlans();

    if (plans.isEmpty()) {
      System.out.println("Тарифів не знайдено.");
      return;
    }

    for (int i = 0; i < plans.size(); i++) {
      Plan p = plans.get(i);
      System.out.println(formatPlan(i + 1, p));
    }
  }

  private String formatPlan(int index, Plan plan) {
    StringBuilder sb = new StringBuilder();
    sb.append(index).append(". ").append(plan.getName())
        .append(" | Ціна: ").append(plan.getPrice())
        .append(" | Швидкість: ").append(plan.getSpeedMbps()).append(" Мбіт/с");
    if (plan.getDescription() != null && !plan.getDescription().isEmpty()) {
      sb.append("\n   Опис: ").append(plan.getDescription());
    }
    return sb.toString();
  }

  private void addPlan() {
    System.out.println("\n--- Додати тариф ---");
    AddPlanForm form = new AddPlanForm(scanner);

    try {
      Object[] data = form.collectPlanData();
      String name = (String) data[0];
      BigDecimal price = (BigDecimal) data[1];
      int speedMbps = (Integer) data[2];
      String description = (String) data[3];

      Plan plan = planService.createPlan(name, price, speedMbps, description);
      System.out.println("Тариф додано успішно: " + plan.getName());
    } catch (IllegalArgumentException e) {
      System.out.println("Помилка: " + e.getMessage());
    } catch (Exception e) {
      System.out.println("Помилка: " + e.getMessage());
      e.printStackTrace();
    }
  }
}
