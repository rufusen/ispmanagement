package com.viacheslav.ispmanagement.presentation.pages;

import com.viacheslav.ispmanagement.domain.AuthService;
import com.viacheslav.ispmanagement.domain.EmailService;
import com.viacheslav.ispmanagement.dto.UserRegisterDto;
import com.viacheslav.ispmanagement.model.User;
import com.viacheslav.ispmanagement.presentation.forms.LoginForm;
import com.viacheslav.ispmanagement.presentation.forms.RegisterForm;
import com.viacheslav.ispmanagement.repository.UnitOfWork;
import java.util.Scanner;

public class AuthView {

  private final Scanner scanner;
  private final AuthService authService;

  public AuthView(Scanner scanner) {
    this.scanner = scanner;
    UnitOfWork unitOfWork = new UnitOfWork();
    EmailService emailService = new EmailService();
    this.authService = new AuthService(unitOfWork, emailService);
  }

  public User show() {
    while (true) {
      System.out.println("\n=== АВТЕНТИФІКАЦІЯ ===");
      System.out.println("1. Вхід");
      System.out.println("2. Реєстрація");
      System.out.println("0. Вихід");
      System.out.print("Оберіть опцію: ");

      String choice = scanner.nextLine().trim();

      switch (choice) {
        case "1":
          User user = handleLogin();
          if (user != null) {
            return user;
          }
          break;
        case "2":
          handleRegister();
          break;
        case "0":
          return null;
        default:
          System.out.println("Невірний вибір. Спробуйте ще раз.");
      }
    }
  }

  private User handleLogin() {
    System.out.println("\n--- Вхід ---");
    LoginForm loginForm = new LoginForm(scanner);
    String[] credentials = loginForm.collectCredentials();

    try {
      User user = authService.login(credentials[0], credentials[1]);
      if (user != null) {
        System.out.println("Вхід успішний! Вітаємо, " + user.getEmail());
        return user;
      } else {
        System.out.println("Невірна електронна пошта або пароль.");
        return null;
      }
    } catch (Exception e) {
      System.out.println("Помилка: " + e.getMessage());
      return null;
    }
  }

  private void handleRegister() {
    System.out.println("\n--- Реєстрація ---");
    RegisterForm registerForm = new RegisterForm(scanner);

    try {
      UserRegisterDto dto = registerForm.collectRegistrationData();
      User user = authService.registerUser(dto);
      System.out.println("Реєстрація успішна! Користувач: " + user.getEmail());
    } catch (IllegalArgumentException e) {
      System.out.println("Реєстрація не вдалася: " + e.getMessage());
    } catch (Exception e) {
      System.out.println("Помилка: " + e.getMessage());
    }
  }
}
