package com.viacheslav.ispmanagement.domain;

/**
 * Simple mock email service for sending emails.
 * Outputs to console instead of sending real emails.
 */
public class EmailService {

  public void sendRegistrationEmail(String email) {
    System.out.println("=== EMAIL SERVICE ===");
    System.out.println("Registration email sent to: " + email);
    System.out.println("Subject: Welcome to ISP Management System");
    System.out.println("Body: Thank you for registering!");
    System.out.println("====================");
  }

  public void sendPasswordResetEmail(String email) {
    System.out.println("=== EMAIL SERVICE ===");
    System.out.println("Password reset email sent to: " + email);
    System.out.println("====================");
  }
}
