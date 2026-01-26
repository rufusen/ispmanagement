package com.viacheslav.ispmanagement.presentation.forms;

import com.viacheslav.ispmanagement.dto.UserRegisterDto;
import com.viacheslav.ispmanagement.model.User;
import java.util.Scanner;

public class RegisterForm {

  private final Scanner scanner;

  public RegisterForm(Scanner scanner) {
    this.scanner = scanner;
  }

  public UserRegisterDto collectRegistrationData() {
    System.out.print("Електронна пошта: ");
    String email = scanner.nextLine().trim();

    System.out.print("Пароль: ");
    String password = scanner.nextLine().trim();

    System.out.println("Оберіть роль:");
    System.out.println("1. Адміністратор");
    System.out.println("2. Оператор");
    System.out.println("3. Абонент");
    System.out.print("Вибір (1-3, за замовчуванням 3): ");
    String roleChoice = scanner.nextLine().trim();

    User.Role role;
    switch (roleChoice) {
      case "1":
        role = User.Role.ADMIN;
        break;
      case "2":
        role = User.Role.OPERATOR;
        break;
      case "3":
      default:
        role = User.Role.SUBSCRIBER; // Default to SUBSCRIBER
        break;
    }

    return new UserRegisterDto(email, password, role);
  }
}
