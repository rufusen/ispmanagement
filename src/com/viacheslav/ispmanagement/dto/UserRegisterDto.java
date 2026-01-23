package com.viacheslav.ispmanagement.dto;

import com.viacheslav.ispmanagement.model.User.Role;
import com.viacheslav.ispmanagement.util.EmailValidator;
import com.viacheslav.ispmanagement.util.NotNullValidator;

public final class UserRegisterDto {

  private final String email;
  private final String password;
  private final Role role;

  public UserRegisterDto(String email, String password, Role role) {
    NotNullValidator.validate(email, "Email");
    NotNullValidator.validate(password, "Password");
    NotNullValidator.validate(role, "Role");

    EmailValidator.validate(email);
    if (password.trim().isEmpty()) {
      throw new IllegalArgumentException("Password cannot be empty");
    }

    this.email = email.trim().toLowerCase();
    this.password = password;
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
