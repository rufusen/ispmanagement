package com.viacheslav.ispmanagement.model;

import java.time.LocalDateTime;
import java.util.UUID;

public class Ticket {

  private UUID id;
  private LocalDateTime createdAt;
  private Status status;
  private String description;

  public Ticket() {
  }

  public Ticket(UUID id, LocalDateTime createdAt, Status status, String description) {
    this.id = id;
    this.createdAt = createdAt;
    this.status = status;
    this.description = description;
  }

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(LocalDateTime createdAt) {
    this.createdAt = createdAt;
  }

  public Status getStatus() {
    return status;
  }

  public void setStatus(Status status) {
    this.status = status;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public enum Status {
    OPEN,
    IN_PROGRESS,
    CLOSED
  }
}
