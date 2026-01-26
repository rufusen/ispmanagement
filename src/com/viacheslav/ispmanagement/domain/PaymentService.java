package com.viacheslav.ispmanagement.domain;

import com.viacheslav.ispmanagement.dto.PaymentCreateDto;
import com.viacheslav.ispmanagement.model.Payment;
import com.viacheslav.ispmanagement.model.Subscriber;
import com.viacheslav.ispmanagement.repository.UnitOfWork;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class PaymentService {

  private final UnitOfWork unitOfWork;

  public PaymentService(UnitOfWork unitOfWork) {
    this.unitOfWork = unitOfWork;
  }

  public Payment makePayment(PaymentCreateDto dto) {
    // Validate subscriber exists
    Subscriber subscriber = unitOfWork.subscribers().findById(dto.getSubscriberId())
        .orElseThrow(
            () -> new IllegalArgumentException("Subscriber not found: " + dto.getSubscriberId()));

    // Create payment
    Payment payment = new Payment(
        UUID.randomUUID(),
        LocalDateTime.now(),
        dto.getAmount(),
        dto.getMethod()
    );

    unitOfWork.payments().save(payment);

    // Add payment to subscriber's payment list
    if (subscriber.getPayments() == null) {
      subscriber.setPayments(new java.util.ArrayList<>());
    }
    subscriber.getPayments().add(payment);
    unitOfWork.subscribers().save(subscriber);

    return payment;
  }

  public List<Payment> getPaymentsBySubscriber(UUID subscriberId) {
    Subscriber subscriber = unitOfWork.subscribers().findById(subscriberId)
        .orElseThrow(() -> new IllegalArgumentException("Subscriber not found: " + subscriberId));

    return subscriber.getPayments() != null ? subscriber.getPayments() : List.of();
  }

  public BigDecimal getTotalPaidBySubscriber(UUID subscriberId) {
    List<Payment> payments = getPaymentsBySubscriber(subscriberId);
    return payments.stream()
        .map(Payment::getAmount)
        .reduce(BigDecimal.ZERO, BigDecimal::add);
  }

  public List<Payment> getPaymentsByMinAmount(BigDecimal minAmount) {
    return unitOfWork.payments().filterByMinAmount(minAmount);
  }

  public Payment findById(UUID id) {
    return unitOfWork.payments().findById(id).orElse(null);
  }
}
