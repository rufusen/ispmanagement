package com.viacheslav.ispmanagement.presentation.forms;

import java.util.Scanner;

public class LoginForm {

  private final Scanner scanner;

  public LoginForm(Scanner scanner) {
    this.scanner = scanner;
  }

  public String[] collectCredentials() {
    System.out.print("Електронна пошта: ");
    String email = scanner.nextLine().trim();

    System.out.print("Пароль: ");
    String password = scanner.nextLine().trim();

    return new String[]{email, password};
  }
}
