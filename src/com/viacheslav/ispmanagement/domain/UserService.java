package com.viacheslav.ispmanagement.domain;

import com.viacheslav.ispmanagement.dto.UserUpdateDto;
import com.viacheslav.ispmanagement.model.User;
import com.viacheslav.ispmanagement.repository.UnitOfWork;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.UUID;

public class UserService {

  private final UnitOfWork unitOfWork;

  public UserService(UnitOfWork unitOfWork) {
    this.unitOfWork = unitOfWork;
  }

  public User findById(UUID id) {
    return unitOfWork.users().findById(id).orElse(null);
  }

  public User findByEmail(String email) {
    return unitOfWork.users().findByEmail(email).orElse(null);
  }

  public List<User> getAllUsers() {
    return unitOfWork.users().findAll();
  }

  public List<User> getUsersByRole(User.Role role) {
    return unitOfWork.users().filterByRole(role);
  }

  public User updateUser(UUID userId, UserUpdateDto dto) {
    User user = unitOfWork.users().findById(userId)
        .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

    // Check email uniqueness if email changed
    if (!user.getEmail().equalsIgnoreCase(dto.getEmail())) {
      if (unitOfWork.users().findByEmail(dto.getEmail()).isPresent()) {
        throw new IllegalArgumentException("Email already exists: " + dto.getEmail());
      }
    }

    user.setEmail(dto.getEmail());
    user.setRole(dto.getRole());

    // Update password if provided
    if (dto.getPassword() != null) {
      user.setPasswordHash(hashPassword(dto.getPassword()));
    }

    unitOfWork.users().save(user);
    return user;
  }

  public void deleteUser(UUID userId) {
    if (unitOfWork.users().findById(userId).isEmpty()) {
      throw new IllegalArgumentException("User not found: " + userId);
    }
    unitOfWork.users().deleteById(userId);
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
}
