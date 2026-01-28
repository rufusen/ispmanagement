package com.viacheslav.ispmanagement.presentation;

import com.viacheslav.ispmanagement.model.User;
import com.viacheslav.ispmanagement.presentation.pages.AuthView;
import com.viacheslav.ispmanagement.presentation.pages.MainMenuView;
import com.viacheslav.ispmanagement.presentation.pages.PaymentsView;
import com.viacheslav.ispmanagement.presentation.pages.PlansView;
import com.viacheslav.ispmanagement.presentation.pages.SubscribersView;
import com.viacheslav.ispmanagement.presentation.pages.TicketsView;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class ConsoleApplication {

  private final Scanner scanner;

  public ConsoleApplication() {
    this.scanner = new Scanner(
        new InputStreamReader(System.in, StandardCharsets.UTF_8)
    );
  }

  public static void main(String[] args) {
    configureUtf8Console();
    new ConsoleApplication().run();
  }

  /**
   * Фікс кирилиці для Windows + EXE (Launch4j)
   */
  private static void configureUtf8Console() {
    try {
      // Міняємо code page Windows на UTF-8
      new ProcessBuilder("cmd", "/c", "chcp 65001")
          .inheritIO()
          .start()
          .waitFor();
    } catch (Exception ignored) {
    }

    // Фіксимо Java output
    System.setOut(new PrintStream(System.out, true, StandardCharsets.UTF_8));
    System.setErr(new PrintStream(System.err, true, StandardCharsets.UTF_8));
  }

  public void run() {
    System.out.println("=== Система управління ISP ===");

    while (true) {
      AuthView authView = new AuthView(scanner);
      User currentUser = authView.show();

      if (currentUser == null) {
        System.out.println("До побачення!");
        break;
      }

      while (true) {
        MainMenuView mainMenu = new MainMenuView(scanner, currentUser);
        String choice = mainMenu.show();

        if (currentUser.getRole() == User.Role.SUBSCRIBER) {
          switch (choice) {
            case "1":
              new SubscribersView(scanner, currentUser).show();
              break;
            case "2":
              new PaymentsView(scanner, currentUser).show();
              break;
            case "3":
              new TicketsView(scanner, currentUser).show();
              break;
            case "0":
              System.out.println("Вихід з системи...");
              break;
            default:
              System.out.println("Невірний вибір. Спробуйте ще раз.");
              continue;
          }
        } else {
          switch (choice) {
            case "1":
              new SubscribersView(scanner, currentUser).show();
              break;
            case "2":
              new PlansView(scanner, currentUser).show();
              break;
            case "3":
              new PaymentsView(scanner, currentUser).show();
              break;
            case "4":
              new TicketsView(scanner, currentUser).show();
              break;
            case "0":
              System.out.println("Вихід з системи...");
              break;
            default:
              System.out.println("Невірний вибір. Спробуйте ще раз.");
              continue;
          }
        }

        if ("0".equals(choice)) {
          break;
        }
      }
    }

    scanner.close();
  }
}
