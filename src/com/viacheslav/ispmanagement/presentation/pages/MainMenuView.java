package com.viacheslav.ispmanagement.presentation.pages;

import com.viacheslav.ispmanagement.model.User;
import java.util.Scanner;

public class MainMenuView {

  private final Scanner scanner;
  private final User currentUser;

  public MainMenuView(Scanner scanner, User currentUser) {
    this.scanner = scanner;
    this.currentUser = currentUser;
  }

  public String show() {
    System.out.println("\n=== ГОЛОВНЕ МЕНЮ ===");
    System.out.println(
        "Ввійшли як: " + currentUser.getEmail() + " (" + currentUser.getRole() + ")");
    System.out.println();

    if (currentUser.getRole() == User.Role.SUBSCRIBER) {
      // SUBSCRIBER menu
      System.out.println("1. Мій профіль");
      System.out.println("2. Мої платежі");
      System.out.println("3. Мої квитки");
    } else {
      // ADMIN and OPERATOR menu
      System.out.println("1. Абоненти");
      System.out.println("2. Тарифи");
      System.out.println("3. Платежі");
      System.out.println("4. Квитки");
    }

    System.out.println("0. Вихід");
    System.out.print("Оберіть опцію: ");

    return scanner.nextLine().trim();
  }
}
