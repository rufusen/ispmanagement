package com.viacheslav.ispmanagement.dto;

import com.viacheslav.ispmanagement.util.NotNullValidator;
import java.util.UUID;

public final class TicketCreateDto {

  private final UUID subscriberId;
  private final String description;

  public TicketCreateDto(UUID subscriberId, String description) {
    NotNullValidator.validate(subscriberId, "Subscriber ID");
    NotNullValidator.validate(description, "Description");

    if (description.trim().isEmpty()) {
      throw new IllegalArgumentException("Ticket description cannot be empty");
    }

    this.subscriberId = subscriberId;
    this.description = description.trim();
  }

  public UUID getSubscriberId() {
    return subscriberId;
  }

  public String getDescription() {
    return description;
  }
}
