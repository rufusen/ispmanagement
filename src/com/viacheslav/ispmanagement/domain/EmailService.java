package com.viacheslav.ispmanagement.domain;

public class EmailService {

  public void sendRegistrationEmail(String email) {
    System.out.println("=== EMAIL SERVICE ===");
    System.out.println("Лист реєстрації надіслано на: " + email);
    System.out.println("Тема: Ласкаво просимо до ISP Management System");
    System.out.println("Текст: Дякуємо за реєстрацію!");
    System.out.println("====================");
  }

  public void sendPasswordResetEmail(String email) {
    System.out.println("=== EMAIL SERVICE ===");
    System.out.println("Password reset email sent to: " + email);
    System.out.println("====================");
  }
}
