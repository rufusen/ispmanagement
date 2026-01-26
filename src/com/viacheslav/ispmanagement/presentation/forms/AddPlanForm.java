package com.viacheslav.ispmanagement.presentation.forms;

import java.math.BigDecimal;
import java.util.Scanner;

public class AddPlanForm {

  private final Scanner scanner;

  public AddPlanForm(Scanner scanner) {
    this.scanner = scanner;
  }

  public Object[] collectPlanData() {
    System.out.print("Назва тарифу: ");
    String name = scanner.nextLine().trim();

    System.out.print("Ціна: ");
    String priceStr = scanner.nextLine().trim();
    BigDecimal price;
    try {
      price = new BigDecimal(priceStr);
    } catch (NumberFormatException e) {
      throw new IllegalArgumentException("Невірний формат ціни");
    }

    System.out.print("Швидкість (Мбіт/с): ");
    String speedStr = scanner.nextLine().trim();
    int speedMbps;
    try {
      speedMbps = Integer.parseInt(speedStr);
    } catch (NumberFormatException e) {
      throw new IllegalArgumentException("Невірний формат швидкості");
    }

    System.out.print("Опис: ");
    String description = scanner.nextLine().trim();

    return new Object[]{name, price, speedMbps, description};
  }
}
