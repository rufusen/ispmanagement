package com.viacheslav.ispmanagement.domain;

import com.viacheslav.ispmanagement.dto.UserRegisterDto;
import com.viacheslav.ispmanagement.model.User;
import com.viacheslav.ispmanagement.repository.UnitOfWork;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

/**
 * Service for user authentication and registration.
 */
public class AuthService {

  private final UnitOfWork unitOfWork;
  private final EmailService emailService;

  public AuthService(UnitOfWork unitOfWork, EmailService emailService) {
    this.unitOfWork = unitOfWork;
    this.emailService = emailService;
  }

  /**
   * Registers a new user.
   *
   * @param dto registration data
   * @return created user
   * @throws IllegalArgumentException if email already exists
   */
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

    // Send registration email
    emailService.sendRegistrationEmail(user.getEmail());

    return user;
  }

  /**
   * Authenticates a user by email and password.
   *
   * @param email    user email
   * @param password plain text password
   * @return authenticated user or null if credentials are invalid
   */
  public User login(String email, String password) {
    return unitOfWork.users().findByEmail(email)
        .filter(user -> verifyPassword(password, user.getPasswordHash()))
        .orElse(null);
  }

  /**
   * Checks if email is already registered.
   *
   * @param email email to check
   * @return true if email exists
   */
  public boolean isEmailRegistered(String email) {
    return unitOfWork.users().findByEmail(email).isPresent();
  }

  /**
   * Hashes a password using SHA-256.
   *
   * @param password plain text password
   * @return hashed password
   */
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

  /**
   * Verifies a password against a hash.
   *
   * @param password     plain text password
   * @param passwordHash stored password hash
   * @return true if password matches hash
   */
  private boolean verifyPassword(String password, String passwordHash) {
    return hashPassword(password).equals(passwordHash);
  }
}
