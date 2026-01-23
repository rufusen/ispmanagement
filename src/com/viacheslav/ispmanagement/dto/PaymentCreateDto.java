package com.viacheslav.ispmanagement.dto;

import com.viacheslav.ispmanagement.util.NotNullValidator;
import java.math.BigDecimal;
import java.util.UUID;

public final class PaymentCreateDto {

  private final UUID subscriberId;
  private final BigDecimal amount;
  private final String method;

  public PaymentCreateDto(UUID subscriberId, BigDecimal amount, String method) {
    NotNullValidator.validate(subscriberId, "Subscriber ID");
    NotNullValidator.validate(amount, "Amount");
    NotNullValidator.validate(method, "Payment method");

    if (amount.compareTo(BigDecimal.ZERO) <= 0) {
      throw new IllegalArgumentException("Payment amount must be greater than 0");
    }

    if (method.trim().isEmpty()) {
      throw new IllegalArgumentException("Payment method cannot be empty");
    }

    this.subscriberId = subscriberId;
    this.amount = amount;
    this.method = method.trim();
  }

  public UUID getSubscriberId() {
    return subscriberId;
  }

  public BigDecimal getAmount() {
    return amount;
  }

  public String getMethod() {
    return method;
  }
}
