package com.viacheslav.ispmanagement.presentation;

import com.viacheslav.ispmanagement.model.User;
import com.viacheslav.ispmanagement.presentation.pages.AuthView;
import com.viacheslav.ispmanagement.presentation.pages.MainMenuView;
import com.viacheslav.ispmanagement.presentation.pages.PaymentsView;
import com.viacheslav.ispmanagement.presentation.pages.PlansView;
import com.viacheslav.ispmanagement.presentation.pages.SubscribersView;
import com.viacheslav.ispmanagement.presentation.pages.TicketsView;
import java.util.Scanner;

public class ConsoleApplication {

  private final Scanner scanner;

  public ConsoleApplication() {
    this.scanner = new Scanner(System.in);
  }

  public static void main(String[] args) {
    ConsoleApplication app = new ConsoleApplication();
    app.run();
  }

  public void run() {
    System.out.println("=== Система управління ISP ===");

    while (true) {
      // Show authentication
      AuthView authView = new AuthView(scanner);
      User currentUser = authView.show();

      if (currentUser == null) {
        System.out.println("До побачення!");
        break;
      }

      // Main menu loop
      while (true) {
        MainMenuView mainMenu = new MainMenuView(scanner, currentUser);
        String choice = mainMenu.show();

        if (currentUser.getRole() == User.Role.SUBSCRIBER) {
          // SUBSCRIBER menu
          switch (choice) {
            case "1":
              SubscribersView subscribersView = new SubscribersView(scanner, currentUser);
              subscribersView.show();
              break;
            case "2":
              PaymentsView paymentsView = new PaymentsView(scanner, currentUser);
              paymentsView.show();
              break;
            case "3":
              TicketsView ticketsView = new TicketsView(scanner, currentUser);
              ticketsView.show();
              break;
            case "0":
              System.out.println("Вихід з системи...");
              break;
            default:
              System.out.println("Невірний вибір. Спробуйте ще раз.");
              continue;
          }
        } else {
          // ADMIN and OPERATOR menu
          switch (choice) {
            case "1":
              SubscribersView subscribersView = new SubscribersView(scanner, currentUser);
              subscribersView.show();
              break;
            case "2":
              PlansView plansView = new PlansView(scanner, currentUser);
              plansView.show();
              break;
            case "3":
              PaymentsView paymentsView = new PaymentsView(scanner, currentUser);
              paymentsView.show();
              break;
            case "4":
              TicketsView ticketsView = new TicketsView(scanner, currentUser);
              ticketsView.show();
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
          break; // Exit main menu loop, return to auth
        }
      }
    }

    scanner.close();
  }
}
