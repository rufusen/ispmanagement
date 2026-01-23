package com.viacheslav.ispmanagement.dto;

import com.viacheslav.ispmanagement.model.User.Role;
import com.viacheslav.ispmanagement.util.EmailValidator;
import com.viacheslav.ispmanagement.util.NotNullValidator;

public final class UserUpdateDto {

  private final String email;
  private final String password; // Optional - can be null
  private final Role role;

  public UserUpdateDto(String email, String password, Role role) {
    NotNullValidator.validate(email, "Email");
    NotNullValidator.validate(role, "Role");

    EmailValidator.validate(email);

    if (password != null && password.trim().isEmpty()) {
      throw new IllegalArgumentException("Password cannot be empty if provided");
    }

    this.email = email.trim().toLowerCase();
    this.password = password != null ? password : null;
    this.role = role;
  }

  public String getEmail() {
    return email;
  }

  public String getPassword() {
    return password;
  }

  public Role getRole() {
    return role;
  }
}
