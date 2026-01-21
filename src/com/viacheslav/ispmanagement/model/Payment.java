package com.viacheslav.ispmanagement.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class Payment {

  private UUID id;
  private LocalDateTime paymentDate;
  private BigDecimal amount;
  private String method;

  public Payment() {
  }

  public Payment(UUID id, LocalDateTime paymentDate, BigDecimal amount, String method) {
    this.id = id;
    this.paymentDate = paymentDate;
    this.amount = amount;
    this.method = method;
  }

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public LocalDateTime getPaymentDate() {
    return paymentDate;
  }

  public void setPaymentDate(LocalDateTime paymentDate) {
    this.paymentDate = paymentDate;
  }

  public BigDecimal getAmount() {
    return amount;
  }

  public void setAmount(BigDecimal amount) {
    this.amount = amount;
  }

  public String getMethod() {
    return method;
  }

  public void setMethod(String method) {
    this.method = method;
  }
}
