package com.viacheslav.ispmanagement.model;

import java.util.UUID;

public class User {

  private UUID id;
  private String email;
  private String passwordHash;
  private Role role;

  public User() {
  }

  public User(UUID id, String email, String passwordHash, Role role) {
    this.id = id;
    this.email = email;
    this.passwordHash = passwordHash;
    this.role = role;
  }

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getPasswordHash() {
    return passwordHash;
  }

  public void setPasswordHash(String passwordHash) {
    this.passwordHash = passwordHash;
  }

  public Role getRole() {
    return role;
  }

  public void setRole(Role role) {
    this.role = role;
  }

  public enum Role {
    ADMIN,
    OPERATOR,
    SUBSCRIBER
  }
}
