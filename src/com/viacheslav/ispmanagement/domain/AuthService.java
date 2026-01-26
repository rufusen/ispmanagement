package com.viacheslav.ispmanagement.domain;

import com.viacheslav.ispmanagement.dto.UserRegisterDto;
import com.viacheslav.ispmanagement.model.Plan;
import com.viacheslav.ispmanagement.model.Subscriber;
import com.viacheslav.ispmanagement.model.User;
import com.viacheslav.ispmanagement.repository.UnitOfWork;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AuthService {

  private final UnitOfWork unitOfWork;
  private final EmailService emailService;

  public AuthService(UnitOfWork unitOfWork, EmailService emailService) {
    this.unitOfWork = unitOfWork;
    this.emailService = emailService;
  }

  public User registerUser(UserRegisterDto dto) {
    // Check email uniqueness
    if (unitOfWork.users().findByEmail(dto.getEmail()).isPresent()) {
      throw new IllegalArgumentException("User with email " + dto.getEmail() + " already exists");
    }

    // Hash password
    String passwordHash = hashPassword(dto.getPassword());

    // Create user
    User user = new User(UUID.randomUUID(), dto.getEmail(), passwordHash, dto.getRole());
    unitOfWork.users().save(user);

    // If SUBSCRIBER role, automatically create Subscriber entity
    if (user.getRole() == User.Role.SUBSCRIBER) {
      createSubscriberForUser(user);
    }

    // Send registration email
    emailService.sendRegistrationEmail(user.getEmail());

    return user;
  }

  private void createSubscriberForUser(User user) {
    // Get or create default plan
    Plan defaultPlan = getOrCreateDefaultPlan();

    // Create subscriber with default values
    Subscriber subscriber = new Subscriber(
        UUID.randomUUID(),
        user.getEmail(), // fullName = email (temporary default)
        "not specified", // phone = "not specified"
        LocalDate.now(), // registrationDate = now()
        null, // address = null (can be set later)
        defaultPlan, // plan = DEFAULT BASIC PLAN
        user, // link to user
        new ArrayList<>(), // payments
        new ArrayList<>(), // equipmentList
        new ArrayList<>() // tickets
    );

    unitOfWork.subscribers().save(subscriber);
  }

  private Plan getOrCreateDefaultPlan() {
    List<Plan> allPlans = unitOfWork.plans().findAll();

    if (allPlans.isEmpty()) {
      // Create Basic Plan
      Plan basicPlan = new Plan(
          UUID.randomUUID(),
          "Basic",
          BigDecimal.ZERO, // price: 0
          50, // speed: 50 Mbps
          "Default basic plan"
      );
      unitOfWork.plans().save(basicPlan);
      return basicPlan;
    }

    // If plans exist, try to find a plan named "Basic"
    Plan basicPlan = allPlans.stream()
        .filter(p -> "Basic".equalsIgnoreCase(p.getName()))
        .findFirst()
        .orElse(null);

    if (basicPlan != null) {
      return basicPlan;
    }

    // If no "Basic" plan exists, return the cheapest plan
    return allPlans.stream()
        .min((p1, p2) -> p1.getPrice().compareTo(p2.getPrice()))
        .orElse(allPlans.get(0)); // fallback to first plan
  }

  public User login(String email, String password) {
    return unitOfWork.users().findByEmail(email)
        .filter(user -> verifyPassword(password, user.getPasswordHash()))
        .orElse(null);
  }

  public boolean isEmailRegistered(String email) {
    return unitOfWork.users().findByEmail(email).isPresent();
  }

  private String hashPassword(String password) {
    try {
      MessageDigest digest = MessageDigest.getInstance("SHA-256");
      byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
      StringBuilder hexString = new StringBuilder();
      for (byte b : hash) {
        String hex = Integer.toHexString(0xff & b);
        if (hex.length() == 1) {
          hexString.append('0');
        }
        hexString.append(hex);
      }
      return hexString.toString();
    } catch (NoSuchAlgorithmException e) {
      throw new RuntimeException("Password hashing failed", e);
    }
  }

  private boolean verifyPassword(String password, String passwordHash) {
    return hashPassword(password).equals(passwordHash);
  }
}
